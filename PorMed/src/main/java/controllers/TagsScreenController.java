package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.json.simple.parser.ParseException;

import fileOperations.CopyMedia;
import fileOperations.JsonOperations;
import fileOperations.WalkFile;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafxCode.AlertMessage;
import javafxCode.Bubbles;

/**
 * Etiket yönetim ekranının kontrolcüsü.
 * Etiket ekleme, silme ve listeleme işlemlerini yönetir.
 */
public class TagsScreenController {

    @FXML
    private BorderPane pane; // Ana pane
    
    @FXML
    private VBox vbox; // Etiketlerin gösterileceği dikey kutu
    
    @FXML
    private Button tagButton; // Etiket ekleme butonu
    
    @FXML
    private TextField tagName; // Etiket adı giriş alanı
    
    @FXML
    private Button backButton; // Geri dönme butonu
    
    public Bubbles bubbleImages = new Bubbles(); // Baloncuk animasyonları
    private Thread bubbleThread; // Baloncuk animasyonu için thread

    /**
     * Controller başlatıldığında çalışan metod.
     * Baloncuk animasyonunu başlatır, klavye olaylarını ayarlar ve etiketleri yükler.
     */
    @FXML
    void initialize() throws FileNotFoundException, IOException, ParseException {
        startBubbleAnimation();
        setupKeyHandlers();
        loadTags();
    }

    /**
     * Baloncuk animasyonunu başlatır.
     */
    private void startBubbleAnimation() {
        Task<Void> task = new Task<Void>() {
            @Override 
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    Platform.runLater(() -> {
                        ImageView bubbleImage = bubbleImages.getBubble();
                        pane.getChildren().addFirst(bubbleImage);
                    });
                    Thread.sleep((1000 + (int)(Math.random() * 2000))); // 1-3 saniye arası rastgele bekleme
                }
                return null;
            }
        };
        
        bubbleThread = new Thread(task);
        bubbleThread.setDaemon(true); // Uygulama kapatıldığında otomatik sonlansın
        bubbleThread.start();
    }

    /**
     * Klavye olaylarını ayarlar.
     */
    private void setupKeyHandlers() {
        pane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                goBack(null); // ESC ile geri dön
            }
            else if (e.getCode() == KeyCode.ENTER) {
                try {
                    addTagFunction(); // ENTER ile etiket ekle
                } catch (IOException | ParseException e1) {
                    System.err.println("Etiket eklenirken hata oluştu: " + e1.getMessage());
                }
            }
        });
    }

    /**
     * Etiketleri yükler ve grid pane'de gösterir.
     * @throws ParseException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private void loadTags() throws FileNotFoundException, IOException, ParseException {
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(900, 400);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10); 
        gridPane.setHgap(10);

        List<String> tags = JsonOperations.getTags(); // Tüm etiketleri al
      
        int k = 0;
        // Etiketleri 6 sütunlu grid'e yerleştir
        for (int i = 0; i <= tags.size() / 6 + 1; i++) {
            for (int j = 0; j < 6; j++) {
                if (k >= tags.size()) break;

                Label tagLabel = createTagLabel(tags.get(k));
                gridPane.add(tagLabel, j, i);
                k++;
            }
        }
        
        gridPane.setMaxSize(1200, 600);
        vbox.getChildren().addFirst(gridPane);
    }

    /**
     * Etiket label'ı oluşturur ve olaylarını ayarlar.
     * @param tagName Etiket adı
     * @return Oluşturulan Label nesnesi
     */
    private Label createTagLabel(String tagName) {
        Label tagLabel = new Label(tagName);
        tagLabel.setMinSize(120, 20);
        tagLabel.setAlignment(Pos.CENTER);
        tagLabel.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
        tagLabel.getStyleClass().add("boxed-label");
        
        // Tıklandığında focus al
        tagLabel.setOnMouseClicked(e -> tagLabel.requestFocus());
        
        // DELETE tuşu ile etiketi sil
        tagLabel.setOnKeyPressed(e->{
        	if(e.getCode()==KeyCode.DELETE) {
        		try {
        			WalkFile visitor=new WalkFile(tagLabel.getText());
					Files.walkFileTree(CopyMedia.returnCurrentDir().resolve("Media"), visitor);
					Parent root = FXMLLoader.load(getClass().getResource("/views/TagsScreen.fxml"));
		            backButton.getScene().setRoot(root);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        });
        return tagLabel;
    }

    /**
     * Ekranı yeniler.
     */
    private void refreshScreen() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/TagsScreen.fxml"));
            backButton.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Ekran yenilenirken hata oluştu: " + e.getMessage());
        }
    }

    /**
     * Etiket ekleme butonu olayı.
     */
    @FXML
    void addTag(ActionEvent event) throws FileNotFoundException, IOException, ParseException {
        addTagFunction();
    }
    
    /**
     * Etiket ekleme işlemini yapar.
     */
    void addTagFunction() throws IOException, ParseException {
        Alert alert = null;
        if (!tagName.getText().isBlank()) {
            JsonOperations.addNewTag(tagName.getText());
            refreshScreen();
            alert = AlertMessage.makeSucces("Etiket başarıyla eklendi!");
            alert.show();
        } else {
            alert = AlertMessage.makeWarning("Lütfen bir etiket adı girin!");
            alert.show();
        }
    }
    
    /**
     * Ana ekrana dönme işlemi.
     */
    @FXML
    void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
            backButton.getScene().setRoot(root);
        } catch (Exception e) {
            System.err.println("Ana ekrana dönülürken hata oluştu: " + e.getMessage());
        }
    }

    /**
     * Kaynakları temizler.
     */
    public void cleanup() {
        if (bubbleThread != null && bubbleThread.isAlive()) {
            bubbleThread.interrupt();
        }
    }
}