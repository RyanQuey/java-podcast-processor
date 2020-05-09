import java.util.HashMap;
import java.util.Map;
import java.lang.System;
import java.lang.Exception;
// leave spaces here for easy reading, but change when putting into file or search terms
/* 
search-terms = [
    "data engineering",
]
*/



public class GetPodcasts {
    private static String get () {
      // TODO 
      // later iterate over terms array
      String term = "data engineering";

      try {
      //for each term, ask itunes for results
      // TODO iterate over array of search terms
      //for term in search-terms:
        // convert term to url encoded, using + (as according to itunes examples)
        // TODO

        // set limit to 200, let's just get all of it (default: 50)
        // contents = urllib.request.urlopen(f"?media=podcast&term={term}&limit=200&version=2&lang=en_us&country=US").read()
        String urlStr = "https://itunes.apple.com/search";

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("media", "podcast");
        queryParams.put("term", term);
        queryParams.put("limit", "200");
        // set version, language and country in case their defaults change
        queryParams.put("version", "2");
        queryParams.put("lang", "en_us");
        queryParams.put("country", "US");
         
        // write params to request...yes it's this crazy.
        // Basically converts our map to a string, then writes that string to the http url connection via "output stream" api. 
        // (is an api for writing data to something, in this case, writing params to the url)

        // begin reading
        String result = HttpReq.get(urlStr, queryParams);

        System.out.println(result);
        // use `with` to prevent leaving file open after code runs
        // TODO
        //term_for_file = re.sub(r"\s+", "-", term)
        /* 
        with open(f"{term}-podcasts.json", "r+") as file:
            file.write(contents)
            file.close()

            */

        // write to json file
        return result;

      } catch (Exception e) {
        System.out.println("Error:");
        System.out.println(e);

        return null;
      }
    }


  public static void main(String[] args){
    System.out.println("Hello World");    
    String result = get();
    
    System.out.println(result);     
  }
}


