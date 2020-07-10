# start producing. Standalone thing to run in separate terminal to run against run-search-per-term-${project-package.version}.jar
echo "Starting producer for queue.podcast-analysis-tool.query-term"
$HOME/lib/kafka_2.12-2.5.0/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic queue.podcast-analysis-tool.query-term


