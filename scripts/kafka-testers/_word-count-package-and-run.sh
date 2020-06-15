#!/bin/bash -eux

# consumes what our kafka streams word count stuff produces
echo "Starting word count kafka streams"
cd $HOME/projects/kafka-for-podcast-tool/streams.examples/
mvn clean package
mvn exec:java -Dexec.mainClass=myapps.WordCount
