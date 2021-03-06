package com.ryanquey.podcast.dataClasses.searchQuery;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.data.GettableByName;
import com.datastax.oss.driver.api.core.data.SettableByName;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.api.core.type.DataType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated by the DataStax driver mapper, do not edit directly.
 */
public class SearchQueryByTermRecordHelper__MapperGenerated extends EntityHelperBase<SearchQueryByTermRecord> {
  private static final Logger LOG = LoggerFactory.getLogger(SearchQueryByTermRecordHelper__MapperGenerated.class);

  private static final GenericType<String> GENERIC_TYPE = new GenericType<String>(){};

  private static final GenericType<Integer> GENERIC_TYPE1 = new GenericType<Integer>(){};

  private static final GenericType<Instant> GENERIC_TYPE2 = new GenericType<Instant>(){};

  private final List<String> primaryKeys;

  public SearchQueryByTermRecordHelper__MapperGenerated(MapperContext context) {
    super(context, "search_queries_by_term");
    LOG.debug("[{}] Entity SearchQueryByTermRecord will be mapped to {}{}",
        context.getSession().getName(),
        getKeyspaceId() == null ? "" : getKeyspaceId() + ".",
        getTableId());
    this.primaryKeys = ImmutableList.<String>builder()
        .add("term")
        .add("search_type")
        .add("external_api")
        .build();
  }

  @Override
  public Class<SearchQueryByTermRecord> getEntityClass() {
    return SearchQueryByTermRecord.class;
  }

  @Override
  public <SettableT extends SettableByName<SettableT>> SettableT set(SearchQueryByTermRecord entity,
      SettableT target, NullSavingStrategy nullSavingStrategy) {

    if (entity.getTerm() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("term", entity.getTerm(), String.class);
    }

    if (entity.getSearchType() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("search_type", entity.getSearchType(), String.class);
    }

    if (entity.getExternalApi() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("external_api", entity.getExternalApi(), String.class);
    }

    if (entity.getResultJson() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("result_json", entity.getResultJson(), String.class);
    }

    if (entity.getUpdatedAt() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("updated_at", entity.getUpdatedAt(), Instant.class);
    }

    if (entity.getPodcastCount() != null || nullSavingStrategy == NullSavingStrategy.SET_TO_NULL) {
      target = target.set("podcast_count", entity.getPodcastCount(), Integer.class);
    }

    return target;
  }

  @Override
  public SearchQueryByTermRecord get(GettableByName source) {
    SearchQueryByTermRecord returnValue = new SearchQueryByTermRecord();

    returnValue.setTerm(source.get("term", String.class));

    returnValue.setSearchType(source.get("search_type", String.class));

    returnValue.setExternalApi(source.get("external_api", String.class));

    returnValue.setResultJson(source.get("result_json", String.class));

    returnValue.setUpdatedAt(source.get("updated_at", Instant.class));

    returnValue.setPodcastCount(source.get("podcast_count", Integer.class));
    return returnValue;
  }

  @Override
  public RegularInsert insert() {
    throwIfKeyspaceMissing();
    InsertInto insertInto = (keyspaceId == null)
        ? QueryBuilder.insertInto(tableId)
        : QueryBuilder.insertInto(keyspaceId, tableId);
    return insertInto
        .value("term", QueryBuilder.bindMarker("term"))
        .value("search_type", QueryBuilder.bindMarker("search_type"))
        .value("external_api", QueryBuilder.bindMarker("external_api"))
        .value("result_json", QueryBuilder.bindMarker("result_json"))
        .value("updated_at", QueryBuilder.bindMarker("updated_at"))
        .value("podcast_count", QueryBuilder.bindMarker("podcast_count"));
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
        .column("term")
        .column("search_type")
        .column("external_api")
        .column("result_json")
        .column("updated_at")
        .column("podcast_count");
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
        .setColumn("result_json", QueryBuilder.bindMarker("result_json"))
        .setColumn("updated_at", QueryBuilder.bindMarker("updated_at"))
        .setColumn("podcast_count", QueryBuilder.bindMarker("podcast_count")));
  }

  @Override
  public DefaultUpdate updateByPrimaryKey() {
    return ((DefaultUpdate)updateStart()
        .where(Relation.column("term").isEqualTo(QueryBuilder.bindMarker("term")))
        .where(Relation.column("search_type").isEqualTo(QueryBuilder.bindMarker("search_type")))
        .where(Relation.column("external_api").isEqualTo(QueryBuilder.bindMarker("external_api"))));
  }

  @Override
  public void validateEntityFields() {
    CqlIdentifier keyspaceId = this.keyspaceId != null ? this.keyspaceId : context.getSession().getKeyspace().orElse(null);
    String entityClassName = "com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryByTermRecord";
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
    expectedCqlNames.add(CqlIdentifier.fromCql("term"));
    expectedCqlNames.add(CqlIdentifier.fromCql("search_type"));
    expectedCqlNames.add(CqlIdentifier.fromCql("external_api"));
    expectedCqlNames.add(CqlIdentifier.fromCql("result_json"));
    expectedCqlNames.add(CqlIdentifier.fromCql("updated_at"));
    expectedCqlNames.add(CqlIdentifier.fromCql("podcast_count"));
    Optional<TableMetadata> tableMetadata = keyspace.flatMap(v -> v.getTable(tableId));
    Optional<UserDefinedType> userDefinedType = keyspace.flatMap(v -> v.getUserDefinedType(tableId));
    if (tableMetadata.isPresent()) {
      // validation of missing Clustering Columns
      List<CqlIdentifier> expectedCqlClusteringColumns = new ArrayList<>();
      expectedCqlClusteringColumns.add(CqlIdentifier.fromCql("search_type"));
      expectedCqlClusteringColumns.add(CqlIdentifier.fromCql("external_api"));
      List<CqlIdentifier> missingTableClusteringColumnNames = findMissingColumns(expectedCqlClusteringColumns, tableMetadata.get().getClusteringColumns().keySet());
      if (!missingTableClusteringColumnNames.isEmpty()) {
        throw new IllegalArgumentException(String.format("The CQL ks.table: %s.%s has missing Clustering columns: %s that are defined in the entity class: %s", keyspaceId, tableId, missingTableClusteringColumnNames, entityClassName));
      }
      // validation of missing PKs
      List<CqlIdentifier> expectedCqlPKs = new ArrayList<>();
      expectedCqlPKs.add(CqlIdentifier.fromCql("term"));
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
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("result_json"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("search_type"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("term"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("external_api"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("podcast_count"), GENERIC_TYPE1);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("updated_at"), GENERIC_TYPE2);
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
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("result_json"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("search_type"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("term"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("external_api"), GENERIC_TYPE);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("podcast_count"), GENERIC_TYPE1);
      expectedTypesPerColumn.put(CqlIdentifier.fromCql("updated_at"), GENERIC_TYPE2);
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
