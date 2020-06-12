#!/bin/bash -eux
# based on:
# https://kafka.apache.org/quickstart


cd $HOME/kafka_2.12-2.5.0 && \
  # start zookeeper unless it's already running
  {
    bin/zookeeper-shell.sh localhost:2181 ls /brokers/ids
  } || {
    # start zookeeper with some default configs
    echo "starting zookeeper daemon"
    bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
    # NOTE this does not error if fails... TODO
  } && \

# then start kafka server unless it's already going
  { 
    nc -vz localhost 9092 && 
    echo "kafka server (broker) running, so no need started again"
    true
  } || {
    # could not connect to kafka server, so start it
    echo "starting kafka server daemon"
    bin/kafka-server-start.sh -daemon config/server.properties
  } && \

  # then create topics if they don't exist already
  # create topic(s) for this project
  bash $HOME/projects/java-podcast-processor/scripts/_create-kafka-topics.sh
