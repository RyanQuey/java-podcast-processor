package com.ryanquey.podcast;

import com.ryanquey.podcast.kafkaMains.KafkaMain;
import com.ryanquey.podcast.kafkaMains.RunSearchPerTermMain;
import com.ryanquey.podcast.kafkaMains.ExtractPodcastsPerSearchMain;
import com.ryanquey.podcast.kafkaMains.ExtractEpisodesPerPodcastMain;


public class Main extends KafkaMain {

  private static void processArgs(String[] args) throws Exception {
    System.out.println("Starting consumer based on first arg: ");     
    String consumerClass = args[0];
    System.out.print(consumerClass);     
		switch (consumerClass) {
			case "run-search-per-term":
			  RunSearchPerTermMain.startConsumer(); 
				break;
			case "extract-podcasts-per-search":
			  ExtractPodcastsPerSearchMain.startConsumer(); 
				break;
			case "extract-episodes-per-podcast":
			  ExtractEpisodesPerPodcastMain.startConsumer(); 
				break;
			default:
        System.out.println("Invalid arg or no arg, starting ALL OF THEM!");
        // TODO is that the best default? 
			  KafkaMain.startConsumer(); 
				// code block
		}
  }

  public static void main (String[] args) throws Exception {
    try {
      // TODO only call this when settings via cmd line args are sent in
      System.out.println("setting up whatever we'll need to interact with Kafka...");
      KafkaMain.setup();
      System.out.println("starting consumer...");
      Main.startConsumer();
      System.out.println("Finished, tearing down kafka");
      KafkaMain.tearDown();

    } catch (Exception e) {
      System.out.println("Error in Main:");
		  e.printStackTrace();
		  throw e;

    } finally {
      // NOTE this will look like it build successfully even if we errored out. 
      // TODO only do this if we did not catch and throw the error.
      // then find out what error code to use (ie, not 0) for errors and throw that for errors
      Runtime.getRuntime().exit(0);
    }
  }
}
