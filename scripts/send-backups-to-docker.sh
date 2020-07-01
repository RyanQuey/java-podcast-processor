# note that this is not for dsbulk, since dsbulk just works on the exposed cassandra port. 
# So as long as C* is running, no need to use this for dsbulk

SERVER_CONTAINER_NAME=example_compose_yamls_seed_node_1
docker cp ./db-backups/episodes_by_podcast.csv $SERVER_CONTAINER_NAME:/episodes_by_podcast.csv
docker cp ./db-backups/podcasts_by_language.csv $SERVER_CONTAINER_NAME:/podcasts_by_language.csv
docker cp ./db-backups/search_results_by_term.csv $SERVER_CONTAINER_NAME:/search_results_by_term.csv
docker exec -it example_compose_yamls_seed_node_1 cqlsh
