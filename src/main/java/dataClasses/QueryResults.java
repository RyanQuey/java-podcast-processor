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

import dataClasses.Podcast;

/* 
 * Represents a single file of search results
 * For one file, gets all search results and retrieves the rss feed data
 *
 */
public class QueryResults {
  String filename;
  File file;
  private ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
  private ArrayList<String> podcastIds = new ArrayList<String>();

  Episode(File queryResultsFile) {
    this.file = queryResultsFile;
    this.filename = this.file.getName();
  }

  // reads file, pulls data we need, and sets to array
  // no reason to set to variable, should only call once ever per QueryResult instance
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

  ArrayList<Podcast> getPodcasts () {
    if (podcasts.size() > 0) {
      return podcasts;

    } else {
      JSONArray resultsJson = getSearchResults();

      for (int i = 0; i < resultsJson.length(); i++) {
        JSONObject podcastJson = resultsJson.getJSONObject(i);
        System.out.println(podcastJson);

        Podcast podcast = new Podcast(podcastJson);
        podcasts.add(podcast);
        podcastIds.add(podcast.id);
      }
    };
  }

  ArrayList<String> getPodcastIds () {
    if (podcastIds.size() > 0) {
      return podcastIds;

    } else {
      getPodcasts();
      return podcastIds;

    }
  }

  // gets rss data for a podcast (which includes all the episode data)
  public void getEpisodes(Map<String, Podcast> podcastsProcessed, ArrayList<Podcast> podcastIdsProcessed, boolean refreshData){
    for (Podcast podcast : getPodcasts()) {

      // get RSS for podcast, to get episode list
      podcast.getEpisodes();


      // write to file. Later TODO send to db. Or do something useful with it! 

    };


    System.out.println("finished getting episodes for this set of query results, stored in: ", this.filename);     
  }
}


