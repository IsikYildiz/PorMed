package fileOperations;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

// Dosya işlemlerini yöneten sınıf
public class CopyMedia {
    // Desteklenen video uzantıları
    final public static String[] supportedExtensions = {"webm", "mkv", "flv", "vob", "ogv", "ogg", "rrc", "gifv", "mng", "mov", "avi", "qt", "wmv", "yuv", "rm", "asf", "amv", "mp4", "m4p", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "m4v", "svi", "3gp", "3g2", "mxf", "roq", "nsv", "flv", "f4v", "f4p", "f4a", "f4b", "mod"}; 
    // Desteklenen resim uzantıları
    final public static String[] supportedImageExtensions = {"png", "jpeg"};
    
    // Media klasörünü oluşturur
    public static Path CreateMedia() throws IOException {
        Path currentDir = returnCurrentDir();
        Path folderPath = currentDir.resolve("Media");
        
        if(!Files.isDirectory(folderPath)) {
            Files.createDirectory(folderPath);
            System.out.println("Media klasörü oluşturuldu!");
        }
        
        writeMediaJson(); // Genel JSON dosyasını yaz
        return folderPath;
    }
    
    // Genel medya JSON dosyasını oluşturur
    public static void writeMediaJson() throws IOException {
        Path mediaJsonPath = returnMediaJson();
        if(!Files.exists(mediaJsonPath)) {
            try (FileWriter file = new FileWriter(mediaJsonPath.toFile())) {
                JSONObject mediaJson = JsonOperations.createMediaJson();
                file.write(mediaJson.toString());
            }
        }
    }
    
    // JSON dosyası yazar
    public static void writeJson(Path filePath, JSONObject jsonFile) throws IOException {
        try (FileWriter file = new FileWriter(filePath.toFile())) {
            file.write(jsonFile.toString());
        }
    }
    
    // Genel medya JSON dosyasının yolunu döndürür
    public static Path returnMediaJson() {
        return returnCurrentDir().resolve("Media").resolve("general.json");
    }
    
    // Çalışma dizinini döndürür
    public static Path returnCurrentDir() {
        return Paths.get(System.getProperty("user.dir"));
    }
    
    // Video uzantısı kontrolü yapar
    public static boolean CheckExtention(String videoFile) {
        String ext = getExtension(videoFile).toLowerCase();
        return Arrays.stream(supportedExtensions).anyMatch(ext::equals);
    }
    
    // Resim uzantısı kontrolü yapar
    public static boolean CheckImageExtension(String videoFile) {
        String ext = getExtension(videoFile).toLowerCase();
        return Arrays.stream(supportedImageExtensions).anyMatch(ext::equals);
    }
    
    // Dosya uzantısını döndürür
    public static String getExtension(String videoFile) {
        int i = videoFile.lastIndexOf('.');
        return i >= 0 ? videoFile.substring(i + 1) : "";
    }
    
    // Dosya adından uzantıyı kaldırır
    public static String removeExtension(Path videoFile) {
        String fileName = videoFile.getFileName().toString();
        int i = fileName.lastIndexOf(".");
        return i >= 0 ? fileName.substring(0, i) : fileName;
    }
    
    // Yeni video/dizi klasörü oluşturur ve dosyaları kopyalar
    public static String CreateVideo(String videoName, Path videoFile) throws ParseException, IOException {
        Path media = returnCurrentDir().resolve("Media");
        Path newVideoDir = media.resolve(videoName);
        
        if(Files.isRegularFile(videoFile)) {
            return handleSingleVideoFile(videoName, videoFile, newVideoDir);
        } 
        else if(Files.isDirectory(videoFile)) {
            return handleVideoDirectory(videoName, videoFile, newVideoDir);
        }
        
        return "Dosya bulunamadı.";
    }
    
    // Tek video dosyası işleme
    private static String handleSingleVideoFile(String videoName, Path videoFile, Path newVideoDir) throws IOException, ParseException {
        if(!CheckExtention(videoFile.toString())) {
            return "Dosya bir video değil.";
        }
        
        Files.createDirectory(newVideoDir);
        writeJson(newVideoDir.resolve(videoName + ".json"), JsonOperations.createSerieJson(videoName));
        copyDefaultPoster(newVideoDir, videoName);
        
        Path newVideo = newVideoDir.resolve(removeExtension(videoFile));
        Files.createDirectory(newVideo);
        
        writeJson(newVideo.resolve(removeExtension(videoFile) + ".json"), 
                 JsonOperations.createVideoJson(removeExtension(videoFile), 
                 newVideoDir.resolve(videoName + ".json").toFile()));
        
        Files.copy(videoFile, newVideo.resolve(videoFile.getFileName()));
        return "Başarılı";
    }
    
    // Video klasörü işleme
    private static String handleVideoDirectory(String videoName, Path videoFile, Path newVideoDir) throws IOException, ParseException {
        Files.createDirectory(newVideoDir);
        writeJson(newVideoDir.resolve(videoName + ".json"), JsonOperations.createSerieJson(videoName));
        copyDefaultPoster(newVideoDir, videoName);
        
        try (DirectoryStream<Path> videoFileStream = Files.newDirectoryStream(videoFile)) {
            int videoCount = 0;
            
            for(Path file : videoFileStream) {
                if(CheckExtention(file.toString())) {
                    processVideoFile(file, newVideoDir, videoName);
                    videoCount++;
                }
            }
            
            if(videoCount == 0) {
                cleanupEmptyDirectory(newVideoDir);
                return "Video bulunamadı.";
            }
        }
        
        return "Başarılı";
    }
    
    // Varsayılan poster kopyalama
    private static void copyDefaultPoster(Path targetDir, String videoName) throws IOException {
        Files.copy(CopyMedia.class.getResourceAsStream("/ui/DefaultMovie.png"), 
                 targetDir.resolve(videoName + "poster.png"));
    }
    
    // Tek video işleme
    private static void processVideoFile(Path file, Path newVideoDir, String serieName) throws IOException, ParseException {
        String videoName = removeExtension(file);
        Path newVideo = newVideoDir.resolve(videoName);
        
        Files.createDirectory(newVideo);
        writeJson(newVideo.resolve(videoName + ".json"), 
                JsonOperations.createVideoJson(videoName, 
                newVideoDir.resolve(serieName + ".json").toFile()));
        
        Files.copy(file, newVideo.resolve(file.getFileName()));
    }
    
    // Boş dizini temizleme
    private static void cleanupEmptyDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for(Path file : stream) {
                Files.delete(file);
            }
        }
        Files.delete(dir);
    }
}