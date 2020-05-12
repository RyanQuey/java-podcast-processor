package dataClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONException;
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
  public String filename;
  public String relativePath;
  public File file;
  private ArrayList<Podcast> podcasts = new ArrayList<Podcast>();
  private ArrayList<String> podcastIds = new ArrayList<String>();

  public QueryResults(File queryResultsFile) 
    throws FileNotFoundException {
      this.file = queryResultsFile;
      this.filename = this.file.getName();
      this.relativePath = "podcast-data/" + this.file.getName();

      if (!file.exists()) {
        throw new FileNotFoundException(this.filename + "not found (No such file or directory)!");
      }
  }

  // reads file, pulls data we need, and sets to array
  // no reason to set to variable, should only call once ever per QueryResult instance
  private JSONArray getSearchResults () throws IOException {
    String fileContents;

    try {
      fileContents = FileHelpers.read(this.relativePath);
    } catch (IOException e) {
			// could be different types of errors I think...though maybe all are IO? but Exception is fiene
      System.out.println(e);
      e.printStackTrace();

      throw e;
    }

    try {
      JSONObject contentsJson = (JSONObject) new JSONObject(fileContents);
      JSONArray resultsJson = (JSONArray) contentsJson.get("results");
      // currently not using
      //int resultsCount = (int) contentsJson.get("resultCount");

      return resultsJson;

    } catch (JSONException e) {
      System.out.println(e);
      e.printStackTrace();

      // so that this can only throw one type of exception:
      throw new IOException("failed to read JSON for file " + this.filename);
    }
  };

  public ArrayList<Podcast> getPodcasts () throws IOException {
    if (podcasts.size() > 0) {
      return podcasts;

    } else {
      JSONArray resultsJson = getSearchResults();

      for (int i = 0; i < resultsJson.length(); i++) {
        JSONObject podcastJson = resultsJson.getJSONObject(i);

        Podcast podcast;
        try {
          podcast = new Podcast(podcastJson);
        } catch (Exception e) {
          // normally just allow ExecutionException (which is what this ends up being), at least what I've seen so far) to throw, but for this, is really just a json issue, want to continue no matter what
          System.out.println("Error getting info for podcast " + i);
          System.out.println("moving to next");
          System.out.println(e);
          e.printStackTrace();
          continue;
        }
        podcasts.add(podcast);
        podcastIds.add(podcast.id);
      }
    };

    return podcasts;
  }

  public ArrayList<String> getPodcastIds () throws IOException {
    if (podcastIds.size() > 0) {
      return podcastIds;

    } else {
      getPodcasts();
      return podcastIds;

    }
  }

  // gets rss data for a podcast (which includes all the episode data)
  // TODO currently, we are not verifying whether or not we've already gotten data for this podcast. 
  // if we do, can use following method definition or something like it:
  // public void getEpisodes(HashMap<String, Podcast> podcastsProcessed, ArrayList<Podcast> podcastIdsProcessed, boolean refreshData){
  public void getEpisodes() throws IOException {
    for (Podcast podcast : getPodcasts()) {

      // get RSS for podcast, to get episode list

      podcast.getEpisodes();


      // write to file. Later TODO send to db. Or do something useful with it! 

    };


    System.out.println("finished getting episodes for this set of query results which is stored in: " + this.filename);
  }
}


