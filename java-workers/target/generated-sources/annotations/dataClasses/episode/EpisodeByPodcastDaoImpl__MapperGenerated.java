package dataClasses.episode;

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
public class EpisodeByPodcastDaoImpl__MapperGenerated extends DaoBase implements EpisodeByPodcastDao {
  private static final Logger LOG = LoggerFactory.getLogger(EpisodeByPodcastDaoImpl__MapperGenerated.class);

  private final EpisodeByPodcastRecordHelper__MapperGenerated episodeByPodcastRecordHelper;

  private final PreparedStatement findOneStatement;

  private final PreparedStatement findAllByPodcastStatement;

  private final PreparedStatement createStatement;

  private final PreparedStatement saveStatement;

  private EpisodeByPodcastDaoImpl__MapperGenerated(MapperContext context,
      EpisodeByPodcastRecordHelper__MapperGenerated episodeByPodcastRecordHelper,
      PreparedStatement findOneStatement, PreparedStatement findAllByPodcastStatement,
      PreparedStatement createStatement, PreparedStatement saveStatement) {
    super(context);
    this.episodeByPodcastRecordHelper = episodeByPodcastRecordHelper;
    this.findOneStatement = findOneStatement;
    this.findAllByPodcastStatement = findAllByPodcastStatement;
    this.createStatement = createStatement;
    this.saveStatement = saveStatement;
  }

  @Override
  public EpisodeByPodcastRecord findOne(String podcastApi, String podcastApiId,
      String episodeGuid) {
    BoundStatementBuilder boundStatementBuilder = findOneStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("podcast_api", podcastApi, String.class);

    boundStatementBuilder = boundStatementBuilder.set("podcast_api_id", podcastApiId, String.class);

    boundStatementBuilder = boundStatementBuilder.set("episode_guid", episodeGuid, String.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToSingleEntity(boundStatement, episodeByPodcastRecordHelper);
  }

  @Override
  public EpisodeByPodcastRecord findAllByPodcast(String podcastApi, String podcastApiId) {
    BoundStatementBuilder boundStatementBuilder = findAllByPodcastStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("podcastApi", podcastApi, String.class);

    boundStatementBuilder = boundStatementBuilder.set("podcastApiId", podcastApiId, String.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToSingleEntity(boundStatement, episodeByPodcastRecordHelper);
  }

  @Override
  public void create(EpisodeByPodcastRecord episode) {
    BoundStatementBuilder boundStatementBuilder = createStatement.boundStatementBuilder();
    episodeByPodcastRecordHelper.set(episode, boundStatementBuilder, NullSavingStrategy.DO_NOT_SET);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  @Override
  public void save(EpisodeByPodcastRecord episode) {
    BoundStatementBuilder boundStatementBuilder = saveStatement.boundStatementBuilder();
    episodeByPodcastRecordHelper.set(episode, boundStatementBuilder, NullSavingStrategy.DO_NOT_SET);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  public static CompletableFuture<EpisodeByPodcastDao> initAsync(MapperContext context) {
    LOG.debug("[{}] Initializing new instance for keyspace = {} and table = {}",
        context.getSession().getName(),
        context.getKeyspaceId(),
        context.getTableId());
    throwIfProtocolVersionV3(context);
    try {
      // Initialize all entity helpers
      EpisodeByPodcastRecordHelper__MapperGenerated episodeByPodcastRecordHelper = new EpisodeByPodcastRecordHelper__MapperGenerated(context);
      if ((Boolean)context.getCustomState().get("datastax.mapper.schemaValidationEnabled")) {
        episodeByPodcastRecordHelper.validateEntityFields();
      }
      List<CompletionStage<PreparedStatement>> prepareStages = new ArrayList<>();
      // Prepare the statement for `public abstract dataClasses.episode.EpisodeByPodcastRecord findOne(java.lang.String, java.lang.String, java.lang.String) `:
      SimpleStatement findOneStatement_simple = episodeByPodcastRecordHelper.selectByPrimaryKeyParts(3).build();
      LOG.debug("[{}] Preparing query `{}` for method public abstract dataClasses.episode.EpisodeByPodcastRecord findOne(java.lang.String, java.lang.String, java.lang.String) ",
          context.getSession().getName(),
          findOneStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findOneStatement = prepare(findOneStatement_simple, context);
      prepareStages.add(findOneStatement);
      // Prepare the statement for `public abstract dataClasses.episode.EpisodeByPodcastRecord findAllByPodcast(java.lang.String, java.lang.String) `:
      SimpleStatement findAllByPodcastStatement_simple = episodeByPodcastRecordHelper.selectStart().whereRaw("podcast_api = ':podcastApi' AND podcast_api_id = ':podcastApiId'").build();
      LOG.debug("[{}] Preparing query `{}` for method public abstract dataClasses.episode.EpisodeByPodcastRecord findAllByPodcast(java.lang.String, java.lang.String) ",
          context.getSession().getName(),
          findAllByPodcastStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findAllByPodcastStatement = prepare(findAllByPodcastStatement_simple, context);
      prepareStages.add(findAllByPodcastStatement);
      // Prepare the statement for `public abstract void create(dataClasses.episode.EpisodeByPodcastRecord) `:
      SimpleStatement createStatement_simple = episodeByPodcastRecordHelper.insert().build();
      LOG.debug("[{}] Preparing query `{}` for method public abstract void create(dataClasses.episode.EpisodeByPodcastRecord) ",
          context.getSession().getName(),
          createStatement_simple.getQuery());
      CompletionStage<PreparedStatement> createStatement = prepare(createStatement_simple, context);
      prepareStages.add(createStatement);
      // Prepare the statement for `public abstract void save(dataClasses.episode.EpisodeByPodcastRecord) `:
      SimpleStatement saveStatement_simple = SimpleStatement.newInstance(((DefaultUpdate)episodeByPodcastRecordHelper.updateByPrimaryKey()).asCql());
      LOG.debug("[{}] Preparing query `{}` for method public abstract void save(dataClasses.episode.EpisodeByPodcastRecord) ",
          context.getSession().getName(),
          saveStatement_simple.getQuery());
      CompletionStage<PreparedStatement> saveStatement = prepare(saveStatement_simple, context);
      prepareStages.add(saveStatement);
      // Initialize all method invokers
      // Build the DAO when all statements are prepared
      return CompletableFutures.allSuccessful(prepareStages)
          .thenApply(v -> (EpisodeByPodcastDao) new EpisodeByPodcastDaoImpl__MapperGenerated(context,
              episodeByPodcastRecordHelper,
              CompletableFutures.getCompleted(findOneStatement),
              CompletableFutures.getCompleted(findAllByPodcastStatement),
              CompletableFutures.getCompleted(createStatement),
              CompletableFutures.getCompleted(saveStatement)))
          .toCompletableFuture();
    } catch (Throwable t) {
      return CompletableFutures.failedFuture(t);
    }
  }

  public static EpisodeByPodcastDao init(MapperContext context) {
    BlockingOperation.checkNotDriverThread();
    return CompletableFutures.getUninterruptibly(initAsync(context));
  }
}
