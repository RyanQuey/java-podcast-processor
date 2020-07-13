# start producing to a test topic that doesn't have hooks that will affect our data
echo "Starting producer for queue.podcast-analysis-tool.test-reaction"
$HOME/lib/kafka_2.12-2.5.0/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic queue.podcast-analysis-tool.test-reaction


