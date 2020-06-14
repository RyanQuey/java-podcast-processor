// convert java Podcast obj to serialized version that Kafka can send in topic
package kafkaHelpers.serializers;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SerializationException;

import dataClasses.podcast.Podcast;

// combines answers https://stackoverflow.com/a/40158971/6952495 
public class PodcastDeserializer implements Deserializer<Podcast>
{
	private boolean isKey;

	@Override
	public void configure(Map<String, ?> configs, boolean isKey)
	{
		this.isKey = isKey;
	}

	@Override
	public Podcast deserialize(String s, byte[] data)
	{
		if (value == null) {
			return null;
		}

		try {
			Podcast podcast = SerializationUtils.deserialize(data);
			return podcast;

		} catch (IOException | RuntimeException e) {
			throw new SerializationException("Error deserializing value", e);
		}
	}

	@Override
	public void close()
	{

	}
}
