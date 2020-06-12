#!/bin/bash -eux
# for try/catch stuff, see here https://stackoverflow.com/a/25180186/6952495


# want to start this in a daemon, and asynchronously, since it takes a while.
# just make sure not to run the main jar file until Cassandra is ready
# TODO suppress logs in this console
# should only run if 
{ 
  $HOME/dse-6.8.0/bin/nodetool status && \
    echo "cassandra already running, so don't start cassandra"

  # suppress the error and return false, so can go to the "catch" block
  set +e
} || {
  echo "starting cassandra " && \
    bash $HOME/dse-6.8.0/bin/dse cassandra -k -s
} && \


cd $HOME/projects/java-podcast-processor && \
# start zookeeper with some default configs
# create topic(s) for this project
# then start kafka server
# hopefully will error out if anything doesn't run correctly
# TODO add handling so can be ran multiple times and handles if something has already been started and NOT error out, just continue through the script
echo "running _start-kafka-server.sh script" && \
bash ./scripts/_start-kafka-server.sh && \

echo "now packaging java packages" && \
mvn clean package && \
while [ $? == 1 ]; do
  # keep running until last command in loop returns true

  { # try
    $HOME/dse-6.8.0/bin/nodetool status | grep 'UN' &> /dev/null
    # returning true means there was no error, and Cassandra is running

    # suppress the error and return false, so can go to the catch block
    set +e

	} || { # catch
    # TODO add a timeout or handle if cassandra is down
  	echo "Cassandra is  not up yet, waiting and try again"
		# false will make $? return 1
		false
	}

  # returns true if: nodetool status ran without erroring and there is substring 'UN' in the output.
  
  # if above returns false, will try again
done && \
  echo "run the jar" && \

  mvn exec:exec

