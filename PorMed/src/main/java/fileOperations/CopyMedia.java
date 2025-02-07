package fileOperations;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CopyMedia {
	final public static String[] supportedExtensions = {"webm", "mkv", "flv", "vob", "ogv", "ogg", "rrc", "gifv", "mng", "mov", "avi", "qt", "wmv", "yuv", "rm", "asf", "amv", "mp4", "m4p", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "m4v", "svi", "3gp", "3g2", "mxf", "roq", "nsv", "flv", "f4v", "f4p", "f4a", "f4b", "mod"}; 
	
	//Uygulamanın bulunduğu klasörde /Media klasörü yoksa media klasörünü oluşturur. Daha sonra Media klasörünün yolu geriye döndürülür.
	public static Path CreateMedia(){
		Path currentDir = Paths.get(System.getProperty("user.dir"));
		Path folderPath = currentDir.resolve("Media");
		if(!Files.isDirectory(folderPath)){
			try {
				Files.createDirectory(folderPath);
				System.out.println("Media directory created!");
				return folderPath;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return folderPath;
	}
	
	//Bir dosyanın yolunu string olarak alır ve uzantısı vidyo uzantısıysa true yoksa false döndürür.
	public static boolean CheckExtention(String videoFile) {
		String extension="";
		int i=videoFile.lastIndexOf('.');
		if(i>=0) {
			extension=videoFile.substring(i+1);
		}
		return Arrays.stream(supportedExtensions).anyMatch(extension::equals);
	}
	
	//Dosya ile klasör ayrı ayrı alındığından bu fonksiyon ikiye bölünebilir.
	public static String CreateVideo(String videoName,Path videoFile) {
		Path media=CreateMedia();
		String video=videoFile.getFileName().toString();
		Path newVideoDir=media.resolve(videoName);
		
		if(Files.isRegularFile(videoFile)) {
			try {
				if(CheckExtention(videoFile.toString())) {
					Files.createDirectory(newVideoDir);
					Path newVideo=newVideoDir.resolve(video);
					Files.createDirectory(newVideo);
					Path targetFile=newVideo.resolve(video);
					Files.copy(videoFile, targetFile);
					return "Succes";
				}
				else {
					return "File isn't a video.";
				}
			} catch (IOException e) {
				System.out.println("Video directory could not be created.");
				e.printStackTrace();
			}
		}
		else if(Files.isDirectory(videoFile)) {
			try (DirectoryStream<Path> videoFileStream = Files.newDirectoryStream(videoFile)) {
				Files.createDirectory(newVideoDir);
				for(Path file:videoFileStream) {
					if(CheckExtention(file.toString())) {
						Path newVideo=newVideoDir.resolve(file.getFileName().toString());
						Files.createDirectory(newVideo);
						Path targetFile=newVideo.resolve(file.getFileName().toString());
						Files.copy(file, targetFile);
					}
					else {
						continue;
					}
				}
				try (DirectoryStream<Path> newVideoDirStream = Files.newDirectoryStream(newVideoDir)) {
					if(!newVideoDirStream.iterator().hasNext()) {
						Files.delete(newVideoDir);
						return "No video found.";
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Succes";
		}
		return "No file found.";
	}
}
