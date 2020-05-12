import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.io.File;
import org.json.JSONObject;

// local imports
import helpers.HttpReq;
import helpers.FileHelpers;
import dataClasses.PodcastSearch;

public class Main {

  public static void main(String[] args){
    boolean podcastSearchRequested = false;
    System.out.println("Running with options:");     
    for (String s: args) {
      System.out.println(s);     

      if (s.equals("--perform-search")) {
        podcastSearchRequested = true;
      } else {
        System.out.println("skipping search this time");     
      };
    };

    if (podcastSearchRequested) {
      PodcastSearch podcastSearch = new PodcastSearch();
      podcastSearch.performAllQueries(args);
    }

    System.out.println("finished");     
  }
}


