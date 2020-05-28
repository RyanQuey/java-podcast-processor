package migrations;

import cassandraHelpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200524201500CreatePodcastsTable {

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      // Our queries: 
      // - want to find across apis, so don't do by genres (which are probably api specific)
      // - want to search by language, primary_genre. 
      //      But language is difficult since could be en or English presumably...
      // - feed_url added on there to make sure it is unique
      // - make sure if another is found from different api, 
      // - Default ordering should be fine for this.
      String query = "CREATE TABLE IF NOT EXISTS podcast_analysis_tool.podcasts_by_language (" + 
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
          "feed_url TEXT, " +
          "genres LIST<TEXT>, " +
          "api_genre_ids LIST<TEXT>," +
          "primary_genre TEXT," +
          "release_date TIMESTAMP," +
          "explicit BOOLEAN," +
          "episode_count INT," +
          "rss_feed TEXT," +
          "found_by_queries List<frozen<Map<Text, Text>>>, " +
          "description TEXT," +
          "summary TEXT," +
          "subtitle TEXT," +
          "webmaster TEXT," +
          "owner_email TEXT," +
          "author TEXT," +
          "language TEXT," +
          "website_url TEXT," +
          "updated_at TIMESTAMP, " +
          "PRIMARY KEY ((language), primary_genre, feed_url)) " +
          "WITH CLUSTERING ORDER BY(primary_genre ASC, feed_url ASC);";

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
