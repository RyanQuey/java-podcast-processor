import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions._
import java.sql.Timestamp.from

import org.apache.spark.sql.kafka010._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

// singleton class (our main)
object SparkKafkaStreamingAvgTimeDiff {
	def main (args: Array[String]) {
    /* 
     * this is the only real class that we are using for this 
     */

    val spark = SparkSession
      .builder
      .appName("SparkKafkaStreamingTest")
      .getOrCreate()
      
    import spark.implicits._

    /*
     * Currently using `podcast` topic as the "action" and `episode topic as the reaction for our proof of concept
     * This way we can do things like:
     *   - test how many episodes were successfully parsed out, by comparing our `episode` kafka topic with the episodeCount returned from itunes api call
     *   - test how long it took to retrieve the rss feed over HTTP Call (compare podcast timestamp to first episode timestamp, though there will also be a little bit of time added from how long it took to extract that first episode from the XML)
     *   - test how many episodes failed
     *   - test how long each episode took to parse on average, and how long it took for all of them to finish
     *
     * Really these are the most interesting topics that we have
     *
     * TODO make the topic dynamically set based on args, so can run this job for different topics
     *
        "queue.podcast-analysis-tool.search-results-json"
        "queue.podcast-analysis-tool.podcast"
        "queue.podcast-analysis-tool.episode"
     */

		val actionDf = spark
			.readStream
			.format("kafka")
			.option("kafka.bootstrap.servers", "localhost:9092") 
			.option("subscribe", "queue.podcast-analysis-tool.podcast") 
			.option("startingOffsets", "earliest") // get from beginning (I think just beginning of when we started streaming (?) Note that it only outputs for topics that have had a new event happen since after we started running these spark scripts (even if we've stopped and started it in the meantime)
			.load()

		val reactionDf = spark
			.readStream
			.format("kafka")
			.option("kafka.bootstrap.servers", "localhost:9092")
			.option("subscribe", "queue.podcast-analysis-tool.episode")
      .option("startingOffsets", "earliest") // get from beginning (I think just beginning of when we started streaming (?) Note that it only outputs for topics that have had a new event happen since after we started running these spark scripts (even if we've stopped and started it in the meantime)
			.load()

		// only grab window of 15 minutes for each (should be plenty). 
		val actionWithWatermarkDf = actionDf.withWatermark("timestamp", "1 minutes")
      .select(
        $"topic".as("action_topic"), 
        $"offset".as("action_offset"), 
        $"value".cast(StringType).as("action_value"),
        $"timestamp".as("action_timestamp")
      )

		val reactionWithWatermarkDf = reactionDf.withWatermark("timestamp", "1 minutes")
      .select(
        $"topic".as("reaction_topic"), 
        $"offset".as("reaction_offset"), 
        $"value".cast(StringType).as("reaction_value"),
        $"timestamp".as("reaction_timestamp")
      )

    // inner join where values match. 
    // Not currently getting for example actions that don't have corresponding reactions (left outer join)
    // same value, with action time less than reaction time, but not more than 10 minutes less than reaction time 
    //(don't want our tests interferering with each other too much!
    val actionWithReactionDf = actionWithWatermarkDf.join(
      reactionWithWatermarkDf,
      expr( 
        """
        action_value = reaction_value AND
        action_timestamp <= reaction_timestamp AND
        action_timestamp + INTERVAL 10 minutes >= reaction_timestamp 
        """
      )
    // time between action and reaction in seconds
    ).withColumn("reaction_time_sec", unix_timestamp($"reaction_timestamp") - unix_timestamp($"action_timestamp"))
    .select(
      $"action_value".as("value"), // joining on value, so don't need both!
      $"action_offset",
      $"reaction_offset",
      $"action_timestamp",
      $"reaction_timestamp",
      $"reaction_time_sec",
      $"action_topic" // something to group by for the agg
    )
    .withWatermark("action_timestamp", "10 minutes") // needs this watermark, or can't do aggs on this stream


    // need this window also, or can't do aggs on streaming because of same error: 
    // `Append output mode not supported when there are streaming aggregations on streaming DataFrames/DataSets wit
    // hout watermark`
    val avgReactionTimeDf = actionWithReactionDf.groupBy($"action_topic", window($"action_timestamp", "5 minutes")).agg(
      first($"reaction_time_sec").as("first_reaction_time_sec"),
      last($"reaction_time_sec").as("last_reaction_time_sec"),
      avg($"reaction_time_sec").as("avg_reaction_time_sec"),
      sum($"reaction_time_sec").as("sum_reaction_time_sec")
    ).drop("action_topic")

		import org.apache.spark.sql.streaming.ProcessingTime

    val processingTimeSec = 3

		val actionWithReactionQuery = actionWithReactionDf.writeStream
			.outputMode("append")
			.option("truncate", "false")
			.format("console") // can't do to console or will jam up Zeppelin
			.trigger(ProcessingTime(s"$processingTimeSec seconds"))
			.start()

    // can't do complete here, need to do append because we join two streams. 
    // See here for some interaction with this issue: https://stackoverflow.com/a/54118633/6952495
    // Or here: https://stackoverflow.com/a/45497609/6952495
    // Basically, they recommend saving results to kafka and then getting totals there instead, to get total sums and averages, with no windows
    // OR TRY mapGroupWithState 
		val aggQuery = avgReactionTimeDf.writeStream
			.outputMode("append")
			.format("console") // can't do to console or will jam up Zeppelin
			.option("truncate", "false")
			.trigger(ProcessingTime(s"$processingTimeSec seconds"))
			.start()

    aggQuery.awaitTermination()
    actionWithReactionQuery.awaitTermination()
  }
}
