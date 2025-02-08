package fileOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

public class JsonOperations {
	public final static String[] defaultTags= {"Action","Adventure","Animation","Biography","Comdey","Crime","Documentry","Drama","Family","Fantasy","Game-Show","History","Horror","Musical","Mystery","News","Reality-TV","Romance","Sci-Fi","Sport","Western","Talk-Show"};
	
	public static JSONObject returnTagTemplate(String tagName) {
		JSONObject newTag=new JSONObject();
		newTag.put("tag", tagName);
		return newTag;
	}
	
	public static JSONObject returnCollectionTemplate(String collectionName) {
		JSONObject newCollection=new JSONObject();
		newCollection.put("collection", collectionName);
		return newCollection;
	}
	
	public static JSONObject createMediaJson() {
		JSONObject mediaJson=new JSONObject();
		
		JSONArray tags=new JSONArray();
		for(String tag:defaultTags) {
			tags.put(returnTagTemplate(tag));
		}
		
		JSONArray collections=new JSONArray();
		collections.put(returnCollectionTemplate(null));
		
		mediaJson.put("lastId",0);
		mediaJson.put("tags",tags);
		mediaJson.put("collections",collections);
		
		return mediaJson;
	}
	
	public static JSONObject getJson(File jsonFile) throws FileNotFoundException, IOException, ParseException {
		String content = new String(Files.readAllBytes(jsonFile.toPath()));
		JSONObject json=new JSONObject(content);
		return json;
	}
	
	public static void updJson(JSONObject newMediaJson,File toUpd) throws IOException {
		FileWriter file = new FileWriter(toUpd,false);
        file.write(newMediaJson.toString());
        file.close();
	}
	
	public static void addNewTag(String tagName) throws FileNotFoundException, IOException, ParseException {
		JSONObject mediaJson=getJson(CopyMedia.returnMediaJson().toFile());
		JSONObject newTag=returnTagTemplate(tagName);
		mediaJson.append("tags", newTag);
	}
	
	public static void addNewCollection(String collectionName) throws FileNotFoundException, IOException, ParseException {
		JSONObject mediaJson=getJson(CopyMedia.returnMediaJson().toFile());
		JSONObject newCollectionName=returnTagTemplate(collectionName);
		mediaJson.append("collections", newCollectionName);
	}
	
	public static JSONObject createSerieJson(String serieName) throws FileNotFoundException, IOException, ParseException {
		JSONObject serieJson=new JSONObject();
		JSONObject mediaJson=getJson(CopyMedia.returnMediaJson().toFile());
		int id=mediaJson.getInt("lastId");
		mediaJson.put("lastId", id+1);
		updJson(mediaJson,CopyMedia.returnMediaJson().toFile());
		
		JSONArray tags=new JSONArray();
		tags.put(returnTagTemplate(null));
		
		JSONArray collections=new JSONArray();
		collections.put(returnCollectionTemplate(null));
		
		serieJson.put("serieId",id+1);
		serieJson.put("serieName",serieName);
		serieJson.put("lastVideoId",0);
		serieJson.put("watched",0);
		serieJson.put("rating", "");
		serieJson.put("comment", "");
		serieJson.put("tags",tags);
		serieJson.put("collections",collections);
		
		return serieJson;
	}
	
	public static JSONObject createVideoJson(String videoName,File serieJsonFile) throws FileNotFoundException, IOException, ParseException {
		JSONObject videoJson=new JSONObject();
		JSONObject serieJson=getJson(serieJsonFile);
		int id=serieJson.getInt("lastVideoId");
		serieJson.put("lastVideoId", id+1);
		updJson(serieJson,serieJsonFile);
		
		JSONArray tags=new JSONArray();
		tags.put(returnTagTemplate(null));
		
		JSONArray collections=new JSONArray();
		collections.put(returnCollectionTemplate(null));
		
		videoJson.put("videoId",id+1);
		videoJson.put("videoName",videoName);
		videoJson.put("watched",0);
		videoJson.put("rating", "");
		videoJson.put("comment", "");
		videoJson.put("tags",tags);
		videoJson.put("collections",collections);
		
		return videoJson;
	}
}
