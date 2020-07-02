package com.ryanquey.podcast.migrations;

import com.ryanquey.podcast.cassandraHelpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200520161500CreateSearchQueriesUDT {

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      // See here: https://stackoverflow.com/a/29317493/6952495
      // Mostly just want some basic identifying information for this search query that we can persist on other tables such as the podcast table
      //
      // Also want to have updated_at so can see when the search was ran, 
      // in case the record it points to is more up to date than the associated record or something
      // I can see a use case for that
      CassandraDb.execute("CREATE TYPE IF NOT EXISTS podcast_analysis_tool.search_query (external_api TEXT, search_type TEXT, term TEXT);");

      System.out.println("ran migration CreateSearchQueriesUDT");

    } catch (Exception e) {
      System.out.println("migration unsuccessful");
      System.out.println(e);
      // TODO these should throw errors, should always work. If not just stop the program
    }
  }
}
