package com.ryanquey.podcast.dataClasses.podcast;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.core.cql.BoundStatement;

import java.util.UUID;

import com.ryanquey.podcast.dataClasses.podcast.PodcastByLanguageRecord;

@Dao
// NOTE this means that in order to erase a field, cannot set it to null
@DefaultNullSavingStrategy(NullSavingStrategy.DO_NOT_SET)
public interface PodcastByLanguageDao {

  /** Simple selection by full primary key. */
  // TODO add something like 
  @Select
  PodcastByLanguageRecord findOne(String language, String primaryGenre, String feedUrl);

  /*
  @Select(customWhereClause = "language in ('en', 'en-US', 'UNKNOWN') AND primary_genre = :primaryGenre AND feed_url = :feedUrl") 
  Podcast findByGenreAndUrl(String language, String primaryGenre, String feedUrl);
  */



  /**
   * Selection by partial primary key, this will return multiple rows.
   *
   * <p>Also, note that this queries a different table: DAOs are not limited to a single entity, the
   * return type of the method dictates what rows will be mapped to.
   */
	/*
  @Select
  PagingIterable<PodcastByLanguage> getByLanguage(String language);
  @Select
  PagingIterable<PodcastByPrimaryGenre> getByLanguage(String language, String primaryGenre);
	*/

  /**
   * Creating a video is a bit more complex: because of denormalization, it involves multiple
   * tables.
   *
   * <p>A query provider is a nice way to wrap all the queries in a single operation, and hide the
   * details from the DAO interface.
   */
  // not doing for now, only doing one table
  /*
  @QueryProvider(
      providerClass = CreatePodcastQueryProvider.class,
      entityHelpers = {Podcast.class, UserPodcast.class, LatestPodcast.class, PodcastByTag.class})
  void create(Podcast video);
  */
  @Insert
  void create(PodcastByLanguageRecord podcast);

  /**
   * Update using a template: the template must have its full primary key set; beyond that, any
   * non-null field will be considered as a value to SET on the target row.
   *
   * <p>Note that we specify the null saving strategy for emphasis, but this is the default.
   *
   * For more on how to do customWhereClause and other options, see here: https://github.com/datastax/java-driver/tree/4.x/manual/mapper/daos/update#parameters
   *
   */

  // this one just writes. Normally a good default, but for our use case often we'll want to append the found_by_queries
  @Update
  void save(PodcastByLanguageRecord podcast);
}
