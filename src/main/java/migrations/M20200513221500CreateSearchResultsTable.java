package migrations;

import cassandraHelpers.CassandraDb;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;

public class M20200513221500CreateSearchResultsTable {

  public static void run () throws InvalidQueryException {
    // because using the try block, automatically closes session on finish
    try {
      // See here: https://stackoverflow.com/a/29317493/6952495
      // so can search based on term/search type, but still store multiple records for a single query
      // Our queries: 
      // - want to find results based on term and search_type
      // - want all of same term to be grouped together, so can compare and see what is best search_type
      // - all of same Term and search type and api and created time should be the same record, since it should be the same results
      // - For now and not really worrying about managing multiple APIs. If we do, perhaps create new table sorted by api
      // - Default ordering should be fine for this.
      CassandraDb.execute("CREATE TABLE IF NOT EXISTS podcast_analysis_tool.search_results_by_term (term TEXT, search_type TEXT, result_json TEXT, external_api TEXT, updated_at TIMESTAMP, PRIMARY KEY ((term), search_type, external_api));");

      System.out.println("ran migration CreateSearchResultsTable");

    } catch (Exception e) {
      System.out.println("unsuccessful");
      System.out.println(e);
    }
  }
}
