package dataClasses;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.update;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.time.Instant;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
// DSE Mapper
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

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

import helpers.CassandraDb;
import helpers.FileHelpers;
import helpers.HttpReq;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 *
 */

@Entity
@CqlName("podcasts_by_language")
public class Podcast {
  @PartitionKey 
  private String language;
  @ClusteringColumn(1) // or maybe 1?
  private String primaryGenre;
  @ClusteringColumn(2) // or maybe 2?
  private String feedUrl; // rss feed url

  private String owner; 
  private String name; 
  private String imageUrl30;  
  private String imageUrl60;  
  private String imageUrl100;  
  private String imageUrl600;  
  private String api; // name of the api TODO move this, apiId, and apiUrl to a nested map once we implement other apis
  private String apiId; // id assigned by api
  private String apiUrl; // url within api
  private String country;
  private ArrayList<String> genres;
  private ArrayList<String> apiGenreIds;
  private Instant releaseDate;
  boolean explicit;
  int episodeCount;

  // to get from rss, that itunes doesn't return in search
  // from description
  private String description;
  // not sure how it would be different from description, but rome seems to include it as part of the itunes rss api
  private String summary;
  // from itunes:subtitle
  private String descriptionSubtitle;
  // from webMaster
  private String webmaster;
  // from itunes:owner > itunes:email
  private String ownerEmail;
  private String author; //not yet sure how this is distinct from owner. But airflow's podcast for example has different http://feeds.soundcloud.com/users/soundcloud:users:385054355/sounds.rss
  // from image:link
  private String websiteUrl; // TODO make all these urls of java class Url
  private Instant updatedAt;

  // these should match the queryresult
  private String term;
  private String searchType;
  private Instant searchedAt; 
  // list of queries, each query giving term, searchType, api, and when search was performed
  private List<Map<String, String>> foundByQueries; 

  ///////
  // these are not persisted to db
  
  private SyndFeed rssFeed;
  private String rssFeedStr;
  // test does it this way, demo (https://rometools.github.io/rome/Modules/ITunesPodcasting.html) does it FeedInformation
  // FeedInformationImpl implements FeedInformation though, so probably use FeedInformationImpl
  private FeedInformationImpl feedInfo;
  // FeedInformation feedInfo;

  QueryResults fromQuery; 
  Exception errorGettingRss; 

  private static CassandraDb db;

  // access through getters
  private ArrayList<Episode> episodes = new ArrayList<Episode>();

  /////////////////////////////////////////////////
  // constructors

  // for DSE DAO
  public Podcast() {}

  // TODO add some error handling, so that for every attribute, if it doesn't work, just move on, no problem. Just get as much information as we can
  public Podcast(JSONObject podcastJson, QueryResults fromQuery) 
    throws ExecutionException {
      // really is an `org.apache.commons.exec.ExecuteException`, but that inherits from IOException
      // sometimes it is `org.json.JSONException` which causes teh ExecuteException 

      //  assuming Itunes as API...:

      this.owner = (String) podcastJson.get("artistName"); 
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
      // I want to be ordered, since probably matches order of api genre ids
      this.genres = (ArrayList<String>) FileHelpers.jsonArrayToList(genresJson);

      // I want to be ordered, since probably matches order of genres
      JSONArray apiGenreIdsJson = (JSONArray) podcastJson.get("genreIds");
      this.apiGenreIds = (ArrayList<String>) FileHelpers.jsonArrayToList(apiGenreIdsJson);
      this.primaryGenre = (String) podcastJson.get("primaryGenreName");
      // itunes format: "2020-05-04T15:00:00Z"
      String rdStr = (String) podcastJson.get("releaseDate");
      this.releaseDate = db.stringToInstant(rdStr);
      
      // definitely don't want to break on this. And sometimes they set as collectionExplicitness instead I guess (?...at least, I saw one that itunes returned that way)
      
      String rating; 
      if (podcastJson.has("contentAdvisoryRating")) {
        rating = (String) podcastJson.get("contentAdvisoryRating");
      } else if (podcastJson.has("collectionExplicitness")) {
        rating = (String) podcastJson.get("collectionExplicitness");
      } else {
        rating = "UNKNOWN";
      }

      this.explicit = Arrays.asList("notExplicit", "Clean").contains(rating);

      this.episodeCount = (int) podcastJson.get("trackCount");
      // TODO persist somehow, probably with type List, and list chronologically the times that this was returned. BUt for me, don't need that info
      this.fromQuery = fromQuery;
      this.term = this.fromQuery.term;
      this.searchType = this.fromQuery.searchType;
      this.searchedAt = this.fromQuery.updatedAt;
  }

  // TODO 
  public Podcast(String primary_genre, String feed_url) {
  
  }

  /*
  public Podcast fetch () {
    String query = "SELECT * FROM podcast_analysis_tool.podcasts_by_language WHERE language in ('en', 'en-US', 'UNKNOWN') AND primary_genre = " + this.primaryGenre + " AND feed_url = " + this.feedUrl + " LIMIT 1";
    ResultSet result = db.execute(query);

    Row dbRecord = result.one();

  }
  */

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
        throw new IllegalArgumentException("feedUrl does not exist");
      }

      try {
        System.out.println("Getting feed for: " + this.feedUrl);

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
      CloseableHttpResponse response;
      CloseableHttpClient client; 
      HttpUriRequest request; 
      try {

				// setup connection
				try {
					client = HttpClients.createMinimal(); 
					request = new HttpGet(this.feedUrl);
          response = client.execute(request); 

        } catch (Exception e) {
          throw e;
        }

        // set feed data to our object
        try {
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
          // TODO not in their example, but I'm guessing I have to do this
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

        // TODO might not need to save the string
        try {
          this.rssFeedStr = this.rssFeed.toString();
        } catch (NoSuchMethodError e) {
          System.out.println("can't turn to string for some reason, moving on");
          System.out.println(e);
          e.printStackTrace();
        }

        System.out.println("Got rss feed");
   
        return this.rssFeed;

      } catch (Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();

        throw e;

      }
  }


  // TODO what do I want to do for error handling?
  // private Map<String, String> convertRssToMap () {
  private void convertRssToEpisodes () {
    // inexpensive way to make sure that we have the feet already set
    try {
      this.getRss();

      for (SyndEntry entry : this.rssFeed.getEntries()) {
        Episode episode = new Episode(entry);
        episode.podcast = this;

        this.episodes.add(episode);
      }
    } catch (Exception e) {
      System.out.println("Error: " + e);
      e.printStackTrace();

    
    }
	}

  // might not use since we're getting from the api already. but Good to have on hand
  // should it really be RuntimeException? not sure hwat it should be, just guessing here
  public void updateBasedOnRss () throws Exception {
    // see here for how this would look like https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/test/java/com/rometools/modules/itunes/ITunesParserTest.java#L78

    this.getRss();

    this.owner = feedInfo.getOwnerName();
    this.ownerEmail = feedInfo.getOwnerEmailAddress();
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
    this.summary = feedInfo.getSummary();
    // might not work...maybe just using summary? But I see rss with description, not summary...
    this.description = this.rssFeed.getDescription();
    // not sure if this is what I think i tis TODO
    this.websiteUrl = this.rssFeed.getLink();
    // if they didn't set a language, default to "UNKNOWN" to avoid error: `InvalidQueryException: Key may not be empty`. Especially critical since we often sort by 
    this.language = this.rssFeed.getLanguage() == null ? this.rssFeed.getLanguage() : "UNKNOWN";
    // saw it here: https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/FeedInformationImpl.java#L179
    this.descriptionSubtitle = feedInfo.getSubtitle();

    //feedInfo.getComplete(); (boolean, I'm guessing maybe for pagination?)

    System.out.println("Set properties for    " + this.name + "   "  + "with rss feed url at " + this.feedUrl);
    // NOTE feedInfo.getNewFeedUrl() not working
  }

  // TODO rename, not a getter. This can call http requests under the hood
  public ArrayList<Episode> getEpisodes () throws Exception {
    // TODO find better way to see if there's any episodes...though in general, most podcasts should have at least one (?)
    if (this.episodes.size() != 0) {
      // TODO return episodes
      return this.episodes;
    }

    // TODO will have different return value later;
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

  // using the mapper https://github.com/datastax/java-driver/tree/4.x/manual/mapper#dao-interface
  // TODO deprecated; use DAO instead
  public void save () {
    Term ts = db.getTimestamp();

    Map<String, String> foundBy = new HashMap<String, String>();
    List<Map<String, String>> foundByList = Arrays.asList(foundBy);

    // want to create or update if exists
    String query = update("podcasts_by_language")
      .setColumn("owner", literal(this.owner))
      .setColumn("name", literal(this.name))
      .setColumn("image_url30", literal(this.imageUrl30))
      .setColumn("image_url60", literal(this.imageUrl60))
      .setColumn("image_url100", literal(this.imageUrl100))
      .setColumn("image_url600", literal(this.imageUrl600))
      .setColumn("api", literal(this.api))
      .setColumn("api_id", literal(this.apiId))
      .setColumn("api_url", literal(this.apiUrl))
      .setColumn("country", literal(this.country))
      //.setColumn("feed_url", literal(this.feedUrl)) // don't set because updating, so can't set any in primary key
      .setColumn("genres", literal(this.genres)) // hoping ArrayList converts to List here;
      .setColumn("api_genre_ids", literal(this.apiGenreIds))
      //.setColumn("primary_genre", literal(this.primaryGenre)) // can't update primary key
      .setColumn("release_date", literal(this.releaseDate))
      .setColumn("explicit", literal(this.explicit))
      .setColumn("episode_count", literal(this.episodeCount))
      //.setColumn("rss_feed", literal(this.rssFeedStr)) // don't save this for now, is really large and since I'm printing query, hard to debug
      .append("found_by_queries", literal(foundByList))
      .setColumn("description", literal(this.description))
      .setColumn("summary", literal(this.summary))
      .setColumn("description_subtitle", literal(this.descriptionSubtitle))
      .setColumn("webmaster", literal(this.webmaster))
      .setColumn("owner_email", literal(this.ownerEmail))
      .setColumn("author", literal(this.author))
      //.setColumn("language", literal(this.language))
      .setColumn("website_url", literal(this.websiteUrl))
      .setColumn("updated_at", ts)
      // only update this unique record, so set by compound primary key
      .whereColumn("language").isEqualTo(literal(this.language))
      .whereColumn("primary_genre").isEqualTo(literal(this.primaryGenre))
      .whereColumn("feed_url").isEqualTo(literal(this.feedUrl))
      .asCql();

      System.out.println("now executing:");
      System.out.println(query);
    db.execute(query);
  }

  public String getLanguage() {
      return language;
  }

  public void setLanguage(String language) {
      this.language = language;
  }

  public String getPrimaryGenre() {
      return primaryGenre;
  }

  public void setPrimaryGenre(String primaryGenre) {
      this.primaryGenre = primaryGenre;
  }

  public String getFeedUrl() {
      return feedUrl;
  }

  public void setFeedUrl(String feedUrl) {
      this.feedUrl = feedUrl;
  }

  public String getOwner() {
      return owner;
  }

  public void setOwner(String owner) {
      this.owner = owner;
  }

  public String getName() {
      return name;
  }

  public void setName(String name) {
      this.name = name;
  }

  public String getImageUrl30() {
      return imageUrl30;
  }

  public void setImageUrl30(String imageUrl30) {
      this.imageUrl30 = imageUrl30;
  }

  public String getImageUrl60() {
      return imageUrl60;
  }

  public void setImageUrl60(String imageUrl60) {
      this.imageUrl60 = imageUrl60;
  }

  public String getImageUrl100() {
      return imageUrl100;
  }

  public void setImageUrl100(String imageUrl100) {
      this.imageUrl100 = imageUrl100;
  }

  public String getImageUrl600() {
      return imageUrl600;
  }

  public void setImageUrl600(String imageUrl600) {
      this.imageUrl600 = imageUrl600;
  }

  public String getApi() {
      return api;
  }

  public void setApi(String api) {
      this.api = api;
  }

  public String getApiId() {
      return apiId;
  }

  public void setApiId(String apiId) {
      this.apiId = apiId;
  }

  public String getApiUrl() {
      return apiUrl;
  }

  public void setApiUrl(String apiUrl) {
      this.apiUrl = apiUrl;
  }

  public String getCountry() {
      return country;
  }

  public void setCountry(String country) {
      this.country = country;
  }

  public ArrayList<String> getGenres() {
      return genres;
  }

  public void setGenres(ArrayList<String> genres) {
      this.genres = genres;
  }

  public ArrayList<String> getApiGenreIds() {
      return apiGenreIds;
  }

  public void setApiGenreIds(ArrayList<String> apiGenreIds) {
      this.apiGenreIds = apiGenreIds;
  }

  public Instant getReleaseDate() {
      return releaseDate;
  }

  public void setReleaseDate(Instant releaseDate) {
      this.releaseDate = releaseDate;
  }

  public boolean isExplicit() {
      return explicit;
  }

  public void setExplicit(boolean explicit) {
      this.explicit = explicit;
  }

  public int getEpisodeCount() {
      return episodeCount;
  }

  public void setEpisodeCount(int episodeCount) {
      this.episodeCount = episodeCount;
  }

  public String getDescription() {
      return description;
  }

  public void setDescription(String description) {
      this.description = description;
  }

  public String getSummary() {
      return summary;
  }

  public void setSummary(String summary) {
      this.summary = summary;
  }

  public String getDescriptionSubtitle() {
      return descriptionSubtitle;
  }

  public void setDescriptionSubtitle(String descriptionSubtitle) {
      this.descriptionSubtitle = descriptionSubtitle;
  }

  public String getWebmaster() {
      return webmaster;
  }

  public void setWebmaster(String webmaster) {
      this.webmaster = webmaster;
  }

  public String getOwnerEmail() {
      return ownerEmail;
  }

  public void setOwnerEmail(String ownerEmail) {
      this.ownerEmail = ownerEmail;
  }

  public String getAuthor() {
      return author;
  }

  public void setAuthor(String author) {
      this.author = author;
  }

  public String getWebsiteUrl() {
      return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
      this.websiteUrl = websiteUrl;
  }

  public Instant getUpdatedAt() {
      return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
      this.updatedAt = updatedAt;
  }

  public List<Map<String, String>> getFoundByQueries() {
      return foundByQueries;
  }

  public void setFoundByQueries(List<Map<String, String>> foundByQueries) {
      this.foundByQueries = foundByQueries;
  }  
};



