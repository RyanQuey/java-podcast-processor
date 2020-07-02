package com.ryanquey.podcast.helpers;

import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// import java.lang.Exception;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
// import java.io.InputStream;

// TODO in the future just use this (?): <dependency>
//   <groupId>org.apache.httpcomponents</groupId>
//     <artifactId>httpclient</artifactId>
//       <version>4.5.2</version>
//       </dependency>

public class HttpReq {
// add what will be returned
  public static String get(String urlStr, Map<String, String> queryParams) 
    // TODO just declare multiple throws
    throws IOException {
      // set limit to 200, let's just get all of it (default: 50)
      // contents = urllib.request.urlopen(f"?media=podcast&term={term}&limit=200&version=2&lang=en_us&country=US").read()
      URL url = stringToUrl(urlStr);
      String contentStr;

      try {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // add a user agent so are less likely to get so many 403's
        // https://stackoverflow.com/a/5202215/6952495
        con.addRequestProperty("User-Agent", "Mozilla/4.76"); 

        con.setRequestMethod("GET");

        // write params to request...yes it's this crazy.
        // Basically converts our map to a string, then writes that string to the http url connection via "output stream" api. 
        // (is an api for writing data to something, in this case, writing params to the url)
        if (queryParams != null) {
          // allows for a get request (false forces a POST)
          con.setDoOutput(true);

          DataOutputStream out = new DataOutputStream(con.getOutputStream());
          out.writeBytes(getParamsString(queryParams));
          out.flush();
          out.close();
        }
        int status = con.getResponseCode();

        // begin reading (sends the http request itself...I think)
        
        BufferedReader in = new BufferedReader(
          new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
        }
        in.close();

        con.disconnect();

        String contentString;

        contentStr = content.toString();

      } catch (IOException e) {
        System.out.println(e);

        throw e;
      }
      return contentStr;
  }

  // helper for building url with query parays, converts map > string
  private static String getParamsString(Map<String, String> params) 
    throws UnsupportedEncodingException{
      StringBuilder result = new StringBuilder();

      for (Map.Entry<String, String> entry : params.entrySet()) {
        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        result.append("=");

        // TODO add error handling so don't get NullPointerException on runtime, as runtime error, if something didn't get set key or value right. 
        // want something on compile time if possible

        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        result.append("&");
      }

      String resultString = result.toString();
      return resultString.length() > 0
        ? resultString.substring(0, resultString.length() - 1)
        : resultString;
  }

  private static URL stringToUrl(String urlStr) 
    throws MalformedURLException {
      try {
        URL url = new URL(urlStr);
        return url;

      } catch (MalformedURLException e) {
        System.out.println(e);

        throw e;
      }
  
  }
}



