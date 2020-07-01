package dataClasses.searchQuery;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.api.mapper.annotations.DefaultNullSavingStrategy;
import com.datastax.oss.driver.api.core.PagingIterable;

@Dao
// NOTE this means that in order to erase a field, cannot set it to null
@DefaultNullSavingStrategy(NullSavingStrategy.DO_NOT_SET)
public interface SearchQueryByTermDao {

  /** Simple selection by full primary key. */
  // java driver docs: "If the query returns no rows, the method will return null. If it returns more than one row, subsequent rows will be discarded"
  // Can get different behavior if we set a different return type
  @Select
  SearchQueryByTermRecord findOne(String term, String searchType, String externalApiype);

  @Select
  PagingIterable<SearchQueryByTermRecord> findAll();

  /**
   * Selection by partial primary key, this will return multiple rows.
   *
   * <p>Also, note that this queries a different table: DAOs are not limited to a single entity, the
   * return type of the method dictates what rows will be mapped to.
   */

  /**
   * Creating a video is a bit more complex: because of denormalization, it involves multiple
   * tables.
   *
   * <p>A query provider is a nice way to wrap all the queries in a single operation, and hide the
   * details from the DAO interface.
   */
  // not doing for now, only doing one table
  @Insert
  void create(SearchQueryByTermRecord searchQuery);

  /**
   * Update using a template: the template must have its full primary key set; beyond that, any
   * non-null field will be considered as a value to SET on the target row.
   *
   * <p>Note that we specify the null saving strategy for emphasis, but this is the default.
   *
   * For more on how to do customWhereClause and other options, see here: https://github.com/datastax/java-driver/tree/4.x/manual/mapper/daos/update#parameters
   * For our use case allowing three languages so can do e.g., dao.update(podcast, en, en-US, and UNKNOWN);
   *
   * perhaps want to set uniqueness by api and api_id instead, since those are probably more certain. 
   * might just end up making those part of the primary key in the end.
   */
  @Update
  void save(SearchQueryByTermRecord searchQuery);
}
