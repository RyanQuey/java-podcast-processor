import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
// leave spaces here for easy reading, but change when putting into file or search terms
/* 
search-terms = [
    "data engineering",
]
*/

public class GetPodcasts {
  private static String get (String term) {

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
    // TODO 
    // later iterate over terms array
    String[] searchTerms = {
      "data engineering",
      "big data",
      "Apache Kafka",
      "Apache Cassandra",
      "Apache Spark",
      "Apache hadoop",
      "DataStax",
      "Confluent",

      "machine learning",
      "data science",
      "TensorFlow",

      "AWS",
      "Microsoft Azure",
      "Google Cloud Platform",

      "full stack development",
      "software engineering",
      "backend engineering",
      "DevOps",
    };

    //for each term, ask itunes for results

    for (String term : searchTerms) {
      String podcastJSON = get(term);

      // write to a file 
      String filename = term.replaceAll(" ", "-") + ".json";
      CreateFile.write(filename, podcastJSON);
    };

    System.out.println("finished");     
  }
}


