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

Produced by: currently, just a simple CLI kafka producer
Consumed by: RunSearchPerTermConsumer class
- The RunSearchPerTermConsumer class hits an external podcast API once for each search type.

####  "queue.podcast-analysis-tool.search-results-json"
Value: Results of the search of "query-term", which we use to extract podcasts from. 

Note that for each term, we will run about five of these searches, and so produce about five events for this topic (I forget the exact number, but it is one for each search type)

Produced by: RunSearchPerTermConsumer class
Consumed by: ExtractPodcastsPerSearchConsumer class
- Does no CRUD. Simply reads the JSON, extracting out all podcasts from the search result json retrieved from the external API (if any were found for this query term/search type combination)

####  "queue.podcast-analysis-tool.podcast"
Value: A single podcast extracted from "search-results-json"

Produced by: ExtractPodcastsPerSearchConsumer class
Consumed by: ExtractEpisodesPerPodcastConsumer class
- For each podcast, fetches the RSS feed associated with the podcast (assuming an RSS feed was returned as part of the podcast data received back from the external API), and then parses that RSS and extracts out each episode mentioned in the RSS feed. Persists both the podcast and all the episodes

####  "queue.podcast-analysis-tool.episode"
Value: A single episode pulled from RSS feed of a podcast

Produced by: ExtractEpisodesPerPodcastConsumer class
Consumed by: (None yet, as of 7/2020)

####  "queue.podcast-analysis-tool.test"
Just a test topic, especially for Spark

####  "queue.podcast-analysis-tool.test-reaction"
Just a test topic, especially for Spark. Used for timing between the "test" topic and then this topic, to build a spark job that compares time between the two events.

## Data Classes ("`dataClasses`")

### General Concept
These more or less take the place of a model in the MVC. As a whole, they represent the business logic for a given resource. 

### Java Classes
Each "data class" has the following java classes with it (or should have once everything is finished):
- a "base" class, which other classes can inherit from (e.g., `PodcastBase`). 
    * This specifies the resource's fields and has getters and setters for those fields
    * Should only have fields that are going to be persisted in the db (otherwise, our DAO class won't work, since it inherits from the base)
    * Other classes 

- a "primary" class, using just the name (e.g., `Podcast`)
    * Adds methods and fields that are helpful for interacting with this resource, but that should not get persisted
    * Add helper methods here, and other kinds of wrappers for interacting with this resource
    * Interact with related/associated records here (e.g., `podcasts` will interact with `episodes`).
    * In general, should do most of our direct interaction with the resource using the "primary" class.

For every DB table, should also have: 
- a "Record" class
    * This stands between the base class and the DAO class, AND between the primary and DAO class, as an abstraction
    * As an abstraction between between the base and DAO class: 
        - For for Cassandra for example, this allows us to have something to set primary and clustering keys for this given table, so we don't have to set it on the base class (which is particularly necessary if we have multiple tables for this resource)
        - has a `getDao ()` method to fetch the "DAO" class for this "record" class

    * As an abstraction between the primary and DAO class:
        - Means that if we want to persist to all tables at once (a common scenario for a denormalized schema), we can do so easily by calling the `persist ()` method on the "primary" class instance (e.g., `podcast.persist();`. This will in turn call the `save` method for all the "record" classes of this resource. 
        - Consistently define a few simple apis to do CRUD, e.g., `save` to create/update (Cassandra makes it easy to call one method and create if record exists, or update if it doesn't). `findOne` for grabbing a single record using its entire primary key. And of course, `delete`, etc (NOTE not all are implemented on each "record" class yet, but it's a TODO. Will add as needed).
        - Alternatively, if we want to call something other than `save` on the record class, we can easily change the definition in the "primary" class and everything still works wherever `persist` is called on the "primary" class. This allows for us to use the "primary" class as an easy to use wrapper around all the "record" classes, since we can call the same thing no matter what and just change behavior by adjusting a few simple, consistent methods found on the "primary" class. (see `Podcast` class for example; we want to not just save the PodcastByLanguageRecord instance, we also want to append the `foundBy` field to existing `foundByQueries` data, so we call `podcastByLanguageRecord.saveAndAppendFoundBy()` instead. 
        - Each record class should also have a constructor that takes the "primary" class as an argument and returns an instance of this "record" class, so you can easily do something like `new PodcastByLanguageRecord(podcast);`. 

- a "DAO" class
    * Directly interacts with the DB java driver for this resource. 
    * Especially for Cassandra where data is often going to be duplicated (i.e, denormalized), is necessary to have separate subclasses for each table

### Evaluation of this system
Given that I have a limited amount of exposure to other systems, I tend to find this to be very effective, at the very least when interacting with Cassandra Java Driver. I would imagine however that this is also very effective for other setups as well. 

Having a base like this maintains continuity and a clear place to look for what fields in a resource gets persisted (i.e., what is on the "base" class) and what is temporary (ie what is on the primary class)


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
