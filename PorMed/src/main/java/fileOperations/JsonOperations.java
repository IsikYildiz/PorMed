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
    // Varsayılan etiketler listesi
    public final static String[] defaultTags = {"Action","Adventure","Animation","Biography","Comedy","Crime","Documentry","Drama","Family","Fantasy","Game-Show","History","Horror","Musical","Mystery","News","Reality-TV","Romance","Sci-Fi","Sport","Western","Talk-Show"};
    
    // Belirtilen isimde yeni bir etiket JSONObject'i oluşturur
    public static JSONObject returnTagTemplate(String tagName) {
        JSONObject newTag = new JSONObject();
        newTag.put("tag", tagName);
        return newTag;
    }
    
    // Medya klasörü için temel JSON yapısını oluşturur
    public static JSONObject createMediaJson() {
        JSONObject mediaJson = new JSONObject();
        JSONArray tags = new JSONArray();
        
        for(String tag : defaultTags) {
            tags.put(returnTagTemplate(tag));
        }
        
        mediaJson.put("lastId", 0); // Son ID'yi saklar
        mediaJson.put("tags", tags); // Tüm etiketleri saklar
        return mediaJson;
    }
    
    // JSON dosyasını okuyup JSONObject olarak döndürür
    public static JSONObject getJson(File jsonFile) throws FileNotFoundException, IOException, ParseException {
        String content = new String(Files.readAllBytes(jsonFile.toPath()));
        return new JSONObject(content);
    }
    
    // JSON dosyasını günceller
    public static void updJson(JSONObject newJson, File toUpd) throws IOException {
        if(!CopyMedia.getExtension(toUpd.toString()).equals("json")) {
            throw new IOException("Dosya uzantısı .json olmalıdır");
        }
        
        try (FileWriter file = new FileWriter(toUpd, false)) {
            file.write(newJson.toString());
        }
    }
    
    // Medya klasöründeki tüm etiketleri alfabetik sırada döndürür
    public static List<String> getTags() throws FileNotFoundException, IOException, ParseException {
        JSONObject mediaJson = getJson(CopyMedia.returnMediaJson().toFile());
        List<String> tagNames = new ArrayList<>();
        
        JSONArray tags = mediaJson.getJSONArray("tags");
        for(int i = 0; i < tags.length(); i++) {
            tagNames.add(tags.getJSONObject(i).getString("tag"));
        }
        
        Collections.sort(tagNames);
        return tagNames;
    }
    
    // Yeni etiket ekler
    public static void addNewTag(String tagName) throws FileNotFoundException, IOException, ParseException {
        JSONObject mediaJson = getJson(CopyMedia.returnMediaJson().toFile());
        mediaJson.append("tags", returnTagTemplate(tagName));
        updJson(mediaJson, CopyMedia.returnMediaJson().toFile());
    }
    
    // Etiket siler
    public static void delTag(String tagName, Path jsonFile) throws FileNotFoundException, IOException, ParseException {
        JSONObject json = getJson(jsonFile.toFile());
        JSONArray tags = json.getJSONArray("tags");
        
        for(int i = 0; i < tags.length(); i++) {
            JSONObject tag = tags.getJSONObject(i);
            if(tag.has("tag") && tag.getString("tag").equals(tagName)) {
                tags.remove(i);
                break; // Etiket bulunduğunda döngüden çık
            }
        }
        
        updJson(json, jsonFile.toFile());
    }
    
    // Yeni dizi JSON'u oluşturur
    public static JSONObject createSerieJson(String serieName) throws FileNotFoundException, IOException, ParseException {
        JSONObject serieJson = new JSONObject();
        JSONObject mediaJson = getJson(CopyMedia.returnMediaJson().toFile());
        
        int id = mediaJson.getInt("lastId");
        mediaJson.put("lastId", id + 1); // ID'yi artır
        updJson(mediaJson, CopyMedia.returnMediaJson().toFile());
        
        JSONArray tags = new JSONArray();
        tags.put(returnTagTemplate(null));
        
        // Dizi bilgilerini ayarla
        serieJson.put("serieId", id + 1);
        serieJson.put("nextSerieId", serieName);
        serieJson.put("lastVideoId", 0);
        serieJson.put("rating", "");
        serieJson.put("comment", "");
        serieJson.put("tags", tags);
        
        return serieJson;
    }
    
    // Yeni video JSON'u oluşturur
    public static JSONObject createVideoJson(String videoName, File serieJsonFile) throws FileNotFoundException, IOException, ParseException {
        JSONObject videoJson = new JSONObject();
        JSONObject serieJson = getJson(serieJsonFile);
        
        int id = serieJson.getInt("lastVideoId");
        serieJson.put("lastVideoId", id + 1); // Video ID'sini artır
        updJson(serieJson, serieJsonFile);
        
        JSONArray tags = new JSONArray();
        tags.put(returnTagTemplate(null));
        
        // Video bilgilerini ayarla
        videoJson.put("videoId", id + 1);
        videoJson.put("videoName", videoName);
        videoJson.put("rating", "");
        videoJson.put("comment", "");
        videoJson.put("tags", tags);
        
        return videoJson;
    }
}