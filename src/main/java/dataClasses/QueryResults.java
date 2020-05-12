package dataClasses;

import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;

import helpers.HttpReq;
import helpers.FileHelpers;

/* 
 * Represents a single file of search results
 * For one file, gets all search results and retrieves the rss feed data
 *
 */
public class QueryResults {
  String filename = "./podcast-data/artist_big-data.json";

  // reads file, pulls data we need, and sets to array
  private JSONArray getSearchResults () {
    try {
      String fileContents = FileHelpers.read(this.filename);
      System.out.println(fileContents);
      JSONObject contentsJson = (JSONObject) new JSONObject(fileContents);

      JSONArray resultsJson = (JSONArray) contentsJson.get("results");
      // currently not using
      //int resultsCount = (int) contentsJson.get("resultCount");

      return resultsJson;

    } catch (Exception e) {
      System.out.println("Error:");
      System.out.println(e);

      return null;
    }
  };

  // gets rss data for a podcast (which includes all the episode data)
  public void getEpisodes(boolean refreshData){
    // TODO eventually iterate over each file dynamically
    JSONArray resultsJson = getSearchResults();

    for (int i = 0; i < resultsJson.length(); i++) {
      JSONObject podcastJson = resultsJson.getJSONObject(i);
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


