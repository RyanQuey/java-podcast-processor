package dataClasses;

import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/* 
 * Represents a single file of search results
 * For one file, gets all search results and retrieves the rss feed data
 *
 */
public class QueryResults {
  String filename = "./podcast-data/artist_big-data.json";

  // reads file, pulls data we need, and sets to array
  private String getSearchResults (String filename) {
    try {
      String fileContents = FileHelpers.readFile(filename);
      System.out.println(fileContents);
      JSONObject contentsJson = (JSONObject) new JSONObject(fileContents);
      int resultsCount = fileContents.get("resultCount");
      JSONObject resultsJson = (JSONArray) fileContents.get("results");

      return resultsJson;

    } catch (Exception e) {
      System.out.println("Error:");
      System.out.println(e);

      return null;
    }
  };

  // gets rss data for a podcast (which includes all the episode data)
  public void getEpisodes(String[] args){
    boolean refreshData = false;
    for (String s: args) {
      System.out.println(s);
    };

    // TODO eventually iterate over each file dynamically
    JSONObject resultsJson = getSearchResults(filename);

    for (int i = 0; i < resultsJson.length(); i++) {
      JSONObject podcastJson = resultsJson.getJSONArray(i);
      System.out.println(podcastJson);
      Podcast podcast = new Podcast(podcastJson);
      System.out.println(podcast);

      // get RSS for podcast, to get episode list

      // translate RSS into something more readable/useful...unless we want to data lake it

      // write to file. Later TODO send to db
    };


    System.out.println("finished");     
  }
}


