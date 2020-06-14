import java.util.ArrayList;
import java.util.List;
import java.lang.System;
import java.lang.Exception;
// import java.io.IOException; 

// local imports
import cassandraHelpers.CassandraDb;

import dataClasses.PodcastSearch;
import dataClasses.searchQuery.SearchQuery;
import dataClasses.podcast.Podcast;
import dataClasses.episode.Episode;
import dataClasses.episode.EpisodeByPodcastRecord;
import dataClasses.episode.EpisodeByPodcastDao;
import kafkaHelpers.Consumers;

// import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.datastax.oss.driver.api.core.cql.Row;


public class KafkaDriver {

  /////////////////////////////////////
  // static vars


  ///////////////////////////////////////
  // private static methods
  
  private static void processArgs(String[] args) {
    System.out.println("Running with options:");     
    for (String s: args) {
      System.out.println(s);     
    }
  }
  // if we don't run this, this java package just keeps running
  private static void closeDb () {
    CassandraDb.closeSession();
  }


  private static void processOnePodcast () {
    // set these according to whichever podcast we want
    /*
    // forgot which one this was
    String language = "en";
    String primaryGenre = "Technology";
    String feedUrl = "https://datastaxdds.libsyn.com/rss";
    */

    // Making Data Simple 
    String language = "UNKNOWN";
    String primaryGenre = "Entrepreneurship";
    String feedUrl = "https://feeds.buzzsprout.com/214745.rss";

    // TODO try to set this as a static var or method on the Podcast class  
    System.out.println("finding by dao");

    try {
      System.out.println("searching");
      Podcast podcast = Podcast.findOne(language, primaryGenre, feedUrl);

      System.out.println("Found podcast:");
      System.out.println(podcast.getName());

      System.out.println("extracting episodes");
      podcast.extractEpisodes();
      podcast.persistEpisodes();

    } catch (Exception e) {
		  e.printStackTrace();
    }

  }

  ////
  // kafka consumer initializers
  //
  //////////////////////////////////
  // main

  // InvalidQueryException from initializing db. Make sure to not continue messing stuff up if the db isn't ready!
  // NOTE not building the most efficient and streamlined process here. Just iteratively building out apis on important models/classes, which will be called across our app later on.
  public static void main (String[] args) throws Exception {
    try {
      processArgs(args);
      CassandraDb.initialize(); 

      System.out.println("*************************");
      System.out.println("starting kafka consumers:");
      Consumers.initializeAll();
      // TODO only call this when settings via cmd line args are sent in
      //processOnePodcast();


      // TODO note that this is still not letting process close
      closeDb();
      System.out.println("Finished running");     
      
      // closes the process...not sure why necessary TODO
      // https://stackoverflow.com/a/7416103/6952495

    } catch (Exception e) {
      System.out.println("Error in Main:");
		  e.printStackTrace();
		  throw e;

    } finally {
      // NOTE this will look like it build successfully even if we errored out. 
      // TODO only do this if we did not catch and throw the error.
      // then find out what error code to use (ie, not 0) for errors and throw that for errors
      Runtime.getRuntime().exit(0);
    }
  }
}
