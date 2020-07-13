import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions._
import java.sql.Timestamp.from

import org.apache.spark.sql.kafka010._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

// singleton class (our main). Runs a word count over network (localhost:9999)
object SparkKafkaStreamingAvgTimeDiff {
	def main (args: Array[String]) { 
    /* 
     */

    val spark = SparkSession
      .builder
      .appName("SparkKafkaStreamingTest")
      .getOrCreate()
      
    import spark.implicits._

		val actionDf = spark
			.readStream
			.format("kafka")
			.option("kafka.bootstrap.servers", "localhost:9092") // NOTE doesn't tell me when not able to connect !
			.option("subscribe", "queue.podcast-analysis-tool.test") 
			.option("startingOffsets", "earliest") // get from beginning (I think just beginning of when we started streaming (?) Note that it only outputs for topics that have had a new event happen since after we started running these spark scripts (even if we've stopped and started it in the meantime)
			.load()

		val reactionDf = spark
			.readStream
			.format("kafka")
			.option("kafka.bootstrap.servers", "localhost:9092") // NOTE doesn't tell me when not able to connect !
			.option("subscribe", "queue.podcast-analysis-tool.test-reaction")
      .option("startingOffsets", "earliest") // get from beginning (I think just beginning of when we started streaming (?) Note that it only outputs for topics that have had a new event happen since after we started running these spark scripts (even if we've stopped and started it in the meantime)
			.load()

		// only grab window of 15 minutes for each (should be plenty). 
		val actionWithWatermarkDf = actionDf.withWatermark("timestamp", "15 minutes")
      .select(
        $"topic".as("action_topic"), 
        $"offset".as("action_offset"), 
        $"value".cast(StringType).as("action_value"),
        $"timestamp".as("action_timestamp")
      )

		val reactionWithWatermarkDf = reactionDf.withWatermark("timestamp", "25 minutes")
      .select(
        $"topic", 
        $"offset", 
        $"value".cast(StringType).as("value"),
        $"timestamp"
      )

    val actionToReactionDf = actionWithWatermarkDf.join(
      reactionWithWatermarkDf,
      expr( // same value, with action time less than reaction time, but not more than 10 minutes less than reaction time
        """
        action_value = value AND
        action_timestamp <= timestamp AND
        action_timestamp + INTERVAL 10 minutes >= timestamp 
        """
      )
    )
  /*
      .agg(
        first($"timestamp").as("first"),
        last($"timestamp").as("last"),
        avg($"timestamp").as("avg")
    ).withColumn("totalDiffSec", (unix_timestamp($"last") - unix_timestamp($"first")))// divide by 1000 to get seconds
    .withColumn("avgDiffSec", $"totalDiffSec" /2) 
    */

		import org.apache.spark.sql.streaming.ProcessingTime

    val processingTimeSec = 3

		val aggQuery = actionToReactionDf.writeStream
			.outputMode("append")
			.format("console") // can't do to console or will jam up Zeppelin
			.trigger(ProcessingTime(s"$processingTimeSec seconds"))
			.start()

    aggQuery.awaitTermination()
  }
}
