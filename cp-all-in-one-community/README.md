![image](../images/kafka.png)

# How to use

## Setup
Don't call directly, or it won't be connected over docker-compose to the rest of the podcast tool.
Just use the startup scripts in the main project folder

## Interact with the containers

### create topic
```sh
docker exec kafka-broker kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic my-test-topic
```
Note that I'm not using `docker-compose exec ...`, but `docker exec ...`, contrary to what the official Confluent documents say. This seems to make it easier to specify our kafka-broker container name for our setup.

### Since the docker zookeeper port is exposed, we can also interact using other zookeeper tools if we have them on our host computer:

```sh
$KAFKA_HOME/bin/zookeeper-shell.sh localhost:2181 ls /brokers/topics

```
 result something like:

```
WatchedEvent state:SyncConnected type:None path:null
[__confluent.support.metrics, __consumer_offsets, _confluent-ksql-default__command_topic, _schemas, docker-connect-configs, docker-connect-offsets, docker-connect-status, queue.podcast-analysis-tool.episode, queue.podcast-analysis-tool.podcast, queue.podcast-analysis-tool.query-term, queue.podcast-analysis-tool.search-results-json, my-test-topic]
```

### Add some sample data
Following this [demo](https://docs.confluent.io/current/quickstart/cos-docker-quickstart.html#step-3-install-a-ak-connector-and-generate-sample-data)
NOTE their example is wrong as of 7/10/20, I reported it though. It should have the -H right before the headers as it does here:

Also note that this is totally unrelated to the podcast tool

1) Create topics "users" and "pageviews"

2) 
```sh
curl -L -O -H 'Accept: application/vnd.github.v3.raw' https://api.github.com/repos/confluentinc/kafka-connect-datagen/contents/config/connector_pageviews_cos.config
curl -X POST -H 'Content-Type: application/json' --data @connector_pageviews_cos.config http://localhost:8083/connectors
```

Should return a little chart showing percent downloaded etc (normal for curl GET requests) for the first, and this for the second: 

```
{"name":"datagen-pageviews","config":{"connector.class":"io.confluent.kafka.connect.datagen.DatagenConnector","key.converter":"org.apache.kafka.connect.storage.StringConverter","kafka.topic":"pageviews","quickstart":"pageviews","max.interval":"100","iterations":"10000000","tasks.max":"1","name":"datagen-pageviews"},"tasks":[],"type":"source"}
```

3) Now again for topic "users"
```sh
curl -L -H -O 'Accept: application/vnd.github.v3.raw' https://api.github.com/repos/confluentinc/kafka-connect-datagen/contents/config/connector_users_cos.config
curl -X POST -H 'Content-Type: application/json' --data @connector_users_cos.config http://localhost:8083/connectors
```

### connect to ksql cli
Following [this](https://docs.confluent.io/current/quickstart/cos-docker-quickstart.html#step-4-create-and-write-to-a-stream-and-table-using-ksqldb)

```sh
docker exec -it ksqldb-cli ksql http://ksqldb-server:8088
```
If you get something like this, check your docker-compose and make sure that the container names are setup correctly:
```
Remote server at http://ksql-server:8088 does not appear to be a valid KSQL
server. Please ensure that the URL provided is for an active KSQL server.
```

#### in the ksql server:
Create a stream
```
CREATE STREAM pageviews (viewtime BIGINT, userid VARCHAR, pageid VARCHAR) \
WITH (KAFKA_TOPIC='pageviews', VALUE_FORMAT='AVRO');
```

Check your stream
```
SHOW STREAMS;
```


# Docs from Confluent:

[Confluent Community Docker Image Quick Start](https://docs.confluent.io/current/quickstart/cos-docker-quickstart.html)
Make sure not to use their "Confluent Platform" one, unless you change the set up of course to use confluent enterprise platform instead

## Overview

This [docker-compose.yml](docker-compose.yml) launches only the community services in Confluent Platform and runs them in containers in your local host, enabling you to build your own development environments (see [cp-all-in-one](../cp-all-in-one/docker-compose.yml) for launching all services in Confluent Platform).
For an example of how to use this Docker setup, refer to the [Confluent Platform quickstart](https://docs.confluent.io/current/quickstart/index.html?utm_source=github&utm_medium=demo&utm_campaign=ch.cp-all-in-one_type.community_content.cp-all-in-one-community)


## Additional Examples

For additional examples that showcase streaming applications within an event streaming platform, see [these demos](https://github.com/confluentinc/examples).


