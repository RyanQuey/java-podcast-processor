package kafkaMains;

import kafkaHelpers.Consumers;

public class ExtractEpisodesPerPodcastMain extends KafkaMain {

  private static void startConsumer () throws Exception {
    System.out.println("*************************");
    System.out.println("extract out and persist episodes for each podcast retrieved by a search");
    Consumers.initializePodcastConsumer();
  }

  public static void main (String[] args) throws Exception {
    try {
      // TODO only call this when settings via cmd line args are sent in
      KafkaMain.setup();
      startConsumer();
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
