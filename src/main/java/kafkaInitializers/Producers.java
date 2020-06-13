// Producers
// https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html

package kafkaInitializers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
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

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import dataClasses.searchQuery.SearchQuery;

public class Producers {
  ///////////////////////////////////
  // private fields
  static private Properties props = new Properties();
  static {
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  }

  static Producer<String, String> stringProducer = new KafkaProducer<>(props);
  static Producer<String, SearchQuery> searchQueryProducer = new KafkaProducer<>(props);
  // use like:
  // producer.send(new ProducerRecord<String, String>("queue.podcast-analysis-tool.search-queries-with-results", Integer.toString(i), Integer.toString(i)));

  //////////////////////////////////////
  // some static methods (initializers)

   // TODO in a clo
   static public void closeAll () {
     stringProducer.close();
     searchQueryProducer.close();
   }
}
