package controllers;

import fileOperations.Media;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafxCode.Bubbles;

public class MainScreenController {
    @FXML
    private Button copyButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button openMediaButton;
    @FXML
    private Button openTagButton;
    @FXML
    private BorderPane pane;
    
    public Bubbles bubbleImages = new Bubbles();
    
    @FXML
    void initialize() {
        startBubbleAnimation();
    }

    // Baloncuk animasyonunu başlatan metod
    private void startBubbleAnimation() {
        Task<Void> task = new Task<Void>() {
            @Override 
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    Platform.runLater(() -> {
                        ImageView bubbleImage = bubbleImages.getBubble();
                        pane.getChildren().addFirst(bubbleImage);
                    });
                    Thread.sleep((1 + (int)(Math.random() * 3)) * 1000); // 1-3 saniye arası rastgele bekleme
                }
                return null;
            }
        };
        
        Thread th = new Thread(task);
        th.setDaemon(true); // Uygulama kapatıldığında thread'in de sonlanmasını sağlar
        th.start();
    }

    // Yeni medya ekranını açan metod
    @FXML
    void copyMedia(ActionEvent event) {
        loadNewScene("/views/NewMediaScreen.fxml", event);
    }
    
    // Uygulamadan çıkış yapan metod
    @FXML
    void exit(ActionEvent event) {
        Platform.exit(); // Daha temiz bir çıkış için Platform.exit() kullanıldı
    }

    // Medya ekranını açan metod
    @FXML
    void openMedia(ActionEvent event) {
        try {
            FXMLLoader mediaLoader = new FXMLLoader(getClass().getResource("/views/MediaScreen.fxml"));
            Parent root = mediaLoader.load();
            MediaScreenController controller = mediaLoader.getController();
            controller.series = Media.getSeries();
            controller.pageNum = 1;
            controller.checkPageNum();
            controller.getMedia();
            openMediaButton.getScene().setRoot(root);
        } catch (Exception e) {
            handleSceneLoadError(e);
        }
    }

    // Etiket ekranını açan metod
    @FXML
    void openTag(ActionEvent event) {
        loadNewScene("/views/TagsScreen.fxml", event);
    }

    // Yeni sahne yüklemek için ortak metod
    private void loadNewScene(String fxmlPath, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            ((Button)event.getSource()).getScene().setRoot(root);
        } catch (Exception e) {
            handleSceneLoadError(e);
        }
    }

    // Hata yönetimi için ortak metod
    private void handleSceneLoadError(Exception e) {
        System.err.println("Hata: FXML dosyası açılamadı veya bulunamadı");
        e.printStackTrace(); // Hata detaylarını yazdır
    }
}