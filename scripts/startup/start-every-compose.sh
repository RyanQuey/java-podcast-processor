#!/bin/bash -eux

if [ "$BASH" != "/bin/bash" ]; then
  echo "Please do ./$0"
  exit 1
fi

#########################################
# instructions: 
# start with bash NOT sh. Currently only works in bash
# CUrrent status: doesn't work very well, but if you read it multiple times it works ...:(
#########################################

# for more advanced try/catch stuff, see here https://stackoverflow.com/a/25180186/6952495
# not necessary for now though

# want to start this in a daemon, and asynchronously, since it takes a while.
# just make sure not to run the main jar file until Cassandra is ready
# TODO suppress logs in this console

# always base everything relative to this file to make it simple
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
project_root_path=$parent_path/../..
export FLASK_DIR="$project_root_path/flask_server"
export JAVA_WORKERS_DIR="$project_root_path/java-workers"

# TODO accept cli flag for always rebuilding
# first checks if there is an image exists, in which case use Dockerfile.build_from_base. Otherwise, use Dockerfile.base
# TODO combine those into one, by using args, only difference is specifying the image

# get from docker images, or load base image from the jar, or build again
built_image=false
docker inspect "ryanquey/java-workers:latest" > /dev/null 2>&1 && {
  echo "ryanquey/java-workers:latest image exists! Just use that"

} || {
  echo "ryanquey/java-workers:latest image does not exist! Check for jar"
  if [ ! -f $project_root_path/ryanquey-java-workers-latest.jar ]; then
    echo "no jar for ryanquey/java-workers File not found! Building image"
    echo "ryanquey/java-workers File not found! Building image"
    docker build -f $JAVA_WORKERS_DIR/Dockerfile.base -t "ryanquey/java-workers" $JAVA_WORKERS_DIR && \
    built_image=true
  else
    echo "ryanquey/java-workers jar File found! importing image"
    docker image load -i  ${project_root_path:-.}/ryanquey-java-workers-latest.jar
  fi
} && \
# fire everything up in one docker-compose statement
# Note that if it is in one docker-compose statement like this, it allows the separate services to talk to one another even though they have separate docker-compose yml files
docker-compose \
  -f $project_root_path/elassandra-docker-compose.yml \
  -f $FLASK_DIR/docker-compose.yml \
  -f $JAVA_WORKERS_DIR/kafka-docker-compose.yml \
  up -d && \
  # rebuild all elasticsearch indices
  echo "waiting for Cassandra to be available..." && \
CASSANDRA_IS_UP=false
while [[ $CASSANDRA_IS_UP == false ]]; do
  # keep running until last command in loop returns true

  docker exec java-podcast-processor_seed_node_1 nodetool status | grep -q 'UN' && CASSANDRA_IS_UP=true
  if [[ $CASSANDRA_IS_UP == false ]]; then
    # TODO add a timeout or handle if cassandra is down
  	echo "Cassandra is not up yet, waiting and try again"
  	sleep 1s
  fi

  # returns true if: nodetool status ran without erroring and there is substring 'UN' in the output.
  
  # if above returns false, will try again
done && \
  # refresh the es indices based on json files. Only do it this recklessly in dev
  bash $JAVA_WORKERS_DIR/src/main/resources/create_es_indices/rebuild_all_indices.sh && \

  # no need to do this, now can create via env vars passed to docker-compose for kafka
	# bash _create-kafka-topics.sh && \
  echo "SUCCESS!"
	if [ built_image == true ]; then
		echo "Saving newly built image to jar: "
		docker image save ryanquey/java-workers:latest -o $project_root_path/ryanquey-java-workers-latest.jar
	fi
	# let's go ahead and create the Kafka topics as well

  # TODO add zeppelin?