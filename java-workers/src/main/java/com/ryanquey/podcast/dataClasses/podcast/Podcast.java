package com.ryanquey.podcast.dataClasses.podcast;

// import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
// import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.update;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.util.concurrent.ExecutionException;
import java.time.Instant;
import java.lang.Exception;

// import com.datastax.oss.driver.api.core.cql.ResultSet;
// import com.datastax.oss.driver.api.core.cql.Row;

import com.rometools.modules.itunes.AbstractITunesObject;
import com.rometools.modules.itunes.FeedInformationImpl;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ryanquey.podcast.cassandraHelpers.CassandraDb;
import com.ryanquey.podcast.helpers.JsonHelpers;
import com.ryanquey.podcast.helpers.DataClassesHelpers;

import com.ryanquey.podcast.dataClasses.episode.Episode;
import com.ryanquey.podcast.dataClasses.searchQuery.SearchQuery;
import com.ryanquey.podcast.dataClasses.searchQuery.SearchQueryUDT;
// import helpers.HttpReq;

/* 
 * this is what we will use for the most part after having retrieved a database record, or just when interacting with the data within the app
 * - any field that we do not want persisted to the database should be here, since Cassandra Java driver does not seem to allow fields that do not have corresponding columns in db
 *
 */

public class Podcast extends PodcastBase {

  // access through getters
  private ArrayList<Episode> episodes = new ArrayList<Episode>();

  /////////////////////
  // these are not persisted to db
  
  private SyndFeed rssFeed;
  //private String rssFeedStr;
  // test does it this way, demo (https://rometools.github.io/rome/Modules/ITunesPodcasting.html) does it FeedInformation
  // FeedInformationImpl implements FeedInformation though, so probably use FeedInformationImpl
  private FeedInformationImpl feedInfo;
  // FeedInformation feedInfo;

  // the query that we got this podcast from
  // TODO remove and just set to foundByQueries directly
  // TODO add this back in, currently all found_by_queries columns are set to null

  // in case we wanted to persist this error
  Exception errorGettingRss;



  ////////////////////
  // Constructors
  // required for jackson deserialization https://stackoverflow.com/a/56923998/6952495
  public Podcast() {}

  public Podcast(PodcastByLanguageRecord podcastByLanguageRecord) {
    DataClassesHelpers.copyMatchingFields(podcastByLanguageRecord, this);
  }

  // TODO add some error handling, so that for every attribute, if it doesn't work, just move on, no problem. Just get as much information as we can
  public Podcast(JSONObject podcastJson, SearchQuery fromQuery) 
    throws Exception {
      // really is an `org.apache.commons.exec.ExecuteException`, but that inherits from IOException
      // sometimes it is `org.json.JSONException` which causes teh ExecuteException 

      //  assuming Itunes as API...:

      this.setOwner((String) podcastJson.get("artistName")); 
      this.setName((String) podcastJson.get("collectionName")); 
      this.setImageUrl30((String) podcastJson.get("artworkUrl30"));
      this.setImageUrl60((String) podcastJson.get("artworkUrl60"));  
      this.setImageUrl100((String) podcastJson.get("artworkUrl100"));  
      this.setImageUrl600((String) podcastJson.get("artworkUrl600"));  
      // TODO find way to dynamically get this from the result. Perhaps bake it into the filename or get from apiUrl 
      this.setApi("itunes"); 
      this.setApiId((String) String.valueOf(podcastJson.get("collectionId")));
      this.setApiUrl((String) podcastJson.get("collectionViewUrl"));
      this.setCountry((String) podcastJson.get("country"));
      this.setFeedUrl((String) podcastJson.get("feedUrl"));

      JSONArray genresJson = (JSONArray) podcastJson.get("genres");
      // I want to be ordered, since probably matches order of api genre ids
      this.setGenres((ArrayList<String>) JsonHelpers.jsonArrayToList(genresJson));

      // I want to be ordered, since probably matches order of genres
      JSONArray apiGenreIdsJson = (JSONArray) podcastJson.get("genreIds");
      this.setApiGenreIds((ArrayList<String>) JsonHelpers.jsonArrayToList(apiGenreIdsJson));
      this.setPrimaryGenre((String) podcastJson.get("primaryGenreName"));
      // itunes format: "2020-05-04T15:00:00Z"
      String rdStr = (String) podcastJson.get("releaseDate");
      this.setReleaseDate(CassandraDb.stringToInstant(rdStr));
      
      // definitely don't want to break on this. And sometimes they set as collectionExplicitness instead I guess (?...at least, I saw one that itunes returned that way)
      
      String rating; 
      if (podcastJson.has("contentAdvisoryRating")) {
        rating = (String) podcastJson.get("contentAdvisoryRating");
      } else if (podcastJson.has("collectionExplicitness")) {
        rating = (String) podcastJson.get("collectionExplicitness");
      } else {
        rating = "UNKNOWN";
      }

      // have seen all of these returned, not sure what the difference is
      this.setExplicit(Arrays.asList("notExplicit", "Clean", "cleaned").contains(rating));

      this.setEpisodeCount((int) podcastJson.get("trackCount"));

      // TODO persist somehow, probably with type List, and list chronologically the times that this was returned. BUt for me, don't need that info
      this.addToFoundByQueries(new SearchQueryUDT(fromQuery));
      this.setUpdatedAt(Instant.now());
  }

  // should have one of these for each record Class, and can determine which record Class by what their partition keys and clustering keys are
  public static Podcast findOne(String language, String primaryGenre, String feedUrl) throws Exception {
    PodcastByLanguageRecord p =  PodcastByLanguageRecord.getDao().findOne(language, primaryGenre, feedUrl);

    if (p == null) {
      return null;
    } else {
      return new Podcast(p);
    }
  }

  // TODO 
  /*
  public Podcast(String primary_genre, String feed_url) {
  
  }
  */

  /////////////////////////////////////////
  // Static methods

  /////////////////////////////////////////
  // instance methods
  /*
  public Podcast fetch () {
    // even better would be to use Lucerne/solr instead, or add indexes and do something like where language LIKE '%en%' 
    String query = "SELECT * FROM podcast_analysis_tool.podcasts_by_language WHERE language in ('en', 'en-us', 'en-US', 'en-EN', 'en-en', 'en-CA', 'en-ca', 'en-GB', 'en-gb', 'UNKNOWN') AND primary_genre = " + this.primaryGenre + " AND feed_url = " + this.getFeedUrl() + " LIMIT 1";
    ResultSet result = CassandraDb.execute(query);

    Row CassandraDb.ecord = result.one();

  }
  */

	// wrapper around getRssStr and getRss, with extra error handling, and makes sure we don't make the http request multiple times if unnecessary
	// TODO rename to disambiguate from getRssFeed
	// TODO once we are sure with this can return, specify String or whatever rssFeed is 
	private Object getRss () 
	  throws Exception {
      if (rssFeed != null) {
        return rssFeed;
      }

      // some data is faulty, so skip
      if (this.getFeedUrl() == null || this.getFeedUrl() == "") {
        // TODO maybe want better error handling for this
        throw new IllegalArgumentException("feedUrl does not exist");
      }

      try {
        System.out.println("Getting feed for: " + this.getFeedUrl());

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
  /*
  private String getRssStr () 
    throws Exception {
      try {
        String result = HttpReq.get(this.getFeedUrl(), null);

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
  */

  // gets RSS and just outputs as a Rome RSS `SyndFeed` obj
  private SyndFeed getRssFeed () 
    throws Exception {
      System.out.println("initializing connection..." + Instant.now().toString());
      CloseableHttpResponse response;
      CloseableHttpClient client; 
      HttpUriRequest request; 
      try {

				// setup connection
				try {
					client = HttpClients.createMinimal(); 
					request = new HttpGet(this.getFeedUrl());
          System.out.println("now starting to execute request..." + Instant.now().toString());
          response = client.execute(request); 

        } catch (Exception e) {
          throw e;
        }

        // set feed data to our object
        try {
          System.out.println("starting to get content..." + Instant.now().toString());
          InputStream stream = response.getEntity().getContent();
          SyndFeedInput input = new SyndFeedInput();
          try {
            this.rssFeed = input.build(new XmlReader(stream));
          } catch (NoSuchMethodError e) {
            // I don't know why, but sometimes this error happens here too. If so, jus t skip this podcast. Maybe one day keep a record of errored podcasts
            // TODO find out why NoSuchMethodError's thrown here aren't caught by the parent try-catch blocks. Instead i tjust stops the program altogether
            System.out.println(e);
            e.printStackTrace();
            throw new RuntimeException("Failed to read this rss xml, not sure why");
          }

          System.out.println("Reading feed for:");
          System.out.println(this.rssFeed.getTitle());

				  // TODO find out what kinds of exception
        } catch (Exception e) {
          System.out.println("error getting feed from url");
          System.out.println(e);
          e.printStackTrace();
          throw e;

        } finally {
          // I think it's all read at this point, so can close no matter what (?)
          response.close();
				}

        // NOTE TODO add a more robust fetching mechanism, as recommended in the github home page and described here: `https://github.com/rometools/rome/issues/276`
        // this.rssFeed = input.build(new XmlReader(rssData));
				final Module module = this.rssFeed.getModule(AbstractITunesObject.URI);
        this.feedInfo = (FeedInformationImpl) module;
        // this.feedInfo = (FeedInformation) module;

        // TODO 
        // can now do like getDescription, getTitle, etc. 
        // if itunes, can do getImage, getCategory
        //

        // TODO might not need to save the string. If so, just remove this
        /*
        try {
          this.rssFeedStr = this.rssFeed.toString();
        } catch (NoSuchMethodError e) {
          System.out.println("can't turn to string for some reason, moving on");
          System.out.println(e);
          e.printStackTrace();
        }
        */

        return this.rssFeed;

      } catch (Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();

        throw e;

      }
  }


  // TODO what do I want to do for error handling?
  // NOTE don't call this directly I don't think...mostly just call extractEpisodes
  private void convertRssToEpisodes () {
    // inexpensive way to make sure that we have the feet already set
    try {
      this.getRss();

      for (SyndEntry entry : this.rssFeed.getEntries()) {
        Episode episode = new Episode(entry, this);

        this.episodes.add(episode);
      }
    } catch (Exception e) {
      System.out.println("Error: " + e);
      e.printStackTrace();

    
    }
	}

  // might not use since we're getting from the api already. but Good to have on hand
  // should it really be RuntimeException? not sure hwat it should be, just guessing here
  // TODO there is actually a danger of creating multiple records, if podcast has different language in itunes as opposed to in rss feed. 
  // Probably best solution is to stop partitioning based on language, and instead partition on something that cna be retrieved from itunes and the rss data consistently, like the feedUrl. Then just index it for when want to query across partitions
  public void updateBasedOnRss () throws Exception {
    // see here for how this would look like https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/test/java/com/rometools/modules/itunes/ITunesParserTest.java#L78

    this.getRss();

    this.setOwner(feedInfo.getOwnerName());
    this.setOwnerEmail(feedInfo.getOwnerEmailAddress());
    /* not going to use these for now; just use what itunes returned
    // "http://a1.phobos.apple.com/Music/y2005/m06/d26/h21/mcdrrifv.jpg"
    feedInfo.getImage().toExternalForm();
    // category 1: something like "Comedy"
    feedInfo.getCategories().get(0).getName());
    // category two:         "Arts & Entertainment",
    feedInfo.getCategories().get(1).getName());
    // "subCategory", Something like: "Entertainment",
    feedInfo.getCategories().get(1).getSubcategories().get(0).getName());
    */
    // something like "A weekly, hour-long romp through the worlds of media, politics, sports and show business, leavened with an eclectic mix of mysterious music, hosted by Harry Shearer."
    this.setSummary(feedInfo.getSummary());
    // might not work...maybe just using summary? But I see rss with description, not summary...
    this.setDescription(this.rssFeed.getDescription());
    // not sure if this is what I think i tis TODO
    this.setWebsiteUrl(this.rssFeed.getLink());
    // if they didn't set a language, default to "UNKNOWN" to avoid error: `InvalidQueryException: Key may not be empty`. Especially critical since we often sort by 
    this.setLanguage(this.rssFeed.getLanguage() == null ? "UNKNOWN" : this.rssFeed.getLanguage());
    // saw it here: https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/FeedInformationImpl.java#L179
    this.setSubtitle(feedInfo.getSubtitle());

    //feedInfo.getComplete(); (boolean, I'm guessing maybe for pagination?)

    System.out.println("Set properties for    " + this.getName() + "   "  + "with rss feed url at " + this.getFeedUrl());
    // NOTE feedInfo.getNewFeedUrl() not working
  }

  public ArrayList<Episode> extractEpisodes () throws Exception {
    // TODO find better way to see if there's any episodes...though in general, most podcasts should have at least one (?)
    if (this.episodes.size() != 0) {
      return this.episodes;
    }

    try {
      getRss();
      convertRssToEpisodes();
      return this.episodes;

    } catch (Exception e) {
      // TODO determine what type of exception this would throw 
      System.out.println("Error getting episodes");
      this.errorGettingRss = e;

      throw e;
    }

    // extract episodes from rss feed;
    // TODO
  }

  // TODO in its current implementation, it would make sense to do this in bulk, a bulk add or something perhaps. 
  // But keep in mind, want to first implement as more or less a stream, one at a time as they come in
  // Currently also persists podcast so that it stores the episodes on its own record too
  public void persistEpisodes() throws Exception {
    for (Episode episode : getEpisodes()) {
      System.out.println("Saving episode " + episode.getTitle());
      //Episode.getDao().save(episode);
      episode.persist();
    };

    // add something to save this current record in IF we save episodes directly to podcasts somewhere
    // PodcastByLanguageRecord.getDao().save(this);
  }

  // persists to all podcast tables
  public void persist () throws Exception {
    // currently only one table for this podcast so just save that
    PodcastByLanguageRecord p = new PodcastByLanguageRecord(this);
    // for what we want to do, don't want to just write to disk. Want
    p.saveAndAppendFoundBy();
  }

  public ArrayList<Episode> getEpisodes() {
      return this.episodes;
  }

  public void setEpisodes(ArrayList<Episode> episodes) {
      this.episodes = episodes;
  }

};



