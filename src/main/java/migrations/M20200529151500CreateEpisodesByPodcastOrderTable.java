package migrations;

import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

import cassandraHelpers.CassandraDb;
// import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200529151500CreateEpisodesByPodcastOrderTable {
  public static void run () throws Exception {
    // because using the try block, automatically closes session on finish
    try {
      // Our queries: 
      // - basically, we want to attach the episodes to the podcast.
      // - So, the queries for now are by podcast, and then can search episodes from there
      // - later, will create another table for searching episodes directly, in order to search keywords and summaries

      String episodeTable = "CREATE TABLE IF NOT EXISTS podcast_analysis_tool.episodes_by_order_in_podcast (" + 
          "podcast_api TEXT, " + 
          "podcast_api_id TEXT, " + 
          "order_num INT," +

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

      System.out.println(episodeTable);
      CassandraDb.execute(episodeTable);

      System.out.println("ran migration M20200529151500CreateEpisodesByPodcastOrderTable");

    } catch (InvalidQueryException e) {
      System.out.println("unsuccessful");
      System.out.println(e);
      // probably to drop all created ones, since they're not all ran at once, just in case want to change. Makes this almost like a transaction
      // HOWEVER don't want to drop tables like this in production. So leaving this out for now, just fix manually
      /*
      CassandraDb.execute("DROP TYPE podcast_analysis_tool.podcast;");
      CassandraDb.execute("DROP TYPE podcast_analysis_tool.episode;");
      CassandraDb.execute("DROP TABLE podcast_analysis_tool.episodes_by_order_in_podcast;");
      */

      throw e;
    }
  }
}
