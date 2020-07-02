package com.ryanquey.podcast.dataClasses.episode;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.time.Instant;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.module.Module; 
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.AbstractITunesObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.ryanquey.podcast.dataClasses.podcast.Podcast;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 * TODO will later make its own table, add an index to keywords. Maybe also find a way to search for words from the summary (e.g., "interview" etc)
 * for now, just nesting within the parent podcast
 *
 */
public class Episode extends EpisodeBase {
  private Podcast podcast;

  // constructors
  public Episode() {}

  public Episode(SyndEntry entry, Podcast podcast) {
    Module entryModule = entry.getModule(AbstractITunesObject.URI);
    // probably same as before, use EntryInformationImpl rather than EntryInformation
    EntryInformationImpl entryInfo = (EntryInformationImpl) entryModule;

    ////////////////////
    // directly from podcast
    this.setPodcast(podcast);
    this.setPodcastApi(podcast.getApi());
    this.setPodcastApiId(podcast.getApiId());
    this.setPodcastWebsiteUrl(podcast.getWebsiteUrl());


    ////////////////////
    // from rss
    // NOTE entryInfo.getUri is useless, returns some itunes protocol definiton or something. needs to be entry.uri
    // https://stackoverflow.com/a/36450507/6952495
    this.setEpisodeGuid(entry.getUri());

    this.setEpisodeUrl(entry.getLink());
    if (entry.getDescription() != null) {
      this.setDescription(entry.getDescription().getValue());
    }
    this.setComments(entry.getComments());

    // https://github.com/rometools/rome/blob/master/rome/src/main/java/com/rometools/rome/feed/synd/SyndContent.java
    // though, at least for some blogs it is identical to description
    List<SyndContent> contents = entry.getContents();
    String contentStr = "";
    for (SyndContent content : contents) {
      contentStr += content.getValue();
    }
    this.setContent(contentStr);
    // NOTE entry returns a Date instance
    Date date = entry.getUpdatedDate();
    if (date != null) {
      // convert to localDate
      this.setEpisodeUpdatedDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
    // NOTE entry returns a Date instance
    Date pubDate = entry.getPublishedDate();
    if (pubDate != null) {
      
      this.setPublishedDate(pubDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

      
    // maybe want...maybe too much hassle digging into source code. Did not see any official documentation on this either
    //List<SyndPerson> contributors = entry.getContributors();
    // returns the same class as contributors, and may be often the same stuff. But might get anyways
    // entry.getAuthors();
    //TODO look into
    //entry.getLinks();



    // probably don't need, it seems like just a shorter version of the description, at least for the episodes I have seen so far
    this.setSummary(entryInfo.getSummary());
    // from rome rss docs: An encapsulation of the duration of a podcast. This will serialize (via .toString()) to HH:MM:SS format, and can parse [H]*H:[M]*M:[S]*S or [M]*M:[S]*S.
    // want to convert to: Alternative ISO 8601 format
    // easy to read but also CQL compatible (https://docs.datastax.com/en/dse/5.1/cql/cql/cql_reference/upsertDates.html#ISO8601format)
    // seems like you need the years on there no matter what

    try {
      String durationStr = entryInfo.getDuration().toString();
      if (durationStr.length() == 5) {
        // "00:00"
        durationStr = "00:" + durationStr;
      }
      this.setDuration("P0000-00-00T" + durationStr);
    } catch (Exception e) {
      if (entryInfo.getDuration() != null) {
        System.out.println("Skipping duration " + entryInfo.getDuration().toString());
      } else {
        System.out.println("Skipping duration, since there is none");
      }
    }
    this.setSubtitle(entryInfo.getSubtitle());
    this.setExplicit(entryInfo.getExplicit());
    this.setAuthor(entryInfo.getAuthor());

    // may be will have to pass in the entry instead and do this.podcastWebsiteUrl = entry.getLink();
    this.setClosedCaptioned(entryInfo.getClosedCaptioned());

    // make sure this Integer doesn't return null, or else it returns nullPointerException when setting as int
    // NOTE unfortunately, I think sometimes might just end up defaulting to zero
    if (entryInfo.getOrder() != null) {
      this.setOrderNum(entryInfo.getOrder());
    }
    // getImage returns a url
    if (entryInfo.getImage() != null) {
      this.setImageUrl(entryInfo.getImage().toString());
    }

    this.setEpisodeType(entryInfo.getEpisodeType());
    this.setTitle(entryInfo.getTitle());

    this.setKeywords(new HashSet<String>(Arrays.asList(entryInfo.getKeywords())));
    // make sure this Integer doesn't return null, or else it returns nullPointerException when setting as int
    if (entryInfo.getEpisode() != null) {
      this.setEpisodeNum(entryInfo.getEpisode());
    }
    // make sure this Integer doesn't return null, or else it returns nullPointerException when setting as int
    if (entryInfo.getSeason() != null) {
      this.setSeasonNum(entryInfo.getSeason());
    }
    this.setUpdatedAt(Instant.now());

  }

  // persists to all episode tables
  public void persist () throws Exception {
    EpisodeByPodcastRecord e =  new EpisodeByPodcastRecord(this);

    EpisodeByPodcastDao dao = EpisodeByPodcastRecord.getDao();

    dao.save(e);
  }

  //////////////////////
  // getters and setters
  // don't serialize to jackson (for Kafka)
  @JsonIgnore
  public Podcast getPodcast() {
    return podcast;
  }

  // don't deserialize to jackson (for Kafka)
  @JsonIgnore
  public void setPodcast(Podcast podcast) {
    this.podcast = podcast;
  }
};



