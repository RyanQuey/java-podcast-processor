package dataClasses;

import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;
import org.json.simple.JSONObject;

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
  private final String episodeCount;

  Podcast(JSONObject podcastJson) {
    //  assuming Itunes as API...:
    this.artistName = podcastJson.get("artistName"); 
    this.name = podcastJson.get("collectionName"); 
    this.imageUrl30 = podcastJson.get("artworkUrl30");  
    this.imageUrl30 = podcastJson.get("artworkUrl60");  
    this.imageUrl30 = podcastJson.get("artworkUrl100");  
    this.imageUrl30 = podcastJson.get("artworkUrl600");  
    this.apiId = podcastJson.get("collectionId");
    this.apiUrl = podcastJson.get("collectionViewUrl");
    this.country = podcastJson.get("country");
    this.feedUrl = podcastJson.get("feedUrl");
    this.genres = podcastJson.get("genres");
    this.apiGenreIds = podcastJson.get("genreIds");
    this.primaryGenre = podcastJson.get("primaryGenreName");
    // itunes format: "2020-05-04T15:00:00Z"
    this.releaseDate = podcastJson.get("releaseDate");
    this.episodeCount = podcastJson.get("trackCount");
    this.explicit = podcastJson.get("contentAdvisoryRating") == "Clean";
  }

  // TODO 
  private String getRss () {

    try {

      // apparently apple doesn't like the pluses (?)
      String queryTerm = term.replaceAll(" ", " ");

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

};



