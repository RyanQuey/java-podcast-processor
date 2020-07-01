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

# base image for the jars
built_image=false
if [ ! -f $project_root_path/ryanquey-java-workers-latest.jar ]; then
  echo "ryanquey/java-workers File not found! Building image"
	docker build -f $JAVA_WORKERS_DIR/Dockerfile.base -t "ryanquey/java-workers" $JAVA_WORKERS_DIR && \
	built_image=true
elif
  echo "ryanquey/java-workers jar File found! importing image"
	docker image load -i  ${project_root_path:-.}/ryanquey-java-workers-latest.jar
fi

docker-compose \
  -f $project_root_path/elassandra-docker-compose.yml \
  -f $FLASK_DIR/docker-compose.yml \
  -f $JAVA_WORKERS_DIR/kafka-docker-compose.yml \
  up -d && \
  # rebuild all elasticsearch indices
  # TODO try using docker exec nodetool status | grep -q 'UN' && CASSANDRA_IS_UP=true instead
  echo "waiting 60s for it to come up...(TODO ping server to know when it's ready rather than set time)" && \
  sleep 60s && \
  bash $JAVA_WORKERS_DIR/src/main/resources/create_es_indices/rebuild_all_indices.sh && \

  echo "SUCCESS!"
	if [ built_image ]; then
		echo "Saving newly built image to jar: "
		docker image save ryanquey/java-workers:latest -o $project_root_path/ryanquey-java-workers-latest.jar
	fi

  # TODO add zeppelin






false && (
echo "running _start-kafka-server.sh script" && \
bash ./scripts/startup/_start-kafka-server.sh

# TODO add a cli arg that can package too
# don't want to package right now, just run!
# echo "now packaging java packages" && \
# mvn clean package && \
CASSANDRA_IS_UP=false
while [[ $CASSANDRA_IS_UP == false ]]; do
  # keep running until last command in loop returns true

  $HOME/dse-6.8.0/bin/nodetool status | grep -q 'UN' && CASSANDRA_IS_UP=true
  if [[ $CASSANDRA_IS_UP == false ]]; then
    # TODO add a timeout or handle if cassandra is down
  	echo "Cassandra is not up yet, waiting and try again"
  	sleep 1s
  elif [[ $JUST_STARTED_CASSANDRA == true ]]; then
  	echo "Cassandra is up, but just started and even when getting UN for status, not yet ready to connect. So waiting a bit first anyways"
    # sleep 2 minutes anyways...
		# TODO test how long we need
		# last time was at least a minute after all this ran.
    # TODO add this back in if running something in particular that requires C* to be up 
    # sleep 120s
  fi

  # returns true if: nodetool status ran without erroring and there is substring 'UN' in the output.
  
  # if above returns false, will try again
done && \

  # mvn exec:exec
  echo "not running jar now, since we're building several. Just be satisifed with the build" && \
  echo "start zeppelin" && \
  $HOME/dse-6.8.0/bin/dse exec ~/zeppelin/bin/zeppelin.sh
)
