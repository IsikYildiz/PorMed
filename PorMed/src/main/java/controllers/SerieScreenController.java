package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafxCode.Bubbles;
import fileOperations.CopyMedia;
import fileOperations.Media;
import fileOperations.JsonOperations;
import org.json.JSONObject;
import java.nio.file.Files;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SerieScreenController {

    @FXML
    private Button backButton;
    @FXML
    private TextArea comment;
    @FXML
    private BorderPane pane;
    @FXML
    private TextField rating;
    @FXML
    private TextField serieName;
    @FXML
    private VBox vbox;
    @FXML
    private ScrollPane videoPane;
    @FXML
    private VBox videoList;
    @FXML
    private Button posterButton;
    
    public Bubbles bubbleImages = new Bubbles();
    public List<Path> videos;
    public Path serie;
    public Parent mediaRoot;
    private JSONObject serieJson;

    // Pencere başlatıldığında çalışan metod
    @FXML
    void initialize() throws FileNotFoundException, IOException, ParseException {
        startBubbleAnimation();
        setupKeyListeners();
        loadSerieData();
    }

    // Baloncuk animasyonunu başlatır
    private void startBubbleAnimation() {
        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                while(!isCancelled()) {
                    Platform.runLater(() -> {
                        ImageView bubbleImage = bubbleImages.getBubble();
                        pane.getChildren().addFirst(bubbleImage);
                    });
                    Thread.sleep((1 + (int)(Math.random() * 3)) * 1000);
                }
                return null;
            }
        };
        
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    // Klavye dinleyicilerini ayarlar
    private void setupKeyListeners() {
        pane.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                goBack(null);
            }
        });

        rating.setOnMouseClicked(e -> rating.requestFocus());
        rating.setOnKeyPressed(e -> {
            if(rating.isFocused() && e.getCode() == KeyCode.ENTER) {
                saveRatingAndComment();
            }
        });
    }

    // Dizi verilerini yükler
    private void loadSerieData() throws IOException, ParseException {
        // Dizi JSON dosyasını bul
        Path jsonFile = Files.list(serie)
                .filter(p -> p.toString().endsWith(".json"))
                .findFirst()
                .orElse(null);
        
        if(jsonFile != null) {
            serieJson = JsonOperations.getJson(jsonFile.toFile());
            rating.setText(serieJson.optString("rating", ""));
            comment.setText(serieJson.optString("comment", ""));
        }
    }

    // Dizi özelliklerini ayarlar ve videoları listeler
    void setProperties() {
        serieName.setText(serie.getFileName().toString());
        videoList.getChildren().clear();
        
        for(Path video : videos) {
            createVideoLabel(video);
        }
    }

    // Video etiketi oluşturur
    private void createVideoLabel(Path video) {
        Label videoLabel = new Label(video.getFileName().toString());
        videoLabel.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
        videoLabel.getStyleClass().add("video-label");
        videoLabel.getProperties().put("videoPath", video);
        
        videoLabel.setOnMouseClicked(e -> openVideoScreen(video));
        videoList.getChildren().add(videoLabel);
    }

    // Video ekranını açar
    private void openVideoScreen(Path video) {
        try {
            FXMLLoader serieLoader = new FXMLLoader(getClass().getResource("/views/VideoScreen.fxml"));
            Parent root = serieLoader.load();
            VideoScreenController controller = serieLoader.getController();
            controller.serieRoot = pane.getScene().getRoot();
            controller.videoPath = video;
            controller.setVideo();
            pane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Yeni poster ekler
    @FXML
    void addPoster(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Poster Resmi Seç");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg")
        );
        
        Stage stage = (Stage) pane.getScene().getWindow();
        Path selectedFile = fileChooser.showOpenDialog(stage).toPath();
        
        if(selectedFile != null && CopyMedia.CheckImageExtension(selectedFile.toString())) {
            try {
                String extension = CopyMedia.getExtension(selectedFile.toString());
                Path target = serie.resolve(serie.getFileName() + "poster." + extension);
                Files.copy(selectedFile, target);
                
                // Eski poster varsa sil
                Files.list(serie)
                    .filter(p -> CopyMedia.CheckImageExtension(p.toString()) && !p.equals(target))
                    .forEach(p -> {
                        try { Files.delete(p); } 
                        catch (IOException e) { e.printStackTrace(); }
                    });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Yeni video ekler
    @FXML
    void addVideo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Video Dosyası Seç");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Video Dosyaları", CopyMedia.supportedExtensions)
        );
        
        Stage stage = (Stage) pane.getScene().getWindow();
        Path selectedFile = fileChooser.showOpenDialog(stage).toPath();
        
        if(selectedFile != null && CopyMedia.CheckExtention(selectedFile.toString())) {
            try {
                String videoName = CopyMedia.removeExtension(selectedFile);
                Path videoDir = serie.resolve(videoName);
                Files.createDirectory(videoDir);
                
                // Video JSON'u oluştur
                Path jsonFile = serie.resolve(serie.getFileName() + ".json");
                JsonOperations.createVideoJson(videoName, jsonFile.toFile());
                
                // Videoyu kopyala
                Files.copy(selectedFile, videoDir.resolve(selectedFile.getFileName()));
                
                // Listeyi yenile
                videos = Media.getVideos(serie);
                setProperties();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Diziyi siler
    @FXML
    void deleteSerie(ActionEvent event) {
        try {
            // Tüm alt dizinleri ve dosyaları sil
            Files.walk(serie)
                .sorted((a, b) -> b.compareTo(a)) // Reverse order for deletion
                .forEach(path -> {
                    try { Files.delete(path); } 
                    catch (IOException e) { e.printStackTrace(); }
                });
            
            // Ana dizine dön
            goBack(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Önceki ekrana döner
    @FXML
    void goBack(ActionEvent event) {
        try {
            saveRatingAndComment();
            pane.getScene().setRoot(mediaRoot);
        } catch (Exception e) {
            System.err.println("Hata: FXML dosyası açılamadı veya bulunamadı");
            e.printStackTrace();
        }
    }

    // Puan ve yorum bilgilerini kaydeder
    private void saveRatingAndComment() {
        try {
            if(serieJson != null) {
                serieJson.put("rating", rating.getText());
                serieJson.put("comment", comment.getText());
                
                // JSON dosyasını güncelle
                Path jsonFile = Files.list(serie)
                        .filter(p -> p.toString().endsWith(".json"))
                        .findFirst()
                        .orElse(null);
                
                if(jsonFile != null) {
                    JsonOperations.updJson(serieJson, jsonFile.toFile());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}