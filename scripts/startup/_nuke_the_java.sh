# forces everything to be rebuilt from scratch, not reusing even the docker image we created
# should be generally safe to do, since our java jars are stateless. Just takes longer since we have to rebuild everything

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
PROJECT_ROOT_PATH=$parent_path/../..
JAVA_WORKERS_DIR="$PROJECT_ROOT_PATH/java-workers"

# destroy the local target folder (calling `mvn package` later will rebuild this)
rm -rf $JAVA_WORKERS_DIR/target/

# stop everything (gently) so we can remove the containers (and helps make sure the data doesn't get corrupted)
bash $parent_path/stop-every-compose.sh && {
  # remove the containers
  docker rm java-podcast-processor_run-search-per-term_1
  docker rm java-podcast-processor_extract-podcasts-per-search_1
  docker rm java-podcast-processor_extract-episodes-per-podcast_1
} && \
  # remove the image
  docker image rm ryanquey/java-workers



# to rebuild, run `./start-every-compose.sh rebuild`
# (though should rebuild even without saying "rebuild" I think)
