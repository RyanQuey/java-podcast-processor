package com.ryanquey.podcast.migrations;

import com.ryanquey.podcast.cassandraHelpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200513211500CreateKeyspace {
  static CassandraDb db;

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      db.execute("CREATE KEYSPACE IF NOT EXISTS podcast_analysis_tool WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 } AND DURABLE_WRITES =  false ;");

      System.out.println("ran migration CreateKeyspace");

    } catch (Exception e) {
      System.out.println("unsuccessful");
      System.out.println(e);
      // TODO these should throw errors, should always work. If not just stop the program
    }
  }
}
