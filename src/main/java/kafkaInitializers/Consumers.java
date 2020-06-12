// Consumers
// https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
// has example I borrowed from as well

package kafkaInitializers;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Consumers {
  ///////////////////////////////////
  // private fields
  private  Properties props = new Properties();
  props.setProperty("bootstrap.servers", "localhost:9092");
  props.setProperty("group.id", "test");
  props.setProperty("enable.auto.commit", "false");
  // once we have topics that are not just simple strings, cannot use this
  // TODO
  props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

  static private String[] searchTypes = {
    // empty for getting default, which I believe searches more generally (?) or maybe all terms
    "all",
    "titleTerm", 
    "keywordsTerm", 
    "descriptionTerm",
    "artistTerm"
  };

  private refreshData = false;




  //////////////////////////////////////
  // some static methods (initializers)

  public static void initializeQueryTermConsumer(String[] args) throws Exception {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
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
            for (String searchType : Consumer.searchTypes) {
              try {
                // hit the external api, unless search has been done recently enough
                SearchQuery searchQuery = new SearchQuery(term, searchType);
                searchQuery.performSearchIfNecessary(refreshData);

                // if got new results, send results
                if (searchQuery.madeApiCall) {

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

  // this one persists to db in batch
  public static void initializeSearchQueryWithResultsConsumer(String[] args) throws Exception {
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

  static initializeLogger () {
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Pattern.compile("queue.*"));
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        boolean successful = true;
        for (ConsumerRecord<String, String> record : records) {
          try {
            // run the search
            for (String searchType : Consumer.searchTypes) {
              System.println.out("Got record:");
              System.println.out(record);
              System.println.out(record.value());
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

  static initializeAll () {
    Consumers.initializeLogger();
    Consumers.initializeQueryTermConsumer();
    Consumers.initializeSearchQueryWithResultsConsumer();
  }
}
