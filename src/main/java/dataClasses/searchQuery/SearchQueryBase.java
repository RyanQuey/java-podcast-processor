package dataClasses.searchQuery;

import java.time.Instant;
import java.util.UUID;


/* 
 * Represents a single set of search results, given a term and search type
 * Has no uuid; don't want there to be two record with same term and search type to exist. 
 * If want to search, search by term and search type. We lose the ability to see change over time, but gain simplicity, persist less records, and then it is easier to reason about the SearchQuery UDT also.
 * For our use case, we don't need historical records of the podcasts. If an episode disappears from the rss, you probably can't retrieve it either.
 * So just persist what we have currently
 *
 */

public class SearchQueryBase {
  public String term;
  public String searchType;
  public String resultJson;
  public String externalApi = "itunes";
  public Instant updatedAt;
  public Integer podcastCount;

  public String getTerm() {
    return term;
  }
  public void setTerm(String term) {
    this.term = term;
  }
  public String getSearchType() {
    return searchType;
  }
  public void setSearchType(String searchType) {
    this.searchType = searchType;
  }
  public String getExternalApi() {
    return externalApi;
  }
  public void setExternalApi(String externalApi) {
    this.externalApi = externalApi;
  }
  public String getResultJson() {
    return resultJson;
  }
  public void setResultJson(String resultJson) {
    this.resultJson = resultJson;
  }
  public Instant getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
  public Integer getPodcastCount() {
    return podcastCount;
  }
  public void setPodcastCount(Integer podcastCount) {
    this.podcastCount = podcastCount;
  }


}


