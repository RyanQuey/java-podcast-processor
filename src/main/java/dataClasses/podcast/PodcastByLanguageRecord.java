package dataClasses.podcast;

// import com.datastax.oss.driver.api.core.cql.ResultSet;
// import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.term.Term;

// DSE Mapper
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.core.cql.BoundStatement;


import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

import cassandraHelpers.CassandraDb;
import dataClasses.searchQuery.SearchQueryUDT;
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

  /////////////////////////////////
  // helpers for interacting with DB

  public PodcastByLanguageRecord findOne () throws Exception {
    if (this.language == null || this.primaryGenre == null || this.feedUrl == null) {
      throw new IllegalArgumentException("all Primary keys clustering columns are required to do a search");
    }
    PodcastByLanguageRecord data = PodcastByLanguageRecord.getDao().findOne(this.language, this.primaryGenre, this.feedUrl);
    // refresh current record's fields
    DataClassesHelpers.copyMatchingFields(data, this);

    return this;
  }

  // saves record, not appending any to collection data types.
  // We will only use for this class when we've already retrieved the found_by_queries and set them on the Podcast instance
  public void saveNoAppend () throws Exception {
    PodcastByLanguageRecord.getDao().save(this);
  }

  // This is messy and less than ideal. 
  // Most ideal would be if they allowed a quick option on the DAO from the mapper class; this is not YET in the java driver https://community.datastax.com/questions/4980/how-can-i-append-or-prepend-to-a-collection-using.html?childToView=5016#answer-5016
  // Next ideal is probably what they recommend in the above link: using query provider class. But this seems complicated and I'm just trying to learn basics here still.
  // Next ideal is probably using query builder and making a custom codec. 

  // attempt using bound statement from the DAO:
  // this one returns a bound statement with nothing bound!
  /*
  public void save () throws Exception {
    // this should be a fully executable CQL statement, but overwrites the found_by_queries list. We want to append
    BoundStatement boundSaveStatement = PodcastByLanguageRecord.getDao().buildSaveQuery(this);
    // remove what was there
    String oldQuery = boundSaveStatement.getPreparedStatement().getQuery();
    System.out.println("original:");
    System.out.println(oldQuery);
    boundSaveStatement.unset("found_by_queries");
    // set what we want
    String query = boundSaveStatement.getPreparedStatement().getQuery();
    System.out.println("about to run this from the bound safe statement");
    System.out.println(query);
    CassandraDb.execute(query);
  }
  */

  // currently doing all this since I can't figure out how to append using the default DAO stuff wihtout using @Query... And this is easier than writing out @Query
  public void saveAndAppendFoundBy () throws Exception {
    System.out.println("0000000000000000000000000");
    System.out.println("about to persist podcast");
    Term ts = CassandraDb.getTimestamp();

    // currently assuming there'll only be one on a given Podcast
    // TODO allow if multiple
    SearchQueryUDT foundBy = this.getFoundByQueries().iterator().next();
    String foundByStr = foundBy.toCQLString();

    // want to create or update if exists
    String query = update("podcasts_by_language")
      .setColumn("owner", literal(this.getOwner()))
      .setColumn("primary_genre", literal(this.getPrimaryGenre()))
      .setColumn("name", literal(this.getName()))
      .setColumn("image_url30", literal(this.getImageUrl30()))
      .setColumn("image_url60", literal(this.getImageUrl60()))
      .setColumn("image_url100", literal(this.getImageUrl100()))
      .setColumn("image_url600", literal(this.getImageUrl600()))
      .setColumn("api", literal(this.getApi()))
      .setColumn("api_id", literal(this.getApiId()))
      .setColumn("api_url", literal(this.getApiUrl()))
      .setColumn("country", literal(this.getCountry()))
      //.setColumn("feed_url", literal(this.getGetFeedUrl()())) // don't set because updating, so can't set any in primary key
      .setColumn("genres", literal(this.getGenres())) // hoping ArrayList converts to List here;
      .setColumn("api_genre_ids", literal(this.getApiGenreIds()))
      //.setColumn("primary_genre", literal(this.getPrimaryGenre())) // can't update primary key
      .setColumn("release_date", literal(this.getReleaseDate()))
      .setColumn("explicit", literal(this.isExplicit()))
      .setColumn("episode_count", literal(this.getEpisodeCount()))
      //.setColumn("rss_feed", literal(this.getRssFeedStr())) // don't save this for now, is really large and since I'm printing query, hard to debug
      // one curly brace set for appending to a SET type, one for the UDT itself
      .append("found_by_queries", raw("{ " + foundByStr + " }"))
      .setColumn("description", literal(this.getDescription()))
      .setColumn("summary", literal(this.getSummary()))
      .setColumn("subtitle", literal(this.getSubtitle()))
      .setColumn("webmaster", literal(this.getWebmaster()))
      .setColumn("owner_email", literal(this.getOwnerEmail()))
      .setColumn("author", literal(this.getAuthor()))
      //.setColumn("language", literal(this.getLanguage()))
      .setColumn("website_url", literal(this.getWebsiteUrl()))
      .setColumn("updated_at", ts)
      // only update this unique record, so set by compound primary key
      .whereColumn("language").isEqualTo(literal(this.getLanguage()))
      .whereColumn("feed_url").isEqualTo(literal(this.getFeedUrl()))
      .asCql();

    System.out.println("now executing:");
    System.out.println(query);

    CassandraDb.execute(query);
  }

};



