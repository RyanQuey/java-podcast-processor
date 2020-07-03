// Producers
// https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html

package com.ryanquey.podcast.kafkaHelpers;

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

import com.ryanquey.podcast.dataClasses.searchQuery.SearchQuery;
import com.ryanquey.podcast.dataClasses.podcast.Podcast;
import com.ryanquey.podcast.dataClasses.episode.Episode;
import com.ryanquey.podcast.kafkaHelpers.serializers.SearchQuerySerializer;

public class Producers {
  ///////////////////////////////////
  // private fields

  // default props
  static private Properties props = new Properties();
  static private void setPropertyDefaults (Properties props) {
    String kafkaUrl = System.getenv("KAFKA_URL") != null ? System.getenv("KAFKA_URL") : "localhost:9092";
    System.out.println("Kafka broker URL: " + kafkaUrl);
    props.setProperty("bootstrap.servers", kafkaUrl);
    props.setProperty("acks", "all");
    props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  }

  static private Properties stringProps = new Properties();
  static private Properties searchQueryProps = new Properties();
  static private Properties podcastProps = new Properties();
  static private Properties episodeProps = new Properties();
  static {
    Producers.setPropertyDefaults(stringProps);
    Producers.setPropertyDefaults(searchQueryProps);
    Producers.setPropertyDefaults(podcastProps);
    Producers.setPropertyDefaults(episodeProps);
    
    // TODO do something so if this doesn't import, java can prevent the build right away
    searchQueryProps.setProperty("value.serializer", "com.ryanquey.podcast.kafkaHelpers.serializers.SearchQuerySerializer");
    podcastProps.setProperty("value.serializer", "com.ryanquey.podcast.kafkaHelpers.serializers.PodcastSerializer");
    episodeProps.setProperty("value.serializer", "com.ryanquey.podcast.kafkaHelpers.serializers.EpisodeSerializer");
  }

  static Producer<String, String> stringProducer = new KafkaProducer<>(stringProps);
  static Producer<String, SearchQuery> searchQueryProducer = new KafkaProducer<>(searchQueryProps);
  static Producer<String, Podcast> podcastProducer = new KafkaProducer<>(podcastProps);
  static Producer<String, Episode> episodeProducer = new KafkaProducer<>(episodeProps);
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
