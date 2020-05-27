package migrations;

import helpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200527151500CreateEpisodesAndAddEpisodesToPodcastsTable {
  static CassandraDb db;

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      // Our queries: 
      // - basically, we want to attach the episodes to the podcast.
      // - So, the queries for now are by podcast, and then can search episodes from there
      // - later, will create another table for searching episodes directly, in order to search keywords and summaries
      String query = "CREATE TYPE IF NOT EXISTS podcast_analysis_tool.episodes (" +
          "podcast podcast, " + 
          "podcast_api TEXT, " + 
          "podcast_api_id TEXT, " + 
          "podcast_website_url TEXT, " + 

          "summary TEXT," +
          "duration DURATION," +
          "subtitle TEXT," +
          "order INT," +
          "image_url TEXT," +

          "episode_type TEXT," +
          "episode_num INT," +
          "season_num INT," +

          "title TEXT, " +
          "author TEXT," +
          "keywords SET<TEXT>, " +
          "explicit BOOLEAN," +
          "updated_at TIMESTAMP, " +
          "closed_captioned BOOLEAN" +
					");" +

      "CREATE TYPE IF NOT EXISTS podcast_analysis_tool.podcast (" + 
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
          "description_subtitle TEXT," +
          "webmaster TEXT," +
          "owner_email TEXT," +
          "author TEXT," +
          "language TEXT," +
          "website_url TEXT," +
          "updated_at TIMESTAMP, " +
          "episodes LIST<episode> " +
          ");" +

      // by default, does not make any changes if column already exists
			"ALTER TABLE podcast_analysis_tool.podcasts_by_language (" + 
          "ADD episodes LIST<episode>);" + 

      "CREATE TABLE IF NOT EXISTS podcast_analysis_tool.episodes_by_order_in_podcast_ (" + 
          "podcast podcast, " + 
          "podcast_api TEXT, " + 
          "podcast_api_id TEXT, " + 
          "podcast_website_url TEXT, " + 

          "summary TEXT," +
          "duration DURATION," +
          "subtitle TEXT," +
          "order INT," +
          "image_url TEXT," +

          "episode_type TEXT," +
          "episode_num INT," +
          "season_num INT," +

          "title TEXT, " +
          "author TEXT," +
          "keywords SET<TEXT>, " +
          "explicit BOOLEAN," +
          "updated_at TIMESTAMP, " +
          "closed_captioned BOOLEAN," +

          "PRIMARY KEY ((language), primary_genre, feed_url)) " +
          "WITH CLUSTERING ORDER BY(primary_genre ASC, feed_url ASC);";

      System.out.println(query);
      db.execute(query);

      // TODO add everything from model that we'll get from rss

      System.out.println("ran migration CreatePodcastsTable");

    } catch (InvalidQueryException e) {
      System.out.println("unsuccessful");
      System.out.println(e);
      throw e;
    }
  }
}
