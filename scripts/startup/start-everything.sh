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
JUST_STARTED_CASSANDRA=false
$HOME/dse-6.8.0/bin/nodetool status && 
  echo "cassandra already running, so don't start cassandra" || {
    echo "starting cassandra"
    JUST_STARTED_CASSANDRA=true
    bash $HOME/dse-6.8.0/bin/dse cassandra -k -s
} && \


cd $HOME/projects/java-podcast-processor && \
# start zookeeper with some default configs
# create topic(s) for this project
# then start kafka server
# hopefully will error out if anything doesn't run correctly
# TODO add handling so can be ran multiple times and handles if something has already been started and NOT error out, just continue through the script
echo "running _start-kafka-server.sh script" && \
bash ./scripts/startup/_start-kafka-server.sh && \

echo "now packaging java packages" && \
mvn clean package && \
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
    sleep 120s
  fi

  # returns true if: nodetool status ran without erroring and there is substring 'UN' in the output.
  
  # if above returns false, will try again
done && \
  echo "run the jar" && \

  # mvn exec:exec
  echo "not running jar now, since we're building several. Just be satisifed with the build"

