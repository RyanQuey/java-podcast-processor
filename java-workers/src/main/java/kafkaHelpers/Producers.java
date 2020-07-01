// Producers
// https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html

package kafkaHelpers;

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
import dataClasses.podcast.Podcast;
import dataClasses.episode.Episode;
import kafkaHelpers.serializers.SearchQuerySerializer;

public class Producers {
  ///////////////////////////////////
  // private fields

  // default props
  static private Properties props = new Properties();
  static private void setPropertyDefaults (Properties props) {
    String kafkaUrl = System.getenv("KAFKA_URL") != null ? System.getenv("KAFKA_URL") : "localhost:9092";
    props.put("bootstrap.servers", kafkaUrl);
    props.put("acks", "all");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
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
    
    searchQueryProps.put("value.serializer", "kafkaHelpers.serializers.SearchQuerySerializer");
    podcastProps.put("value.serializer", "kafkaHelpers.serializers.PodcastSerializer");
    episodeProps.put("value.serializer", "kafkaHelpers.serializers.EpisodeSerializer");
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
