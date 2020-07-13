# run this if you recently restarted your computer or something
# if you just want to submit, can just run `bash ./submit-script.sh` instead


# not sure which of these I need, but 
# assumes confluentinc docker images are already pulled and built into containers before
printf "== Setting up kafka (using Docker) == \n"
docker start kafka-broker zookeeper connect ksql-datagen ksqldb-cli ksqldb-server rest-proxy schema-registry && \
  printf "\n\n== Packaging using sbt ==\n" && \
  sbt package

