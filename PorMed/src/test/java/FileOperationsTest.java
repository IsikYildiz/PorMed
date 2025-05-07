package fileOperations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.*;
import fileOperations.CopyMedia;
import fileOperations.JsonOperations;

class FileOperationsTest {
    private static final Path TEST_DIR = Paths.get("testMedia");
    private static final Path TEST_VIDEO = TEST_DIR.resolve("testVideo.mp4");
    private static final Path TEST_IMAGE = TEST_DIR.resolve("testImage.png");

    @BeforeAll
    static void setUp() throws IOException {
        // Test için geçici dizin oluştur
        if (!Files.exists(TEST_DIR)) {
            Files.createDirectory(TEST_DIR);
        }
        
        // Test dosyalarını oluştur
        if (!Files.exists(TEST_VIDEO)) {
            Files.createFile(TEST_VIDEO);
        }
        if (!Files.exists(TEST_IMAGE)) {
            Files.createFile(TEST_IMAGE);
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        // Test sonunda geçici dizini sil
        if (Files.exists(TEST_DIR)) {
            Files.walk(TEST_DIR)
                 .sorted((a, b) -> b.compareTo(a)) // reverse order
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });
        }
    }

    @Test
    void testCreateMedia() throws IOException {
        Path mediaPath = CopyMedia.CreateMedia();
        assertTrue(Files.isDirectory(mediaPath));
        assertTrue(Files.exists(mediaPath.resolve("general.json")));
    }

    @Test
    void testCheckExtension() {
        assertTrue(CopyMedia.CheckExtention("video.mp4"));
        assertTrue(CopyMedia.CheckExtention("video.MP4")); // case insensitive
        assertFalse(CopyMedia.CheckExtention("video.txt"));
        assertFalse(CopyMedia.CheckExtention("noextension"));
    }

    @Test
    void testCheckImageExtension() {
        assertTrue(CopyMedia.CheckImageExtension("image.png"));
        assertTrue(CopyMedia.CheckImageExtension("image.JPEG")); // case insensitive
        assertFalse(CopyMedia.CheckImageExtension("image.gif"));
        assertFalse(CopyMedia.CheckImageExtension("noextension"));
    }

    @Test
    void testGetExtension() {
        assertEquals("mp4", CopyMedia.getExtension("video.mp4"));
        assertEquals("", CopyMedia.getExtension("noextension"));
        assertEquals("png", CopyMedia.getExtension(".hidden.png"));
    }

    @Test
    void testRemoveExtension() {
        assertEquals("video", CopyMedia.removeExtension(Paths.get("video.mp4")));
        assertEquals("file", CopyMedia.removeExtension(Paths.get("file")));
        assertEquals(".hidden", CopyMedia.removeExtension(Paths.get(".hidden.png")));
    }

    @Test
    void testCreateVideo_SingleFile() throws Exception {
        String result = CopyMedia.CreateVideo("singleTest", TEST_VIDEO);
        assertEquals("Başarılı", result);
        
        Path videoDir = CopyMedia.returnCurrentDir().resolve("Media").resolve("singleTest");
        assertTrue(Files.isDirectory(videoDir));
        assertTrue(Files.exists(videoDir.resolve("singleTest.json")));
        assertTrue(Files.exists(videoDir.resolve("singleTestposter.png")));
        assertTrue(Files.exists(videoDir.resolve("testVideo").resolve("testVideo.json")));
        assertTrue(Files.exists(videoDir.resolve("testVideo").resolve("testVideo.mp4")));
        
        // Temizlik
        Files.walk(videoDir)
             .sorted((a, b) -> b.compareTo(a))
             .forEach(path -> {
                 try {
                     Files.delete(path);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             });
    }

    @Test
    void testCreateVideo_InvalidFile() throws Exception {
        Path invalidFile = TEST_DIR.resolve("invalid.txt");
        if (!Files.exists(invalidFile)) {
            Files.createFile(invalidFile);
        }
        
        String result = CopyMedia.CreateVideo("invalidTest", invalidFile);
        assertEquals("Dosya bir video değil.", result);
    }

    @Test
    void testJsonOperations_CreateMediaJson() throws Exception {
        JSONObject mediaJson = JsonOperations.createMediaJson();
        assertNotNull(mediaJson);
        assertTrue(mediaJson.has("lastId"));
        assertTrue(mediaJson.has("tags"));
        
        JSONArray tags = mediaJson.getJSONArray("tags");
        assertTrue(tags.length() > 0);
    }

    @Test
    void testJsonOperations_TagManagement() throws Exception {
        // Başlangıçta etiketleri al
        List<String> initialTags = JsonOperations.getTags();
        int initialCount = initialTags.size();
        
        // Yeni etiket ekle
        String newTag = "TestTag" + System.currentTimeMillis();
        JsonOperations.addNewTag(newTag);
        
        // Etiket listesini kontrol et
        List<String> updatedTags = JsonOperations.getTags();
        assertEquals(initialCount + 1, updatedTags.size());
        assertTrue(updatedTags.contains(newTag));
        
        // Etiketi sil
        Path mediaJsonPath = CopyMedia.returnMediaJson();
        JsonOperations.delTag(newTag, mediaJsonPath);
        
        // Etiket listesini tekrar kontrol et
        List<String> finalTags = JsonOperations.getTags();
        assertEquals(initialCount, finalTags.size());
        assertFalse(finalTags.contains(newTag));
    }

    @Test
    void testJsonOperations_CreateSerieJson() throws Exception {
        JSONObject serieJson = JsonOperations.createSerieJson("testSerie");
        assertNotNull(serieJson);
        assertTrue(serieJson.has("serieId"));
        assertTrue(serieJson.has("nextSerieId"));
        assertTrue(serieJson.has("lastVideoId"));
        assertTrue(serieJson.has("rating"));
        assertTrue(serieJson.has("comment"));
        assertTrue(serieJson.has("tags"));
    }

    @Test
    void testJsonOperations_CreateVideoJson() throws Exception {
        // Önce bir dizi JSON'u oluştur
        JSONObject serieJson = JsonOperations.createSerieJson("testSerieForVideo");
        Path serieJsonPath = CopyMedia.returnCurrentDir().resolve("Media").resolve("testSerieForVideo.json");
        try (FileWriter file = new FileWriter(serieJsonPath.toFile())) {
            file.write(serieJson.toString());
        }
        
        // Video JSON'u oluştur
        JSONObject videoJson = JsonOperations.createVideoJson("testVideo", serieJsonPath.toFile());
        assertNotNull(videoJson);
        assertTrue(videoJson.has("videoId"));
        assertTrue(videoJson.has("videoName"));
        assertTrue(videoJson.has("rating"));
        assertTrue(videoJson.has("comment"));
        assertTrue(videoJson.has("tags"));
        
        // Temizlik
        Files.delete(serieJsonPath);
    }
}