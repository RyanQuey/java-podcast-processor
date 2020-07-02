package com.ryanquey.podcast.dataClasses.searchQuery;

// import com.datastax.oss.driver.api.core.cql.ResultSet;
// import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
// DSE Mapper
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
// import com.datastax.oss.driver.api.querybuilder.term.Term;

import java.util.UUID;

import com.ryanquey.podcast.cassandraHelpers.CassandraDb;
import com.ryanquey.podcast.helpers.DataClassesHelpers;

import java.lang.IllegalArgumentException;
/* 
 * For one file, gets all search results and retrieves the rss feed data
 *
 */

@Entity
@CqlName("search_queries_by_term")
public class SearchQueryByTermRecord extends SearchQueryBase {
  @PartitionKey 
  private String term;
  @ClusteringColumn(0)
  private String searchType;
  @ClusteringColumn(1) 
  private String externalApi;

  /////////////////////////////////////////////////
  // Static methods
  // NOTE never set this as a static var, in case it is set incorrectly. (5/29/20)
  // Does not seem to throw a helpful error if a var, but if a static method, will give helpful feedback
  static public SearchQueryByTermDao getDao () {
    return CassandraDb.inventoryMapper.searchQueryByTermDao("search_queries_by_term");
  }

  static public SearchQueryByTermRecord findOne (SearchQuery searchQuery) throws Exception {
    return SearchQueryByTermRecord.getDao().findOne(searchQuery.term, searchQuery.searchType, searchQuery.externalApi); 
  }

  /////////////////////////////////////////////////
  // constructors

  // for DSE DAO
  public SearchQueryByTermRecord () {};

  // to get the record instance based on our app's SearchQuery instance
  public SearchQueryByTermRecord (SearchQuery searchQuery) {
    DataClassesHelpers.copyMatchingFields(searchQuery, this);
  };


  /////////////////////////////////
  // helpers for interacting with DB

  // Finds the db row corresponding to this search query, if it exists
  // helper to avoid having to always build a dao and input the terms and stuff
  // only works if partition key and clustering columns are set on this record already
  public SearchQueryByTermRecord findOne () throws Exception {
    if (this.term == null || this.searchType == null || this.externalApi == null) {
      throw new IllegalArgumentException("all Primary keys clustering columns are required to do a search");
    }
    SearchQueryByTermRecord data = SearchQueryByTermRecord.getDao().findOne(this.term, this.searchType, this.externalApi);
    // refresh current record's fields
    DataClassesHelpers.copyMatchingFields(data, this);

    return this;
  }

  public void save () {
    SearchQueryByTermRecord.getDao().save(this);
  }
};



