package migrations;

import helpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200513221500CreateSearchResultsTable {
  static CassandraDb db;

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      // skipping timestamps for now, because they are proving to be difficult and also Cassandra has something built-in (writetime) https://docs.datastax.com/en/cql-oss/3.3/cql/cql_using/useWritetime.html
      // UPDATE trying after all...
      // keep in mind, can always create more tables for different indexes
      // See here: https://stackoverflow.com/a/29317493/6952495
      // so can search based on filename, but still store multiple records for a single query
      db.execute("CREATE TABLE IF NOT EXISTS podcast_analysis_tool.search_results_by_filename (filename TEXT, term TEXT, search_type TEXT, result_json TEXT, created_at TIMESTAMP, updated_at TIMESTAMP, PRIMARY KEY ((filename), created_at));");

      System.out.println("ran migration CreateSearchResultsTable");

    } catch (Exception e) {
      System.out.println("unsuccessful");
      System.out.println(e);
    }
  }
}
