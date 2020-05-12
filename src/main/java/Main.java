import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
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
  // instance vars

  // if true, get new search results as well.
  boolean podcastSearchRequested = false;

  // if true, only process the search results we just received (not all)
  boolean processNewSearches = false;
  
  // if true, process a default query (mostly for testing)
  boolean processDefaultQuery = false;

  PodcastSearch podcastSearch = new PodcastSearch();

  // which files to process
  ArrayList<File> searchResultsToProcess = new ArrayList<File>();

  // podcasts that have already been processed (or at least started to be processed, whether successful or not)
  // TODO maybe later, will know if we want this to be files or strings. Strings are less memory presumably
  ArrayList<Podcast> podcastIdsProcessed = new ArrayList<String>();
  // keyed by podcast id
  Map<String, Podcast> podcastsProcessed = new Map<String, Podcast>(); 

  ArrayList<Episode> episodesFound = new ArrayList<Episode>();

  ///////////////////////////////////////
  // private static methods
  
  private static void processArgs(String[] args) {
    System.out.println("Running with options:");     
    for (String s: args) {
      System.out.println(s);     

      if (s.equals("--perform-search")) {
        podcastSearchRequested = true;
      } else {
        System.out.println("skipping search this time");     
      };

      if (s.equals("--process-new-searches")) {
        processNewSearches = true;
      };

      if (s.equals("--process-default-query")) {
        processDefaultQuery = true;
      };
    };
  }

  private static void performSearch() {
    podcastSearch.performAllQueries(args);

    if (processNewSearches) {
      searchResultsToProcess.addAll(podcastSearch.resultPaths);
    }
  }

  private static void setSearchResultsToProcess() {
    // process all files in folder
    if (processDefaultQuery) {
      File file = new File(FileHelpers.getFilePath("podcast-data/artist_big-data.json"));

      // beware, will be more than one, in case user passed in --process-new-searches too on accident. 
      searchResultsToProcess.add(file);

    } else if (processNewSearches) {
      System.out.println("only processing new search results");
      // note: if didn't run search, won't do anything

    } else {
      // process all files
      File[] files = new File(FileHelpers.getFilePath("")).listFiles();

      searchResultsToProcess.addAll(files);
    }
  }

  private static void getEpisodes() {
    System.out.println("now processing files:");     
    System.out.println(searchResultsToProcess);     
    for (File searchResultFile : searchResultsToProcess) {
      String filename = searchResultFile.getName();

      // get the episodes for a given search result
      QueryResults queryResults = new QueryResults(searchResultFile);
      queryResults.getPodcasts();
      queryResults.getEpisodes();
    }
  }

  //////////////////////////////////
  // main

  public static void main(String[] args){
    processArgs(args);

    if (podcastSearchRequested) {
      performSearch();
    } 

    setSearchResultsToProcess();

    getEpisodes();

    System.out.println("Finished running");     
  }
}


