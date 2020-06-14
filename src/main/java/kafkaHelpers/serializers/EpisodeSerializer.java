// convert java Episode obj to serialized version that Kafka can send in topic
package kafkaHelpers.serializers;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SerializationException;

import dataClasses.episode.Episode;

// combines answers https://stackoverflow.com/a/40158971/6952495 
public class EpisodeSerializer implements Serializer<Episode>
{
    private boolean isKey;


    @Override
    public void configure(Map<String, ?> configs, boolean isKey)
    {
      this.isKey = isKey;
    }

    @Override
    public byte[] serialize(String topic, Episode episode)
    {
      if (episode == null) {
        return null;
      }

      try {
        // trying this https://stackoverflow.com/a/13174951/6952495
				// if not consider https://stackoverflow.com/a/51874263/6952495 to serialize
        byte[] data = SerializationUtils.serialize(episode);
           
        return data;

      } catch (IOException | RuntimeException e) {
        throw new SerializationException("Error serializing value", e);
      }
    }

    @Override
    public void close()
    {

    }
}
