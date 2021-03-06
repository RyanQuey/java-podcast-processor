// convert java SearchQuery obj to serialized version that Kafka can send in topic
package com.ryanquey.podcast.kafkaHelpers.serializers;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SerializationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.ryanquey.podcast.dataClasses.searchQuery.SearchQuery;

// combines answers https://stackoverflow.com/a/40158971/6952495 
public class SearchQuerySerializer implements Serializer<SearchQuery>
{
    private boolean isKey;


    @Override
    public void configure(Map<String, ?> configs, boolean isKey)
    {
      this.isKey = isKey;
    }

    @Override
    public byte[] serialize(String topic, SearchQuery searchQuery)
    {
      if (searchQuery == null) {
        return null;
      }

      try {
        // trying this https://stackoverflow.com/a/13174951/6952495
				// if not consider https://stackoverflow.com/a/51874263/6952495 to serialize
        //byte[] data = SerializationUtils.serialize(searchQuery);
        byte[] data = CustomDeserializer.objectMapper.writeValueAsBytes(searchQuery);
           
        return data;

      } catch (JsonProcessingException | RuntimeException e) {
        throw new SerializationException("Error serializing value", e);
      }
    }

    @Override
    public void close()
    {

    }
}
