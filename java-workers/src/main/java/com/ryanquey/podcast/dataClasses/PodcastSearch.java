package com.ryanquey.podcast.dataClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.lang.System;
import java.lang.Exception;
import java.lang.Thread;
import org.json.JSONObject;

import java.io.IOException; 
import java.lang.InterruptedException;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.ResultSet;

// local imports
import com.ryanquey.podcast.helpers.HttpReq;
import com.ryanquey.podcast.cassandraHelpers.CassandraDb;
import com.ryanquey.podcast.dataClasses.searchQuery.SearchQuery;

/* 
 * represents an entire set of searches
 * currently not persisting anything from this into the database
 */ 
public class PodcastSearch {

  private static CassandraDb db;

  private int totalCounter = 0;
  private int byMinuteCounter = 0;
  private long start;
  private boolean keepGoing = true;

  public static String[] searchTerms = {
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
    // careful including this...end up getting so many explicit results unrelated to tech...
    //"vagrant",
    "hashicorp vagrant",
    // careful including this...end up getting so many Green Bay packer results...
    //"packer",
    "hashicorp packer",

    "hortonworks",
    "mapr",
    "mapr data platform",
    "cloudera",
    "new relic",

    "datastax",
    "confluent",

    "machine learning",
    "artificial intelligence",
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

    "prosthetics machine learning",
    "prosthetics artificial intelligence",
    "prosthetics",
  };

  // TODO make static
  private String[] searchTypes = {
    // empty for getting default, which I believe searches more generally (?) or maybe all terms
    "all",
    "titleTerm", 
    "keywordsTerm", 
    "descriptionTerm",
    "artistTerm"
  };

  public ArrayList<SearchQuery> searchQueries = new ArrayList<SearchQuery>();


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

    } else if (totalCounter > 35) {
      // just a shot in the dark, but let's not hit more than 50 times per run (once stopped at 62 after not running for a whole day)
      // 35 will keep things under two rounds though, so don't have to do our thread.sleep thing too many times
      // TODO stop looping if get here
      this.keepGoing = false;
    };
  }

  // TODO refactor, separate out  and put a lot into the SearchQuery class
  // TODO maintain references to results received from this search
  // TODO refactor: remove args from here, and just set as variable in the caller if we want to call that
  public void performAllQueries(String[] args) throws Exception {
    boolean refreshData = false;
    for (String s: args) {
      if (s == "refresh-data") {
        System.out.println("***Refreshing all searches, regardless of whether they've been performed recently or not***");
        refreshData = true;
      }
    };
    
    // TODO find related search queries manually...or even Google APIs? Could make this part of the whole thing

    //for each term, send as several different types of terms 
    System.out.println("Starting queries");

    start = System.currentTimeMillis();

    for (String term : PodcastSearch.searchTerms) {
      for (String searchType : searchTypes) {
        // don't want to throw errors for these
        SearchQuery searchQuery = new SearchQuery(term, searchType);
        try {
          // hit the external api, unless search has been done OR refreshData is true
          searchQuery.performSearchIfNecessary(refreshData);
        } catch (Exception e) {
          System.out.println("Skipping searchQuery: " + term + " for type: " + searchType + "due to error");
          // should log error already before this

          // Stop hitting their API if we max out the quota
          // NOTE this conditional is a little bit fragile, if they ever change their message. But works for now TODO
          if (e.toString().equals("java.io.IOException: Server returned HTTP response code: 403 for URL: https://itunes.apple.com/search")) {
            System.out.println("itunes doesn't want us to query anymore, taking a break");     
            return;
            
          } 
          continue;
        }

        if (searchQuery.madeApiCall) {
          searchQuery.persist();
          this.searchQueries.add(searchQuery);
          incrementApiHitCounter(); 
        }

        System.out.println("******************************************");
        System.out.println("Total retrieved so far for this run: " + totalCounter);
        if (!keepGoing) {
          System.out.println("Stopping there for now");

          return;
        }
      };
    };

    System.out.println("finished finding podcasts");     
  }
}


