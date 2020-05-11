package helpers;

import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.lang.Exception;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class HttpReq {
// add what will be returned
  public static String get(String urlStr, Map queryParams) 
    throws Exception {
      // set limit to 200, let's just get all of it (default: 50)
      // contents = urllib.request.urlopen(f"?media=podcast&term={term}&limit=200&version=2&lang=en_us&country=US").read()
      URL url;
      String contentStr;

      try {
        url = new URL(urlStr);

      } catch (MalformedURLException e) {
        System.out.println(e);

        throw e;
      }

      try {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // write params to request...yes it's this crazy.
        // Basically converts our map to a string, then writes that string to the http url connection via "output stream" api. 
        // (is an api for writing data to something, in this case, writing params to the url)
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(getParamsString(queryParams));
        out.flush();
        out.close();

        // begin reading (sends the http request itself...I think)
        int status = con.getResponseCode();
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
        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        result.append("&");
      }

      String resultString = result.toString();
      return resultString.length() > 0
        ? resultString.substring(0, resultString.length() - 1)
        : resultString;
  }
}



