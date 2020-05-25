import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.System;
import java.lang.Exception;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.io.IOException; 
import java.io.FileNotFoundException;
import java.io.File;
import org.json.JSONObject;

// local imports
import helpers.HttpReq;
import helpers.FileHelpers;
import helpers.CassandraDb;

import dataClasses.QueryResults;
import dataClasses.PodcastSearch;
import dataClasses.Podcast;
import dataClasses.Episode;

import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.datastax.oss.driver.api.core.cql.Row;

public class Main {

  /////////////////////////////////////
  // static vars

  static CassandraDb db;

  // if true, get new search results as well.
  static boolean podcastSearchRequested = false;

  static String toProcess = "";
  
  static PodcastSearch podcastSearch = new PodcastSearch();

  // which files to process
  static ArrayList<QueryResults> searchResultsToProcess = new ArrayList<QueryResults>();

  static ArrayList<Episode> episodesFound = new ArrayList<Episode>();


  ///////////////////////////////////////
  // private static methods
  
  private static void processArgs(String[] args) {
    System.out.println("Running with options:");     
    for (String s: args) {
      System.out.println(s);     

      ////////////////////
      // perform search or not?
      if (s.equals("--perform-search=true")) {
        podcastSearchRequested = true;
      };

      ////////////////////
      // what searches to process?
      if (s.equals("--process=new")) {
        // if true, only process the search results we just received (not all)
        toProcess = "new-search";

      } else if (s.equals("--process=default-query")) {
        // if true, process a default query (FOR TESTING ONLY)
        toProcess = "default-query";
        System.out.println(toProcess);     
      } else if (s.equals("--process=all")) {
        toProcess = "all";
      } else if (s.equals("--process=none")) {
        toProcess = "none";
      } else if (toProcess.equals("")) {
        // the default results to process. NOTE that other flags will also hit this conditional, so don't just do `} else {...`
        toProcess = "new-search";
        System.out.println(toProcess);     
      };
    };
  }

  private static void performSearch(String[] args) {
    // initialize this when we initialize the podcastSearch itself
    podcastSearch.performAllQueries(args);
  }

  private static void setSearchResultsToProcess() {
    if (toProcess.equals("default-query")) {
      // process only the specified default file (FOR TESTING ONLY)
      // make sure to copy the QueryResults constructor when term and searchType are passed in. Keep this in sync with that (that is going to be more up to date than this)
      String searchType = "all";
      String term = "big data";
      System.out.println("Only going to process the default query: (" + term + ", " + searchType + ")");
      // TODO don't use file, query db instead

      // beware, might be more than one in actuality, if user passed in --process-new-search too on accident. 

      QueryResults qr = new QueryResults(term, searchType, false);
      System.out.println(qr.term);
      searchResultsToProcess.add(qr);

    } else if (toProcess.equals("new-search")) {
      // For the most part, will use this. the other ones are for testing
      // NOTE Even if we had already persisted the search results previously, if the search was done, we are processing currently. Maybe Want to change his behavior later, depending on how we do the searches.
      System.out.println("only processing new search results");
      searchResultsToProcess.addAll(podcastSearch.results);
      // note: if didn't run search, won't do anything

    } else if (toProcess.equals("none")) {
      System.out.println("processing none");

    } else if (toProcess.equals("all")) {
      // process all files, not looking at podcasts
      // TODO paginate, don't just send all at once!

      // TODO maybe better to just iterate over the podcasts, some of which might already have the data (?)
      try {
        /* if processing data from files
        List<File> files = Arrays.asList(new File(FileHelpers.getFilePath("podcast-data")).listFiles());
        for (File file : files) {
          searchResultsToProcess.add(new QueryResults(file));
        }
        */

        // NOTE each search can have zero or many search results
        // NOTE is it List<Row> or List<ElementT>?
        List<Row> allSearches = PodcastSearch.fetchAllSearches();
        
        for (Row dbRow : allSearches) {
          // TODO for better performance, and less memory use, don't add them all here. Instead, just iterate over the ResultSet, and call "resultSet.one()" multiple times. 
          // But  for now no problem, since for the most part we shouldn't even process all the records, should process the results as we get them.
          searchResultsToProcess.add(new QueryResults(dbRow));
        }

      } catch (Exception e) { 
        // temporarily just using a more general exception. But I'm guessing that hitting db with db.execute can throw a very specific error...
      // } catch (IOException e) { 
        System.out.println("Failed to process default query");
        System.out.println(e);
      }

    }
  }

  private static void processSearchResults() {
    System.out.println("now processing files (count): " + searchResultsToProcess.size());

    // iterate over search result files
    for (QueryResults queryResults : searchResultsToProcess) {
      String filename = queryResults.filename;

      try {
				// need to getPodcasts before we can call getEpisodes
				// perhaps one day, queryResults will have a single method that gets podcasts, and then as it gets it it persists it immediately. But right now just building out our api

				// most often unnecessary, but if so it will only do a quick boolean check
				queryResults.getPodcastJson(false);
				// hits the feedUrls for each podcast and pulls out data from that xml to get data about the podcasts 
				queryResults.getPodcasts();
				// persists data we just got
				queryResults.persistPodcasts();
        // get episodes for each of those podcasts
				queryResults.getEpisodes();
				// queryResults.persistEpisodes();
      } catch (IOException e) {
				System.out.println("An error occurred while retrieving podcast and episode data for :" + queryResults.filename);
				System.out.println(e);
				e.printStackTrace();
				System.out.println("continuing...");

        continue;
      }
    }
  }

  // if we don't run this, this java package just keeps running
  private static void closeDb () {
    db.closeSession();
  }


  ///////////////////////////////////////
  // Some top level methods we could call as "Main"
  private static void runSearchesAndProcess (String[] args) {
    if (podcastSearchRequested) {
      performSearch(args);
    } 

    System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");     
    System.out.println("Now beginning to process search results");     
    setSearchResultsToProcess();

    processSearchResults();
  }

  // TODO finish adding this helper
  private static void processOneSearch () {
    String term = "big data";
    String searchType = "all";
  }

  //////////////////////////////////
  // main

  // InvalidQueryException from initializing db. Make sure to not continue messing stuff up if the db isn't ready!
  // NOTE not building the most efficient and streamlined process here. Just iteratively building out apis on important models/classes, which will be called across our app later on.
  public static void main (String[] args) throws Exception {
    processArgs(args);
    db.initialize(); 

    runSearchesAndProcess(args);

    // TODO note that this is still not letting process close
    closeDb();
    System.out.println("Finished running");     
    
    // closes the process...not sure why necessary TODO
    // https://stackoverflow.com/a/7416103/6952495
    Runtime.getRuntime().exit(0);
  }
}
