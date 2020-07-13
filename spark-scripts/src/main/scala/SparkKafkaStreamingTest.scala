import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

import org.apache.spark.sql.kafka010._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

// singleton class (our main). Runs a word count over network (localhost:9999)
object SparkKafkaStreamingTest {
	def main (args: Array[String]) { 
    /* 
     * sbt will download the dependency and check for errors on compile, but need to still tell spark to send these packages to master/driver/worker nodes. 
     * Format using this example: https://stackoverflow.com/a/35130993/6952495
     * But trying to specify in the config here rather than in the spark conf file
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
			//.option("subscribePattern", "queue.*")
			.option("subscribePattern", "queue.podcast-analysis-tool.query-term")
			.option("auto.offset.reset", "earliest") // get from beginning
			.option("startingOffsets", "earliest") // get from beginning
			.load()

		df.printSchema

		// not sure what I'm using this one for
		//val selectedDf = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
		//  .as[(String, String)]
			
		val messageByTopicCountDf = df.groupBy("topic")
		val count =  messageByTopicCountDf.count()
			
			

		// Spark docs: "The output is stored in memory as an in-memory table. Both, Append and Complete output modes, are supported. This should be used for debugging purposes on low data volumes as the entire output is collected and stored in the driver’s memory. Hence, use it with caution.""

		import org.apache.spark.sql.streaming.ProcessingTime

		// write to memory so we can read it using Spark
		val query = count.writeStream
			.outputMode("complete")
			.format("memory") // can't do to console or will jam up Zeppelin
			.queryName("in_memory_table") // for querying this stream 
			.trigger(ProcessingTime("3 seconds"))
			.start()

		// DON"T DO THIS will just block everything
		// https://stackoverflow.com/a/47487443/6952495
		// query.awaitTermination()


		spark.table("in_memory_table").show()

    query.awaitTermination()
  }
}
