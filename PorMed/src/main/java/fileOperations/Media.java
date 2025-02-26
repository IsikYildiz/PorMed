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

public class Media {
	public static List<Path> videoPaths=new ArrayList<>();
	
	public static void addVideoPath(Path videoPath) {
		videoPaths.add(videoPath);
	}
	
	public static void clearVideoPaths() {
		videoPaths.clear();
	}
	
	public static List<Path> getVideoPaths(){
		Collections.sort(videoPaths);
		return videoPaths;
	}
	
	public static List<Path> getSeries() throws IOException {
		try (Stream<Path> seriePathsStream = Files.list(CopyMedia.returnCurrentDir().resolve("Media"))) {
	        return seriePathsStream
	                .filter(Files::isDirectory)
	                .collect(Collectors.toList()); 
	    }
	}
	
	public static Path getSeriePoster(Path serie) throws IOException {
		Path poster=null;
		List<Path> serieDir=Files.list(serie).toList();
		for(int i=0;i<serieDir.size();i++) {
			if(CopyMedia.CheckImageExtension(serieDir.get(i).toString())) {
				poster=serieDir.get(i);
			}
		}
		return poster;
	}
	
	public static List<Path> getSeriesFiltered(String search,List<String> filterTags) throws IOException, ParseException{
		List<Path> series=getSeries();
		List<Path> filteredSeries=new ArrayList<>();
		for(Path file:series) {
			if(search.equals("") || file.getFileName().toString().contains(search)) {
				if(!filterTags.isEmpty()){
					Stream<Path> serieJsonPathsStream=Files.list(file);
					for(Path serieFile:serieJsonPathsStream.collect(Collectors.toList())) {
						if(CopyMedia.getExtension(serieFile.toString()).equals("json")) {
							JSONObject serieJson=JsonOperations.getJson(serieFile.toFile());
							JSONArray serieTags=serieJson.getJSONArray("tags");
							List<String> tags=new ArrayList<>();
							for(int i=0;i<serieTags.length();i++)
				            {
				                JSONObject tag = serieTags.getJSONObject(i);
				                try {
				                	tags.add(tag.getString("tag"));
				                }
				                catch (JSONException e) {
				                	continue;
				                }
				            }
							if(tags.containsAll(filterTags)) {
								filteredSeries.add(file);
							}
						}
					}
					serieJsonPathsStream.close();
				}
				else {
					filteredSeries.add(file);
				}
			}
		}
		return filteredSeries;
	}
}
