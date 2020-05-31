package dataClasses.podcast;

// import com.datastax.oss.driver.api.core.cql.ResultSet;
// import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
// DSE Mapper
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
// import com.datastax.oss.driver.api.querybuilder.term.Term;


import cassandraHelpers.CassandraDb;
import helpers.DataClassesHelpers;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 *
 */

@Entity
@CqlName("podcasts_by_language")
public class PodcastByLanguageRecord extends PodcastBase {
  @PartitionKey 
  private String language;
  @ClusteringColumn(0)
  private String primaryGenre;
  @ClusteringColumn(1) 
  private String feedUrl; // rss feed url

  /////////////////////////////////////////////////
  // Static methods
  // NOTE never set this as a static var, in case it is set incorrectly. (5/29/20)
  // Does not seem to throw a helpful error if a var, but if a static method, will give helpful feedback
  static public PodcastByLanguageDao getDao () {
    return CassandraDb.inventoryMapper.podcastByLanguageDao("podcasts_by_language");
  }

  /////////////////////////////////////////////////
  // constructors

  // for DSE DAO
  public PodcastByLanguageRecord() {};
  public PodcastByLanguageRecord(Podcast podcast) {
    DataClassesHelpers.copyMatchingFields(podcast, this);
  };


};



