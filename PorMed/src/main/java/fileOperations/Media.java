package fileOperations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

// Medya içeriğini yöneten sınıf
public class Media {
    
    // Tüm dizilerin listesini döndürür
    public static List<Path> getSeries() throws IOException {
        try (Stream<Path> seriePathsStream = Files.list(CopyMedia.returnCurrentDir().resolve("Media"))) {
            List<Path> series = seriePathsStream
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
            Collections.sort(series);
            return series;
        }
    }
    
    // Bir dizideki tüm videoları döndürür
    public static List<Path> getVideos(Path serie) throws IOException {
        try (Stream<Path> videoPathsStream = Files.list(serie)) {
            List<Path> videos = videoPathsStream
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
            Collections.sort(videos);
            return videos;
        }
    }
    
    // Dizinin poster dosyasını bulur
    public static Path getSeriePoster(Path serie) throws IOException {
        try (Stream<Path> files = Files.list(serie)) {
            return files
                    .filter(path -> CopyMedia.CheckImageExtension(path.toString()))
                    .findFirst()
                    .orElse(null);
        }
    }
    
    // Filtrelenmiş dizi listesi döndürür
    public static List<Path> getSeriesFiltered(String search, List<String> filterTags) throws IOException, ParseException {
        List<Path> series = getSeries();
        List<Path> filteredSeries = new ArrayList<>();
        
        for(Path serie : series) {
            if(matchesSearch(serie, search) && matchesTags(serie, filterTags)) {
                filteredSeries.add(serie);
            }
        }
        
        Collections.sort(filteredSeries);
        return filteredSeries;
    }
    
    // Arama kriterlerine uygun mu kontrol eder
    private static boolean matchesSearch(Path serie, String search) {
        return search.isEmpty() || serie.getFileName().toString().toLowerCase().contains(search.toLowerCase());
    }
    
    // Etiket kriterlerine uygun mu kontrol eder
    private static boolean matchesTags(Path serie, List<String> filterTags) throws IOException, ParseException {
        if(filterTags.isEmpty()) return true;
        
        try (Stream<Path> files = Files.list(serie)) {
            for(Path file : files.collect(Collectors.toList())) {
                if(CopyMedia.getExtension(file.toString()).equals("json")) {
                    JSONObject serieJson = JsonOperations.getJson(file.toFile());
                    JSONArray serieTags = serieJson.getJSONArray("tags");
                    List<String> tags = extractTags(serieTags);
                    
                    if(tags.containsAll(filterTags)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    // JSON'dan etiket listesi çıkarır
    private static List<String> extractTags(JSONArray jsonTags) {
        List<String> tags = new ArrayList<>();
        
        for(int i = 0; i < jsonTags.length(); i++) {
            JSONObject tag = jsonTags.getJSONObject(i);
            try {
                tags.add(tag.getString("tag"));
            } catch (JSONException e) {
                continue;
            }
        }
        
        return tags;
    }
}