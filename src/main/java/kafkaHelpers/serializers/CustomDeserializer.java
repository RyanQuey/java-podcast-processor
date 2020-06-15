// convert java Podcast obj to serialized version that Kafka can send in topic
package kafkaHelpers.serializers;

import java.util.Map;
import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;



import dataClasses.podcast.Podcast;

// combines answers https://stackoverflow.com/a/40158971/6952495 
// currently not using as superclass, but will once I can figure out if I can use Deserializer<Object> for the subclasses (rather than Deserializer<Podcast> etc)
// for now, just has some global fields that don't want to make multiple of
//public class CustomDeserializer implements Deserializer<Object> {
public class CustomDeserializer  {
  public static ObjectMapper objectMapper = new ObjectMapper();
  // this is costly, only want one per app
  // https://github.com/FasterXML/jackson-modules-java8/tree/master/datetime#registering-module
  static {
		// don't need to do all, just do date time for now
    // objectMapper.findAndRegisterModules();
		JavaTimeModule module = new JavaTimeModule();
		objectMapper.registerModule(module);
  }


	private boolean isKey;

	/*
	@Override
	public void configure(Map<String, ?> configs, boolean isKey)
	{
		this.isKey = isKey;
	}

	@Override
	public void close()
	{

	}
	*/
}
