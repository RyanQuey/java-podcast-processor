package dataClasses.episode;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.time.Instant;


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

  // TODO figure out what is different from the track info, and store here
  // the rest, get rid of and just reference via the track
  private String summary;
  // just based on the podcast, not specific to the episode
  private String duration; // maybe do different java type, like CqlDuration or something more vanilla java
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



