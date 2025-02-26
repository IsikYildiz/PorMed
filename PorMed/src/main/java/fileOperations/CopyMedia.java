package fileOperations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

//Bu sınıf dosyalarla ilgilenir. Media gibi klasörler oluşturur, dosya uzantılarını kontrol eder, Videoları kopyalar v.b
public class CopyMedia {
	final public static String[] supportedExtensions = {"webm", "mkv", "flv", "vob", "ogv", "ogg", "rrc", "gifv", "mng", "mov", "avi", "qt", "wmv", "yuv", "rm", "asf", "amv", "mp4", "m4p", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "m4v", "svi", "3gp", "3g2", "mxf", "roq", "nsv", "flv", "f4v", "f4p", "f4a", "f4b", "mod"}; 
	final public static String[] supportedImageExtensions= {"png","jpeg"};
	
	//Uygulamanın bulunduğu klasörde /Media klasörü yoksa media klasörünü oluşturur. Daha sonra Media klasörünün yolu geriye döndürülür.
	public static Path CreateMedia(){
		Path currentDir = returnCurrentDir();
		Path folderPath = currentDir.resolve("Media");
		if(!Files.isDirectory(folderPath)){
			try {
				Files.createDirectory(folderPath);
				System.out.println("Media directory created!");
				writeMediaJson();
				return folderPath;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writeMediaJson();
		return folderPath;
	}
	
	//Genel JSON dosyasını Media klasörünün içinde oluşturur.
	public static void writeMediaJson() {
		try {
			if(!returnCurrentDir().resolve("Media").resolve("general.json").toFile().exists()) {
				FileWriter file = new FileWriter(new File(returnMediaJson().toString()));
				JSONObject mediaJson=JsonOperations.createMediaJson();
		        file.write(mediaJson.toString());
		        file.close();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//Verilen dosya yoluna bir JSON dosyası oluşturur ve verilen JSONObject i dosyaya yazar.
	public static void writeJson(Path filePath, JSONObject jsonFile) {
		try {
			FileWriter file = new FileWriter(new File(filePath.toString()));
			JSONObject json=jsonFile;
	        file.write(json.toString());
	        file.close();
		}
        catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//Media klasmründeki general.json dosyasının yolunu döndürür.
	public static Path returnMediaJson() {
		return returnCurrentDir().resolve("Media").resolve("general.json");
	}
	
	//Bilgisayarın old. klasörün yolunu döndürür.
	public static Path returnCurrentDir() {
		return Paths.get(System.getProperty("user.dir"));
	}
	
	//Bir dosyanın yolunu string olarak alır ve uzantısı vidyo uzantısıysa true yoksa false döndürür.
	public static boolean CheckExtention(String videoFile) {
		return Arrays.stream(supportedExtensions).anyMatch(getExtension(videoFile)::equals);
	}
	
	//Bir dosyanın yolunu string olarak alır ve uzantısı resim uzantısıysa true yoksa false döndürür.
	public static boolean CheckImageExtension(String videoFile) {
		return Arrays.stream(supportedImageExtensions).anyMatch(getExtension(videoFile)::equals);
	}
	
	//Bir dosyanın adını veya yolunu string olarak alıp geriye dosya uzantısını döndürür.
	public static String getExtension(String videoFile) {
		String extension="";
		int i=videoFile.lastIndexOf('.');
		if(i>=0) {
			extension=videoFile.substring(i+1);
		}
		return extension;
	}
	
	//Bir dosyanın yolunu alır ve geriye dosyanın uzantısız ismini döndürür.
	public static String removeExtension(Path videoFile) {
		int i=videoFile.getFileName().toString().lastIndexOf(".");
		String videoName=videoFile.getFileName().toString().substring(0, i);
		return videoName;
	}
	
	//Bir seri klasörü oluşturur, ve verilen videoyu/dosyanın içindeki videoları klasörün içine kopyalar.
	public static String CreateVideo(String videoName,Path videoFile) throws ParseException {
		Path media=returnCurrentDir().resolve("Media");
		String video=videoFile.getFileName().toString();
		Path newVideoDir=media.resolve(videoName);
		
		if(Files.isRegularFile(videoFile)) {
			try {
				if(CheckExtention(videoFile.toString())) {
					Files.createDirectory(newVideoDir);
					writeJson(newVideoDir.resolve(videoName+".json"),JsonOperations.createSerieJson(videoName));
					Files.copy(CopyMedia.class.getResourceAsStream("/ui/DefaultMovie.png"), newVideoDir.resolve(videoName+"poster.png"));
					Path newVideo=newVideoDir.resolve(removeExtension(videoFile));
					
					Files.createDirectory(newVideo);
					writeJson(newVideo.resolve(removeExtension(videoFile)+".json"),JsonOperations.createVideoJson(removeExtension(videoFile),newVideoDir.resolve(videoName+".json").toFile()));
					Path targetFile=newVideo.resolve(video);
					Files.copy(videoFile, targetFile);
					
					return "Succes";
				}
				else {
					return "File isn't a video.";
				}
			} catch (IOException e) {
				return "There was a problem. Make sure to not use same name.";
			}
		}
		else if(Files.isDirectory(videoFile)) {
			try (DirectoryStream<Path> videoFileStream = Files.newDirectoryStream(videoFile)) {
				Files.createDirectory(newVideoDir);
				writeJson(newVideoDir.resolve(videoName+".json"),JsonOperations.createSerieJson(videoName));
				Files.copy(CopyMedia.class.getResourceAsStream("/ui/DefaultMovie.png"), newVideoDir.resolve(videoName+"poster.png"));
				for(Path file:videoFileStream) {
					if(CheckExtention(file.toString())) {
						Path newVideo=newVideoDir.resolve(removeExtension(file));
						Files.createDirectory(newVideo);
						writeJson(newVideo.resolve(removeExtension(file)+".json"),JsonOperations.createVideoJson(removeExtension(file),newVideoDir.resolve(videoName+".json").toFile()));
						Path targetFile=newVideo.resolve(removeExtension(file));
						Files.copy(file, targetFile);
					}
					else {
						continue;
					}
				}
				if(newVideoDir.toFile().list().length<2) {
					DirectoryStream<Path> Stream = Files.newDirectoryStream(newVideoDir);
					for(Path file:Stream) {
						Files.delete(file);
					}
					Files.delete(newVideoDir);
					Stream.close();
					return "No video found.";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "Succes";
		}
		return "No file found.";
	}
}
