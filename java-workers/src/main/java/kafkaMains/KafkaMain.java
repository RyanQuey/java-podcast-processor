package kafkaMains;

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


public class KafkaMain {

  /////////////////////////////////////
  // static fields
  // overwrite this in subclasses
  private static void startConsumer () throws Exception {
    System.out.println("*************************");
    System.out.println("starting kafka consumers:");
    Consumers.initializeAll();
  }


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

  // everything to run before the child's specific stuff
  public static void setup () throws Exception {
    // processArgs(args);

    CassandraDb.initialize(); 
  }

  // everything to run after the child's specific stuff
  public static void tearDown () {
    // TODO note that this is still not letting process close
    // closes the db process...not sure why necessary TODO
    // https://stackoverflow.com/a/7416103/6952495
    closeDb();

    System.out.println("Finished running");     
  }

  // make sure this is overridden in the child classes
  public static void main (String[] args) throws Exception {
    try {
      // TODO only call this when settings via cmd line args are sent in
      setup();
      startConsumer();
      tearDown();

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
