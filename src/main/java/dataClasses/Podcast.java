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

import java.net.URL;
import java.io.InputStreamReader;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.module.Module; // TODO confirm
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rometools.modules.itunes.AbstractITunesObject;
import com.rometools.modules.itunes.EntryInformation;

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
  SyndFeed rssFeed;
  String rssFeedStr;
  QueryResults fromQuery; 
  Exception errorGettingRss; 

  // access through getters
  private ArrayList<Episode> episodes = new ArrayList<Episode>();

  // TODO add some error handling, so that for every attribute, if it doesn't work, just move on, no problem. Just get as much information as we can
  public Podcast(JSONObject podcastJson, QueryResults fromQuery) 
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
      this.fromQuery = fromQuery;
  }

	// wrapper around getRssStr and getRss, with extra error handling, and makes sure we don't make the http request multiple times if unnecessary
	// TODO once we are sure with this can return, specify String or whatever rssFeed is 
	private Object getRss () 
	  throws Exception {
      if (rssFeed != null) {
        return rssFeed;
      }

      // some data is faulty, so skip
      if (this.feedUrl == null || this.feedUrl == "") {
        // TODO maybe want better error handling for this
        return "";
      }

      try {
        System.out.println("Podcast info file at: " + this.feedUrl);
        System.out.println("Making request to: " + this.feedUrl);

        // getRssStr();
        return getRssFeed();

      } catch (Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();

        throw e;
      }

	}


  // gets RSS and just outputs as string.
	// not using as much now; using rss lib instead
	// DEPRECATED; just use getRssFeed. Also returns string
  private String getRssStr () 
    throws Exception {
      try {
        String result = HttpReq.get(this.feedUrl, null);

        System.out.println("RSS retrieved as String");
        System.out.println(result);

        this.rssFeedStr = result;
        return this.rssFeedStr;

      } catch (Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();

        throw e;
      }
  }

  // gets RSS and just outputs as a Rome RSS `SyndFeed` obj
	// TODO consider using this which has some sort of caching system built-in:
	// https://rometools.github.io/rome/Fetcher/UsingTheRomeFetcherModuleToRetrieveFeeds.html
  private SyndFeed getRssFeed () 
    throws Exception {
      try {
        SyndFeedInput input = new SyndFeedInput();
        // NOTE TODO add a more robust fetching mechanism, as recommended in the github home page and described here: `https://github.com/rometools/rome/issues/276`
        SyndFeed syndfeed = input.build(new XmlReader(new URL(this.feedUrl)));

        // TODO 
        // can now do like getDescription, getTitle, etc. 
        // if itunes, can do getImage, getCategory
        //

        this.rssFeed = syndfeed;
        // TODO might not need to save the string
        this.rssFeedStr = syndfeed.toString();
   
        return this.rssFeed;

      } catch (Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();

        throw e;
      }
  }


  // TODO what do I want to do for error handling?
  // private Map<String, String> convertRssToMap () {
  private void convertRssToMap () {
    // inexpensive way to make sure that we have the feet already set
    try {
      this.getRss();

      for (SyndEntry entry : this.rssFeed.getEntries()) {
        // sets the module to use for this feed
        // maybe use this instead:         final Module module = syndfeed.getModule(AbstractITunesObject.URI);
        //
        Module entryModule = entry.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
        EntryInformation entryInfo = (EntryInformation) entryModule;
        // see here; base what we do off of tests
        // https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/test/java/com/rometools/modules/itunes/ITunesParserTest.java#L128
        // TODO NEXT
        // also here: https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/EntryInformation.java
        // 
        // https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/EntryInformationImpl.java#L37-L42


      }
      /*
      XStream rssStream = new XStream();
      Map<String, String> extractedMap = (Map<String, String>) rssStream.fromXML(xml);
      assert extractedMap.get("name").equals("chris");
      assert extractedMap.get("island").equals("faranga");
  */
    } catch (Exception e) {
      System.out.println("Error: " + e);
      e.printStackTrace();

    
    }
	}

  // might not use since we're getting from the api already. but Good to have on hand
  public void extractPodcastInfo () {
    // see here for how this would look like https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/test/java/com/rometools/modules/itunes/ITunesParserTest.java#L78
  }

  public void getEpisodes () {
    if (this.episodes.size() != 0) {
      // TODO return episodes
      return ;// this.episodes;
    }

    // TODO will have different return value later;
    try {
      getRss();
      convertRssToMap();
      


    } catch (Exception e) {
      this.errorGettingRss = e;
    }

    // extract episodes from rss feed;
    // TODO
  }
};



