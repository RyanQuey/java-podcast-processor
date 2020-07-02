// convert java Podcast obj to serialized version that Kafka can send in topic
package com.ryanquey.podcast.kafkaHelpers.serializers;

import java.util.Map;
import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ryanquey.podcast.dataClasses.podcast.Podcast;

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
	// params: key, val
	public Podcast deserialize(String s, byte[] data)
	{
		if (data == null) {
			return null;
		}

		try {
			//Podcast podcast = (Podcast) SerializationUtils.deserialize(data);

			Podcast podcast = CustomDeserializer.objectMapper.readValue(data, Podcast.class);
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
