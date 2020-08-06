import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

import org.apache.spark.sql.kafka010._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

// level 3: get Sparks Streaming to work with kafka topics
object SparkKafkaStreamingTest {
	def main (args: Array[String]) { 
    /* 
     * simple singleton class to subscribe to our podcast Kafka topics, and count message per topic (countDf)
     * Runs over micro-batch interval of `processingTimeSec` seconds
     */

    val spark = SparkSession
      .builder
      .appName("SparkKafkaStreamingTest")
      .getOrCreate()

      
    import spark.implicits._

		val df = spark
			.readStream
			.format("kafka")
			.option("kafka.bootstrap.servers", "localhost:9092") // NOTE doesn't tell me when not able to connect !
			.option("subscribe", 
        "queue.podcast-analysis-tool.query-term," + 
        "queue.podcast-analysis-tool.test," + 
        "queue.podcast-analysis-tool.search-results-json," + 
        "queue.podcast-analysis-tool.podcast," + 
        "queue.podcast-analysis-tool.episode"
        ) // subscribe to some topics
			//.option("subscribePattern", "queue.podcast-analysis-tool.*") // subscribe to all our topics
			//.option("subscribe", "queue.podcast-analysis-tool.query-term") // subscribe to one topic
			.option("startingOffsets", "earliest") // get from beginning (I think just beginning of when we started streaming (?)
			.load()

		df.printSchema

		// select as 
		// https://stackoverflow.com/a/32634826/6952495
		val castDf = df.select(
      $"offset", 
      $"topic",
      $"key".cast(StringType).as("key"), 
      $"value".cast(StringType).as("value"),
      unix_timestamp(col("timestamp")).as("unix_timestamp")
    )
			
		////////////////////////////////////
    // One df to count topics
		// NOTE this is not a DF, needs an action called on it first
		val messageByTopicCountAgg = castDf.groupBy("topic")
		// this is a DF :)
		val countDf =  messageByTopicCountAgg.count()
			
    ///////////////////////////////////
    // One df to get avg interval between events
		//val intervalBetweenEvents = 
			

		// Spark docs: "The output is stored in memory as an in-memory table. Both, Append and Complete output modes, are supported. This should be used for debugging purposes on low data volumes as the entire output is collected and stored in the driver’s memory. Hence, use it with caution.""

		import org.apache.spark.sql.streaming.ProcessingTime

    val processingTimeSec = 1
		// write to memory so we can read it using Spark
		val topicCountQuery = countDf.writeStream
			.outputMode("complete")
			.format("console") // can't do to console or will jam up Zeppelin
			.trigger(ProcessingTime(s"$processingTimeSec seconds"))
			.start()

		val allEventsQuery = castDf.writeStream
			.outputMode("append")
			.format("console") // can't do to console or will jam up Zeppelin
			.trigger(ProcessingTime(s"$processingTimeSec seconds"))
			.start()

      /*
		// https://stackoverflow.com/a/57065726/6952495
		// every 10 seconds, print out whatever we want here
		while(topicCountQuery.isStreaming){
      Thread.sleep(processingTimeSec * 1000)

		  // will have one row per topic
		  spark.sql("SELECT * FROM counts LIMIT 5").show()

    }

    */

    allEventsQuery.awaitTermination()
    topicCountQuery.awaitTermination()
  }
}

