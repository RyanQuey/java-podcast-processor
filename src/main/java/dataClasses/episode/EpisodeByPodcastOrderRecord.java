package dataClasses.episode;
// import java.util.Set;
// import java.util.HashSet;
// import java.util.Arrays;
// import java.time.Instant;
// 
// import com.rometools.rome.feed.synd.SyndEntry;
// import com.rometools.rome.feed.module.Module; 
// import com.rometools.modules.itunes.EntryInformationImpl;
// import com.rometools.modules.itunes.AbstractITunesObject;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;

import cassandraHelpers.CassandraDb;
import helpers.DataClassesHelpers;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 * TODO will later make its own table, add an index to keywords. Maybe also find a way to search for words from the summary (e.g., "interview" etc)
 * for now, just nesting within the parent podcast
 *
 */
@Entity
@CqlName("episodes_by_order_in_podcast")
public class EpisodeByPodcastOrderRecord extends EpisodeBase {
  @PartitionKey(0)
  private String podcastApi;
  @PartitionKey(1)
  private String podcastApiId;
  @ClusteringColumn(0)
  private Integer orderNum;

  static public EpisodeByPodcastOrderDao getDao () {
    return CassandraDb.inventoryMapper.episodeByPodcastOrderDao("episodes_by_order_in_podcast");
  }

  // for DAO
  public EpisodeByPodcastOrderRecord () {}

  public EpisodeByPodcastOrderRecord (Episode episode) {
    DataClassesHelpers.copyMatchingFields(episode, this);
  }
};



