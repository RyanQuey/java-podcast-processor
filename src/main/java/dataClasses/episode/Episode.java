package dataClasses.episode;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.time.Instant;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.module.Module; 
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.AbstractITunesObject;

import dataClasses.podcast.Podcast;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 * TODO will later make its own table, add an index to keywords. Maybe also find a way to search for words from the summary (e.g., "interview" etc)
 * for now, just nesting within the parent podcast
 *
 */
public class Episode extends EpisodeBase {
  private Podcast podcast;

  // constructors
  public Episode(SyndEntry entry, Podcast podcast) {
    Module entryModule = entry.getModule(AbstractITunesObject.URI);
    // probably same as before, use EntryInformationImpl rather than EntryInformation
    EntryInformationImpl entryInfo = (EntryInformationImpl) entryModule;

    // directly from podcast
    this.setPodcast(podcast);
    this.setPodcastApi(podcast.getApi());
    this.setPodcastApiId(podcast.getApiId());
    this.setPodcastWebsiteUrl(podcast.getWebsiteUrl());

    // from rss
    this.setSummary(entryInfo.getSummary());
    // from rome rss docs: An encapsulation of the duration of a podcast. This will serialize (via .toString()) to HH:MM:SS format, and can parse [H]*H:[M]*M:[S]*S or [M]*M:[S]*S.
    // want to convert to: Alternative ISO 8601 format
    // easy to read but also CQL compatible (https://docs.datastax.com/en/dse/5.1/cql/cql/cql_reference/upsertDates.html#ISO8601format)
    this.setDuration("PT" + entryInfo.getDuration().toString());
    this.setSubtitle(entryInfo.getSubtitle());
    this.setExplicit(entryInfo.getExplicit());
    this.setAuthor(entryInfo.getAuthor());

    // may be will have to pass in the entry instead and do this.podcastWebsiteUrl = entry.getLink();
    this.setClosedCaptioned(entryInfo.getClosedCaptioned());
    this.setOrderNum(entryInfo.getOrder());
    // getImage returns a url
    this.setImageUrl(entryInfo.getImage().toString());

    this.setEpisodeType(entryInfo.getEpisodeType());
    this.setTitle(entryInfo.getTitle());

    this.setKeywords(new HashSet<String>(Arrays.asList(entryInfo.getKeywords())));
    this.setEpisodeNum(entryInfo.getEpisode());
    this.setSeasonNum(entryInfo.getSeason());
    this.setUpdatedAt(Instant.now());

  }

  // persists to all episode tables
  public void persist () throws Exception {
    EpisodeByPodcastOrderRecord.getDao().save(new EpisodeByPodcastOrderRecord(this));
  }

  //////////////////////
  // getters and setters
  public Podcast getPodcast() {
    return podcast;
  }

  public void setPodcast(Podcast podcast) {
    this.podcast = podcast;
  }
};



