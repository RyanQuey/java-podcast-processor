package com.ryanquey.podcast.migrations;

import com.ryanquey.podcast.cassandraHelpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200524201500CreatePodcastsTable {

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      // Our queries: 
      // - want to find across apis, so don't do by genres (which are probably api specific)
      // - want to search by language, primary_genre. 
      //      But language is difficult since could be en or English presumably...
      //      primary_genre should be searchable, but so should other genres. Just make that indexed separately for now
      // - feed_url added on there to make sure it is unique
      // - make sure if another is found from different api, 
      // - Default ordering should be fine for this.
      String query = "CREATE TABLE IF NOT EXISTS podcast_analysis_tool.podcasts_by_language (" + 
          "language TEXT," +
          "feed_url TEXT, " +
          "owner TEXT, " + 
          "name TEXT, " +
          "image_url30 TEXT," +
          "image_url60 TEXT," + 
          "image_url100 TEXT," +
          "image_url600 TEXT, " +
          "api TEXT, " +
          "api_id TEXT, " +
          "api_url TEXT, " +
          "country TEXT," +
          "genres LIST<TEXT>, " +
          "api_genre_ids LIST<TEXT>," +
          "primary_genre TEXT," +
          "release_date TIMESTAMP," +
          "explicit BOOLEAN," +
          "episode_count INT," +
          //don't persist this, can be MBs in itself
          //"rss_feed TEXT," +
          // want a set, so doesn't store non-unique values
          "found_by_queries SET<frozen<search_query>>, " +
          "description TEXT," +
          "summary TEXT," +
          "subtitle TEXT," +
          "webmaster TEXT," +
          "owner_email TEXT," +
          "author TEXT," +
          "website_url TEXT," +
          "updated_at TIMESTAMP, " +
          "PRIMARY KEY ((language), feed_url)) " +
          "WITH CLUSTERING ORDER BY(feed_url ASC);";

      System.out.println(query);
      CassandraDb.execute(query);

      // TODO add everything from model that we'll get from rss

      System.out.println("ran migration CreatePodcastsTable");

    } catch (InvalidQueryException e) {
      System.out.println("unsuccessful");
      System.out.println(e);
      throw e;
    }
  }
}
