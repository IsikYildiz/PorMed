package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafxCode.Bubbles;

// Vidyoları oynatan kontrolcü
public class VideoScreenController {

    @FXML
    private BorderPane pane; 
    
    @FXML
    private Button backButton; 
  
    public Bubbles bubbleImages = new Bubbles(); 
    public Parent serieRoot; 
    public Path videoPath; 
    private MediaPlayer mediaPlayer; 
    private Thread bubbleThread; 

    
    @FXML
    void initialize() throws FileNotFoundException, IOException, ParseException {
        startBubbleAnimation();
        setupKeyHandlers();
    }

    // Baloncuk animasyonunu başlatır.
    private void startBubbleAnimation() {
        Task<Void> task = new Task<Void>() {
            @Override 
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    Platform.runLater(() -> {
                        ImageView bubbleImage = bubbleImages.getBubble();
                        pane.getChildren().addFirst(bubbleImage);
                    });
                    Thread.sleep((1000 + (int)(Math.random() * 2000))); 
                }
                return null;
            }
        };
        
        bubbleThread = new Thread(task);
        bubbleThread.setDaemon(true); 
        bubbleThread.start();
    }

   // Kısayol tuşlarını ekler.
    private void setupKeyHandlers() {
        pane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                navigateBack();
            }
        });
    }

    // Vidyoyu başlatır.
    void setVideo() throws MalformedURLException {
        Media media = new Media(videoPath.toUri().toURL().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(1500);
        mediaView.setFitHeight(750);
        
        pane.setCenter(mediaView);
    }

    // Geri gitmeyi sağlar.
    @FXML
    void goBack(ActionEvent event) {
        navigateBack();
    }

    // Önceki ekrana dön.
    private void navigateBack() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            pane.getScene().setRoot(serieRoot);
        } catch (Exception e) {
            System.err.println("Error: Could not navigate back to previous screen");
            e.printStackTrace();
        }
    }

    // Kontrolcü kullanılmadığı zaman kaynakları boşalt.
    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        if (bubbleThread != null && bubbleThread.isAlive()) {
            bubbleThread.interrupt();
        }
    }
}