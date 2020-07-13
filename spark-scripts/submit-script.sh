# NOTE does not package your jar though. If made changes to java/scala code, make sure to run this instead: 
#
# bash setup-and-submit-script.sh
#
# Note that we can set these configs when initializing our spark session as well.
# However, need to specify the main class here, so it knows which class to look at and which config to load!
# Also, it's probably better to not hardcode a number of workers in the scala code itself, since that is platform dependent
#
# specifying packages here too...I'd rather do it in the scala code itself using conf. 
# But having a hard time getting that to work, so just doing it here for now, should be comma separated
#     * sbt will download the dependency and check for errors on compile, but need to still tell spark to send these packages to master/driver/worker nodes. 
#     * Format using this example: https://stackoverflow.com/a/35130993/6952495

# not exporting yet, not sure if it will affect dse if it gets set globally. Probably safe to export in one terminal though, but no need to yet
SPARK_HOME=$HOME/lib/spark

MAIN="SparkKafkaStreamingAvgTimeDiff"
printf "\n\n== Now submitting spark job $MAIN to spark-submit ==\n"
$SPARK_HOME/bin/spark-submit \
  --class $MAIN \
  --master local[3] \
  --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.6 \
  target/scala-2.11/spark-scripts-for-podcast-analysis-tool_2.11-0.3.0.jar
