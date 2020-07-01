package dataClasses.podcast;

import java.util.ArrayList;
// import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.time.Instant;

import dataClasses.searchQuery.SearchQueryUDT;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 *
 */

public class PodcastBase {
  private String language;
  private String feedUrl; // rss feed url

  private String owner; 
  private String primaryGenre;
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
  // NOTE currently is not actually counting the episodes, but based on the info they tell us in "trackCount"
  int episodeCount;

  // to get from rss, that itunes doesn't return in search
  // from description
  private String description;
  // not sure how it would be different from description, but rome seems to include it as part of the itunes rss api
  private String summary;
  // from itunes:subtitle
  private String subtitle;
  // from webMaster
  private String webmaster;
  // from itunes:owner > itunes:email
  private String ownerEmail;
  private String author; //not yet sure how this is distinct from owner. But airflow's podcast for example has different http://feeds.soundcloud.com/users/soundcloud:users:385054355/sounds.rss
  // from image:link
  private String websiteUrl; // TODO make all these urls of java class Url (?)
  private Instant updatedAt;

  // these should match the queryresult
  // list of queries, each query giving term, searchType, api, and when search was performed
  private Set<SearchQueryUDT> foundByQueries; 

  //////////////////////////////////
  // helpers to add to Lists or Sets

  public void addToFoundByQueries (SearchQueryUDT searchQueryUDT) {
    if (this.foundByQueries == null) {
      this.foundByQueries = new HashSet<SearchQueryUDT>();
    }

    this.foundByQueries.add(searchQueryUDT);
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

  public String getSubtitle() {
      return subtitle;
  }

  public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
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

  public Set<SearchQueryUDT> getFoundByQueries() {
      return foundByQueries;
  }

  public void setFoundByQueries(Set<SearchQueryUDT> foundByQueries) {
      this.foundByQueries = foundByQueries;
  }  

};



