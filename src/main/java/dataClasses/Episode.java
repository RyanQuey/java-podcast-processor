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


/* 
 * For one file, gets all search results and retrieves the rss feed data
 *
 */
public class Episode {
  // TODO figure out what is different from the track info, and store here
  // the rest, get rid of and just reference via the track
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
  String podcastId;
  boolean explicit;

  private String rssFeedData;
  private Podcast podcast;

  // TODO make a flag to signal initialization from our db rather than from rss
  Episode(String episodeRss) {
    //  assuming Itunes as API...:
    /*
    this.artistName = (String) episodeRss.get("artistName"); 
    this.name = (String) episodeRss.get("collectionName"); 
    this.imageUrl30 = (String) episodeRss.get("artworkUrl30");  
    this.imageUrl60 = (String) episodeRss.get("artworkUrl60");  
    this.imageUrl100 = (String) episodeRss.get("artworkUrl100");  
    this.imageUrl600 = (String) episodeRss.get("artworkUrl600");  
    // TODO find way to dynamically get this from the file. Perhaps bake it into the filename or get from apiUrl 
    this.country = (String) episodeRss.get("country");
    this.feedUrl = (String) episodeRss.get("feedUrl");
    this.genres = (ArrayList<String>) episodeRss.get("genres");
    this.primaryGenre = (String) episodeRss.get("primaryGenreName");
    // itunes format: "2020-05-04T15:00:00Z"
    this.releaseDate = (String) episodeRss.get("releaseDate");
    this.explicit = (String) episodeRss.get("contentAdvisoryRating") == "Clean";
    this.episodeCount = (int) episodeRss.get("trackCount");

    this.id = this.api + "-" + this.apiId;
    */
  }

};



