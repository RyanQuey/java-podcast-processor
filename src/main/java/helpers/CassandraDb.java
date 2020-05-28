package helpers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.ResultSet;
// import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.data.CqlDuration;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.querybuilder.term.Term;

import java.time.Instant;
import java.time.LocalDateTime;
// import java.sql.Timestamp;

// import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
// import migrations.*;
import migrations.M20200513211500CreateKeyspace;
import migrations.M20200524201500CreatePodcastsTable;
import migrations.M20200513221500CreateSearchResultsTable;
import migrations.M20200527151500CreateEpisodesAndAddEpisodesToPodcastsTable;

public class CassandraDb {
  public static CqlSession session = CqlSession.builder().build();

  public static void initialize () throws Exception {
    // create keyspace if doesn't exist already, and initialize tables
    runMigrations();
    // TODO they don't recommend changing keyspace during a run. Not sure when you're supposed to set it htough
    session.execute("USE podcast_analysis_tool;");
  }

  // TODO remove...find more elegant solution for running migrations so can run independently of connecting to db, perhaps a separate jar file I can run or something
  // currently not maintaining versioning for this, not really necessary
  // since doing IF NOT EXISTS then can run all these all the time we want to migrate
  private static void runMigrations () throws Exception {
    M20200513211500CreateKeyspace.run(); 
    M20200513221500CreateSearchResultsTable.run();
    M20200524201500CreatePodcastsTable.run(); 
    M20200527151500CreateEpisodesAndAddEpisodesToPodcastsTable.run();
    System.out.println("***finished writing migrations***");
  }

	// close session when not actively using...or just when everything is finished running?
  public static void closeSession () {
    session.close();
	}

  public static ResultSet execute (String cql) {
    return session.execute(cql);
  }

  public static void getReleaseVersion () {
    ResultSet rs = session.execute("select release_version from system.local"); 
    Row row = rs.one();

    System.out.println("release version:");
    System.out.println(row.getString("release_version"));
  };


  ////////////////////////////////////
  // some helpers for building queries
  
  public static Term getTimestamp() {
    // TODO note that this doesn't work, even though it looks like it should 
    // see https://docs.datastax.com/en/developer/java-driver/4.6/manual/core/#cql-to-java-type-mapping
    //return TypeCodecs.TypeCodec(Instant.now()); 
    // return LocalDateTime.now();// .toDate(); 
    return currentTimestamp();
    //return TypeCodecs.TypeCodec(Instant.now()); 
  }

  // string that matches format of cassandra's timestamp (Cassandra allows optional T letter)
  // something like: "2020-05-24T22:10:29.748809"
  public static String getTimestampStr() {
    return LocalDateTime.now().toString();
  }

  // TODO move to separate time helpers file
  // convert string to instant (which Cassandra codec accepts to be sent to cql for columns of type TIMESTAMP)
  public static Instant stringToInstant(String str) {
    return Instant.parse(str);
    /*
    return LocalDateTime.parse(str, DateTimeFormatter.ofPattern( "hh:mm a, EEE M/d/uuuu").toInstant();
    */
  }

  // NOTE untested, and currently not in use I don't think
  // CqlDuration already has a constructor taking a string. But this takes format 00:00:00  (HH:MM:SS) or 00:00 (MM:SS) instead of 
  public static CqlDuration stringToCqlDuration(String str) {
    String[] split = str.split(":");
    String hours;
    String minutes;
    String seconds;

    if (split.length == 3) {
      hours = split[0];
      minutes = split[1];
      seconds = split[2];
    } else {
      // assume other format supported by Rome RSS, MM:SS
      hours = "00";
      minutes = split[0];
      seconds = split[1];
    }

    // p for period, T for time. Alternative ISO 8601 format
    return CqlDuration.from(String.format("PT%s:%s:%s", hours, minutes, seconds));
  }
}
