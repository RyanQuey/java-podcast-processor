# java-podcast-processor
Personal tool to grab podcast data related to several topics I'm interested in, store them, and process using [Airflow](https://github.com/apache/airflow), [Kafka](https://kafka.apache.org/), [Spark](https://spark.apache.org/), and [Cassandra](https://cassandra.apache.org/). The particular Cassandra distribution used is [Elassandra](https://www.elassandra.io/), which allows seamless integration with [Elasticsearch](https://www.elastic.co/). 

![image](https://github.com/RyanQuey/java-podcast-processor/raw/master/screenshots/wh_Podcast%20Analysis%20Tool.png)

[Workers](https://github.com/RyanQuey/java-podcast-processor/tree/master/java-workers) are divided into separate Main classes in a single Java jar and consume and produce to Kafka, in order to distribute the workload across the cluster. Everything is built on top of [Docker containers](https://www.docker.com/) and linked together using docker-compose.

Results displayed using a [searchkit](https://github.com/searchkit/searchkit) interface over [React](https://reactjs.org/) (built using [Gatsby](https://www.gatsbyjs.org/)), served by a Python [Flask app](https://flask.palletsprojects.com/). 

See README files in subdirectories for how to setup and use this tool (though some are out of date).

## Related Links
  * For the related Zeppelin notebooks, see [here](https://github.com/RyanQuey/dse-zeppelin-notebooks). 
  * For Airflow DAGs source code, see [here](https://github.com/RyanQuey/airflow-with-podcasts).
  * I recently presented on Kafka with Cassandra for the [Cassandra & DataStax DC Meetup](https://www.meetup.com/Cassandra-DataStax-DC/) using this project for their Cassandra Lunch. The presentation was featured on [Anant Corporation's blog](https://blog.anant.us/cassandra-lunch-18-connecting-cassandra-to-kafka/). 
  
  <div><a href="https://youtu.be/g9ITZ_qyezs" target="_blank"><img src="https://raw.githubusercontent.com/RyanQuey/java-podcast-processor/master/screenshots/lunch18-presentation-screenshot.png" /></a></div>

## Start Everything
- Start everything with: `./scripts/startup/start-every-compose.sh`
    * Starts up all services, connecting them using chained docker-compose files.
    * Runs shell script to make sure Cassandra and Kafka are successfully started
    * Once C* and Kafka are up, runs initializers (e.g., creating kafka topics and ES indices for Elassandra)

- Open up React Gatsby project (serving searchkit) via flask app at http://localhost:5000/search


## Features
### Results Displayed Using Searchkit
![image](https://github.com/RyanQuey/java-podcast-processor/raw/master/screenshots/searchkit-podcasts-sample-search.png)

## Setup
- install Docker compose
- Start everything with: `./scripts/startup/start-every-compose.sh`
- View from Zeppelin using [these Zeppelin notebooks](https://github.com/RyanQuey/dse-zeppelin-notebooks).

## Development
- If made changes to java code and want to rebuild what docker is running, run

    `./scripts/startup/start-every-compose.sh rebuild`

## Debugging
- Can get logs from containers by using `docker logs`:
    * Get logs for running searches: `docker logs -f java-podcast-processor_run-search-per-term_1`
    * Get logs for RSS feeds: `docker logs -f java-podcast-processor_extract-episodes-per-podcast_1`

# Released under MIT License

Copyright (c) 2020 Ryan Quey.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
