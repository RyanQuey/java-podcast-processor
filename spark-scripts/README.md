

## How to start

In the `./spark-scripts` dir:

```
sbt package
$HOME/lib/dse-6.8.0/bin/dse spark-submit \
  --class "SimpleApp" \
  --master local[4] \
  target/scala-2.11/spark-scripts-for-podcast-analysis-tool_2.11-0.3.0.jar
```

But change the class to whichever class is your main class. 

Or since we made a quick script, which set the latest main class for us:  
```
bash submit-script.sh

# OR

sbt package && bash submit-script.sh
```


## Setup For Development
### Install Scala
- Using [sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html) rather than IntelliJ
- We want Scala 2.11 since that is what current DSE 6.8 uses and what we have setup in Zeppelin, etc

```
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt

### What we did to get everything setup (for record keeping):
touch spark-scripts/build.sbt
sbt
```

#### Now in the sbt console:
Set Scala version, and save to build.sbt
```
set ThisBuild / scalaVersion := "2.11.12"
session save
```

Turn on auto Compile (builds our directory tree)
```
~compile
```

#### Now in bash
Make directory tree, and better setup, and make our main file
```
mkdir -p src/main/scala/com/ryanquey/podcast
vim src/main/scala/com/ryanquey/podcast/Main.scala
# and then, edit it as we need

```

Then setup up build.sbt to [add dependencies](https://www.scala-sbt.org/1.x/docs/sbt-by-example.html#Add+a+library+dependency) etc
[Spark's tutorial for sbt quickstart](https://spark.apache.org/docs/2.4.0/quick-start.html#self-contained-applications) is helpful too.

Make sure to run `reload` in sbt console

### ALTERNATIVELY
If it's not working well, try Scala with maven
https://docs.scala-lang.org/tutorials/scala-with-maven.html

I started a pom.xml file already, didn't get very far but it's kept under pom.xml.bkup in this folder

It would be nice to be able to use SBT though since it is more scala-native and also uses scala-style (such as loading dependencies how Zeppelin or spark-submit does)

### Using Spark standalone
In the long run, this is what I want to use, since I switched over to using Elassandra
TODO
- add Spark docker image and link using docker-compose
- Setup spark cassandra connector

### Using DSE

#### if you want to connect to Kafka
$HOME/lib/dse-6.8.0/bin/dse spark  --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0

## Notes: 
### What are all these classes?
It is a learning project, so I some old examples of things that work as we moved up in complexity. 

`SparkKafkaStreamingAvgTimeDiff` is the only real class that matters for podcast app.

In order of complexity (and what I built from first to last):
  1)    SimpleApp.scala
  2)    SparkStreamingTest.scala
  3)    SparkKafkaStreamingTest.scala
  4)    SparkAggKafkaStreamingTest.scala
  5)    SparkKafkaStreamingAvgTimeDiffTest.scala
  6)    SparkKafkaStreamingAvgTimeDiff.scala

### Our setup
Initialized using:

```
mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DgroupId=com.ryanquey.podcast \
  -DartifactId=spark-scripts
```

Followed instructions from Cloudera [here](https://docs.cloudera.com/documentation/enterprise/5-5-x/topics/spark_develop_run.html)
Also [official Spark docs](https://spark.apache.org/docs/2.4.0/submitting-applications.html). But they are not very helpful for actually describing how to layout your spark scala code. (Must be assumed you know scala haha) 
