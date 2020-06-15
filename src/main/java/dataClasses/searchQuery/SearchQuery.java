package dataClasses.searchQuery;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.datastax.oss.driver.api.core.PagingIterable;

import dataClasses.podcast.Podcast;

import helpers.DataClassesHelpers;
import helpers.HttpReq;


/* 
 * Represents a single set of search results, given a term and search type
 *
 * as opposed to PodcastSearch, this is just one query (ie one search ran against an external API)
 */

public class SearchQuery extends SearchQueryBase implements Serializable {

  // renaming to searchQueryByTermRecord
  // public Row dbRow;

  // whether or not had to hit the external api in the lifecycle of this object to retrieve the json
  // Can be true or false, whether or not record persisted. 
  // false would mean we've saved to db so didn't hit api this time
  // true would mean we hit their external API, and then persisted it this round
  // if true but persisted is false, means we've hit their api, but not yet persisted
  public boolean madeApiCall;

  // whether or not this object is up to date with our db, assuming no one else wrote to db during the lifecycle of this object
  private boolean persisted = false;

  // whether we've fetched podcasts on this instance yet
  private boolean extractedPodcasts = false;

  private ArrayList<Podcast> podcasts = new ArrayList<Podcast>();

  /////////////////////////////
  // constructors

  // for when initializing from just search term and search type
  public SearchQuery () {}
  
  public SearchQuery (String term, String searchType) {
    this.term = term;
    this.searchType = searchType;
  }

  public SearchQuery (SearchQueryByTermRecord searchQueryByTermRecord) {
    DataClassesHelpers.copyMatchingFields(searchQueryByTermRecord, this);
  }

  // Not using, DAO should handle this
  //
  // for when we have already ran the search, and retriving from DB
  // public SearchQuery(Row dbRow) 
  //   // TODO figure out what to catch if the data in db is corrupted
  //   {
  //     this.term = dbRow.getString("term");
  //     this.searchType = dbRow.getString("search_type");
  //     this.dbRow = dbRow;
  // }

  /////////////////////////////////
  // static methods

  // get all searches from all time
  // https://docs.datastax.com/en/drivers/java/4.6/com/datastax/oss/driver/api/core/PagingIterable.html
  public static List<SearchQuery> findAll () throws Exception {
    PagingIterable<SearchQueryByTermRecord> iterable = SearchQueryByTermRecord.getDao().findAll();
    // in this case, we want it to be easy. And there's not that many. So just get all of them 
    // if performance is an issue, consider using map https://docs.datastax.com/en/drivers/java/4.6/com/datastax/oss/driver/api/core/PagingIterable.html#map-java.util.function.Function-

    List<SearchQuery> allSearchQueries = new ArrayList<SearchQuery>(); 
    for (SearchQueryByTermRecord record : iterable) {
      allSearchQueries.add(new SearchQuery(record));
    }

    return allSearchQueries;
  }



  ////////////////////////////////
  // display helpers

  public String friendlyName () {
    return "(" + this.getTerm() + ", " + this.getSearchType() + ")";
  };


  //////////////////////////////////////
  // some db stuff 

  // check to see if we have ever hit the external API already for this search query

  // basically the opposite of a "dirty" flag, this checks to see if we're up to date with the db AND there is a record in the db
  // checks if this search query is persisted
  // TODO maybe want to rename this one too, sometimes is__ methods are used as getters for booleans. So rename to make clear this can hit our db
  public boolean isPersisted () throws Exception {
    // if we make changes at any point, need to set this to false. So if true, it should definitely be true. If null or false, then check db and see. NOTE this is a read-only command
    if (this.persisted) {
      return true;
    }

    this.reloadFromDb();

    return this.persisted;
  }

  public void reloadFromDb () throws Exception {
    SearchQueryByTermRecord searchQueryByTermRecord = SearchQueryByTermRecord.findOne(this);

    // TODO test what this is if nothing is there in db
    if (searchQueryByTermRecord != null) {

      // update this object based on what we retrieved
      DataClassesHelpers.copyMatchingFields(searchQueryByTermRecord, this);
      // record that this obj is up to date with db
      this.persisted = true;

    } else {
      // don't change anything on current object. Otherwise, for example, this would remove term and searchType from current object
      // record that this obj is up to date with db
      this.persisted = false;
    }
  }

  // converts resultJson (a json string representing what itunes returned to us, including the results themselves and metadata) to a java JSONObject
  // then gets the results from the "results" key of that object
  //
  // assumes format that itunes search api returns
  private JSONArray extractSearchResultsJSON () throws IllegalArgumentException, IOException {

    try {
      JSONObject contentsJson = (JSONObject) new JSONObject(this.resultJson);
      JSONArray resultsJson = (JSONArray) contentsJson.get("results");
      // currently not using
      //int resultsCount = (int) contentsJson.get("resultCount");

      System.out.println("got results");
      return resultsJson;

    } catch (JSONException e) {
      System.out.println(e);
      e.printStackTrace();

      // so that this can throw one less type of exception:
      throw new IOException("failed to read JSON for  " + this.friendlyName());
    }
  };

  ////////////////////////////////////////
  // methods for performing search on external api

  // retrieves from db or from external api
  // If refreshData is true, that means that a search is definitely necessary
  // if not, then only necessary to hit eexternal API if we don't have data already for this search query
  // One way or the other, returns the json
  public String performSearchIfNecessary (boolean refreshData) 
    // TODO which type of exception?
    throws Exception {

      // check if we should skip
      if (this.resultJson != null) {
        // don't set this.madeApiCall = true here, since maybe we hit the external API earlier on in the lifecycle
        return this.resultJson;

      } else if (!refreshData && this.isPersisted() ) { 
        // note that isPersisted will retrieve from db if we have to
        System.out.println("Already have this record; skipping search for" + this.friendlyName());

        return this.resultJson;

      } else {
        this.resultJson = this.performSearch();

        return this.resultJson;
      }
  }

  // hits external api to get podcast json
  // DON'T CALL DIRECTLY (for the most part), even internally call performSearch
  // Could expose this publicly in future though if we wanted to
  private String performSearch () 
    throws IOException {
      try {

        // apparently apple doesn't need (or even like) the pluses in the query, so don't replace
        // String queryTerm = this.term.replaceAll(" ", "+");
        String urlStr = "https://itunes.apple.com/search";

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("media", "podcast");
        queryParams.put("term", this.term);

        // set limit to 200, let's just get all of it (default: 50, max: 200)
        queryParams.put("limit", "200");
        // set version, language and country in case their defaults change
        queryParams.put("version", "2");
        queryParams.put("lang", "en_us");
        queryParams.put("country", "US");
        if (searchType != "all") {
          queryParams.put("attribute", this.searchType);
        }
         
        // write params to request...yes it's this crazy.
        // Basically converts our map to a string, then writes that string to the http url connection via "output stream" api. 
        // (is an api for writing data to something, in this case, writing params to the url)

        // begin reading
        this.madeApiCall = true;  // set whether or not we suceeed in api call here
        // record that this obj is NOT up to date with db
        this.persisted = false;

        String result = HttpReq.get(urlStr, queryParams);
        this.updatedAt = Instant.now();

        System.out.println(result);

        this.resultJson = result;
        return result;

      } catch (IOException e) {
        System.out.println("Error after hitting db for search query:");
        System.out.println(e);

        throw e;
      }
  }

  ///////////////////////////////////////////////////////////////////////////
  // helpers for extracting and persisting associations (mostly podcasts and episodes)

  // takes the results json and extracts podcasts from it
  // DOES NOT PERSIST podcasts or do anything for episodes
  // Does also hit the rss feed using a GET request, so that's a little slow. Maybe want to break that out in the future
  public ArrayList<Podcast> extractPodcasts () throws IOException {
    if (this.extractedPodcasts) {
      // already got them, so just return
      return this.podcasts;

    } else {
      System.out.println("*********EXTRACTING PODCASTS FOR SEARCH " + this.friendlyName() + " *************");
      JSONArray resultsJson = this.extractSearchResultsJSON();

      for (int i = 0; i < resultsJson.length(); i++) {
        try {
          JSONObject resultJson = resultsJson.getJSONObject(i);
          Podcast podcast = extractOnePodcast(resultJson);
          this.podcasts.add(podcast);
        } catch (Exception e) {
          System.out.println("skipping this one and moving to next");
        }
      }
    };

    System.out.println("*********EXTRACTED PODCASTS*************");
    this.extractedPodcasts = true;
    return podcasts;
  }

  // DOES NOT THROW ERRORS just catch it and continue.
  // currently designed to be used in a loop
  // NOTE this currently DOES NOT fetch the rss feed as well
  private Podcast extractOnePodcast (JSONObject resultJson) throws Exception {
    Podcast podcast;

    try {
      System.out.println("extracting from json");
      System.out.println(resultJson);
      podcast = new Podcast(resultJson, this);

      // Removing this, hits external apis and makes things really slow
      // not sure if I want to call this here, but it's fine for now
      //System.out.println("updating based on RSS");
      // could do a try-catch here, if wanted to persist this podcast even if this get request failed. But really I don't care about podcasts that don't have RSS feeds that work, so not bothering. 
      // I'm assuming if hitting their rss feed fails, it's their fault of course, but it's a reasonable enough assumption for my use case (if it's my fault, maybe I'd want to persist the podcast and try getting the rss again later). 
      // But it is often enough that the podcast's rss is wrongly listed or they don't want people crawling it, so just don't bother persisting this podcast
      //podcast.updateBasedOnRss();

      return podcast;

    } catch (Exception e) {
      // normally just allow ExecutionException (which is what this ends up being), at least what I've seen so far) to throw, but for this, is really just a json issue, want to continue no matter what
      System.out.println("Error extracting info for podcast from json: " + resultJson);
      System.out.println("  \n  ");
      System.out.println(e);
      e.printStackTrace();

      throw e;
    }

  }

  // loops over the associated podcasts and persists them
  public void persistPodcasts () throws Exception {
    System.out.println("*********PERSISTING PODCASTS*************");
    if (podcasts.size() == 0) {
      // have none, so just return
      System.out.println("no this search didn't have any podcasts, so not persisting");

    } else {
      for (Podcast podcast : this.getPodcasts()) {
        // get RSS for podcast, to get episode list
        podcast.persist();
      };

      System.out.println("finished getting episodes for this set of query results: " + this.friendlyName());
    };

    System.out.println("*********PERSISTED PODCASTS*************");
  }

  // gets rss data for a podcast (which includes all the episode data)
  // TODO currently, we are not verifying whether or not we've already gotten data for this podcast. 
  // if we do, can use following method definition or something like it:
  //public void getEpisodes() throws Exception { // RENAMED
  public void extractEpisodes() throws Exception {
    System.out.println("*********EXTRACTING EPISODES*************");
    System.out.println("about to get episodes for query" + this.friendlyName());
    for (Podcast podcast : this.getPodcasts()) {
      // get RSS for podcast, to get episode list
      podcast.extractEpisodes();
    };

    System.out.println("finished getting episodes for this set of query results" + this.friendlyName());
    System.out.println("--");
  }

  // note that this could be ran maybe as much as once a week, and get new results, since each podcast's rss feed could be updated within that time with a new podcast
  public void persistEpisodes() throws Exception {
    System.out.println("*********PERSISTING EPISODES*************");
    System.out.println("about to persist episodes for query" + this.friendlyName());
    for (Podcast podcast : this.getPodcasts()) {
      // get RSS for podcast, to get episode list
      podcast.persistEpisodes();
    };

    System.out.println("finished persisting episodes for this set of query results" + this.friendlyName());
    System.out.println("--");
  }

  // does it all. Extract all podcasts, persist all podcasts, and extract all their episodes and persist all their episodes
  public void extractAndPersistData () throws Exception {
    System.out.println("****************EXTRACTING AND PERSISTING PODCASTS AND EPISODES FOR QUERY" + this.friendlyName() + " ******************");
    JSONArray resultsJson = this.extractSearchResultsJSON();

    System.out.println("processing " + resultsJson.length() + " podcasts");
    for (int i = 0; i < resultsJson.length(); i++) {
      try {
        JSONObject resultJson = resultsJson.getJSONObject(i);
        Podcast podcast = extractOnePodcast(resultJson);
        this.podcasts.add(podcast);

        System.out.println(podcast);

        podcast.persist();

        // TODO combine this so as we extract the episode, we persist it immediately
        podcast.extractEpisodes();
        podcast.persistEpisodes();
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("skipping this one and moving to next");
      }
    }

    // though keep in mind, there might be some skipped due to errors
    this.extractedPodcasts = true;

    // persist podcast count to the search query record
    this.setPodcastCount(this.podcasts.size());
    this.persist();

    System.out.println("*********EXTRACTED AND PERSISTED PODCASTS AND EPISODES FOR" + this.friendlyName() + "*************");

  }

  ////////////////////////////////////////
  // DB helpers

  public void persist () throws Exception {
    // currently only one table for this podcast so just save that
    SearchQueryByTermRecord s = new SearchQueryByTermRecord(this);
    s.save();
  }



  //////////////////////
  // Getters and setters

  public ArrayList<Podcast> getPodcasts() {
    if (!extractedPodcasts) {
      throw new IllegalArgumentException("Need to extract podcasts before can get them!");
    }

    return podcasts;
  }

  public void setPodcasts(ArrayList<Podcast> podcasts) {
    this.podcasts = podcasts;
  }



}


