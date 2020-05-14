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
import java.lang.Thread;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import com.google.common.collect.Iterables;

import helpers.HttpReq;
import helpers.FileHelpers;
import helpers.CassandraDb;

import dataClasses.Podcast;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.querybuilder.term.Term;


/* 
 * Represents a single file of search results
 * For one file, gets all search results and retrieves the rss feed data
 *
 * TODO change name to "SearchQuery"
 * as opposed to PodcastSearch, this is just one query
 */

public class QueryResults {
  public String filename;

  // receive when put into db. uuid.
  public String id;

  // helper based on filename
  public String relativePath;

  public String term;
  public String searchType;
  // file where data is written to, if exists
  public File file;
  // to be either fetched from api or file
  public String podcastJson;

  // whether or not had to hit the external api to retrieve the json
  public boolean madeApiCall;

  private ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
  private ArrayList<String> podcastIds = new ArrayList<String>();
  private static CassandraDb db;
  public static List<String> persistMethods = Arrays.asList("write-to-file", "db", "both");

  // for when initializing from just search term and search type
  public QueryResults(String term, String searchType, boolean refreshData) 
    {
      this.term = term;
      this.searchType = searchType;

      String typePrefix = searchType != "all" ? searchType.replace("Term", "") : "generalSearch" ;
      this.filename = typePrefix + "_" + term.replaceAll(" ", "-")  + ".json";
      this.relativePath = "podcast-data/" + filename;

      try {
        // note that this will set even if the file does not exist, we just want a reference to the location
        this.file = new File(FileHelpers.getFilePath(this.relativePath));
      } catch (IOException e) {
        System.out.println("Error getting file: " + this.filename);
        return;
      }

  }

  // for when we have already ran the search
  // NOTE make sure to set term manually
  public QueryResults(File queryResultsFile) 
    throws FileNotFoundException {
      this.file = queryResultsFile;
      this.filename = this.file.getName();
      this.relativePath = "podcast-data/" + this.filename;
      Matcher matcher = Pattern.compile(this.filename).matcher("^(\\D+)_(\\D+).json$");
      matcher.find();
      this.term = matcher.group(1);
      this.searchType = matcher.group(2);
        System.out.println(matcher.group(1));
        System.out.println(" and type should be: ");
        System.out.println(matcher.group(2));

      if (!file.exists()) {
        throw new FileNotFoundException(this.filename + "not found (No such file or directory)!");
      } else if (this.term == null) {

        System.out.println(matcher.group(1));
        System.out.println(" and type should be: ");
        System.out.println(matcher.group(2));
        throw new FileNotFoundException(this.filename + " did not have a term set...");
      }
  }

  //////////////////////////////////////
  // some db stuff 
  // TODO add into something all models can borrow from

  // TODO add some in-instance caching, for when retrieving from database more than once
  public boolean exists () {
    ResultSet result = db.execute("SELECT * FROM search_results_by_filename WHERE filename='" + filename + "' LIMIT 1;");

    return Iterables.size(result) > 0;
  }

  public void insertIntoDb () {
    Term ts = db.getTimestamp();

      /*
    insertInto("search_results")
      .value("id", literal(UUID.randomUUID()))
      .value("filename", literal(this.filename))
      .value("term", literal(this.term))
      .value("search_type", literal(this.searchType))
      .value("result_json", literal(this.podcastJson));
    /*
      .value("created_at", ts)
      .value("updated_at", ts);
      */

    insertInto("search_results_by_filename")
      .value("filename", literal(this.filename))
      .value("term", literal(this.term))
      .value("search_type", literal(this.searchType))
      .value("result_json", literal(this.podcastJson))
      .value("created_at", ts)
      .value("updated_at", ts);
  }


  // persists the search results according to the persisting method
  public void persistSearchResult (String persistMethod, boolean persistEachFileOnce) {
    if (persistMethod.equals("both") || persistMethod.equals("write-to-file")) {
      // write to a file 
      if (!persistEachFileOnce || !this.exists()) {
        FileHelpers.write(this.relativePath, this.podcastJson);
      }
    } 
    
    if (persistMethod.equals("both") || persistMethod.equals("db")) {
      // make sure that this.db is set if use this
      insertIntoDb(); 

    } 

    if (!persistMethods.contains(persistMethod)) {
      System.out.println("--------------------------");
      System.out.println("invalid persistence method " + persistMethod);
      System.out.println("--------------------------");
    }

  }



  ////////////////////////////////////////////
  //

  // either returns json we already have set, or reads file, pulls data we need, and returns as json
  // DOES NOT hit any external apis (for that, see this.getPodcastJson)
  private String getPodcastJsonFromFile () throws IOException {
    try {
      this.podcastJson = FileHelpers.read(this.relativePath);
      return this.podcastJson;

    } catch (IOException e) {
			// could be different types of errors I think...though maybe all are IO? but Exception is fiene
      System.out.println(e);
      e.printStackTrace();

      throw e;
    }
  }


  // reads file, pulls data we need, and sets to array
  // no reason to set to variable, should only call once ever per QueryResult instance
  private JSONArray getSearchResults () throws IOException {
    getPodcastJsonFromFile ();

    try {
      JSONObject contentsJson = (JSONObject) new JSONObject(this.podcastJson);
      JSONArray resultsJson = (JSONArray) contentsJson.get("results");
      // currently not using
      //int resultsCount = (int) contentsJson.get("resultCount");

      return resultsJson;

    } catch (JSONException e) {
      System.out.println(e);
      e.printStackTrace();

      // so that this can only throw one type of exception:
      throw new IOException("failed to read JSON for file " + this.filename);
    }
  };

  public ArrayList<Podcast> getPodcasts () throws IOException {
    if (podcasts.size() > 0) {
      // already got them, so just return
      return podcasts;

    } else {
      JSONArray resultsJson = getSearchResults();

      for (int i = 0; i < resultsJson.length(); i++) {
        JSONObject podcastJson = resultsJson.getJSONObject(i);

        Podcast podcast;
        try {
          podcast = new Podcast(podcastJson, this);
        } catch (Exception e) {
          // normally just allow ExecutionException (which is what this ends up being), at least what I've seen so far) to throw, but for this, is really just a json issue, want to continue no matter what
          System.out.println("Error getting info for podcast " + i);
          System.out.println("moving to next");
          System.out.println(e);
          e.printStackTrace();
          continue;
        }
        podcasts.add(podcast);
        podcastIds.add(podcast.id);
      }
    };

    return podcasts;
  }

  public ArrayList<String> getPodcastIds () throws IOException {
    if (podcastIds.size() > 0) {
      return podcastIds;

    } else {
      getPodcasts();
      return podcastIds;

    }
  }

  // retrieves either from db or file or from external api
  // One way or the other, returns the json
  public String getPodcastJson (boolean refreshData) 
    throws IOException {

      // check if we should skip
      if (this.podcastJson != null) {
        // don't set this.madeApiCall = true here, since maybe we hit the external API earlier on in the lifecycle
        return this.podcastJson;

      } else if (!refreshData && this.file.exists() ) { 
        // TODO probably faster to do this one time rather than hitting db every time for every file, for performance
        System.out.println("skipping search for" + filename);
        this.getPodcastJsonFromFile(); 
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
    // public void getEpisodes(HashMap<String, Podcast> podcastsProcessed, ArrayList<Podcast> podcastIdsProcessed, boolean refreshData){
    public void getEpisodes() throws IOException {
      for (Podcast podcast : getPodcasts()) {

        // get RSS for podcast, to get episode list

        podcast.getEpisodes();


        // write to file. Later TODO send to db. Or do something useful with it! 

      };


      System.out.println("finished getting episodes for this set of query results which is stored in: " + this.filename);
  }
}


