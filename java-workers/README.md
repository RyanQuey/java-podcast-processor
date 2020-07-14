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
## Kafka Implementation

Can use `../scripts/kafka-testers/*.sh` files to test these out some. It is trivial to 
### Kafka Topics

####  "queue.podcast-analysis-tool.query-term"
Value: A search term that we will pass to our external apis using several different query types (e.g., keyword, author, etc)

####  "queue.podcast-analysis-tool.search-results-json"
Value: Results of the search of "query-term", which we use to extract podcasts from.

####  "queue.podcast-analysis-tool.podcast"
Value: A single podcast extracted from "search-results-json"

Gets persisted and used to grab RSS feed for this podcast to fetch episodes for this podcast.

####  "queue.podcast-analysis-tool.episode"
Value: A single episode pulled from RSS feed of a podcast

####  "queue.podcast-analysis-tool.test"
Just a test topic, especially for Spark

####  "queue.podcast-analysis-tool.test-reaction"
Just a test topic, especially for Spark. Used for timing between the "test" topic and then this topic, to build a spark job that compares time between the two events.

## Several main classes
Rather than doing [multiple modules](https://maven.apache.org/guides/mini/guide-multiple-modules.html), it seems better in this case to do just one module, since we want multiple jars but (at least for now) each jar will potentially be reusing the same old classes (e.g., all will use Podcast.java, etc).

So instead, will just run `mvn package` multiple times, one for each main class.

### The Main main java class: Main.java
* There are several "main" methods that can be ran, but the one in Main.java is the *main* main...
  - There is probably a better way that doing this, but this is fine for now
* This main main method just gets runs a search, extracts relevant podcasts, and then processes the podcasts, extracting episodes from rss feeds. Does not interact with kafka
* Make sure to wait for cassandra AND kafka to start up. `nodetool status` will error out until cassandra is up and running (at least on a local machine...will have to figure something else out for prod)


#### Update 6/2020: Using docker-compose and a try-catch block (with retries) in `KafkaMain.setup();` to automatically make sure everything is running before starting

No need to check for anything outside the jar like running `nodetool status`, can just start the jar as long as Cassandra and Kafka are going to start in a reasonable amount of time after the jar is ran.

`mvn clean package && mvn exec:exec` 

In other words, just using docker-compose will take care of this for you

# Next TODOs
- Set things up so I can find podcasts that I really want
- Add visualizations

# Released under MIT License

Copyright (c) 2020 Ryan Quey.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
