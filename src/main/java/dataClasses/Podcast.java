package dataClasses;

import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;
import java.util.ArrayList;
import org.json.JSONObject;

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

  // access through getters
  private ArrayList<Episode> episodes = new ArrayList<Episode>();

  Podcast(JSONObject podcastJson) {
    //  assuming Itunes as API...:
    this.artistName = (String) podcastJson.get("artistName"); 
    this.name = (String) podcastJson.get("collectionName"); 
    this.imageUrl30 = (String) podcastJson.get("artworkUrl30");  
    this.imageUrl60 = (String) podcastJson.get("artworkUrl60");  
    this.imageUrl100 = (String) podcastJson.get("artworkUrl100");  
    this.imageUrl600 = (String) podcastJson.get("artworkUrl600");  
    // TODO find way to dynamically get this from the file. Perhaps bake it into the filename or get from apiUrl 
    this.api = "itunes"; 
    this.apiId = (String) podcastJson.get("collectionId");
    this.apiUrl = (String) podcastJson.get("collectionViewUrl");
    this.country = (String) podcastJson.get("country");
    this.feedUrl = (String) podcastJson.get("feedUrl");
    this.genres = (ArrayList<String>) podcastJson.get("genres");
    this.apiGenreIds = (ArrayList<String>) podcastJson.get("genreIds");
    this.primaryGenre = (String) podcastJson.get("primaryGenreName");
    // itunes format: "2020-05-04T15:00:00Z"
    this.releaseDate = (String) podcastJson.get("releaseDate");
    this.explicit = (String) podcastJson.get("contentAdvisoryRating") == "Clean";
    this.episodeCount = (int) podcastJson.get("trackCount");

    this.id = this.api + "-" + this.apiId;
  }

  // TODO 
  private String getRss () {
    if (rssFeed) {
      return rssFeed;
    }

    try {
      String result = HttpReq.get(this.feedUrl, null);

      System.out.println(result);

      this.rssFeed = result;
      return this.rssFeed;

    } catch (Exception e) {
      System.out.println("Error:");
      System.out.println(e);

      return null;
    }
  }

  private String getEpisodes () {
    if (this.episodes) {
      return this.episodes;
    }

    getRss();
    // extract episodes from rss feed;
    // TODO
  }
};



