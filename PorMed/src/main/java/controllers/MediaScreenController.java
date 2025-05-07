package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import fileOperations.JsonOperations;
import fileOperations.Media;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafxCode.Bubbles;

/**
 * Medya içeriklerinin gösterildiği ekranın kontrolcüsü.
 * Sayfalama, arama ve etiket filtreleme özelliklerini yönetir.
 */
public class MediaScreenController {

    @FXML private Button backButton;          // Ana ekrana dön butonu
    @FXML private Button nextButton;         // Sonraki sayfa butonu
    @FXML private BorderPane pageBorderPane; // Sayfa düzeni
    @FXML private TextField pageNumber;      // Sayfa numarası girişi
    @FXML private BorderPane pane;           // Ana panel
    @FXML private Button previousButton;     // Önceki sayfa butonu
    @FXML private TextField searchField;     // Arama alanı
    @FXML private VBox vbox;                 // İçerik konteyneri
    @FXML private Button searchButton;       // Arama butonu
    @FXML private ScrollPane scrollPane;     // Etiketler için kaydırma paneli
    
    public int pageNum;                      // Geçerli sayfa numarası
    public List<Path> series;                // Gösterilecek seri listesi
    public List<String> selectedTags = new ArrayList<>(); // Seçili etiketler
    public Bubbles bubbleImages = new Bubbles(); // Baloncuk animasyonları
    private Thread bubbleThread;             // Baloncuk animasyon thread'i

    /**
     * Controller başlatıldığında çalışan metod.
     * Animasyonları başlatır, etiketleri yükler ve olay dinleyicilerini ayarlar.
     */
    @FXML void initialize() throws FileNotFoundException, IOException, ParseException {
        startBubbleAnimation();
        loadTags();
        setupEventHandlers();
    }

    // Baloncuk animasyonunu başlatır
    private void startBubbleAnimation() {
        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                while (!isCancelled()) {
                    Platform.runLater(() -> pane.getChildren().addFirst(bubbleImages.getBubble()));
                    Thread.sleep((1000 + (int)(Math.random() * 2000))); // 1-3 sn rastgele bekleme
                }
                return null;
            }
        };
        bubbleThread = new Thread(task);
        bubbleThread.setDaemon(true); // Uygulama kapatıldığında otomatik sonlansın
        bubbleThread.start();
    }

    // Etiketleri yükler ve grid'de gösterir
    private void loadTags() throws FileNotFoundException, IOException, ParseException {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(3); gridPane.setHgap(3);
        gridPane.setMaxSize(600, 750);

        List<String> tags = JsonOperations.getTags();
        int k = 0;
        for (int i = 0; i <= tags.size() / 4 + 1; i++) {
            for (int j = 0; j < 4; j++) {
                if (k >= tags.size()) break;
                Label tagLabel = createTagLabel(tags.get(k));
                gridPane.add(tagLabel, j, i);
                k++;
            }
        }
        scrollPane.setContent(gridPane);
        pane.setRight(scrollPane);
    }

    // Tek bir etiket label'ı oluşturur
    private Label createTagLabel(String tagText) {
        Label tagLabel = new Label(tagText);
        tagLabel.setAlignment(Pos.CENTER);
        tagLabel.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
        tagLabel.getStyleClass().add("boxed-label");
        tagLabel.getProperties().put("selected", "no");
        
        tagLabel.setOnMousePressed(e -> {
            if (tagLabel.getProperties().get("selected").equals("no")) {
                tagLabel.getProperties().put("selected", "yes");
                tagLabel.getStyleClass().add("boxed-label-focused");
                selectedTags.add(tagLabel.getText());
            } else {
                tagLabel.getProperties().put("selected", "no");
                tagLabel.getStyleClass().removeLast();
                selectedTags.remove(tagLabel.getText());
            }
        });
        return tagLabel;
    }

    // Klavye ve fare olaylarını ayarlar
    private void setupEventHandlers() {
        pane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) goBackPage();
            else if (e.getCode() == KeyCode.ENTER) try { givenPage(); } catch (IOException ex) { ex.printStackTrace(); }
        });
    }

    // Medya içeriklerini yükler ve gösterir
    void getMedia() throws IOException {
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(900, 400);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10); gridPane.setHgap(10);
        
        int k = (pageNum-1)*10; // Sayfalama için başlangıç indeksi
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (k >= series.size()) break;
                VBox serie = createSerieBox(series.get(k));
                gridPane.add(serie, j, i);
                k++;
            }
        }
        vbox.getChildren().add(1, gridPane);
    }

    // Tek bir seri kutusu oluşturur
    private VBox createSerieBox(Path seriePath) throws IOException {
        VBox serie = new VBox();
        serie.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
        serie.getStyleClass().add("vbox");
        serie.getProperties().put("path", seriePath);
        
        // Tıklandığında seri ekranını açar
        serie.setOnMouseClicked((MouseEvent event) -> {
            try {
                FXMLLoader serieLoader = new FXMLLoader(getClass().getResource("/views/SerieScreen.fxml"));
                Parent root = serieLoader.load();
                SerieScreenController controller = serieLoader.getController();
                controller.videos = Media.getVideos(Paths.get(serie.getProperties().get("path").toString()));
                controller.serie = Paths.get(serie.getProperties().get("path").toString());
                controller.mediaRoot = pane.getScene().getRoot();
                controller.setProperties();
                pane.getScene().setRoot(root);
            } catch (IOException e) { e.printStackTrace(); }
        });

        // Poster resmini ekler
        Image posterImage = new Image(Media.getSeriePoster(seriePath).toUri().toURL().toExternalForm());
        ImageView poster = new ImageView(posterImage);
        poster.getStyleClass().add("poster");
        poster.setFitHeight(210); poster.setFitWidth(150);
        
        // Seri adını ekler
        Label serieName = new Label(seriePath.getFileName().toString());
        serieName.getStyleClass().add("poster-label");
        serieName.setWrapText(true);
        
        serie.getChildren().addAll(poster, serieName);
        return serie;
    }

    // Sayfa numarasını ayarlar ve kontrol eder
    void setPageNum() { try { pageNum=Integer.parseInt(pageNumber.getText()); } catch (NumberFormatException e){ pageNum=1; } }
    void checkPageNum() {
        if (pageNum < 1) pageNum = 1;
        else if (series.size() > 0 && pageNum > (int) Math.ceil(((double)series.size())/10)) 
            pageNum = (int) Math.ceil(((double)series.size())/10);
    }
    void setPageNumberText() { checkPageNum(); pageNumber.setText(""+pageNum); }

    // Navigasyon işlemleri
    @FXML void goBack(ActionEvent event) { goBackPage(); }
    void goBackPage() { try { pane.getScene().setRoot(FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"))); } catch (Exception e) { e.printStackTrace(); } }
    @FXML void nextPage(ActionEvent event) throws IOException { navigateToPage(pageNum + 1); }
    @FXML void previousPage(ActionEvent event) throws IOException { navigateToPage(pageNum - 1); }
    void givenPage() throws IOException { setPageNum(); navigateToPage(pageNum); }
    
    // Belirtilen sayfaya geçiş yapar
    private void navigateToPage(int newPageNum) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MediaScreen.fxml"));
        Parent root = loader.load();
        MediaScreenController controller = loader.getController();
        controller.series = series;
        controller.pageNum = newPageNum;
        controller.setPageNumberText();
        controller.getMedia();
        pane.getScene().setRoot(root);
    }

    // Arama işlemini gerçekleştirir
    @FXML void search(ActionEvent event) throws IOException, ParseException {
        series = Media.getSeriesFiltered(searchField.getText(), selectedTags);
        navigateToPage(1);
    }

    // Kaynakları temizler
    public void cleanup() { if (bubbleThread != null && bubbleThread.isAlive()) bubbleThread.interrupt(); }
}