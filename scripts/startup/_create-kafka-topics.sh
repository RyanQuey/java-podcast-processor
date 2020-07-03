#!/bin/bash -eux

# NOTE will get warning message: "WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both."
# however, this is given when do a period or a dash, or both. Just a warning, so don't worry when doing with period and dash

# https://unix.stackexchange.com/a/53942/216300
export topics="
  queue.podcast-analysis-tool.query-term
  queue.podcast-analysis-tool.search-results-json
  queue.podcast-analysis-tool.podcast
  queue.podcast-analysis-tool.episode
"
# not doing these for nwo for simplicity
#queue.podcast-analysis-tool.rss-feed-url
#queue.podcast-analysis-tool.rss-feed

KAFKA_IS_UP=true
test_if_kafka_ready="nc -vz localhost 9092 && docker exec java-podcast-processor_zookeeper_1 bash bin/zkCli.sh localhost:2181 ls /brokers/ids"
$($test_if_kafka_ready) || {
  KAFKA_IS_UP=false

  while [[ $KAFKA_IS_UP == false ]]; do
    echo "Kafka is not up yet, waiting..."
    sleep 1s
    # keep running until last command in loop returns true

    # TODO confirm that if kafka can't accept messages, this returns false
    $($test_if_kafka_ready) && KAFKA_IS_UP=true
  done
  echo "Kafka is up!"
}

  # create topics if they don't exist already
  # TODO can just add  --if-not-exists flag...but then Have to use deprecated --zookeeper flag, so who knows
  echo "now creating topics" && \
  for topic in $topics
  do
    {
      docker exec kafka-broker kafka-topics \
        --list --bootstrap-server localhost:9092 | grep $topic &> /dev/null
      if [ $? == 0 ]; then
        echo "skipping topic $topic, already made"
      else
        echo "creating topic $topic for project" && \
        # https://docs.confluent.io/current/quickstart/cos-docker-quickstart.html#step-2-create-ak-topics
        docker exec kafka-broker kafka-topics \
          --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic $topic
      fi
    }
  done && \

  # make sure it ran
  echo "What topics do we have now:" && \
  docker exec kafka-broker kafka-topics --list --bootstrap-server localhost:9092
