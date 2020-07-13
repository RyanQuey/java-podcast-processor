import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

import org.apache.spark.sql.kafka010._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

// singleton class (our main). Runs a word count over network (localhost:9999)
object SparkKafkaStreamingTest {
	def main (args: Array[String]) { 
    /* 
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
			.option("subscribePattern", "queue.podcast-analysis-tool.*")
			//.option("subscribe", "queue.podcast-analysis-tool.query-term")
			.option("startingOffsets", "earliest") // get from beginning
			.load()

		df.printSchema

		// not sure what I'm using this one for
		//val selectedDf = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
		//  .as[(String, String)]
			
		// NOTE this is not a DF, needs an action called on it first
		val messageByTopicCountAgg = df.groupBy("topic")
		val countDf =  messageByTopicCountAgg.count()
			
			

		// Spark docs: "The output is stored in memory as an in-memory table. Both, Append and Complete output modes, are supported. This should be used for debugging purposes on low data volumes as the entire output is collected and stored in the driver’s memory. Hence, use it with caution.""

		import org.apache.spark.sql.streaming.ProcessingTime

    val processingTimeSec = 1
		// write to memory so we can read it using Spark
		val query = countDf.writeStream
			.outputMode("complete")
			.format("memory") // can't do to console or will jam up Zeppelin
			.queryName("in_memory_table") // for querying this stream 
			.trigger(ProcessingTime(s"$processingTimeSec seconds"))
			.start()

		// https://stackoverflow.com/a/57065726/6952495
		// every 10 seconds, print out whatever we want here
		while(query.isActive){
      Thread.sleep(processingTimeSec * 1000)

		  spark.sql("SELECT * FROM in_memory_table LIMIT 30").show()

    }

    query.awaitTermination()
  }
}
