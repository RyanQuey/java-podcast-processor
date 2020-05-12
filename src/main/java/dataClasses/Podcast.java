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
public class Podcast {
  private final String artistName; 
  private final String name; 
  private final String imageUrl30;  
  private final String imageUrl60;  
  private final String imageUrl100;  
  private final String imageUrl600;  
  private final String apiId;
  private final String apiUrl;
  private final String country;
  private final String feedUrl; // rss feed url
  private final ArrayList<String> genres;
  private final ArrayList<String> apiGenreIds;
  private final String primaryGenre;
  private final String releaseDate;
  private final boolean explicit;
  private final int episodeCount;

  Podcast(JSONObject podcastJson) {
    //  assuming Itunes as API...:
    this.artistName = (String) podcastJson.get("artistName"); 
    this.name = (String) podcastJson.get("collectionName"); 
    this.imageUrl30 = (String) podcastJson.get("artworkUrl30");  
    this.imageUrl60 = (String) podcastJson.get("artworkUrl60");  
    this.imageUrl100 = (String) podcastJson.get("artworkUrl100");  
    this.imageUrl600 = (String) podcastJson.get("artworkUrl600");  
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
  }

  // TODO 
  private String getRss () {

    try {

      String result = HttpReq.get(this.feedUrl, null);

      System.out.println(result);

      // write to json file
      return result;

    } catch (Exception e) {
      System.out.println("Error:");
      System.out.println(e);

      return null;
    }
  }

};



