package migrations;

import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

import helpers.CassandraDb;
// import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200527151500CreateEpisodesAndAddEpisodesToPodcastsTable {
  public static void run () throws Exception {
    // because using the try block, automatically closes session on finish
    try {
      // Our queries: 
      // - basically, we want to attach the episodes to the podcast.
      // - So, the queries for now are by podcast, and then can search episodes from there
      // - later, will create another table for searching episodes directly, in order to search keywords and summaries

      // NOTE separate queries into separate strings: I'm not sure if they support multiple on one, but even if they do, debugging is easier if it's separate
      String podcastType = "CREATE TYPE IF NOT EXISTS podcast_analysis_tool.podcast (" + 
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
          "genres <LIST<TEXT>>, " + // all collections in a type must be frozen
          "api_genre_ids <LIST<TEXT>>," +
          "primary_genre TEXT," +
          "release_date TIMESTAMP," +
          "explicit BOOLEAN," +
          "episode_count INT," +
          "rss_feed TEXT," +
          "found_by_queries <List<frozen<Map<Text, Text>>>>, " +
          "description TEXT," +
          "summary TEXT," +
          "description_subtitle TEXT," +
          "webmaster TEXT," +
          "owner_email TEXT," +
          "author TEXT," +
          "language TEXT," +
          "website_url TEXT," +
          "updated_at TIMESTAMP, " +
          //"episodes LIST<frozen<episode>> " + // can't do this yet...and don't need it. Can add later (I think...not sure if you can add these to types). Might want if nest podcasts under search results and want to just grab everything for a search result all at once
          ");";

      String episodeType = "CREATE TYPE IF NOT EXISTS podcast_analysis_tool.episode (" +
          "podcast_api TEXT, " + 
          "podcast_api_id TEXT, " + 
          "order_num INT," +

          "podcast podcast, " + 
          "podcast_website_url TEXT, " + 

          "summary TEXT," +
          "duration DURATION," +
          "subtitle TEXT," +
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
					");";


      // by default, does not make any changes if column already exists
			String addEpisodesToPodcastTable = "ALTER TABLE podcast_analysis_tool.podcasts_by_language " + 
          "ADD episodes LIST<frozen<episode>>;";

      String episodeTable = "CREATE TABLE IF NOT EXISTS podcast_analysis_tool.episodes_by_order_in_podcast (" + 
          "podcast_api TEXT, " + 
          "podcast_api_id TEXT, " + 
          "order_num INT," +

          "podcast frozen<podcast>, " + 
          "podcast_website_url TEXT, " + 

          "summary TEXT," +
          "duration DURATION," +
          "subtitle TEXT," +
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

          "PRIMARY KEY ((podcast_api, podcast_api_id), order_num)) " +
          "WITH CLUSTERING ORDER BY(order_num ASC);";

      System.out.println(podcastType);
      CassandraDb.execute(podcastType);
      System.out.println(episodeType);
      CassandraDb.execute(episodeType);
      try {
        System.out.println(addEpisodesToPodcastTable);
        CassandraDb.execute(addEpisodesToPodcastTable);
      } catch (InvalidQueryException e) {
        // don't care if this fails due to there already being a column with same name.
        if (e.toString().contains("Column with name 'episodes' already exists")) {
          System.out.println("column episodes already exists, so skipping");
        } else {
          throw e;
        }
      }

      System.out.println(episodeTable);
      CassandraDb.execute(episodeTable);

      // TODO add everything from model that we'll get from rss

      System.out.println("ran migration CreatePodcastsTable");

    } catch (InvalidQueryException e) {
      System.out.println("unsuccessful");
      System.out.println(e);
      // probably to drop all created ones, since they're not all ran at once, just in case want to change. Makes this almost like a transaction
      // HOWEVER don't want to drop tables like this in production. So leaving this out for now, just fix manually
      /*
      CassandraDb.execute("DROP TYPE podcast_analysis_tool.podcast;");
      CassandraDb.execute("DROP TYPE podcast_analysis_tool.episode;");
      CassandraDb.execute("DROP TABLE podcast_analysis_tool.episodes_by_order_in_podcast;");

      throw e;
    }
  }
}
