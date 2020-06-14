// convert java SearchQuery obj to serialized version that Kafka can send in topic
package kafkaHelpers.serializers;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SerializationException;

import dataClasses.searchQuery.SearchQuery;

// combines answers https://stackoverflow.com/a/40158971/6952495 
public class SearchQueryDeserializer implements Deserializer<SearchQuery>
{
	private boolean isKey;

	@Override
	public void configure(Map<String, ?> configs, boolean isKey)
	{
		this.isKey = isKey;
	}

	@Override
	public SearchQuery deserialize(String s, byte[] data)
	{
		if (data == null) {
			return null;
		}

		try {
			SearchQuery searchQuery = SerializationUtils.deserialize(data);
			return searchQuery;

		} catch (RuntimeException e) {
			throw new SerializationException("Error deserializing value", e);
		}
	}

	@Override
	public void close()
	{

	}
}
