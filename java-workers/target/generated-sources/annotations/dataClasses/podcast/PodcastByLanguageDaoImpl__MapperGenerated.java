package dataClasses.podcast;

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
public class PodcastByLanguageDaoImpl__MapperGenerated extends DaoBase implements PodcastByLanguageDao {
  private static final Logger LOG = LoggerFactory.getLogger(PodcastByLanguageDaoImpl__MapperGenerated.class);

  private final PodcastByLanguageRecordHelper__MapperGenerated podcastByLanguageRecordHelper;

  private final PreparedStatement findOneStatement;

  private final PreparedStatement createStatement;

  private final PreparedStatement saveStatement;

  private PodcastByLanguageDaoImpl__MapperGenerated(MapperContext context,
      PodcastByLanguageRecordHelper__MapperGenerated podcastByLanguageRecordHelper,
      PreparedStatement findOneStatement, PreparedStatement createStatement,
      PreparedStatement saveStatement) {
    super(context);
    this.podcastByLanguageRecordHelper = podcastByLanguageRecordHelper;
    this.findOneStatement = findOneStatement;
    this.createStatement = createStatement;
    this.saveStatement = saveStatement;
  }

  @Override
  public PodcastByLanguageRecord findOne(String language, String primaryGenre, String feedUrl) {
    BoundStatementBuilder boundStatementBuilder = findOneStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("language", language, String.class);

    boundStatementBuilder = boundStatementBuilder.set("primary_genre", primaryGenre, String.class);

    boundStatementBuilder = boundStatementBuilder.set("feed_url", feedUrl, String.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToSingleEntity(boundStatement, podcastByLanguageRecordHelper);
  }

  @Override
  public void create(PodcastByLanguageRecord podcast) {
    BoundStatementBuilder boundStatementBuilder = createStatement.boundStatementBuilder();
    podcastByLanguageRecordHelper.set(podcast, boundStatementBuilder, NullSavingStrategy.DO_NOT_SET);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  @Override
  public void save(PodcastByLanguageRecord podcast) {
    BoundStatementBuilder boundStatementBuilder = saveStatement.boundStatementBuilder();
    podcastByLanguageRecordHelper.set(podcast, boundStatementBuilder, NullSavingStrategy.DO_NOT_SET);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  public static CompletableFuture<PodcastByLanguageDao> initAsync(MapperContext context) {
    LOG.debug("[{}] Initializing new instance for keyspace = {} and table = {}",
        context.getSession().getName(),
        context.getKeyspaceId(),
        context.getTableId());
    throwIfProtocolVersionV3(context);
    try {
      // Initialize all entity helpers
      PodcastByLanguageRecordHelper__MapperGenerated podcastByLanguageRecordHelper = new PodcastByLanguageRecordHelper__MapperGenerated(context);
      if ((Boolean)context.getCustomState().get("datastax.mapper.schemaValidationEnabled")) {
        podcastByLanguageRecordHelper.validateEntityFields();
      }
      List<CompletionStage<PreparedStatement>> prepareStages = new ArrayList<>();
      // Prepare the statement for `findOne(java.lang.String,java.lang.String,java.lang.String)`:
      SimpleStatement findOneStatement_simple = podcastByLanguageRecordHelper.selectByPrimaryKeyParts(3).build();
      LOG.debug("[{}] Preparing query `{}` for method findOne(java.lang.String,java.lang.String,java.lang.String)",
          context.getSession().getName(),
          findOneStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findOneStatement = prepare(findOneStatement_simple, context);
      prepareStages.add(findOneStatement);
      // Prepare the statement for `create(dataClasses.podcast.PodcastByLanguageRecord)`:
      SimpleStatement createStatement_simple = podcastByLanguageRecordHelper.insert().build();
      LOG.debug("[{}] Preparing query `{}` for method create(dataClasses.podcast.PodcastByLanguageRecord)",
          context.getSession().getName(),
          createStatement_simple.getQuery());
      CompletionStage<PreparedStatement> createStatement = prepare(createStatement_simple, context);
      prepareStages.add(createStatement);
      // Prepare the statement for `save(dataClasses.podcast.PodcastByLanguageRecord)`:
      SimpleStatement saveStatement_simple = SimpleStatement.newInstance(((DefaultUpdate)podcastByLanguageRecordHelper.updateByPrimaryKey()).asCql());
      LOG.debug("[{}] Preparing query `{}` for method save(dataClasses.podcast.PodcastByLanguageRecord)",
          context.getSession().getName(),
          saveStatement_simple.getQuery());
      CompletionStage<PreparedStatement> saveStatement = prepare(saveStatement_simple, context);
      prepareStages.add(saveStatement);
      // Initialize all method invokers
      // Build the DAO when all statements are prepared
      return CompletableFutures.allSuccessful(prepareStages)
          .thenApply(v -> (PodcastByLanguageDao) new PodcastByLanguageDaoImpl__MapperGenerated(context,
              podcastByLanguageRecordHelper,
              CompletableFutures.getCompleted(findOneStatement),
              CompletableFutures.getCompleted(createStatement),
              CompletableFutures.getCompleted(saveStatement)))
          .toCompletableFuture();
    } catch (Throwable t) {
      return CompletableFutures.failedFuture(t);
    }
  }

  public static PodcastByLanguageDao init(MapperContext context) {
    BlockingOperation.checkNotDriverThread();
    return CompletableFutures.getUninterruptibly(initAsync(context));
  }
}
