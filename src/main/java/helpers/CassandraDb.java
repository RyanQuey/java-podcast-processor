package helpers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.querybuilder.term.Term;

import java.time.Instant;
import java.time.LocalDateTime;
import java.sql.Timestamp;


public class CassandraDb {
  private static CqlSession session = CqlSession.builder().build();

  public static void initialize () {
    session.execute("USE podcast_analysis_tool;");
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
}
