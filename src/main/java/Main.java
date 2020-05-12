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

import dataClasses.QueryResults;
import dataClasses.PodcastSearch;
import dataClasses.Podcast;
import dataClasses.Episode;

public class Main {

  /////////////////////////////////////
  // static vars

  // if true, get new search results as well.
  static boolean podcastSearchRequested = false;

  // if true, only process the search results we just received (not all)
  static boolean processNewSearches = false;
  
  // if true, process a default query (mostly for testing)
  static boolean processDefaultQuery = false;

  static PodcastSearch podcastSearch = new PodcastSearch();

  // which files to process
  static ArrayList<File> searchResultsToProcess = new ArrayList<File>();

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

      if (s.equals("--perform-search")) {
        podcastSearchRequested = true;
      };

      if (s.equals("--process=new")) {
        processNewSearches = true;
      };

      if (s.equals("--process=default-query")) {
        processDefaultQuery = true;
      };
    };
  }

  private static void performSearch(String[] args) {
    podcastSearch.performAllQueries(args);

    if (processNewSearches) {
      searchResultsToProcess.addAll(podcastSearch.resultFiles);
    }
  }

  private static void setSearchResultsToProcess() {
    // process all files in folder
    if (processDefaultQuery) {
      try {
        File file = new File(FileHelpers.getFilePath("podcast-data/artist_big-data.json"));

        // beware, will be more than one, in case user passed in --process-new-searches too on accident. 
        searchResultsToProcess.add(file);
      } catch (IOException e) { 
        System.out.println("Failed to process default query");
        System.out.println(e);
      }

    } else if (processNewSearches) {
      System.out.println("only processing new search results");
      // note: if didn't run search, won't do anything

    } else {
      try {
        // process all files
        List<File> files = Arrays.asList(new File(FileHelpers.getFilePath("podcast-data")).listFiles());

        searchResultsToProcess.addAll(files);
      } catch (IOException e) { 
        System.out.println("Failed to process default query");
        System.out.println(e);
      }

    }
  }

  private static void getEpisodes() {
    System.out.println("now processing files (count): " + searchResultsToProcess.size());
    for (File searchResultFile : searchResultsToProcess) {
      String filename = searchResultFile.getName();

      // get the episodes for a given search result
      QueryResults queryResults; 
      try {
        queryResults = new QueryResults(searchResultFile);
      } catch (FileNotFoundException e) {
        System.out.println("skipping missing file: " + filename);
        continue;
      }

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

  //////////////////////////////////
  // main

  public static void main(String[] args){
    processArgs(args);

    if (podcastSearchRequested) {
      performSearch(args);
    } 

    setSearchResultsToProcess();

    getEpisodes();

    System.out.println("Finished running");     
  }
}


