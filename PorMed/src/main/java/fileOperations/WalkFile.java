package fileOperations;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.json.simple.parser.ParseException;

// Taglerin silinmesiyle ilgili sınıftır.
public class WalkFile implements FileVisitor<Path>{
	public static String tag;
	
	public WalkFile(String tagName) {
		tag=tagName;
	}
	
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
       return FileVisitResult.CONTINUE;
    }
    
    // Tüm json dosyalarını gezer ve eğer tag varsa siler.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    	if(CopyMedia.getExtension(file.toString()).equals("json")) {
    		try {
    			JsonOperations.delTag(tag, file);
    		} catch (IOException | ParseException e) {
    			e.printStackTrace();
    		}
    	}
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        // This is important to note. Test this behaviour
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
       return FileVisitResult.CONTINUE;
    }
}
