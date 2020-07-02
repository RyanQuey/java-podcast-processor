// convert java Episode obj to serialized version that Kafka can send in topic
package com.ryanquey.podcast.kafkaHelpers.serializers;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ryanquey.podcast.dataClasses.episode.Episode;

// combines answers https://stackoverflow.com/a/40158971/6952495 
public class EpisodeDeserializer implements Deserializer<Episode>
{
	private boolean isKey;

	@Override
	public void configure(Map<String, ?> configs, boolean isKey)
	{
		this.isKey = isKey;
	}

	@Override
	public Episode deserialize(String s, byte[] data)
	{
		if (data == null) {
			return null;
		}

		try {
			// Episode episode = SerializationUtils.deserialize(data);
			Episode episode = CustomDeserializer.objectMapper.readValue(data, Episode.class);
			return episode;

		} catch (IOException | RuntimeException e) {
			throw new SerializationException("Error deserializing value", e);
		}
	}

	@Override
	public void close()
	{

	}
}
