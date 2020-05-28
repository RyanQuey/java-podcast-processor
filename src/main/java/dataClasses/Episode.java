package dataClasses;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.time.Instant;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.module.Module; 
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.AbstractITunesObject;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;

import cassandraHelpers.CassandraDb;
import cassandraHelpers.EpisodeDao;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 * TODO will later make its own table, add an index to keywords. Maybe also find a way to search for words from the summary (e.g., "interview" etc)
 * for now, just nesting within the parent podcast
 *
 */
@Entity
@CqlName("episodes_by_order_in_podcast")
public class Episode {
  private Podcast podcast;
  @PartitionKey(0)
  private String podcastApi;
  @PartitionKey(1)
  private String podcastApiId;
  private String podcastWebsiteUrl;

  // TODO figure out what is different from the track info, and store here
  // the rest, get rid of and just reference via the track
  private String summary;
  // just based on the podcast, not specific to the episode
  private String duration; // maybe do different java type, like CqlDuration or something more vanilla java
  private String subtitle;
  @ClusteringColumn(0)
  private Integer orderNum;
  private String imageUrl;

  private String episodeType;
  private Integer episodeNum;
  private Integer seasonNum;
  private String title;
  private String author;
  private Set<String> keywords;
  private boolean explicit;
  private Instant updatedAt;


  // stuff we can get from rss
  private boolean closedCaptioned;

  static public EpisodeDao dao = CassandraDb.inventoryMapper.episodeDao("episodes_by_order_in_podcast");

  // for DAO
  public Episode() {}

  public Episode(SyndEntry entry, Podcast podcast) {
    Module entryModule = entry.getModule(AbstractITunesObject.URI);
    // probably same as before, use EntryInformationImpl rather than EntryInformation
    EntryInformationImpl entryInfo = (EntryInformationImpl) entryModule;

    // directly from podcast
    this.podcast = podcast;
    this.podcastApi = podcast.getApi();
    this.podcastApiId = podcast.getApiId();
    this.podcastWebsiteUrl = podcast.getWebsiteUrl();

    // from rss
    this.summary = entryInfo.getSummary();
    // from rome rss docs: An encapsulation of the duration of a podcast. This will serialize (via .toString()) to HH:MM:SS format, and can parse [H]*H:[M]*M:[S]*S or [M]*M:[S]*S.
    // want to convert to: Alternative ISO 8601 format
    // easy to read but also CQL compatible (https://docs.datastax.com/en/dse/5.1/cql/cql/cql_reference/upsertDates.html#ISO8601format)
    this.duration = "PT" + entryInfo.getDuration().toString();
    this.subtitle = entryInfo.getSubtitle();
    this.explicit = entryInfo.getExplicit();
    this.author = entryInfo.getAuthor();

    // may be will have to pass in the entry instead and do this.podcastWebsiteUrl = entry.getLink();
    this.closedCaptioned = entryInfo.getClosedCaptioned();
    this.orderNum = entryInfo.getOrder();
    // getImage returns a url
    this.imageUrl = entryInfo.getImage().toString();

    this.episodeType = entryInfo.getEpisodeType();
    this.title = entryInfo.getTitle();

    this.keywords = new HashSet<String>(Arrays.asList(entryInfo.getKeywords()));
    this.episodeNum = entryInfo.getEpisode();
    this.seasonNum = entryInfo.getSeason();
    this.updatedAt = Instant.now();

  }

  public String getSummary() {
      return summary;
  }

  public void setSummary(String summary) {
      this.summary = summary;
  }

  public String getPodcastWebsiteUrl() {
      return podcastWebsiteUrl;
  }

  public void setPodcastWebsiteUrl(String podcastWebsiteUrl) {
      this.podcastWebsiteUrl = podcastWebsiteUrl;
  }

  public String getDuration() {
      return duration;
  }

  public void setDuration(String duration) {
      this.duration = duration;
  }

  public String getSubtitle() {
      return subtitle;
  }

  public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
  }

  public int getOrderNum() {
      return orderNum;
  }

  public void setOrderNum(int orderNum) {
      this.orderNum = orderNum;
  }

  public String getImageUrl() {
      return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
  }

  public String getEpisodeType() {
      return episodeType;
  }

  public void setEpisodeType(String episodeType) {
      this.episodeType = episodeType;
  }

  public Integer getEpisodeNum() {
      return episodeNum;
  }

  public void setEpisodeNum(Integer episodeNum) {
      this.episodeNum = episodeNum;
  }

  public Integer getSeasonNum() {
      return seasonNum;
  }

  public void setSeasonNum(Integer seasonNum) {
      this.seasonNum = seasonNum;
  }

  public String getTitle() {
      return title;
  }

  public void setTitle(String title) {
      this.title = title;
  }

  public String getAuthor() {
      return author;
  }

  public void setAuthor(String author) {
      this.author = author;
  }

  public Podcast getPodcast() {
    return podcast;
  }

  public void setPodcast(Podcast podcast) {
    this.podcast = podcast;
  }

  public String getPodcastApi() {
    return podcastApi;
  }

  public void setPodcastApi(String podcastApi) {
    this.podcastApi = podcastApi;
  }

  public String getPodcastApiId() {
    return podcastApiId;
  }

  public void setPodcastApiId(String podcastApiId) {
    this.podcastApiId = podcastApiId;
  }

  public void setOrderNum(Integer orderNum) {
    this.orderNum = orderNum;
  }

  public Set<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(Set<String> keywords) {
    this.keywords = keywords;
  }

  public boolean isExplicit() {
    return explicit;
  }

  public void setExplicit(boolean explicit) {
    this.explicit = explicit;
  }

  public boolean isClosedCaptioned() {
    return closedCaptioned;
  }

  public void setClosedCaptioned(boolean closedCaptioned) {
    this.closedCaptioned = closedCaptioned;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
};



