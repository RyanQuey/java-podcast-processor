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

public class Main {

  /////////////////////////////////////
  // static vars

  static CassandraDb db;

  // if true, get new search results as well.
  static boolean podcastSearchRequested = false;

  static String toProcess = "none";
  
  static PodcastSearch podcastSearch = new PodcastSearch();

  // which files to process
  static ArrayList<QueryResults> searchResultsToProcess = new ArrayList<QueryResults>();

  // podcasts that have already been processed (or at least started to be processed, whether successful or not)
  // TODO maybe later, will know if we want this to be files or strings. Strings are less memory presumably
  static ArrayList<String> podcastIdsProcessed = new ArrayList<String>();
  // keyed by podcast id
  static HashMap<String, Podcast> podcastsProcessed = new HashMap<String, Podcast>(); 

  static ArrayList<Episode> episodesFound = new ArrayList<Episode>();


  ///////////////////////////////////////
  // private static methods
  
  private static void processArgs(String[] args) {
    System.out.println("Running with options:");     
    for (String s: args) {
      System.out.println(s);     

      if (s.equals("--perform-search=true")) {
        podcastSearchRequested = true;
      };

      if (s.equals("--process=new")) {
        // if true, only process the search results we just received (not all)
        toProcess = "new-search";

      } else if (s.equals("--process=default-query")) {
        // if true, process a default query (mostly for testing)
        toProcess = "default-query";
      } else if (s.equals("--process=none")) {
        toProcess = "none";
      };
    };
  }

  private static void performSearch(String[] args) {
    // initialize this when we initialize the podcastSearch itself
    podcastSearch.performAllQueries(args);
  }

  private static void setSearchResultsToProcess() {
    if (toProcess.equals("default-query")) {
      // process only the specified default file
      try {
        String defaultTerm = "artist";
        File file = new File(FileHelpers.getFilePath("podcast-data/" + defaultTerm + "_big-data.json"));

        // beware, might be more than one in actuality, if user passed in --process-new-search too on accident. 

        searchResultsToProcess.add(new QueryResults(file));
      } catch (IOException e) { 
        System.out.println("Failed to process default query");
        System.out.println(e);
      }

    } else if (toProcess.equals("new-search")) {
      System.out.println("only processing new search results");
      searchResultsToProcess.addAll(podcastSearch.results);
      // note: if didn't run search, won't do anything

    } else if (toProcess.equals("none")) {
      System.out.println("processing none");

    } else {
      // process all files, not looking at podcasts
      // TODO maybe better to just iterate over the podcasts, some of which might already have the data (?)
      try {
        List<File> files = Arrays.asList(new File(FileHelpers.getFilePath("podcast-data")).listFiles());
        for (File file : files) {
          searchResultsToProcess.add(new QueryResults(file));
        }

      } catch (IOException e) { 
        System.out.println("Failed to process default query");
        System.out.println(e);
      }

    }
  }

  private static void getEpisodes() {
    System.out.println("now processing files (count): " + searchResultsToProcess.size());

    // iterate over search result files
    for (QueryResults queryResults : searchResultsToProcess) {
      String filename = queryResults.filename;

      try {
				queryResults.getPodcasts();
				queryResults.getEpisodes();
      } catch (IOException e) {
				System.out.println("An error occurred while retrieving podcast and episode data for :" + queryResults.filename);
				System.out.println(e);
				e.printStackTrace();
				System.out.println("continuing...");

        continue;
      }
    }
  }

  private static void initializeDb () throws InvalidQueryException {
    db.initialize(); 
  }

  // if we don't run this, this java package just keeps running
  private static void closeDb () {
    db.closeSession();
  }

  //////////////////////////////////
  // main

  // InvalidQueryException from initializing db. Make sure to not continue messing stuff up if the db isn't ready!
  public static void main (String[] args) throws InvalidQueryException {
    processArgs(args);
    initializeDb();

    if (podcastSearchRequested) {
      performSearch(args);
    } 

    setSearchResultsToProcess();

    getEpisodes();

    // TODO note that this is still not letting process close
    closeDb();
    System.out.println("Finished running");     
  }
}
