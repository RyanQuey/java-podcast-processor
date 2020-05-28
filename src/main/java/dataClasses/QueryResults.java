package dataClasses;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.IllegalArgumentException;
import java.lang.Thread;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import com.google.common.collect.Iterables;

import helpers.HttpReq;
import cassandraHelpers.CassandraDb;

import dataClasses.Podcast;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.querybuilder.term.Term;


/* 
 * Represents a single set of search results, given a term and search type
 *
 * TODO change name to "SearchQuery"
 * as opposed to PodcastSearch, this is just one query
 */

public class QueryResults {

  // receive when put into db. uuid. TODO maybe don't set or use this
  public String id;

  public String term;
  public String searchType;
  public Row dbRow;

  // to be either fetched from api or db
  // NOTE instead of storing in db as a Cassandra collection, just store as json string. This Is better in case the data we receive from foreign api is corrupted in one way or the other. UNLESS we want to verify on write instead of read, but that seems to be less than optimal for distributed systems like Cassandra. Would rather just dump the info in
  public String podcastJson;
  public String constructedFrom;


  // TODO when add other external apis, need to set dynamically
  public String externalApi = "itunes";

  // whether or not had to hit the external api to retrieve the json
  public boolean madeApiCall;
  private boolean persisted = false;

  private ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
  private static CassandraDb db;
  public Instant updatedAt;

  // for when initializing from just search term and search type
  // TODO refreshData should eventually just force hitting the database immediately; currently does nothing
  public QueryResults(String term, String searchType, boolean refreshData) 
    {
      this.constructedFrom = "term-and-type";
      this.term = term;
      this.searchType = searchType;

      String typePrefix = searchType != "all" ? searchType.replace("Term", "") : "generalSearch" ;
  }

  // for when we have already ran the search, and retriving from DB
  public QueryResults(Row dbRow) 
    // TODO figure out what to catch if the data in db is corrupted
    {
      this.constructedFrom = "db-record";
      this.term = dbRow.getString("term");
      this.searchType = dbRow.getString("search_type");
      this.dbRow = dbRow;
  }

  ////////////////////////////////
  // display helpers

  public String friendlyName () {
    return "(" + this.term + ", " + this.searchType + ")";
  };


  //////////////////////////////////////
  // some db stuff 
  // TODO add some of these methods into something all models can borrow from

  // checks if this search query is persisted
  // TODO for performance, if known batch job, do a single query to grab all records for this partition once and check that instead? 
  // TODO be careful using this, maybe better to not do this in general, since requires a read for every write? But want to avoid hitting external api quota...
  public boolean getPersisted () {
    if (this.persisted) {
      return true;
    }

    String query = "SELECT * FROM search_results_by_term WHERE term='" + term + "' AND search_type = '" + searchType + "' AND external_api = 'itunes' LIMIT 1;";
    System.out.println("checking for existence by using query: " + query);
    ResultSet result = db.execute(query);


    // will be null if nothing found
    // NOTE that return Iterables.size(result) > 0; doesn't work, always returns 0 for some reason
    Row row = result.one();
    if (row != null) {
      this.dbRow = row;
      this.persisted = true;
      this.getPodcastJsonFromDbRecord();

      return true;
    } else {
      return false;
    }
  }

  // TODO implement DAO for query_results
  // untested and not used currently
  public void save () {
    String updateQuery = update("search_results_by_term")
      .setColumn("result_json", literal(this.podcastJson))
      .setColumn("updated_at", literal(this.updatedAt))
      .whereColumn("term").isEqualTo(literal(this.term))
      .whereColumn("search_type").isEqualTo(literal(this.searchType))
      .whereColumn("external_api").isEqualTo(literal(this.externalApi))
      .asCql();

    db.execute(updateQuery);
    this.persisted = true;
  }

  // for now will generate lots of duplicates, but can handle that later
  // NOTE not using, just save it
  public void insertIntoDb () {
    Term ts = db.getTimestamp();

    String insertQuery = insertInto("search_results_by_term")
      .value("term", literal(this.term))
      .value("search_type", literal(this.searchType))
      .value("result_json", literal(this.podcastJson))
      .value("external_api", literal(this.externalApi))
      .value("updated_at", ts)
      .asCql();

    this.persisted = true;
    db.execute(insertQuery);
  }


  ////////////////////////////////////////////
  //

  // pulls data we need from db record, and returns as json
  // DOES NOT hit any external apis (for that, see this.getPodcastJson)
  // TODO rename, sounds like it hits db
  private String getPodcastJsonFromDbRecord () throws IllegalArgumentException {
    if (this.podcastJson != null) {
      // can save a hop to the db
      return this.podcastJson;
    }

    try {
      this.podcastJson = this.dbRow.getString("result_json");
      return this.podcastJson;

    } catch (IllegalArgumentException e) {
			// could be different types of errors I think...though maybe all are IO? but Exception is fiene
      System.out.println(e);
      e.printStackTrace();

      throw e;
    }
  }

  // no reason to set to variable, should only call once ever per QueryResult instance
  // currently assumes externalApi.equals("itunes")
  // TODO rename to fetchPodcastJson or something
  private JSONArray getSearchResults () throws IllegalArgumentException, IOException {
    System.out.println("extracting");
    getPodcastJsonFromDbRecord();
    System.out.println("extracted");

    try {
      JSONObject contentsJson = (JSONObject) new JSONObject(this.podcastJson);
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

  public ArrayList<Podcast> getPodcasts () throws IOException {
    if (podcasts.size() > 0) {
      // already got them, so just return
      return podcasts;

    } else {
      System.out.println("*********GETTING PODCASTS FOR SEARCH " + this.friendlyName() + " *************");
      JSONArray resultsJson = getSearchResults();

      for (int i = 0; i < resultsJson.length(); i++) {

        JSONObject podcastJson = resultsJson.getJSONObject(i);

        Podcast podcast;
        try {
          System.out.println("extracting from json");
          System.out.println(podcastJson);
          podcast = new Podcast(podcastJson, this);
          // not sure if I want to call this here, but it's fine for now
          System.out.println("updating based on RSS");
          podcast.updateBasedOnRss();

          this.podcasts.add(podcast);

        } catch (Exception e) {
          // normally just allow ExecutionException (which is what this ends up being), at least what I've seen so far) to throw, but for this, is really just a json issue, want to continue no matter what
          System.out.println("Error getting info for podcast with json:: " + podcastJson);
          System.out.println("  \n  ");
          System.out.println(e);
          e.printStackTrace();
          System.out.println("moving to next");
          continue;
        }

        System.out.println("adding podcast to result list");
      }
    };

    System.out.println("*********GOT PODCASTS*************");
    return podcasts;
  }

  public void persistPodcasts () throws IOException {
    System.out.println("*********PERSISTING PODCASTS*************");
    if (podcasts.size() == 0) {
      // have none, so just return
      System.out.println("no this search didn't have any podcasts, so not persisting");

    } else {
      for (Podcast podcast : getPodcasts()) {
        // get RSS for podcast, to get episode list
        Podcast.getDao().save(podcast);
      };

      System.out.println("finished getting episodes for this set of query results: " + this.friendlyName());
    };

    System.out.println("*********PERSISTED PODCASTS*************");
  }

  // retrieves from db or from external api
  // One way or the other, returns the json
  public String getPodcastJson (boolean refreshData) 
    // TODO if we ever use refreshData make sure that we do not set created_at, but only updated_at for those records

    // TODO which type of exception?
    throws Exception {

      // check if we should skip
      if (this.podcastJson != null) {
        // don't set this.madeApiCall = true here, since maybe we hit the external API earlier on in the lifecycle
        return this.podcastJson;

      } else if (!refreshData && this.getPersisted() ) { 
        // hit db 
        // TODO this is pretty messy, relies on this.getPersisted() to hit db and retrieve record for us. should find a better way and give this object (and all models) a better, cleaner api. But for now just working on learning these data engineering libs

        System.out.println("Already have this record; skipping search for" + this.friendlyName());

        this.getPodcastJsonFromDbRecord(); 
        return this.podcastJson;

      } else {
        this.madeApiCall = true; 
        this.performQuery();
        return this.podcastJson;
      }
  }

  // hits external api to get podcast json
  private void performQuery () 
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
        String result = HttpReq.get(urlStr, queryParams);
        this.updatedAt = Instant.now();

        System.out.println(result);

        podcastJson = result;

      } catch (IOException e) {
        System.out.println("Error after hitting db for search query:");
        System.out.println(e);

        throw e;
      }
  }

  // gets rss data for a podcast (which includes all the episode data)
  // TODO currently, we are not verifying whether or not we've already gotten data for this podcast. 
  // if we do, can use following method definition or something like it:
  public void getEpisodes() throws Exception {
    System.out.println("*********EXTRACTING EPISODES*************");
    System.out.println("about to get episodes for query" + this.friendlyName());
    for (Podcast podcast : getPodcasts()) {
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
    for (Podcast podcast : getPodcasts()) {
      // get RSS for podcast, to get episode list
      podcast.persistEpisodes();
    };

    System.out.println("finished persisting episodes for this set of query results" + this.friendlyName());
    System.out.println("--");
  }
}


