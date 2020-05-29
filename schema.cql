CREATE KEYSPACE dse_system_local WITH replication = {'class': 'LocalStrategy'}  AND durable_writes = true;

CREATE TABLE dse_system_local.solr_resources (
    core_name text,
    resource_name text,
    resource_value blob,
    PRIMARY KEY (core_name, resource_name)
) WITH CLUSTERING ORDER BY (resource_name ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 7776000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE dse_security WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE dse_security.role_options (
    role text PRIMARY KEY,
    options map<text, text>
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'role options'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 7776000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE dse_security.digest_tokens (
    id blob PRIMARY KEY,
    password blob
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'digest_tokens'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 7776000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE dse_security.spark_security (
    dc text PRIMARY KEY,
    shared_secret text
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'Spark security settings'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 7776000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE solr_admin WITH replication = {'class': 'EverywhereStrategy'}  AND durable_writes = true;

CREATE TABLE solr_admin.solr_resources (
    core_name text,
    resource_name text,
    resource_value blob,
    PRIMARY KEY (core_name, resource_name)
) WITH CLUSTERING ORDER BY (resource_name ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 7776000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE podcast_analysis_tool WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = false;

CREATE TYPE podcast_analysis_tool.podcast (
    owner text,
    name text,
    image_url30 text,
    image_url60 text,
    image_url100 text,
    image_url600 text,
    api text,
    api_id text,
    api_url text,
    country text,
    feed_url text,
    genres list<text>,
    api_genre_ids list<text>,
    primary_genre text,
    release_date timestamp,
    explicit boolean,
    episode_count int,
    rss_feed text,
    found_by_queries list<frozen<map<text, text>>>,
    description text,
    summary text,
    description_subtitle text,
    webmaster text,
    owner_email text,
    author text,
    language text,
    website_url text,
    updated_at timestamp
);

CREATE TYPE podcast_analysis_tool.episode (
    podcast_api text,
    podcast_api_id text,
    order_num int,
    podcast podcast,
    podcast_website_url text,
    summary text,
    duration duration,
    subtitle text,
    image_url text,
    episode_type text,
    episode_num int,
    season_num int,
    title text,
    author text,
    keywords set<text>,
    explicit boolean,
    updated_at timestamp,
    closed_captioned boolean
);

CREATE TABLE podcast_analysis_tool.podcasts_by_language (
    language text,
    primary_genre text,
    feed_url text,
    api text,
    api_genre_ids list<text>,
    api_id text,
    api_url text,
    author text,
    country text,
    description text,
    episode_count int,
    episodes list<frozen<episode>>,
    explicit boolean,
    found_by_queries list<frozen<map<text, text>>>,
    genres list<text>,
    image_url100 text,
    image_url30 text,
    image_url60 text,
    image_url600 text,
    name text,
    owner text,
    owner_email text,
    release_date timestamp,
    rss_feed text,
    subtitle text,
    summary text,
    updated_at timestamp,
    webmaster text,
    website_url text,
    PRIMARY KEY (language, primary_genre, feed_url)
) WITH CLUSTERING ORDER BY (primary_genre ASC, feed_url ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE podcast_analysis_tool.search_results_by_term (
    term text,
    search_type text,
    external_api text,
    result_json text,
    updated_at timestamp,
    PRIMARY KEY (term, search_type, external_api)
) WITH CLUSTERING ORDER BY (search_type ASC, external_api ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE podcast_analysis_tool.episodes_by_order_in_podcast (
    podcast_api text,
    podcast_api_id text,
    order_num int,
    author text,
    closed_captioned boolean,
    duration duration,
    episode_num int,
    episode_type text,
    explicit boolean,
    image_url text,
    keywords set<text>,
    podcast frozen<podcast>,
    podcast_website_url text,
    season_num int,
    subtitle text,
    summary text,
    title text,
    updated_at timestamp,
    PRIMARY KEY ((podcast_api, podcast_api_id), order_num)
) WITH CLUSTERING ORDER BY (order_num ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE dse_leases WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE dse_leases.leases (
    name text,
    dc text,
    duration_ms int,
    epoch bigint,
    holder inet,
    PRIMARY KEY ((name, dc))
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'DSE Lease coordination'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 1209600
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE dse_leases.logs (
    name text,
    dc text,
    monitor inet,
    at timestamp,
    new_holder inet,
    old_holder inet,
    PRIMARY KEY ((name, dc), monitor, at)
) WITH CLUSTERING ORDER BY (monitor ASC, at ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'DSE Lease history'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 1209600
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE system_backups WITH replication = {'class': 'EverywhereStrategy'}  AND durable_writes = true;

CREATE TABLE system_backups.backup_stores (
    name text PRIMARY KEY,
    class text,
    settings map<text, text>
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'backup stores'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE system_backups.backup_configurations (
    name text PRIMARY KEY,
    frequency text,
    include_encryption_keys boolean,
    is_enabled boolean,
    store_names frozen<list<text>>,
    targets frozen<set<text>>
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'Backup configurations'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE weather_app WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE KEYSPACE dse_insights WITH replication = {'class': 'EverywhereStrategy'}  AND durable_writes = true;

CREATE TYPE dse_insights.insights_filters_rule_type (
    policy text,
    pattern text,
    scope text
);

CREATE TYPE dse_insights.insights_config_type (
    mode text,
    upload_url text,
    upload_interval_in_seconds int,
    metric_sampling_interval_in_seconds int,
    data_dir_max_size_in_mb int,
    proxy_type text,
    proxy_url text,
    proxy_authentication text,
    node_system_info_report_period text,
    config_refresh_interval_in_seconds int,
    filtering_rules frozen<set<frozen<insights_filters_rule_type>>>
);

CREATE TABLE dse_insights.tokens (
    "node" uuid PRIMARY KEY,
    bearer_token ascii,
    last_updated timestamp,
    max_added_date_seen_by_node timestamp
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'DSE Insights Tokens'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 0
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'deadline_target_sec': '1200', 'enabled': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE dse_insights.insights_config (
    key int PRIMARY KEY,
    "config" insights_config_type
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'DSE Insights Config'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 0
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'deadline_target_sec': '1200', 'enabled': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE dse_perf WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE dse_perf.node_slow_log (
    node_ip inet,
    date timestamp,
    start_time timeuuid,
    commands list<text>,
    duration bigint,
    parameters map<text, text>,
    source_ip inet,
    table_names set<text>,
    tracing_session_id uuid,
    username text,
    PRIMARY KEY ((node_ip, date), start_time)
) WITH CLUSTERING ORDER BY (start_time DESC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.TimeWindowCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 60
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE dse_insights_local WITH replication = {'class': 'LocalStrategy'}  AND durable_writes = true;

CREATE TYPE dse_insights_local.insights_filters_rule_type (
    policy text,
    pattern text,
    scope text
);

CREATE TYPE dse_insights_local.insights_config_type (
    mode text,
    upload_url text,
    upload_interval_in_seconds int,
    metric_sampling_interval_in_seconds int,
    data_dir_max_size_in_mb int,
    proxy_type text,
    proxy_url text,
    proxy_authentication text,
    node_system_info_report_period text,
    config_refresh_interval_in_seconds int,
    filtering_rules frozen<set<frozen<insights_filters_rule_type>>>
);

CREATE TABLE dse_insights_local.insights_config (
    key int PRIMARY KEY,
    "config" insights_config_type
) WITH additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = 'DSE Insights Config'
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 0
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE killrvideo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE killrvideo.videos_count_by_tag (
    tag text,
    added_year int,
    video_count counter,
    PRIMARY KEY (tag, added_year)
) WITH CLUSTERING ORDER BY (added_year ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

CREATE KEYSPACE dse_system WITH replication = {'class': 'EverywhereStrategy'}  AND durable_writes = true;

CREATE TABLE dse_system.encrypted_keys (
    key_file text,
    cipher text,
    strength int,
    key_id timeuuid,
    key text,
    PRIMARY KEY (key_file, cipher, strength, key_id)
) WITH CLUSTERING ORDER BY (cipher ASC, strength ASC, key_id ASC)
    AND additional_write_policy = '99PERCENTILE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 7776000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 3600000
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = '99PERCENTILE';

