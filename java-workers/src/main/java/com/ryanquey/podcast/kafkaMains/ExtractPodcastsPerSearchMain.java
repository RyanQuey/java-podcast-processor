package com.ryanquey.podcast.kafkaMains;
import com.ryanquey.podcast.kafkaHelpers.Consumers;

public class ExtractPodcastsPerSearchMain extends KafkaMain {

  public static void startConsumer () throws Exception {
    System.out.println("*************************");
    System.out.println("extract out (but don't yet persist) podcasts for each search ran");
    Consumers.initializeSearchResultsJsonConsumer();
  }

}
