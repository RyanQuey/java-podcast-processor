package dataClasses.episode;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.datastax.oss.driver.api.core.data.CqlDuration;


/* 
 * For one file, gets all search results and retrieves the rss feed data
 * TODO will later make its own table, add an index to keywords. Maybe also find a way to search for words from the summary (e.g., "interview" etc)
 * for now, just nesting within the parent podcast
 *
 */
public class EpisodeBase {
  private String podcastApi;
  private String podcastApiId;
  private String podcastWebsiteUrl;

  private String episodeGuid;

  // entry.getLink();
  private String episodeUrl;
  // entry.getUri();
  // https://stackoverflow.com/a/36450507/6952495

  private String description;
  private String content;
  private String comments;
  // Rome rss returns a date, so saving as date
  private LocalDate episodeUpdatedDate;
  private LocalDate publishedDate;




  // TODO figure out what is different from the track info, and store here
  // the rest, get rid of and just reference via the track
  private String summary;
  // just based on the podcast, not specific to the episode
  private CqlDuration duration; // maybe do different java type, like CqlDuration or something more vanilla java
  private String subtitle;
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

  public CqlDuration getDuration() {
      return duration;
  }

  public void setDuration(String duration) {
      this.duration = CqlDuration.from(duration);
  }

  public void setDuration(CqlDuration duration) {
      this.duration = duration;
  }

  public String getSubtitle() {
      return subtitle;
  }

  public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
  }

  public Integer getOrderNum() {
      return orderNum;
  }

  public void setOrderNum(Integer orderNum) {
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

  public String getEpisodeGuid() {
    return episodeGuid;
  }

  public void setEpisodeGuid(String episodeGuid) {
    this.episodeGuid = episodeGuid;
  }

  public String getEpisodeUrl() {
    return episodeUrl;
  }

  public void setEpisodeUrl(String episodeUrl) {
    this.episodeUrl = episodeUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public LocalDate getEpisodeUpdatedDate() {
    return episodeUpdatedDate;
  }

  public void setEpisodeUpdatedDate(LocalDate episodeUpdatedDate) {
    this.episodeUpdatedDate = episodeUpdatedDate;
  }

  // NOTE actually probably inaccurate because it does not set the local time zone, but whatever, it's close enough and we are not bothering to find what their time zone was anyway.
  public void setEpisodeUpdatedDate(Date episodeUpdatedDate) {
    if (episodeUpdatedDate != null) {
      this.episodeUpdatedDate = episodeUpdatedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    } else {
      this.episodeUpdatedDate = null;
    }
  }

  public LocalDate getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(LocalDate publishedDate) {
    this.publishedDate = publishedDate;
  }

  public void setPublishedDate(Date publishedDate) {
    if (publishedDate != null) {
      this.publishedDate = publishedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    } else {
      this.publishedDate = null;
    }
  }

};



