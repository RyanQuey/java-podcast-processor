package com.ryanquey.podcast.dataClasses.searchQuery;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.internal.core.util.concurrent.BlockingOperation;
import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures;
import com.datastax.oss.driver.internal.mapper.DaoBase;
import com.datastax.oss.driver.internal.querybuilder.update.DefaultUpdate;
import java.lang.Boolean;
import java.lang.Override;
import java.lang.String;
import java.lang.Throwable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated by the DataStax driver mapper, do not edit directly.
 */
public class SearchQueryByTermDaoImpl__MapperGenerated extends DaoBase implements SearchQueryByTermDao {
  private static final Logger LOG = LoggerFactory.getLogger(SearchQueryByTermDaoImpl__MapperGenerated.class);

  private final SearchQueryByTermRecordHelper__MapperGenerated searchQueryByTermRecordHelper;

  private final PreparedStatement findOneStatement;

  private final PreparedStatement findAllStatement;

  private final PreparedStatement createStatement;

  private final PreparedStatement saveStatement;

  private SearchQueryByTermDaoImpl__MapperGenerated(MapperContext context,
      SearchQueryByTermRecordHelper__MapperGenerated searchQueryByTermRecordHelper,
      PreparedStatement findOneStatement, PreparedStatement findAllStatement,
      PreparedStatement createStatement, PreparedStatement saveStatement) {
    super(context);
    this.searchQueryByTermRecordHelper = searchQueryByTermRecordHelper;
    this.findOneStatement = findOneStatement;
    this.findAllStatement = findAllStatement;
    this.createStatement = createStatement;
    this.saveStatement = saveStatement;
  }

  @Override
  public SearchQueryByTermRecord findOne(String term, String searchType, String externalApiype) {
    BoundStatementBuilder boundStatementBuilder = findOneStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("term", term, String.class);

    boundStatementBuilder = boundStatementBuilder.set("search_type", searchType, String.class);

    boundStatementBuilder = boundStatementBuilder.set("external_api", externalApiype, String.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToSingleEntity(boundStatement, searchQueryByTermRecordHelper);
  }

  @Override
  public PagingIterable<SearchQueryByTermRecord> findAll() {
    BoundStatementBuilder boundStatementBuilder = findAllStatement.boundStatementBuilder();

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToEntityIterable(boundStatement, searchQueryByTermRecordHelper);
  }

  @Override
  public void create(SearchQueryByTermRecord searchQuery) {
    BoundStatementBuilder boundStatementBuilder = createStatement.boundStatementBuilder();
    searchQueryByTermRecordHelper.set(searchQuery, boundStatementBuilder, NullSavingStrategy.DO_NOT_SET);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  @Override
  public void save(SearchQueryByTermRecord searchQuery) {
    BoundStatementBuilder boundStatementBuilder = saveStatement.boundStatementBuilder();
    searchQueryByTermRecordHelper.set(searchQuery, boundStatementBuilder, NullSavingStrategy.DO_NOT_SET);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  public static CompletableFuture<SearchQueryByTermDao> initAsync(MapperContext context) {
    LOG.debug("[{}] Initializing new instance for keyspace = {} and table = {}",
        context.getSession().getName(),
        context.getKeyspaceId(),
        context.getTableId());
    throwIfProtocolVersionV3(context);
    try {
      // Initialize all entity helpers
      SearchQueryByTermRecordHelper__MapperGenerated searchQueryByTermRecordHelper = new SearchQueryByTermRecordHelper__MapperGenerated(context);
      if ((Boolean)context.getCustomState().get("datastax.mapper.schemaValidationEnabled")) {
        searchQueryByTermRecordHelper.validateEntityFields();
      }
      List<CompletionStage<PreparedStatement>> prepareStages = new ArrayList<>();
      // Prepare the statement for `public abstract com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord findOne(java.lang.String, java.lang.String, java.lang.String) `:
      SimpleStatement findOneStatement_simple = searchQueryByTermRecordHelper.selectByPrimaryKeyParts(3).build();
      LOG.debug("[{}] Preparing query `{}` for method public abstract com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord findOne(java.lang.String, java.lang.String, java.lang.String) ",
          context.getSession().getName(),
          findOneStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findOneStatement = prepare(findOneStatement_simple, context);
      prepareStages.add(findOneStatement);
      // Prepare the statement for `public abstract PagingIterable<com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord> findAll() `:
      SimpleStatement findAllStatement_simple = searchQueryByTermRecordHelper.selectByPrimaryKeyParts(0).build();
      LOG.debug("[{}] Preparing query `{}` for method public abstract PagingIterable<com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord> findAll() ",
          context.getSession().getName(),
          findAllStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findAllStatement = prepare(findAllStatement_simple, context);
      prepareStages.add(findAllStatement);
      // Prepare the statement for `public abstract void create(com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord) `:
      SimpleStatement createStatement_simple = searchQueryByTermRecordHelper.insert().build();
      LOG.debug("[{}] Preparing query `{}` for method public abstract void create(com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord) ",
          context.getSession().getName(),
          createStatement_simple.getQuery());
      CompletionStage<PreparedStatement> createStatement = prepare(createStatement_simple, context);
      prepareStages.add(createStatement);
      // Prepare the statement for `public abstract void save(com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord) `:
      SimpleStatement saveStatement_simple = SimpleStatement.newInstance(((DefaultUpdate)searchQueryByTermRecordHelper.updateByPrimaryKey()).asCql());
      LOG.debug("[{}] Preparing query `{}` for method public abstract void save(com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord) ",
          context.getSession().getName(),
          saveStatement_simple.getQuery());
      CompletionStage<PreparedStatement> saveStatement = prepare(saveStatement_simple, context);
      prepareStages.add(saveStatement);
      // Initialize all method invokers
      // Build the DAO when all statements are prepared
      return CompletableFutures.allSuccessful(prepareStages)
          .thenApply(v -> (SearchQueryByTermDao) new SearchQueryByTermDaoImpl__MapperGenerated(context,
              searchQueryByTermRecordHelper,
              CompletableFutures.getCompleted(findOneStatement),
              CompletableFutures.getCompleted(findAllStatement),
              CompletableFutures.getCompleted(createStatement),
              CompletableFutures.getCompleted(saveStatement)))
          .toCompletableFuture();
    } catch (Throwable t) {
      return CompletableFutures.failedFuture(t);
    }
  }

  public static SearchQueryByTermDao init(MapperContext context) {
    BlockingOperation.checkNotDriverThread();
    return CompletableFutures.getUninterruptibly(initAsync(context));
  }
}
