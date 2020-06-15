// Consumers
// https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
// has example I borrowed from as well

package kafkaHelpers;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
// import org.apache.kafka.streams.KafkaStreams;
// import org.apache.kafka.streams.StreamsBuilder;
// import org.apache.kafka.streams.StreamsConfig;
// import org.apache.kafka.streams.Topology;
// import org.apache.kafka.streams.kstream.KeyValueMapper;
// import org.apache.kafka.streams.kstream.Materialized;
// import org.apache.kafka.streams.kstream.Produced;
// import org.apache.kafka.streams.kstream.ValueMapper;
// import org.apache.kafka.streams.state.KeyValueStore;

import dataClasses.podcast.Podcast;
import dataClasses.searchQuery.SearchQuery;
import dataClasses.searchQuery.SearchQueryBase;
import dataClasses.episode.Episode;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.lang.InterruptedException;
import java.util.regex.Pattern;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Strings;

public class Consumers {
  ///////////////////////////////////
  // private fields
  private static void setPropertyDefaults (Properties props) {
    props.setProperty("bootstrap.servers", "localhost:9092");
    props.setProperty("group.id", "test");
    props.setProperty("enable.auto.commit", "false");
    // once we have topics that are not just simple strings, cannot use this
    // TODO make a default set as a template, and then use that to create a props field for each consumer instance
    props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  }

  private static Properties stringProps = new Properties();
  private static Properties searchQueryProps = new Properties();
  private static Properties podcastProps = new Properties();
  private static Properties episodeProps = new Properties();
  static {
    Consumers.setPropertyDefaults(stringProps);
    Consumers.setPropertyDefaults(searchQueryProps);
    Consumers.setPropertyDefaults(podcastProps);
    Consumers.setPropertyDefaults(episodeProps);

    searchQueryProps.put("value.deserializer", "kafkaHelpers.serializers.SearchQueryDeserializer");
    podcastProps.put("value.deserializer", "kafkaHelpers.serializers.PodcastDeserializer");
    episodeProps.put("value.deserializer", "kafkaHelpers.serializers.EpisodeDeserializer");
  }
  
   private static String[] searchTypes = {
    // empty for getting default, which I believe searches more generally (?) or maybe all terms
    "all",
    "titleTerm", 
    "keywordsTerm", 
    "descriptionTerm",
    "artistTerm"
  };

  private static boolean refreshData = false;

  // if we want consumers to run
  public static boolean running = true;

  // TODO maybe make a separate class in a helpers file or something
  // Try these next
  // https://stackoverflow.com/a/9302776/6952495
  private static int spinnerIndex = 0;
  private static String[] logos = {"\\", "|", "/", "-"};
  private static void spin () {
    System.out.print(Strings.repeat("\b", 12));
    System.out.print("polling..." + logos[spinnerIndex]);
    spinnerIndex ++;
    if (spinnerIndex == logos.length) {
      spinnerIndex = 0;
    }
  }
  //////////////////////////////////////
  // some static methods (initializers)

  // take a term and hit external api (Itunes) to get some podcasts and basic data about them that match that term
  // currently runs each term with all the different search types to see what gets returned
  // After a search_query is persisted to database, send record to queue.podcast-analysis-tool.search-query-with-results
  // TODO rename all these to reflect what topic it consumes, and to show what it does. Maybe runSearchQueryForTerm () {}
  public static void initializeQueryTermConsumer() throws Exception {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(Consumers.stringProps);
    consumer.subscribe(Arrays.asList("queue.podcast-analysis-tool.query-term"));


    try {
      while (Consumers.running) {
        // ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        Consumers.spin();
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        boolean successful = true;

        for (ConsumerRecord<String, String> record : records) {
          try {
            // run the search
            String term = record.value();
            for (String searchType : Consumers.searchTypes) {
              try {
                // hit the external api, unless search has been done recently enough
                SearchQuery searchQuery = new SearchQuery(term, searchType);
                searchQuery.performSearchIfNecessary(Consumers.refreshData);

                // if got new results, send results
                if (searchQuery.madeApiCall) {
                  // TODO not doing keys or partitions yet
                  ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("queue.podcast-analysis-tool.search-results-json", searchQuery.resultJson);
                  Producers.stringProducer.send(producerRecord);
                }
                // to get podcast count for the search
                searchQuery.extractPodcasts();
                searchQuery.persist();

              } catch (Exception e) {
                System.out.println("Skipping searchQuery: " + term + " for type: " + searchType + "due to error");
                // should log error already before this

                // Stop hitting their API if we max out the quota
                // NOTE this conditional is a little bit fragile, if they ever change their message. But works for now TODO
                if (e.toString().equals("java.io.IOException: Server returned HTTP response code: 403 for URL: https://itunes.apple.com/search")) {
                  System.out.println("itunes doesn't want us to query anymore, consider taking a break...TODO");
                } 
              }
            }

          } catch (Throwable e) {
            e.printStackTrace();
            successful = false;
          }
        }

        if (successful) {
          // mark these records as read
          consumer.commitSync();
        }
      }
      consumer.close();

    } catch (Throwable e) {
      // does this go here?
      consumer.close();
      // System.exit(1);
    }
    // System.exit(0);
  }
  
  // Go through results,  and send podcasts feed_url to queue.podcast_analysis_tool.feed_url
  public static void initializeSearchResultsJsonConsumer() throws Exception {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Consumers.stringProps);
    consumer.subscribe(Arrays.asList("queue.podcast-analysis-tool.search-results-json"));

    final CountDownLatch latch = new CountDownLatch(1);

    // attach shutdown handler to catch control-c
    // TODO will this work when not with kafka streams? 
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        Consumers.running = false;
      }
    });

    try {
      // skipping for now, need new system TODO
      // latch.await();

      // keep running forever until ctrl+c is pressed
      // TODO this go before or after the latch?
      while (Consumers.running) {
        Consumers.spin();
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        boolean successful = true;

        // for each search query, go through its results and get out all the feedurls
        for (ConsumerRecord<String, String> record : records) {
          try {
            String resultJson = record.value();
            // our SearchQuery class manages this business logic, so use that
            SearchQuery sq = new SearchQuery();
            sq.setResultJson(resultJson);

            // currently just doing by extracting out podcasts, since all of our feed-url related logic is contained in the Podcast class currently
            // NOTE currently this also fetches the rss feed, in order to update the podcast based on the rss feed
            sq.extractPodcasts();

            // for each podcast, send its feedUrl to the feed-url topic
            for (Podcast p : sq.getPodcasts()) {
              // persist podcast as returned from itunes api NOTE will have to update again once we receive data from rss

              // send podcast  to topic, where it will be persisted and updated based on rss
              // NOTE alternatively to this, set a hook on cassandra that sends to topic when a podcast is persisted (?) TODO
              ProducerRecord<String, Podcast> producerRecord = new ProducerRecord<String, Podcast>("queue.podcast-analysis-tool.podcast", p);

              Producers.podcastProducer.send(producerRecord);
            }

          } catch (Throwable e) {
            // just don't mark as successful
            e.printStackTrace();
            successful = false;
          }
        }

        if (successful) {
          // mark these records as read
          consumer.commitSync();
        }
      }
      // end while loop
      consumer.close();

    } catch (Throwable e) {
      // does this go here?
      consumer.close();
      // System.exit(1);
    }
    // System.exit(0);
  }

  public static void initializePodcastConsumer() throws Exception {
    KafkaConsumer<String, Podcast> consumer = new KafkaConsumer<>(Consumers.podcastProps);
    consumer.subscribe(Arrays.asList("queue.podcast-analysis-tool.podcast"));

    try {
      while (Consumers.running) {
        Consumers.spin();
        ConsumerRecords<String, Podcast> records = consumer.poll(Duration.ofMillis(100));
        boolean successful = true;
        // for each search query, go through its results and get out all the feedurls
        for (ConsumerRecord<String, Podcast> record : records) {
          try {
            Podcast p = record.value();

            // since the rss feed might have data that's more up to date than itunes search api
            // NOTE this performs the fetch to get the rss feed for us
            p.updateBasedOnRss();

            p.persist();

            // send each episode to episodes topic
            p.extractEpisodes();
            for (Episode episode : p.getEpisodes()) {
              ProducerRecord<String, Episode> producerRecord = new ProducerRecord<String, Episode>("queue.podcast-analysis-tool.episode", episode);

              Producers.episodeProducer.send(producerRecord);
            }
          } catch (Throwable e) {
            e.printStackTrace();
            successful = false;
          }
        }

        if (successful) {
          // mark these records as read
          consumer.commitSync();
        }
      } // end while loop

      consumer.close();

    } catch (Throwable e) {
      // does this go here?
      consumer.close();
      // System.exit(1);
    }
    // System.exit(0);
  }








  /*
  // this one consumes all the topics and just as logging
  // For both debugging and just playing around with Kafka
  public static void initializeLogger () {
    // hoping I can just turn all things into strings. Maybe will have to do something to call toString on all objects
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Consumers.stringProps);
    consumer.subscribe(Pattern.compile("queue.*"));

    final CountDownLatch latch = new CountDownLatch(1);

    // attach shutdown handler to catch control-c
    Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook4") {
      @Override
      public void run() {
        consumer.close();
        latch.countDown();
      }
    });

    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
      boolean successful = true;
      for (ConsumerRecord<String, String> record : records) {
        try {
          // run the search
          System.out.println("Got record:");
          System.out.println(record);
          System.out.println(record.value());

        } catch (Throwable e) {
          successful = false;
        }
      }

      if (successful) {
        // mark these records as read
        consumer.commitSync();
      }
    }
  }
  */

  // eventually want all these consumers running on separate machines. But for now just running them all in async jobs
  // TODO I'm trying to run all while avoiding dangers of multithreading. But in reality I don't know how these all work, and the whole latch mechanism is still a mystery. So need to figure this stuff out
  // TODO this doesn't really work currently. All will start, but when it starts processing stuff it will process some then break. 
  // Very brittle
  public static void initializeAll () throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);

    Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook-final") {
      @Override
      public void run() {
        // maybe can allow consumers to close before shutting stuff down?
        try {
          // in the consumer functions themselves, they will close their own consumers
          // TODO make it all close here, just keeps it simple
          System.out.println("waiting for kafka to close consumers...");
          Thread.sleep(2000);
        }catch (InterruptedException e) {
          System.out.println("Don't do this! Why interrupting the sleep!");
        } finally {
          // basically will end the latch.await() thing below
          latch.countDown();
        }
      }
    });

    // consider running each in separate thread?
    CompletableFuture.runAsync(() -> {
      try {
        System.out.println("initializeQueryTermConsumer:");
        Consumers.initializeQueryTermConsumer();
      } catch (Exception e) {
      }
    });
    // consider running each in separate thread?
    CompletableFuture.runAsync(() -> {
      try {
        System.out.println("initializeSearchResultsJsonConsumer:");
        Consumers.initializeSearchResultsJsonConsumer();
      } catch (Exception e) {
      }
    });
    // consider running each in separate thread?
    CompletableFuture.runAsync(() -> {
      try {
        System.out.println("initializePodcastConsumer:");
        Consumers.initializePodcastConsumer();
      } catch (Exception e) {
      }
    });


    // don't keep going, just wait until latch is released
    latch.await();

    /*
    CompletableFuture.runAsync(() -> {
      // Consumers.initializeEpisodeConsumer();
      System.out.println("initializeLogger:");
      Consumers.initializeLogger();
    });
    */
  }
}
