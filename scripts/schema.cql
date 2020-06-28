	CREATE KEYSPACE podcast_analysis_tool WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = false;

	CREATE TABLE podcast_analysis_tool.episodes_by_podcast (
	    podcast_api text,
	    podcast_api_id text,
	    episode_guid text,
	    author text,
	    closed_captioned boolean,
	    comments text,
	    content text,
	    description text,
	    duration duration,
	    episode_num int,
	    episode_type text,
	    episode_updated_date date,
	    episode_url text,
	    explicit boolean,
	    image_url text,
	    keywords set<text>,
	    order_num int,
	    podcast_website_url text,
	    published_date date,
	    season_num int,
	    subtitle text,
	    summary text,
	    title text,
	    updated_at timestamp,
	    PRIMARY KEY ((podcast_api, podcast_api_id), episode_guid)
	) WITH CLUSTERING ORDER BY (episode_guid ASC)
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
	
	CREATE TABLE podcast_analysis_tool.podcasts_by_language (
	    language text,
	    feed_url text,
	    api text,
	    api_genre_ids list<text>,
	    api_id text,
	    api_url text,
	    author text,
	    country text,
	    description text,
	    episode_count int,
	    explicit boolean,
	    found_by_queries set<frozen<search_query>>,
	    genres list<text>,
	    image_url100 text,
	    image_url30 text,
	    image_url60 text,
	    image_url600 text,
	    name text,
	    owner text,
	    owner_email text,
	    primary_genre text,
	    release_date timestamp,
	    subtitle text,
	    summary text,
	    updated_at timestamp,
	    webmaster text,
	    website_url text,
	    PRIMARY KEY (language, feed_url)
	) WITH CLUSTERING ORDER BY (feed_url ASC)
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
	
	CREATE TABLE podcast_analysis_tool.search_queries_by_term (
	    term text,
	    external_api text,
	    search_type text,
	    podcast_count int,
	    result_json text,
	    updated_at timestamp,
	    PRIMARY KEY (term, external_api, search_type)
	) WITH CLUSTERING ORDER BY (external_api ASC, search_type ASC)
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
