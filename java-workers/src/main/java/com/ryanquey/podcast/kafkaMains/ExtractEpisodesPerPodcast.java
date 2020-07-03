package com.ryanquey.podcast.kafkaMains;

import com.ryanquey.podcast.kafkaHelpers.Consumers;

public class ExtractEpisodesPerPodcast {

  public static void startConsumer () throws Exception {
    System.out.println("*************************");
    System.out.println("extract out and persist episodes for each podcast retrieved by a search");
    Consumers.initializePodcastConsumer();
  }

}
