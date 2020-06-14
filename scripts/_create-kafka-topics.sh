#!/bin/bash -eux

# NOTE will get warning message: "WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both."
# however, this is given when do a period or a dash, or both. Just a warning, so don't worry when doing with period and dash

# https://unix.stackexchange.com/a/53942/216300
export topics="
  queue.podcast-analysis-tool.query-term
  queue.podcast-analysis-tool.search-query-with-results
  queue.podcast-analysis-tool.podcast
  queue.podcast-analysis-tool.episode
"
# not doing these for nwo for simplicity
#queue.podcast-analysis-tool.rss-feed-url
#queue.podcast-analysis-tool.rss-feed

{
  nc -vz localhost 9092
} || { 
  echo "kafka not running, start kafka first" 
  false
} && \

  cd $HOME/kafka_2.12-2.5.0 && \

  # create topics if they don't exist already
  # TODO can just add  --if-not-exists flag...but then Have to use deprecated --zookeeper flag, so who knows
  # would be like this: $HOME/kafka_2.12-2.5.0/bin/kafka-topics.sh --create --zookeeper 127.0.0.1:2181 --replication-factor 1 --partitions 1 --topic streams-plaintext-input --if-not-exists
  echo "now creating topics" && \

  for topic in $topics
  do
    {
      bin/kafka-topics.sh --list --bootstrap-server localhost:9092 | grep $topic &> /dev/null
      if [ $? == 0 ]; then
        echo "skipping topic $topic, already made"
      else
        echo "creating topic $topic for project" && \
        bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic $topic
      fi
    }
  done && \

  # make sure it ran
  echo "What topics do we have now:" && \
  bin/kafka-topics.sh --list --bootstrap-server localhost:9092
