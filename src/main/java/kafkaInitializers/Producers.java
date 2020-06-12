// Producers
// https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html

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

import dataClasses.SearchQuery;

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


   Properties props = new Properties();
   props.put("bootstrap.servers", "localhost:9092");
   props.put("acks", "all");
   props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
   props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

   static Producer<String, String> stringProducer = new KafkaProducer<>(props);
   static Producer<String, SearchQuery> searchQueryProducer = new KafkaProducer<>(props);
   // use like:
   // producer.send(new ProducerRecord<String, String>("queue.podcast-analysis-tool.search-queries-with-results", Integer.toString(i), Integer.toString(i)));

  //////////////////////////////////////
  // some static methods (initializers)

   // TODO in a clo
   static public closeAll () {
     producer.close();
   }
}
