[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/RyanQuey/java-podcast-processor) 

# UPDATE: use docker-compose instead. Just run `./scripts/startup/start-every-compose.sh`
It should run literally everything

-------------------------
# Package:

`mvn package`

# Run

## UPDATE
* before, had to use `mvn exec:exec` instead of `java -cp target/podcast-analyzer-0.1.0.jar Main`, for whatever reason it wasn't finding the packages correctly when running, even though it was working fine when packaging. See issue [here](https://stackoverflow.com/questions/37960551/caused-by-java-lang-classnotfoundexception-org-apache-commons-io-fileutils/37960658#comment109230841_37960658).
* However, now that we're bundling all of dependencies into the jar, can use `java-jar...` command

## Run it all (except currently, not the jars themselves), skipping things that are already running
```sh
bash ./scripts/start-everything.sh


### Want to run things with more granularity? Start external services
```sh
# start cassandra DSE
bash $HOME/dse-6.8.0/bin/dse cassandra -k -s

# start kafka server and create topics
bash ./scripts/_start-kafka.sh
```

### Then start the jars
In three separate terminals:

This one responds to terms coming in, and runs searches with all search types
* `java -jar target/run-search-per-term-0.3.0.jar`

For each search ran, grab all podcasts
* `java -jar target/extract-podcasts-per-search-0.3.0.jar`

For all podcasts, persist them and extract out all episodes to send to another topic
* `java -jar target/extract-episodes-per-podcasts-0.3.0.jar `

### Then, start sending terms
* `bash scripts/kafka-testers/start-search-term-producer.sh`

### want to debug, and see the raw strings of other topics? 
Take a look at other scripts in `scripts/kafka-testers/`


# Implementation decisions
## Several main classes
Rather than doing [multiple modules](https://maven.apache.org/guides/mini/guide-multiple-modules.html), it seems better in this case to do just one module, since we want multiple jars but (at least for now) each jar will potentially be reusing the same old classes (e.g., all will use Podcast.java, etc).

So instead, will just run `mvn package` multiple times, one for each main class.

### The Main main java class: Main.java
* There are several "main" methods that can be ran, but the one in Main.java is the *main* main...
  - There is probably a better way that doing this, but this is fine for now
* This main main method just gets runs a search, extracts relevant podcasts, and then processes the podcasts, extracting episodes from rss feeds. Does not interact with kafka
* Make sure to wait for cassandra AND kafka to start up. `nodetool status` will error out until cassandra is up and running (at least on a local machine...will have to figure something else out for prod)
`bin/nodetool status && mvn clean package && mvn exec:exec` 

or 

`bin/nodetool status && mvn clean package && java -jar ./target/podcast-analysis-tool-0.2.0.jar` 

(both commands should do the same thing)


#### Options
- Do not perform a search for podcasts:
Change default args to have `--perform-search=false`

- Perform a search for podcasts and then run:
Change default args to have `--perform-search=true`

- process all the search results ever found
Change default args to have `--process=all`

- process only the new search results retrieved in this last run
Change default args to have `--process=new`
*TODO* should persist something on a SearchQuery record that says if we've processed it or not. Much more reliable

- Process our default query only ("podcast-data/artist_big-data.json"). Mostly for testing:
Change default args to have `--process=default-query`

#### Good defaults:
`--process=new`
`--perform-search=true`



## Next TODOs
- Change it around so that it runs iteratively on an Airflow job
- Once Search result is retrieved and persisted, have a hook that sends to Kafka stream. 
- Have a Spark job consume that Kafka topic and process each podcast to retrieve and persist all episodes
- Set things up so I can find podcasts that I really want
- Add visualizations
