package helpers;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;



public class JsonHelpers {
	// DOES NOT work for nested json
	// converts ints to Strings
	public static ArrayList<String> jsonArrayToList (JSONArray jsonArray) throws JSONException {
		ArrayList<String> list = new ArrayList<String>();     
		if (jsonArray != null) { 
   		int len = jsonArray.length();
   		for (int i=0;i<len;i++){ 
    		list.add(jsonArray.get(i).toString());
   		}
		}

		return list;
  } 

}

