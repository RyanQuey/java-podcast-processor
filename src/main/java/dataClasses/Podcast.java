package dataClasses;

import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.io.IOException; 
import java.util.concurrent.ExecutionException;
import java.lang.Thread;
import java.io.File;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import helpers.HttpReq;
import helpers.FileHelpers;

import dataClasses.Episode;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 *
 */
public class Podcast {
  String artistName; 
  String name; 
  String imageUrl30;  
  String imageUrl60;  
  String imageUrl100;  
  String imageUrl600;  
  String api; // name of the api
  String apiId; // id assigned by api
  String apiUrl; // url within api
  String country;
  String feedUrl; // rss feed url
  ArrayList<String> genres;
  ArrayList<String> apiGenreIds;
  String primaryGenre;
  String releaseDate;
  boolean explicit;
  int episodeCount;
  String id;
  String rssFeed;

  // access through getters
  private ArrayList<Episode> episodes = new ArrayList<Episode>();

  // TODO add some error handling, so that for every attribute, if it doesn't work, just move on, no problem. Just get as much information as we can
  public Podcast(JSONObject podcastJson) 
    throws ExecutionException {
      // really is an `org.apache.commons.exec.ExecuteException`, but that inherits from IOException
      // sometimes it is `org.json.JSONException` which causes teh ExecuteException 

      //  assuming Itunes as API...:

      this.artistName = (String) podcastJson.get("artistName"); 
      this.name = (String) podcastJson.get("collectionName"); 
      this.imageUrl30 = (String) podcastJson.get("artworkUrl30");  
      this.imageUrl60 = (String) podcastJson.get("artworkUrl60");  
      this.imageUrl100 = (String) podcastJson.get("artworkUrl100");  
      this.imageUrl600 = (String) podcastJson.get("artworkUrl600");  
      // TODO find way to dynamically get this from the file. Perhaps bake it into the filename or get from apiUrl 
      this.api = "itunes"; 
      this.apiId = (String) String.valueOf(podcastJson.get("collectionId"));
      this.apiUrl = (String) podcastJson.get("collectionViewUrl");
      this.country = (String) podcastJson.get("country");
      this.feedUrl = (String) podcastJson.get("feedUrl");

      JSONArray genresJson = (JSONArray) podcastJson.get("genres");
      this.genres = (ArrayList<String>) FileHelpers.jsonArrayToList(genresJson);

      JSONArray apiGenreIdsJson = (JSONArray) podcastJson.get("genreIds");
      this.apiGenreIds = (ArrayList<String>) FileHelpers.jsonArrayToList(apiGenreIdsJson);
      this.primaryGenre = (String) podcastJson.get("primaryGenreName");
      // itunes format: "2020-05-04T15:00:00Z"
      this.releaseDate = (String) podcastJson.get("releaseDate");
      this.explicit = (String) podcastJson.get("contentAdvisoryRating") == "Clean";

      this.id = this.api + "-" + this.apiId;
      this.episodeCount = (int) podcastJson.get("trackCount");
  }

  // TODO 
  private String getRss () {
    if (rssFeed != null) {
      return rssFeed;
    }

    try {
      String result = HttpReq.get(this.feedUrl, null);

      System.out.println("RSS retrieved");
      System.out.println(result);

      this.rssFeed = result;
      return this.rssFeed;

    } catch (Exception e) {
      System.out.println("Error:");
      System.out.println(e);

      return null;
    }
  }

  public String getEpisodes () {
    if (true) {
      // TODO return episodes
      return rssFeed;// this.episodes;
    }

    // TODO will have different return value later;
    return getRss();
    // extract episodes from rss feed;
    // TODO
  }
};



