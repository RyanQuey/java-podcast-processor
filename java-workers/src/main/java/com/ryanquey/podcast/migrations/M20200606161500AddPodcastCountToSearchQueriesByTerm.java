package com.ryanquey.podcast.migrations;

import com.ryanquey.podcast.cassandraHelpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200606161500AddPodcastCountToSearchQueriesByTerm {

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      CassandraDb.execute("ALTER TABLE  podcast_analysis_tool.search_queries_by_term ADD (podcast_count INT);");

      System.out.println("ran migration: add podcast count to search queries by term");

    } catch (Exception e) {
      System.out.println("migration AddPodcastCountToSearchQueriesByTerm unsuccessful");
      System.out.println(e);
      if (e.toString().contains("Invalid column name podcast_count because it conflicts with an existing column")) {
        System.out.println("    And that is ok");
      }
      // this will throw an error every time after the first run, so no worries
    }
  }
}
