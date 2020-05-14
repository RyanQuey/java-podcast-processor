package dataClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.lang.System;
import java.lang.Exception;
import java.lang.Thread;
import java.io.File;
import org.json.JSONObject;

import java.io.IOException; 
import java.lang.InterruptedException;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

// local imports
import helpers.HttpReq;
import helpers.FileHelpers;
import helpers.CassandraDb;

public class PodcastSearch {

  // if true, won't persist data corersponding to a given file to db more than one time, unless a new search is ran for that file
  // later will probably remove so can see changes over time
  private boolean persistEachFileOnce = true;
  private String persistMethod = "both"; // could also be "write-to-file" or "db"
  static private CassandraDb db;

  private int totalCounter = 0;
  private int byMinuteCounter = 0;
  private long start;

  private String[] searchTerms = {
    "data engineering",
    "big data",
    "streaming architecture",
    "apache kafka",
    "kafka",
    // isn't returning anything...
    "apache cassandra",
    "cassandra db",
    // isn't returning anything...
    "apache spark",
    "spark data",
    // isn't returning anything...
    "apache hadoop",
    "hadoop",
    "hadoop infrastructure",
    "data lakes",
    "data warehouses",
    "hadoop ecosystem",
    "apache flume",
    "apache hbase",
    "apache hadoop yarn",
    "apache avro",
    "avro",
    "apache storm",
    "apache samza",
    "mapreduce",
    "distributed file systems", 
    "distributed systems",
    "apache hive",
    "zookeeper",
    "airflow",
    "apache airflow",

    "gremlin graph", 
    "tinkerpop", 
    "apache tinkerpop", 
    "graphDb", 
    "Netflix OSS", 

    "elasticsearch",
    "logstash",
    "kibana",
    "lucene",
    "apache lucene",
    "apache solr",
    "solr",


    "microservices",
    "docker",
    "kubernetes",
    "containerization",
    "hashicorp",
    "vagrant",
    "hashicorp vagrant",
    "packer",
    "hashicorp packer",

    "hortonworks",
    "mapr",
    "mapr data platform",
    "cloudera",
    "new relic",

    "datastax",
    "confluent",

    "machine learning",
    "data science",
    "tensorflow",

    "aws",
    "amazon web services",
    "aws dynamodb",

    "microsoft azure",
    "google cloud platform",
    "cloud services",
    "digital ocean",

    "full stack development",
    "software engineering",
    "backend engineering",
    "devops",
  };

  private String[] searchTypes = {
    // empty for getting default, which I believe searches more generally (?) or maybe all terms
    "all",
    "titleTerm", 
    "keywordsTerm", 
    "descriptionTerm",
    "artistTerm"
  };

  public ArrayList<QueryResults> results = new ArrayList<QueryResults>();

  private void incrementApiHitCounter () {
    totalCounter ++;
    byMinuteCounter ++;
    if (byMinuteCounter > 18) {
      // sleep one minute so we don't hit quotas (supposed to be 20/min)
      // ...but I had a timer and it still hit a 403, but several minutes passed where I was under 20 and ok. But then waited 5 minutes, and did 5 or so more, and it hit quota again. So I'm guessing there's other quotas also
      byMinuteCounter = 0;
      try {
        long timePassed = System.currentTimeMillis()-start;
        System.out.println("time passed in ms = "+(System.currentTimeMillis()-start));

        if (timePassed < 60*1000) {
          long sleepTime = 60*1000 - timePassed;
          Thread.sleep(sleepTime);
        }

      } catch (InterruptedException e) {
        System.out.println(e);

      };

    } else if (totalCounter > 100) {
      // just a shot in the dark, but let's not hit more than 100 times per run
    };
  }
  // TODO refactor, separate out  and put a lot into the QueryResults class
  // TODO maintain references to files made in this search
  // TODO refactor: remove args from here, and just set as variable in the caller if we want to call that
  public void performAllQueries(String[] args){
    boolean refreshData = false;
    for (String s: args) {
      if (s == "refresh-data") {
        refreshData = true;
      }
    };
    
    // TODO find related search queries manually...or even Google APIs? Could make this part of the whole thing

    //for each term, send as several different types of terms 
    System.out.println("Starting queries");

    start = System.currentTimeMillis();

    for (String term : searchTerms) {
      for (String searchType : searchTypes) {
        // don't want to throw errors for these
        QueryResults queryResult = new QueryResults(term, searchType, refreshData);
        try {
          queryResult.getPodcastJson(refreshData);
        } catch (IOException e) {
          System.out.println("Skipping queryResult: " + term + " for type: " + searchType + "due to error");
          // should log already before this
          continue;
        }

        // right now, persisting no matter what. Even if we read from file, if persist-method is writing to file, will write again. Even if we read from db, if persist-method is reading to db, write again.
        // TODO remove that redundancy mentioned above (?);
        System.out.println("Persisting json to " + persistMethod);
        queryResult.persistSearchResult(persistMethod, persistEachFileOnce);
        results.add(queryResult);

        if (queryResult.madeApiCall) {
          incrementApiHitCounter(); 
        }

        System.out.println("******************************************");
        System.out.println("Total retrieved so far for this run: " + totalCounter);
      };
    };

    System.out.println("finished finding podcasts");     
  }
}


