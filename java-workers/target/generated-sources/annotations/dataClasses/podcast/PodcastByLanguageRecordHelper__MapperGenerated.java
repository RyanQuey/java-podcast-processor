package dataClasses.podcast;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.data.GettableByName;
import com.datastax.oss.driver.api.core.data.SettableByName;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.SetType;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.MapperException;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.delete.DeleteSelection;
import com.datastax.oss.driver.api.querybuilder.insert.InsertInto;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.driver.api.querybuilder.update.UpdateStart;
import com.datastax.oss.driver.internal.mapper.entity.EntityHelperBase;
import com.datastax.oss.driver.internal.querybuilder.update.DefaultUpdate;
import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableList;
import com.datastax.oss.driver.shaded.guava.common.collect.Sets;
import dataClasses.searchQuery.SearchQueryUDT;
import dataClasses.searchQuery.SearchQueryUDTHelper__MapperGenerated;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated by the DataStax driver mapper, do not edit directly.
 */
public class PodcastByLanguageRecordHelper__MapperGenerated extends EntityHelperBase<PodcastByLanguageRecord> {
  private static final Logger LOG = LoggerFactory.getLogger(PodcastByLanguageRecordHelper__MapperGenerated.class);

  private static final GenericType<ArrayList<String>> GENERIC_TYPE = new GenericType<ArrayList<String>>(){};

  private static final GenericType<Set<UdtValue>> GENERIC_TYPE1 = new GenericType<Set<UdtValue>>(){};

  private static final GenericType<String> GENERIC_TYPE2 = new GenericType<String>(){};

  private static final GenericType<Boolean> GENERIC_TYPE3 = new GenericType<Boolean>(){};

  private static final GenericType<Instant> GENERIC_TYPE4 = new GenericType<Instant>(){};

  private static final GenericType<Integer> GENERIC_TYPE5 = new GenericType<Integer>(){};

  private final List<String> primaryKeys;

  private final SearchQueryUDTHelper__MapperGenerated searchQueryUDTHelper;

  public PodcastByLanguageRecordHelper__MapperGenerated(MapperContext context) {
    super(context, "podcasts_by_language");
    LOG.debug("[{}] Entity PodcastByLanguageRecord will be mapped to {}{}",
        context.getSession().getName(),
        getKeyspaceId() == null ? "" : getKeyspaceId() + ".",
        getTableId());
    this.primaryKeys = ImmutableList.<String>builder()
        .add("language")
        .add("primary_genre")
        .add("feed_url")
        .build();
    this.searchQueryUDTHelper = new SearchQueryUDTHelper__MapperGenerated(context);
  }

  @Override
  public Class<PodcastByLanguageRecord> getEntityClass() {
    return PodcastByLanguageRecord.class;
  }

  @Override
  public <SettableT extends SettableByName<SettableT>> SettableT set(PodcastByLanguageRecord entity,
      SettableT target, NullSavingStrategy nullSavingStrategy) {

    if (entity.getLanguage() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("language", entity.getLanguage(), String.class);
    }

    if (entity.getPrimaryGenre() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("primary_genre", entity.getPrimaryGenre(), String.class);
    }

    if (entity.getFeedUrl() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("feed_url", entity.getFeedUrl(), String.class);
    }

    if (entity.getOwner() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("owner", entity.getOwner(), String.class);
    }

    if (entity.getName() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("name", entity.getName(), String.class);
    }

    if (entity.getImageUrl30() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("image_url30", entity.getImageUrl30(), String.class);
    }

    if (entity.getImageUrl60() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("image_url60", entity.getImageUrl60(), String.class);
    }

    if (entity.getImageUrl100() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("image_url100", entity.getImageUrl100(), String.class);
    }

    if (entity.getImageUrl600() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("image_url600", entity.getImageUrl600(), String.class);
    }

    if (entity.getApi() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("api", entity.getApi(), String.class);
    }

    if (entity.getApiId() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("api_id", entity.getApiId(), String.class);
    }

    if (entity.getApiUrl() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("api_url", entity.getApiUrl(), String.class);
    }

    if (entity.getCountry() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("country", entity.getCountry(), String.class);
    }

    if (entity.getGenres() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("genres", entity.getGenres(), GENERIC_TYPE);
    }

    if (entity.getApiGenreIds() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("api_genre_ids", entity.getApiGenreIds(), GENERIC_TYPE);
    }

    if (entity.getReleaseDate() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("release_date", entity.getReleaseDate(), Instant.class);
    }

    target = target.setBoolean("explicit", entity.isExplicit());

    target = target.setInt("episode_count", entity.getEpisodeCount());

    if (entity.getDescription() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("description", entity.getDescription(), String.class);
    }

    if (entity.getSummary() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("summary", entity.getSummary(), String.class);
    }

    if (entity.getSubtitle() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("subtitle", entity.getSubtitle(), String.class);
    }

    if (entity.getWebmaster() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("webmaster", entity.getWebmaster(), String.class);
    }

    if (entity.getOwnerEmail() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("owner_email", entity.getOwnerEmail(), String.class);
    }

    if (entity.getAuthor() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("author", entity.getAuthor(), String.class);
    }

    if (entity.getWebsiteUrl() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("website_url", entity.getWebsiteUrl(), String.class);
    }

    if (entity.getUpdatedAt() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("updated_at", entity.getUpdatedAt(), Instant.class);
    }

    Set<SearchQueryUDT> mappedCollection = entity.getFoundByQueries();
    if (mappedCollection != null) {
      UserDefinedType searchQueryUDTUdtType = (UserDefinedType) ((SetType) target.getType("found_by_queries")).getElementType();
      Set<UdtValue> rawCollection = Sets.newLinkedHashSetWithExpectedSize(mappedCollection.size());
      for (SearchQueryUDT mappedElement: mappedCollection) {
        UdtValue rawElement = searchQueryUDTUdtType.newValue();
        searchQueryUDTHelper.set(mappedElement, rawElement, NullSavingStrategy.DO_NOT_SET);
        rawCollection.add(rawElement);
      }
      target = target.set("found_by_queries", rawCollection, GENERIC_TYPE1);
    } else if (nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("found_by_queries", null, GENERIC_TYPE1);
    }

    return target;
  }

  @Override
  public PodcastByLanguageRecord get(GettableByName source) {
    PodcastByLanguageRecord returnValue = new PodcastByLanguageRecord();

    returnValue.setLanguage(source.get("language", String.class));

    returnValue.setPrimaryGenre(source.get("primary_genre", String.class));

    returnValue.setFeedUrl(source.get("feed_url", String.class));

    returnValue.setOwner(source.get("owner", String.class));

    returnValue.setName(source.get("name", String.class));

    returnValue.setImageUrl30(source.get("image_url30", String.class));

    returnValue.setImageUrl60(source.get("image_url60", String.class));

    returnValue.setImageUrl100(source.get("image_url100", String.class));

    returnValue.setImageUrl600(source.get("image_url600", String.class));

    returnValue.setApi(source.get("api", String.class));

    returnValue.setApiId(source.get("api_id", String.class));

    returnValue.setApiUrl(source.get("api_url", String.class));

    returnValue.setCountry(source.get("country", String.class));

    returnValue.setGenres(source.get("genres", GENERIC_TYPE));

    returnValue.setApiGenreIds(source.get("api_genre_ids", GENERIC_TYPE));

    returnValue.setReleaseDate(source.get("release_date", Instant.class));

    returnValue.setExplicit(source.getBoolean("explicit"));

    returnValue.setEpisodeCount(source.getInt("episode_count"));

    returnValue.setDescription(source.get("description", String.class));

    returnValue.setSummary(source.get("summary", String.class));

    returnValue.setSubtitle(source.get("subtitle", String.class));

    returnValue.setWebmaster(source.get("webmaster", String.class));

    returnValue.setOwnerEmail(source.get("owner_email", String.class));

    returnValue.setAuthor(source.get("author", String.class));

    returnValue.setWebsiteUrl(source.get("website_url", String.class));

    returnValue.setUpdatedAt(source.get("updated_at", Instant.class));

    Set<UdtValue> rawCollection1 = source.get("found_by_queries", GENERIC_TYPE1);
    if (rawCollection1 != null) {
      Set<SearchQueryUDT> mappedCollection1 = Sets.newLinkedHashSetWithExpectedSize(rawCollection1.size());
      for (UdtValue rawElement1: rawCollection1) {
        SearchQueryUDT mappedElement1 = searchQueryUDTHelper.get(rawElement1);
        mappedCollection1.add(mappedElement1);
      }
      returnValue.setFoundByQueries(mappedCollection1);
    }
    return returnValue;
  }

  @Override
  public RegularInsert insert() {
    throwIfKeyspaceMissing();
    InsertInto insertInto = (keyspaceId == null)
        ? QueryBuilder.insertInto(tableId)
        : QueryBuilder.insertInto(keyspaceId, tableId);
    return insertInto
        .value("language", QueryBuilder.bindMarker("language"))
        .value("primary_genre", QueryBuilder.bindMarker("primary_genre"))
        .value("feed_url", QueryBuilder.bindMarker("feed_url"))
        .value("owner", QueryBuilder.bindMarker("owner"))
        .value("name", QueryBuilder.bindMarker("name"))
        .value("image_url30", QueryBuilder.bindMarker("image_url30"))
        .value("image_url60", QueryBuilder.bindMarker("image_url60"))
        .value("image_url100", QueryBuilder.bindMarker("image_url100"))
        .value("image_url600", QueryBuilder.bindMarker("image_url600"))
        .value("api", QueryBuilder.bindMarker("api"))
        .value("api_id", QueryBuilder.bindMarker("api_id"))
        .value("api_url", QueryBuilder.bindMarker("api_url"))
        .value("country", QueryBuilder.bindMarker("country"))
        .value("genres", QueryBuilder.bindMarker("genres"))
        .value("api_genre_ids", QueryBuilder.bindMarker("api_genre_ids"))
        .value("release_date", QueryBuilder.bindMarker("release_date"))
        .value("explicit", QueryBuilder.bindMarker("explicit"))
        .value("episode_count", QueryBuilder.bindMarker("episode_count"))
        .value("description", QueryBuilder.bindMarker("description"))
        .value("summary", QueryBuilder.bindMarker("summary"))
        .value("subtitle", QueryBuilder.bindMarker("subtitle"))
        .value("webmaster", QueryBuilder.bindMarker("webmaster"))
        .value("owner_email", QueryBuilder.bindMarker("owner_email"))
        .value("author", QueryBuilder.bindMarker("author"))
        .value("website_url", QueryBuilder.bindMarker("website_url"))
        .value("updated_at", QueryBuilder.bindMarker("updated_at"))
        .value("found_by_queries", QueryBuilder.bindMarker("found_by_queries"));
  }

  public Select selectByPrimaryKeyParts(int parameterCount) {
    Select select = selectStart();
    for (int i = 0; i < parameterCount && i < primaryKeys.size(); i++) {
      String columnName = primaryKeys.get(i);
      select = select.whereColumn(columnName).isEqualTo(QueryBuilder.bindMarker(columnName));
    }
    return select;
  }

  @Override
  public Select selectByPrimaryKey() {
    return selectByPrimaryKeyParts(primaryKeys.size());
  }

  @Override
  public Select selectStart() {
    throwIfKeyspaceMissing();
    SelectFrom selectFrom = (keyspaceId == null)
        ? QueryBuilder.selectFrom(tableId)
        : QueryBuilder.selectFrom(keyspaceId, tableId);
    return selectFrom
        .column("language")
        .column("primary_genre")
        .column("feed_url")
        .column("owner")
        .column("name")
        .column("image_url30")
        .column("image_url60")
        .column("image_url100")
        .column("image_url600")
        .column("api")
        .column("api_id")
        .column("api_url")
        .column("country")
        .column("genres")
        .column("api_genre_ids")
        .column("release_date")
        .column("explicit")
        .column("episode_count")
        .column("description")
        .column("summary")
        .column("subtitle")
        .column("webmaster")
        .column("owner_email")
        .column("author")
        .column("website_url")
        .column("updated_at")
        .column("found_by_queries");
  }

  public DeleteSelection deleteStart() {
    throwIfKeyspaceMissing();
    return (keyspaceId == null)
        ? QueryBuilder.deleteFrom(tableId)
        : QueryBuilder.deleteFrom(keyspaceId, tableId);
  }

  public Delete deleteByPrimaryKeyParts(int parameterCount) {
    if (parameterCount <= 0) {
      throw new MapperException("parameterCount must be greater than 0");
    }
    DeleteSelection deleteSelection = deleteStart();
    String columnName = primaryKeys.get(0);
    Delete delete = deleteSelection.whereColumn(columnName).isEqualTo(QueryBuilder.bindMarker(columnName));
    for (int i = 1; i < parameterCount && i < primaryKeys.size(); i++) {
      columnName = primaryKeys.get(i);
      delete = delete.whereColumn(columnName).isEqualTo(QueryBuilder.bindMarker(columnName));
    }
    return delete;
  }

  @Override
  public Delete deleteByPrimaryKey() {
    return deleteByPrimaryKeyParts(primaryKeys.size());
  }

  @Override
  public DefaultUpdate updateStart() {
    throwIfKeyspaceMissing();
    UpdateStart update = (keyspaceId == null)
        ? QueryBuilder.update(tableId)
        : QueryBuilder.update(keyspaceId, tableId);
    return ((DefaultUpdate)update
        .setColumn("owner", QueryBuilder.bindMarker("owner"))
        .setColumn("name", QueryBuilder.bindMarker("name"))
        .setColumn("image_url30", QueryBuilder.bindMarker("image_url30"))
        .setColumn("image_url60", QueryBuilder.bindMarker("image_url60"))
        .setColumn("image_url100", QueryBuilder.bindMarker("image_url100"))
        .setColumn("image_url600", QueryBuilder.bindMarker("image_url600"))
        .setColumn("api", QueryBuilder.bindMarker("api"))
        .setColumn("api_id", QueryBuilder.bindMarker("api_id"))
        .setColumn("api_url", QueryBuilder.bindMarker("api_url"))
        .setColumn("country", QueryBuilder.bindMarker("country"))
        .setColumn("genres", QueryBuilder.bindMarker("genres"))
        .setColumn("api_genre_ids", QueryBuilder.bindMarker("api_genre_ids"))
        .setColumn("release_date", QueryBuilder.bindMarker("release_date"))
        .setColumn("explicit", QueryBuilder.bindMarker("explicit"))
        .setColumn("episode_count", QueryBuilder.bindMarker("episode_count"))
        .setColumn("description", QueryBuilder.bindMarker("description"))
        .setColumn("summary", QueryBuilder.bindMarker("summary"))
        .setColumn("subtitle", QueryBuilder.bindMarker("subtitle"))
        .setColumn("webmaster", QueryBuilder.bindMarker("webmaster"))
        .setColumn("owner_email", QueryBuilder.bindMarker("owner_email"))
        .setColumn("author", QueryBuilder.bindMarker("author"))
        .setColumn("website_url", QueryBuilder.bindMarker("website_url"))
        .setColumn("updated_at", QueryBuilder.bindMarker("updated_at"))
        .setColumn("found_by_queries", QueryBuilder.bindMarker("found_by_queries")));
  }

  @Override
  public DefaultUpdate updateByPrimaryKey() {
    return ((DefaultUpdate)updateStart()
        .where(Relation.column("language").isEqualTo(QueryBuilder.bindMarker("language")))
        .where(Relation.column("primary_genre").isEqualTo(QueryBuilder.bindMarker("primary_genre")))
        .where(Relation.column("feed_url").isEqualTo(QueryBuilder.bindMarker("feed_url"))));
  }

  @Override
  public void validateEntityFields() {
    CqlIdentifier keyspaceId = this.keyspaceId != null ? this.keyspaceId : context.getSession().getKeyspace().orElse(null);
    String entityClassName = "dataClasses.podcast.PodcastByLanguageRecord";
    if (keyspaceId == null) {
      LOG.warn("[{}] Unable to validate table: {} for the entity class: {} because the keyspace is unknown (the entity does not declare a default keyspace, and neither the session nor the DAO were created with a keyspace). The DAO will only work if it uses fully-qualified queries with @Query or @QueryProvider.",
          context.getSession().getName(),
          tableId,
          entityClassName);
      return;
    }
    if(!keyspaceNamePresent(context.getSession().getMetadata().getKeyspaces(), keyspaceId)) {
      LOG.warn("[{}] Unable to validate table: {} for the entity class: {} because the session metadata has no information about the keyspace: {}.",
          context.getSession().getName(),
          tableId,
          entityClassName,
          keyspaceId);
      return;
    }
    Optional<KeyspaceMetadata> keyspace = context.getSession().getMetadata().getKeyspace(keyspaceId);
    List<CqlIdentifier> expectedCqlNames = new ArrayList<>();
    expectedCqlNames.add(CqlIdentifier.fromCql("language"));
    expectedCqlNames.add(CqlIdentifier.fromCql("primary_genre"));
    expectedCqlNames.add(CqlIdentifier.fromCql("feed_url"));
    expectedCqlNames.add(CqlIdentifier.fromCql("owner"));
    expectedCqlNames.add(CqlIdentifier.fromCql("name"));
    expectedCqlNames.add(CqlIdentifier.fromCql("image_url30"));
    expectedCqlNames.add(CqlIdentifier.fromCql("image_url60"));
    expectedCqlNames.add(CqlIdentifier.fromCql("image_url100"));
    expectedCqlNames.add(CqlIdentifier.fromCql("image_url600"));
    expectedCqlNames.add(CqlIdentifier.fromCql("api"));
    expectedCqlNames.add(CqlIdentifier.fromCql("api_id"));
    expectedCqlNames.add(CqlIdentifier.fromCql("api_url"));
    expectedCqlNames.add(CqlIdentifier.fromCql("country"));
    expectedCqlNames.add(CqlIdentifier.fromCql("genres"));
    expectedCqlNames.add(CqlIdentifier.fromCql("api_genre_ids"));
    expectedCqlNames.add(CqlIdentifier.fromCql("release_date"));
    expectedCqlNames.add(CqlIdentifier.fromCql("explicit"));
    expectedCqlNames.add(CqlIdentifier.fromCql("episode_count"));
    expectedCqlNames.add(CqlIdentifier.fromCql("description"));
    expectedCqlNames.add(CqlIdentifier.fromCql("summary"));
    expectedCqlNames.add(CqlIdentifier.fromCql("subtitle"));
    expectedCqlNames.add(CqlIdentifier.fromCql("webmaster"));
    expectedCqlNames.add(CqlIdentifier.fromCql("owner_email"));
    expectedCqlNames.add(CqlIdentifier.fromCql("author"));
    expectedCqlNames.add(CqlIdentifier.fromCql("website_url"));
    expectedCqlNames.add(CqlIdentifier.fromCql("updated_at"));
    expectedCqlNames.add(CqlIdentifier.fromCql("found_by_queries"));
    Optional<TableMetadata> tableMetadata = keyspace.flatMap(v -> v.getTable(tableId));
    Optional<UserDefinedType> userDefinedType = keyspace.flatMap(v -> v.getUserDefinedType(tableId));
    if (tableMetadata.isPresent()) {
      // validation of missing Clustering Columns
      List<CqlIdentifier> expectedCqlClusteringColumns = new ArrayList<>();
      expectedCqlClusteringColumns.add(CqlIdentifier.fromCql("primary_genre"));
      expectedCqlClusteringColumns.add(CqlIdentifier.fromCql("feed_url"));
      List<CqlIdentifier> missingTableClusteringColumnNames = findMissingColumns(expectedCqlClusteringColumns, tableMetadata.get().getClusteringColumns().keySet());
      if (!missingTableClusteringColumnNames.isEmpty()) {
        throw new IllegalArgumentException(String.format("The CQL ks.table: %s.%s has missing Clustering columns: %s that are defined in the entity class: %s", keyspaceId, tableId, missingTableClusteringColumnNames, entityClassName));
      }
      // validation of missing PKs
      List<CqlIdentifier> expectedCqlPKs = new ArrayList<>();
      expectedCqlPKs.add(CqlIdentifier.fromCql("language"));
      List<CqlIdentifier> missingTablePksNames = findMissingColumns(expectedCqlPKs, tableMetadata.get().getPartitionKey());
      if (!missingTablePksNames.isEmpty()) {
        throw new IllegalArgumentException(String.format("The CQL ks.table: %s.%s has missing Primary Key columns: %s that are defined in the entity class: %s", keyspaceId, tableId, missingTablePksNames, entityClassName));
      }
      // validation of all columns
      List<CqlIdentifier> missingTableCqlNames = findMissingCqlIdentifiers(expectedCqlNames, tableMetadata.get().getColumns().keySet());
      if (!missingTableCqlNames.isEmpty()) {
        throw new IllegalArgumentException(String.format("The CQL ks.table: %s.%s has missing columns: %s that are defined in the entity class: %s", keyspaceId, tableId, missingTableCqlNames, entityClassName));
      }
      // validation of types
      Map<CqlIdentifier, GenericType<?>> expectedTypesPerColumn = new LinkedHashMap<>();
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("webmaster"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("country"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("explicit"), GENERIC_TYPE3);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("author"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("primary_genre"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("updated_at"), GENERIC_TYPE4);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("owner"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("website_url"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("subtitle"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("release_date"), GENERIC_TYPE4);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("name"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("found_by_queries"), GENERIC_TYPE1);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("genres"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api_id"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api_genre_ids"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url100"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("language"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("description"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url600"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("episode_count"), GENERIC_TYPE5);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("feed_url"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url60"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url30"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("summary"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api_url"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("owner_email"), GENERIC_TYPE2);
      List<String> missingTableTypes = findTypeMismatches(expectedTypesPerColumn, tableMetadata.get().getColumns(), context.getSession().getContext().getCodecRegistry());
      throwMissingTableTypesIfNotEmpty(missingTableTypes, keyspaceId, tableId, entityClassName);
    }
    else if (userDefinedType.isPresent()) {
      // validation of UDT columns
      List<CqlIdentifier> columns = userDefinedType.get().getFieldNames();
      List<CqlIdentifier> missingTableCqlNames = findMissingCqlIdentifiers(expectedCqlNames, columns);
      if (!missingTableCqlNames.isEmpty()) {
        throw new IllegalArgumentException(String.format("The CQL ks.udt: %s.%s has missing columns: %s that are defined in the entity class: %s", keyspaceId, tableId, missingTableCqlNames, entityClassName));
      }
      // validation of UDT types
      Map<CqlIdentifier, GenericType<?>> expectedTypesPerColumn = new LinkedHashMap<>();
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("webmaster"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("country"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("explicit"), GENERIC_TYPE3);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("author"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("primary_genre"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("updated_at"), GENERIC_TYPE4);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("owner"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("website_url"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("subtitle"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("release_date"), GENERIC_TYPE4);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("name"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("found_by_queries"), GENERIC_TYPE1);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("genres"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api_id"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api_genre_ids"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url100"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("language"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("description"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url600"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("episode_count"), GENERIC_TYPE5);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("feed_url"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url60"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("image_url30"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("summary"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("api_url"), GENERIC_TYPE2);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("owner_email"), GENERIC_TYPE2);
      List<CqlIdentifier> expectedColumns = userDefinedType.get().getFieldNames();
      List<DataType> expectedTypes = userDefinedType.get().getFieldTypes();
      List<String> missingTableTypes = findTypeMismatches(expectedTypesPerColumn, expectedColumns, expectedTypes, context.getSession().getContext().getCodecRegistry());
      throwMissingUdtTypesIfNotEmpty(missingTableTypes, keyspaceId, tableId, entityClassName);
    }
    // warn if there is not keyspace.table for defined entity - it means that table is missing, or schema it out of date.
    else {
      LOG.warn("[{}] There is no ks.table or UDT: {}.{} for the entity class: {}, or metadata is out of date.",
          context.getSession().getName(),
          keyspaceId,
          tableId,
          entityClassName);
    }
  }
}
