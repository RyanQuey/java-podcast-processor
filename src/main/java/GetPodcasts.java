import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;

// local imports
//import helpers.HttpReq;
//import helpers.FileHelpers;

public class GetPodcasts {

  private static String get (String term, String searchType) {

    try {

      // apparently apple doesn't like the pluses (?)
      String queryTerm = term.replaceAll(" ", " ");
      String urlStr = "https://itunes.apple.com/search";

      Map<String, String> queryParams = new HashMap<>();
      queryParams.put("media", "podcast");
      queryParams.put("term", queryTerm);

      // set limit to 200, let's just get all of it (default: 50, max: 200)
      queryParams.put("limit", "200");
      // set version, language and country in case their defaults change
      queryParams.put("version", "2");
      queryParams.put("lang", "en_us");
      queryParams.put("country", "US");
      if (searchType != "all") {
        queryParams.put("attribute", searchType);
      }
       
      // write params to request...yes it's this crazy.
      // Basically converts our map to a string, then writes that string to the http url connection via "output stream" api. 
      // (is an api for writing data to something, in this case, writing params to the url)

      // begin reading
      String result = HttpReq.get(urlStr, queryParams);

      System.out.println(result);
      // use `with` to prevent leaving file open after code runs
      // TODO
      //term_for_file = re.sub(r"\s+", "-", term)
      /* 
      with open(f"{term}-podcasts.json", "r+") as file:
          file.write(contents)
          file.close()

          */

      // write to json file
      return result;

    } catch (Exception e) {
      System.out.println("Error:");
      System.out.println(e);

      return null;
    }
  }

  public static void main(String[] args){
    boolean refreshData = false;
    for (String s: args) {
      if (s == "refresh-data") {
        refreshData = true;
      }
    };
    
    // TODO find related search queries manually...or even Google APIs? Could make this part of the whole thing
    String[] searchTerms = {
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

    String[] searchTypes = {
      // empty for getting default, which I believe searches more generally (?) or maybe all terms
      "all",
      "titleTerm", 
      "keywordsTerm", 
      "descriptionTerm",
      "artistTerm"
    };


    //for each term, send as several different types of terms 
    int totalCounter = 0;
    int byMinuteCounter = 0;
    long start = System.currentTimeMillis();

    for (String term : searchTerms) {

      for (String searchType : searchTypes) {
        String typePrefix = searchType != "all" ? searchType.replace("Term", "") : "generalSearch" ;
        String filename = typePrefix + "_" + term.replaceAll(" ", "-")  + ".json";

        File f = new File(FileHelpers.getFilePath("podcast-data/" + filename));
        // check if we should skip
        if(!refreshData && f.exists()) { 
          System.out.println("skipping " + filename);
          continue;
        }

        String podcastJSON = get(term, searchType);

        // write to a file 
        FileHelpers.write("podcast-data/" + filename, podcastJSON);


        totalCounter ++;
        byMinuteCounter ++;
        if (byMinuteCounter > 18) {
          // sleep one minute so we don't hit quotas (supposed to be 20/min)
          // ...but I had a timer and it still hit a 403, but several minutes passed where I was under 20 and ok. But then waited 5 minutes, and did 5 or so more, and it hit quota again. So I'm guessing there's other quotas also
          byMinuteCounter = 0;
          try {
            System.out.println("Sleep time in ms = "+(System.currentTimeMillis()-start));
            long timePassed = System.currentTimeMillis()-start;
            if (timePassed < 60*1000) {
              Thread.sleep(60*1000 - timePassed);
            }

          } catch (InterruptedException e) {
            System.out.println(e);

          };

        } else if (totalCounter > 100) {
          // just a shot in the dark, but let's not hit more than 100 times per run
        };

        System.out.println("******************************************");
        System.out.println("Total retrieved so far for this run: " + totalCounter);
      };
    };

    System.out.println("finished");     
  }
}


