import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions._
import java.sql.Timestamp.from

import org.apache.spark.sql.kafka010._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

// singleton class (our main). Runs a word count over network (localhost:9999)
object SparkAggKafkaStreamingTest {
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
			.option("subscribe", 
        "queue.podcast-analysis-tool.query-term," + 
        "queue.podcast-analysis-tool.test," + 
        "queue.podcast-analysis-tool.search-results-json," + 
        "queue.podcast-analysis-tool.podcast," + 
        "queue.podcast-analysis-tool.episode"
        ) // subscribe to some topics
			.option("startingOffsets", "latest") // get from beginning (I think just beginning of when we started streaming (?) Note that it only outputs for topics that have had a new event happen since after we started running these spark scripts (even if we've stopped and started it in the meantime)
			.load()

		df.printSchema

    // get first timestamp, last timestamp, mean timestamp, total difference between first and last, and average difference between each timestamp 
    // (trivial because of our dataset, but whatever)
    val aggDf = df.groupBy("topic").agg(
        first($"timestamp").as("first"),
        last($"timestamp").as("last"),
        avg($"timestamp").as("avg")
    ).withColumn("totalDiffSec", (unix_timestamp($"last") - unix_timestamp($"first")))// divide by 1000 to get seconds
    .withColumn("avgDiffSec", $"totalDiffSec" /2) 

		import org.apache.spark.sql.streaming.ProcessingTime

    val processingTimeSec = 3

		val aggQuery = aggDf.writeStream
			.outputMode("complete")
			.format("console") // can't do to console or will jam up Zeppelin
			.trigger(ProcessingTime(s"$processingTimeSec seconds"))
			.start()

    aggQuery.awaitTermination()
  }
}
