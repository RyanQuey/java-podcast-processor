package com.ryanquey.podcast.kafkaMains;

import java.util.ArrayList;
import java.util.List;
import java.lang.System;
import java.lang.Exception;
import java.util.concurrent.TimeUnit;
// import java.io.IOException; 

// local imports
import com.ryanquey.podcast.cassandraHelpers.CassandraDb;

import com.ryanquey.podcast.dataClasses.PodcastSearch;
import com.ryanquey.podcast.dataClasses.searchQuery.SearchQuery;
import com.ryanquey.podcast.dataClasses.podcast.Podcast;
import com.ryanquey.podcast.dataClasses.episode.Episode;
import com.ryanquey.podcast.dataClasses.episode.EpisodeByPodcastRecord;
import com.ryanquey.podcast.dataClasses.episode.EpisodeByPodcastDao;
import com.ryanquey.podcast.kafkaHelpers.Consumers;

// import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.datastax.oss.driver.api.core.cql.Row;


public class KafkaMain {

  /////////////////////////////////////
  // static fields
  // overwrite this in subclasses
  public static void startConsumer () throws Exception {
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
  private static void closeDb () throws Exception {
    CassandraDb.closeSession();
  }

  // everything to run before the child's specific stuff
  public static void setup () throws Exception {
    // given that we don't know if the external Cassandra db is up yet ( especially if it's within another docker container), allow some retries
    Integer limit = 25;
    boolean ready = false;
    Exception error;

    for(int i = 0; i < limit; ++i) {
      try {
        CassandraDb.initialize(); 
        break;

      } catch (Exception e) {
        // TODO more specific error handling. C* will throw a java.nio.channels.ClosedChannelException I think. 
        // I don't know about Kafka

        error = e;
        System.out.println(e);

        System.out.println("Failed to connect to Kafka/C* " + i + 1 + "times. Trying again...");
        TimeUnit.SECONDS.sleep(15);

        if (i + 1 == limit) {
          throw error;
        }
      }
    }
  }

  // everything to run after the child's specific stuff
  public static void tearDown () throws Exception {
    // TODO note that this is still not letting process close
    // closes the db process...not sure why necessary TODO
    // https://stackoverflow.com/a/7416103/6952495
    closeDb();

    System.out.println("Finished running");     
  }

  // make sure this is overridden in the child classes
  // This can be used as a main class though as well, as is
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
