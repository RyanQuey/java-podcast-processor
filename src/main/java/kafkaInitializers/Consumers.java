// Consumers
// https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
// has example I borrowed from as well

package kafkaInitializers;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

import dataClasses.searchQuery.SearchQuery;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class Consumers {
  ///////////////////////////////////
  // private fields
  static private Properties props = new Properties();
  static {
    props.setProperty("bootstrap.servers", "localhost:9092");
    props.setProperty("group.id", "test");
    props.setProperty("enable.auto.commit", "false");
    // once we have topics that are not just simple strings, cannot use this
    // TODO make a default set as a template, and then use that to create a props field for each consumer instance
    props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  }
  
  static private String[] searchTypes = {
    // empty for getting default, which I believe searches more generally (?) or maybe all terms
    "all",
    "titleTerm", 
    "keywordsTerm", 
    "descriptionTerm",
    "artistTerm"
  };

  static private boolean refreshData = false;




  //////////////////////////////////////
  // some static methods (initializers)

  // take a term and hit external api (Itunes) to get some podcasts and basic data about them that match that term
  // currently runs each term with all the different search types to see what gets returned
  // After a search_query is persisted to database, send record to queuing.podcast-analysis-tool.search-query-with-results
  public static void initializeQueryTermConsumer() throws Exception {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Consumers.props);
    consumer.subscribe(Arrays.asList("queuing.podcast-analysis-tool.query-term"));

    // list of all unprocessed records received
    List<ConsumerRecord<String, String>> buffer = new ArrayList<>();

    final CountDownLatch latch = new CountDownLatch(1);

    // attach shutdown handler to catch control-c
    Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
      @Override
      public void run() {
        consumer.close();
        latch.countDown();
      }
    });

    try {
      latch.await();

      // keep running forever until ctrl+c is pressed
      // TODO this go before or after the latch?
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
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
                  Producers.searchQueryProducer.send(searchQuery)
                }

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
            successful = false;
          }
        }

        if (successful) {
          // mark these records as read
          consumer.commitSync();
        }
      }

    } catch (Throwable e) {
      // does this go here?
      consumer.close();
      System.exit(1);
    }
    System.exit(0);
  }
  
  // Go through results,  and send podcasts feed_url to queuing.podcast_analysis_tool.feed_url
  // this one persists to db in batch
  public static void initializeSearchQueryWithResultsConsumer() throws Exception {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Arrays.asList("queuing.podcast-analysis-tool.search-query-with-results"));
    final int minBatchSize = 200;

    // list of all unprocessed records received
    List<ConsumerRecord<String, String>> buffer = new ArrayList<>();

    final CountDownLatch latch = new CountDownLatch(1);


    // attach shutdown handler to catch control-c
    Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
      @Override
      public void run() {
        consumer.close();
        latch.countDown();
      }
    });

    try {
      latch.await();

      // keep running forever until ctrl+c is pressed
      // TODO this go before or after the latch?
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : records) {
          buffer.add(record);
        }
        if (buffer.size() >= minBatchSize) {
          // TODO implement insertIntoDb
          insertIntoDb(buffer);
          consumer.commitSync();
          buffer.clear();
        }
      }

    } catch (Throwable e) {
      // does this go here?
      consumer.close();
      System.exit(1);
    }
    System.exit(0);
  }

  public static void initializeLogger () {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Pattern.compile("queue.*"));
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        boolean successful = true;
        for (ConsumerRecord<String, String> record : records) {
          try {
            // run the search
            for (String searchType : Consumer.searchTypes) {
              System.out.println("Got record:");
              System.out.println(record);
              System.out.println(record.value());
            }

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

  static void initializeAll () {
    Consumers.initializeLogger();
    Consumers.initializeQueryTermConsumer();
    Consumers.initializeSearchQueryWithResultsConsumer();
  }
}
