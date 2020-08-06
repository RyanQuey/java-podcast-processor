/* 
 * SimpleApp.scala 
 * count lines in README.md that have a or b
*/
import org.apache.spark.sql.SparkSession

// level 1: get spark to work from an sbt project
object SimpleApp {
  def main(args: Array[String]) {
    val logFile = "file:///home/ubuntu/projects/java-podcast-processor/spark-scripts/README.md" // Should be some file on your system
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}
