package fileOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

public class JsonOperations {
	public final static String[] defaultTags= {"Action","Adventure","Animation","Biography","Comdey","Crime","Documentry","Drama","Family","Fantasy","Game-Show","History","Horror","Musical","Mystery","News","Reality-TV","Romance","Sci-Fi","Sport","Western","Talk-Show"};
	
	//Verilen isimde tag JSONObject i döndürür.
	public static JSONObject returnTagTemplate(String tagName) {
		JSONObject newTag=new JSONObject();
		newTag.put("tag", tagName);
		return newTag;
	}
	
	//Media klasörüne koyulacak JSONObject ini oluşturur.
	public static JSONObject createMediaJson() {
		JSONObject mediaJson=new JSONObject();
		
		JSONArray tags=new JSONArray();
		for(String tag:defaultTags) {
			tags.put(returnTagTemplate(tag));
		}
		
		mediaJson.put("lastId",0);
		mediaJson.put("tags",tags);
		
		return mediaJson;
	}
	
	//Bir JSON dosyasını okur ve içeriğini JSONObject olarak geri döndürür.
	public static JSONObject getJson(File jsonFile) throws FileNotFoundException, IOException, ParseException {
		String content = new String(Files.readAllBytes(jsonFile.toPath()));
		JSONObject json=new JSONObject(content);
		return json;
	}
	
	//Bir JSONObject i ve dosya alır. Dosyanın içeriğini verilen JSONObject ile değiştirir.
	public static void updJson(JSONObject newJson,File toUpd) throws IOException {
		if(CopyMedia.getExtension(toUpd.toString()).equals("json")) {
			FileWriter file = new FileWriter(toUpd,false);
	        file.write(newJson.toString());
	        file.close();
		}
	}
	
	//Media klasöründeki json dosyasından tagleri alır ve geriye alfabetik sıra ile döndürür.
	public static List<String> getTags() throws FileNotFoundException, IOException, ParseException {
		JSONObject mediaJson=getJson(CopyMedia.returnMediaJson().toFile());
		List<String> tagNames=new ArrayList<String>();
		for(int i=0;i<mediaJson.getJSONArray("tags").length();i++) {
			tagNames.add(mediaJson.getJSONArray("tags").getJSONObject(i).getString("tag"));
		}
		Collections.sort(tagNames);
		return tagNames;
	}
	
	//Verilen isimde yeni bir tagi mediadaki json dosyasına ekler.
	public static void addNewTag(String tagName) throws FileNotFoundException, IOException, ParseException {
		JSONObject mediaJson=getJson(CopyMedia.returnMediaJson().toFile());
		JSONObject newTag=returnTagTemplate(tagName);
		mediaJson.append("tags", newTag);
		updJson(mediaJson,CopyMedia.returnMediaJson().toFile());
	}
	
	//Verilen tag ismi, verilen json dosyasında varsa silinir.
	public static void delTag(String tagName,Path jsonFile) throws FileNotFoundException, IOException, ParseException {
		JSONObject json=getJson(jsonFile.toFile());
		for(int i=0;i<json.getJSONArray("tags").length();i++) {
			if(json.getJSONArray("tags").getJSONObject(i).has("tag")  && json.getJSONArray("tags").getJSONObject(i).getString("tag").equals(tagName)) {
				json.getJSONArray("tags").remove(i);
			}
		}
		updJson(json,jsonFile.toFile());
	}
	
	//Kullanıcıdan alınan isimle bir seri için JSON dosyası oluşturulur.
	public static JSONObject createSerieJson(String serieName) throws FileNotFoundException, IOException, ParseException {
		JSONObject serieJson=new JSONObject();
		JSONObject mediaJson=getJson(CopyMedia.returnMediaJson().toFile());
		int id=mediaJson.getInt("lastId");
		mediaJson.put("lastId", id+1);
		updJson(mediaJson,CopyMedia.returnMediaJson().toFile());
		
		JSONArray tags=new JSONArray();
		tags.put(returnTagTemplate(null));
		
		serieJson.put("serieId",id+1);
		serieJson.put("nextSerieId",serieName);
		serieJson.put("lastVideoId",0);
		serieJson.put("rating", "");
		serieJson.put("comment", "");
		serieJson.put("tags",tags);
		
		return serieJson;
	}
	
	//Videoların JSON dosyalarını oluşturur.
	public static JSONObject createVideoJson(String videoName,File serieJsonFile) throws FileNotFoundException, IOException, ParseException {
		JSONObject videoJson=new JSONObject();
		JSONObject serieJson=getJson(serieJsonFile);
		int id=serieJson.getInt("lastVideoId");
		serieJson.put("lastVideoId", id+1);
		updJson(serieJson,serieJsonFile);
		
		JSONArray tags=new JSONArray();
		tags.put(returnTagTemplate(null));
		
		videoJson.put("videoId",id+1);
		videoJson.put("videoName",videoName);
		videoJson.put("rating", "");
		videoJson.put("comment", "");
		
		return videoJson;
	}
}
