package controllers;

import java.io.File; // Dosya işlemleri için gerekli
import java.io.IOException;
import java.util.List;
import org.json.simple.parser.ParseException;
import fileOperations.CopyMedia; // Medya kopyalama işlemleri için gerekli
import javafx.application.Platform; // JavaFX thread yönetimi için
import javafx.concurrent.Task; // Arka plan işlemleri için
import javafx.event.ActionEvent; // Olay yönetimi için
import javafx.fxml.FXML; // FXML etiketlerini Java koduna bağlamak için
import javafx.fxml.FXMLLoader; // FXML dosyalarını yüklemek için
import javafx.scene.Parent; // UI elemanlarının temel sınıfı
import javafx.scene.control.Alert; // Uyarı mesajları için
import javafx.scene.control.Button; // Buton kontrolü için
import javafx.scene.control.TextField; // Metin giriş alanı için
import javafx.scene.image.ImageView; // Resim görüntüleme için
import javafx.scene.input.KeyCode; // Klavye tuşlarını algılamak için
import javafx.scene.layout.BorderPane; // Layout yöneticisi
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser; // Dizin seçme penceresi için
import javafx.stage.FileChooser; // Dosya seçme penceresi için
import javafxCode.AlertMessage; // Özel uyarı mesajları için
import javafxCode.Bubbles; // Özel görsel efektler için

public class NewMediaScreenController {

    @FXML
    private Button backButton; // Geri gitme butonu

    @FXML
    private Button directoryButton; // Dizin seçme butonu

    @FXML
    private Button fileButton; // Dosya seçme butonu

    @FXML
    private TextField mediaName; // Medya adı giriş alanı

    @FXML
    private BorderPane pane; // Ana panel

    public Bubbles bubbleImages = new Bubbles(); // Kabarcık görselleri nesnesi

    @FXML
    void initialize() {
        // Sayfa yüklendiğinde çalışacak kod
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Arka planda sürekli çalışacak görev
                while (true) {
                    // İşlem iptal edilmediyse devam et
                    if (isCancelled()) {
                        break;
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // UI thread'inde kabarcık ekleme
                            ImageView bubbleImage = bubbleImages.getBubble();
                            pane.getChildren().addFirst(bubbleImage);
                        }
                    });
                    Thread.sleep((1 + (int) Math.random() * 3) * 1000); // Rastgele aralıklarla bekle
                }
                return null;
            }
        };

        Thread th = new Thread(task); // Yeni thread oluştur
        th.start(); // Thread'i başlat

        pane.setOnKeyPressed(e -> {
            // Pane üzerinde tuşa basıldığında çalışacak kod
            if (e.getCode() == KeyCode.ESCAPE) {
                // Escape tuşuna basıldıysa ana ekrana dön
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
                    backButton.getScene().setRoot(root);
                } catch (Exception a) {
                    System.out.println("Error Could Not Open/Find fxml File"); // FXML dosyası bulunamazsa hata mesajı
                }
            }
        });
    }

    @FXML
    void chooseDirectory(ActionEvent event) throws ParseException, IOException {
        // Dizin seçme butonuna tıklandığında çalışacak kod
        Alert alert = null; // Uyarı mesajı nesnesi
        if (!mediaName.getText().isBlank()) {
            // Medya adı boş değilse
            DirectoryChooser chooser = new DirectoryChooser(); // Dizin seçici oluştur
            chooser.setTitle("Select a directory"); // Pencere başlığı
            File selectedFile = chooser.showDialog(fileButton.getScene().getWindow()); // Dizin seçme penceresini göster
            if (selectedFile != null) {
                // Bir dizin seçildiyse
                String message = CopyMedia.CreateVideo(mediaName.getText(), selectedFile.toPath()); // Medya oluştur ve sonucu al
                if (message.equalsIgnoreCase("Success")) {
                    // İşlem başarılıysa başarı mesajı göster
                    alert = AlertMessage.makeSucces(message);
                } else {
                    // İşlem başarısızsa uyarı mesajı göster
                    alert = AlertMessage.makeWarning(message);
                }
                alert.show(); // Uyarı mesajını göster
            }
        } else {
            // Medya adı boşsa uyarı mesajı göster
            alert = AlertMessage.makeWarning("Please Enter a Name!");
            alert.show();
        }
    }

    @FXML
    void chooseFile(ActionEvent event) throws ParseException, IOException {
        // Dosya seçme butonuna tıklandığında çalışacak kod
        Alert alert = null; // Uyarı mesajı nesnesi
        if (!mediaName.getText().isBlank()) {
            // Medya adı boş değilse
            FileChooser chooser = new FileChooser(); // Dosya seçici oluştur
            chooser.setTitle("Select a video"); // Pencere başlığı
            File selectedFile = chooser.showOpenDialog(fileButton.getScene().getWindow()); // Dosya seçme penceresini göster
            if (selectedFile != null) {
                // Bir dosya seçildiyse
                String message = CopyMedia.CreateVideo(mediaName.getText(), selectedFile.toPath()); // Medya oluştur ve sonucu al
                if (message.equalsIgnoreCase("Success")) {
                    // İşlem başarılıysa başarı mesajı göster
                    alert = AlertMessage.makeSucces(message);
                } else {
                    // İşlem başarısızsa uyarı mesajı göster
                    alert = AlertMessage.makeWarning(message);
                }
                alert.show(); // Uyarı mesajını göster
            }
        } else {
            // Medya adı boşsa uyarı mesajı göster
            alert = AlertMessage.makeWarning("Please Enter a Name!");
            alert.show();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        // Geri gitme butonuna tıklandığında çalışacak kod
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml")); // Ana ekran FXML'ini yükle
            backButton.getScene().setRoot(root); // Sahneyi ana ekranla değiştir
        } catch (Exception e) {
            System.out.println("Error Could Not Open/Find fxml File"); // FXML dosyası bulunamazsa hata mesajı
        }
    }

}