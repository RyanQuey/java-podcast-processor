package dataClasses;

import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;
import java.util.ArrayList;
import org.json.JSONObject;

import helpers.HttpReq;
import helpers.FileHelpers;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.module.Module; // TODO confirm
import com.rometools.modules.itunes.EntryInformation;
import com.rometools.modules.itunes.EntryInformationImpl;
import com.rometools.modules.itunes.AbstractITunesObject;

/* 
 * For one file, gets all search results and retrieves the rss feed data
 *
 */
  // TODO use the mapper https://github.com/datastax/java-driver/tree/4.x/manual/mapper#dao-interface
public class Episode {
  // TODO figure out what is different from the track info, and store here
  // the rest, get rid of and just reference via the track
  String summary;
  // may be will have to pass in the entry instead and do String websiteUrl;
  String websiteUrl;
  String duration;
  String guid;
  String subtitle;
  String description;
  int order;
  String imageUrl;

  String episodeType;
  String episodeNum;
  String seasonNum;
  String title;
  String author;
  String keywords;
  boolean explicit;

  private String rssFeedData;
  private Podcast podcast;

  // stuff we can get from rss
  boolean closedCaptioned;


  // see here; base what we do off of tests
  // https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/test/java/com/rometools/modules/itunes/ITunesParserTest.java#L128

  // TODO NEXT
  // also here: https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/EntryInformation.java
  // 
  // https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/EntryInformationImpl.java#L37-L42
  // TODO make a flag to signal initialization from our db rather than from rss
  public Episode(SyndEntry entry) {
    Module entryModule = entry.getModule(AbstractITunesObject.URI);
    // probably same as before, use EntryInformationImpl rather than EntryInformation
    EntryInformationImpl entryInfo = (EntryInformationImpl) entryModule;


    // from rss
    this.closedCaptioned = entryInfo.getClosedCaptioned()
    this.summary = entryInfo.getSummary();
    // may be will have to pass in the entry instead and do this.websiteUrl = entry.getLink();
    this.websiteUrl = entryInfo.getLink();
    // from rome rss docs: An encapsulation of the duration of a podcast. This will serialize (via .toString()) to HH:MM:SS format, and can parse [H]*H:[M]*M:[S]*S or [M]*M:[S]*S.
    this.duration = entryInfo.getDuration().toString();
    this.guid = entryInfo.getGuid();
    this.subtitle = entryInfo.getSubtitle();
    this.description = entryInfo.getDescription();
    this.order = entryInfo.getOrder();
    this.imageUrl = entryInfo.getImage().toString();
    this.explicit = entryInfo.getExplicit();

    this.episodeType = entryInfo.getEpisodeType();
    this.episodeNum = entryInfo.getEpisode();
    this.seasonNum = entryInfo.getSeason();
    this.title = entryInfo.getTitle();
    this.author = entryInfo.getAuthor();
    this.keywords = entryInfo.getKeywords();
  }

  public String getSummary() {
      return summary;
  }

  public void setSummary(String summary) {
      this.summary = summary;
  }

  public String getWebsiteUrl() {
      return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
      this.websiteUrl = websiteUrl;
  }

  public String getDuration() {
      return duration;
  }

  public void setDuration(String duration) {
      this.duration = duration;
  }

  public String getGuid() {
      return guid;
  }

  public void setGuid(String guid) {
      this.guid = guid;
  }

  public String getSubtitle() {
      return subtitle;
  }

  public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
  }

  public String getDescription() {
      return description;
  }

  public void setDescription(String description) {
      this.description = description;
  }

  public int getOrder() {
      return order;
  }

  public void setOrder(int order) {
      this.order = order;
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

  public String getEpisodeNum() {
      return episodeNum;
  }

  public void setEpisodeNum(String episodeNum) {
      this.episodeNum = episodeNum;
  }

  public String getSeasonNum() {
      return seasonNum;
  }

  public void setSeasonNum(String seasonNum) {
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

  public String getKeywords() {
      return keywords;
  }

  public void setKeywords(String keywords) {
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


  // TODO use the mapper https://github.com/datastax/java-driver/tree/4.x/manual/mapper#dao-interface
};



