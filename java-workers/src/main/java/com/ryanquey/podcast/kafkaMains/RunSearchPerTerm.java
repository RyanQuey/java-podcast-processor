package com.ryanquey.podcast.kafkaMains;
import com.ryanquey.podcast.kafkaHelpers.Consumers;

public class RunSearchPerTerm {

  public static void startConsumer () throws Exception {
    System.out.println("*************************");
    System.out.println("run a search query for each term in topic");
    Consumers.runSearchPerTermConsumer();
  }
}
