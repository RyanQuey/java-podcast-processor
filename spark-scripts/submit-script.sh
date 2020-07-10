$HOME/lib/dse-6.8.0/bin/dse spark-submit \
  --class "SparkKafkaStreamingTest" \
  --master local[3] \
  target/scala-2.11/spark-scripts-for-podcast-analysis-tool_2.11-0.3.0.jar
