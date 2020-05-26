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
  private String summary;
  // may be will have to pass in the entry instead and do String websiteUrl;
  private String websiteUrl;
  private String duration;
  private String subtitle;
  private Integer order;
  private String imageUrl;

  private String episodeType;
  private Integer episodeNum;
  private Integer seasonNum;
  private String title;
  private String author;
  private String[] keywords;
  private boolean explicit;

  private String rssFeedData;
  public Podcast podcast;

  // stuff we can get from rss
  private boolean closedCaptioned;


  // see here; base what we do off of tests
  // https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/test/java/com/rometools/modules/itunes/ITunesParserTest.java#L128

  // TODO NEXT
  // also here: https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/EntryInformation.java
  // 
  // https://github.com/rometools/rome/blob/b91b88f8e9fdc239a2258e4efae06b83dffb2621/rome-modules/src/main/java/com/rometools/modules/itunes/EntryInformationImpl.java#L37-L42
  // TODO make a flag to signal initialization from our db rather than from rss
  public Episode(SyndEntry entry, Podcast podcast) {
    Module entryModule = entry.getModule(AbstractITunesObject.URI);
    // probably same as before, use EntryInformationImpl rather than EntryInformation
    EntryInformationImpl entryInfo = (EntryInformationImpl) entryModule;


    // from rss
    this.summary = entryInfo.getSummary();
    // from rome rss docs: An encapsulation of the duration of a podcast. This will serialize (via .toString()) to HH:MM:SS format, and can parse [H]*H:[M]*M:[S]*S or [M]*M:[S]*S.
    this.duration = entryInfo.getDuration().toString();
    this.subtitle = entryInfo.getSubtitle();
    this.explicit = entryInfo.getExplicit();
    this.author = entryInfo.getAuthor();

    // may be will have to pass in the entry instead and do this.websiteUrl = entry.getLink();
    this.closedCaptioned = entryInfo.getClosedCaptioned();
    this.order = entryInfo.getOrder();
    // getImage returns a url
    this.imageUrl = entryInfo.getImage().toString();

    this.episodeType = entryInfo.getEpisodeType();
    this.title = entryInfo.getTitle();

    this.keywords = entryInfo.getKeywords();
    this.episodeNum = entryInfo.getEpisode();
    this.seasonNum = entryInfo.getSeason();

    this.podcast = podcast;
    this.websiteUrl = podcast.getWebsiteUrl();
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

  public String getSubtitle() {
      return subtitle;
  }

  public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
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

  public String[] getKeywords() {
      return keywords;
  }

  public void setKeywords(String[] keywords) {
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



