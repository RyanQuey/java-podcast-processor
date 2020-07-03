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
project_root_path=$parent_path/../../
export FLASK_DIR="$project_root_path/flask_server"
export JAVA_WORKERS_DIR="$project_root_path/java-workers"

docker-compose \
  -f $project_root_path/elassandra-docker-compose.yml \
  -f $project_root_path/cp-all-in-one-community/docker-compose.yml \
  -f $FLASK_DIR/docker-compose.yml \
  -f $JAVA_WORKERS_DIR/docker-compose.yml \
  stop && \
  # rebuild all elasticsearch indices
  echo "ALL STOPPED!"

