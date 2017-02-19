package eu.erbs.debates.io.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class TwitterHandleLoader {

	private static final String API_BASE = "http://www.bundestwitter.de/api/";

	public static Map<String,String> getBundestwitterPoliticians() throws JSONException, IOException{
		return extractHandles(getBundestwitterResponse());
	}

	private static Map<String,String> extractHandles(String bundestwitterResponse) throws JSONException {
		
		Map<String,String> twitterHandles = new HashMap<>();
		JSONArray jsonArray = new JSONArray(bundestwitterResponse);
		JSONObject politician;
		for(int i=0;i<jsonArray.length();i++){
			politician = ((JSONObject) jsonArray.get(i));
			twitterHandles.put(politician.getString("screenname"), politician.getString("name"));
		}
		return twitterHandles;
	}

	public static String getBundestwitterResponse() throws IOException{
		Document doc = Jsoup.connect(API_BASE + "politiker")
				.ignoreContentType(true)
				.get();
		return doc.body().text();
	}

}
