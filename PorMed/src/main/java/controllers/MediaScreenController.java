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

public class MediaScreenController {

	@FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    private BorderPane pageBorderPane;

    @FXML
    private TextField pageNumber;

    @FXML
    private BorderPane pane;

    @FXML
    private Button previousButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox vbox;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private ScrollPane scrollPane;
    
    public int pageNum;
    
    public List<Path> series;
    
    public List<String> selectedTags=new ArrayList<>();
    
    public Bubbles bubbleImages=new Bubbles();
    
    @FXML
    void initialize() throws FileNotFoundException, IOException, ParseException {
    	
    	Task<Void> task = new Task<Void>() {
	         @Override protected Void call() throws Exception {
	        	 while(true) {
	        		 if(isCancelled()) {
	        			 break;
	        		 }
	        		 Platform.runLater(new Runnable() {
	                     @Override public void run() {
	                    	 ImageView bubbleImage=bubbleImages.getBubble();
	                    	 pane.getChildren().addFirst(bubbleImage);
	                     }
	                 });
	        		 Thread.sleep((1+(int)Math.random()*3)*1000);
	        	 }
	        	 return null;
	         }
	     };
	     
	    Thread th = new Thread(task);
        th.start();
        
        pane.setOnKeyPressed(e->{
        	if(e.getCode()==KeyCode.ESCAPE) {
        		goBackPage();
        	}
        	else if(e.getCode()==KeyCode.ENTER) {
        		try {
					givenPage();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        });
        
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(3); 
        gridPane.setHgap(3);
        gridPane.setMaxSize(600, 750);
        
        List<String> tags = JsonOperations.getTags();
        int k = 0;

        for (int i = 0; i <= tags.size() / 4 + 1; i++) {
            for (int j = 0; j < 4; j++) {
                if (k >= tags.size()) {
                    break;
                }
                Label tagLabel = new Label(tags.get(k));
                tagLabel.setAlignment(Pos.CENTER);
                tagLabel.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
                tagLabel.getStyleClass().add("boxed-label");
                tagLabel.getProperties().put("selected", "no");
                tagLabel.setOnMousePressed(e->{
                	if(tagLabel.getProperties().get("selected").equals("no")) {
                		tagLabel.getProperties().put("selected", "yes");
                		tagLabel.getStyleClass().add("boxed-label-focused");
                		selectedTags.add(tagLabel.getText());
                	}
                	else {
                		tagLabel.getProperties().put("selected", "no");
                		tagLabel.getStyleClass().removeLast();
                		selectedTags.remove(tagLabel.getText());
                	}
                });
                
                gridPane.add(tagLabel, j, i);
                k++;
            }
        }
        
        scrollPane.setContent(gridPane);
        pane.setRight(scrollPane);
    }
    
    void goBackPage() {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
            backButton.getScene().setRoot(root);
        } catch (Exception e) {
            System.out.println("Error Could Not Open/Find fxml File");
        }
    }
    
    
    @FXML
    void goBack(ActionEvent event) {
    	goBackPage();
    }
    
    void getMedia() throws IOException {
    	
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(900, 400);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10); 
        gridPane.setHgap(10);
        
        int k = (pageNum-1)*10;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (k >= series.size()) {
                    break;
                }
                VBox serie =new VBox();
                serie.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
                serie.getStyleClass().add("vbox");
                serie.getProperties().put("path", series.get(k));
                serie.setOnMouseClicked((MouseEvent event)->{
                	try {
                		FXMLLoader serieLoader = new FXMLLoader(getClass().getResource("/views/SerieScreen.fxml"));
                    	Parent root=serieLoader.load();
						SerieScreenController controller=serieLoader.getController();
						controller.videos=Media.getVideos(Paths.get(serie.getProperties().get("path").toString()));
						controller.serie=Paths.get(serie.getProperties().get("path").toString());
						controller.mediaRoot=pane.getScene().getRoot();
						controller.setProperties();
	                    pane.getScene().setRoot(root);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                });

                Image posterImage=new Image(Media.getSeriePoster(series.get(k)).toUri().toURL().toExternalForm());
                ImageView poster=new ImageView();
                poster.setImage(posterImage);
                poster.getStyleClass().add("poster");
                poster.setFitHeight(210);
                poster.setFitWidth(150);
                serie.getChildren().add(poster);

                Label serieName = new Label(series.get(k).getFileName().toString());
                serieName.getStyleClass().add("poster-label");
                serieName.setWrapText(true);
                serie.getChildren().addLast(serieName);
                
                gridPane.add(serie, j, i);
                k++;
            }
        }

        vbox.getChildren().add(1,gridPane);
    }
    
    void setPageNum() {
    	try {
        	pageNum=Integer.parseInt(pageNumber.getText());
        }
        catch (NumberFormatException e){
        	pageNum=1;
        }
    }
    
    
    void checkPageNum() {
    	if(pageNum<1) {
    		pageNum=1;
    	}
    	else if(series.size()>0 && pageNum>(int) Math.ceil(((double)series.size())/10)) {
    		pageNum=(int) Math.ceil(((double)series.size())/10); 	
    	}
    }
    
    void setPageNumberText() {
    	checkPageNum();
    	pageNumber.setText(""+pageNum);
    }

    @FXML
    void nextPage(ActionEvent event) throws IOException {
    	FXMLLoader mediaLoader = new FXMLLoader(getClass().getResource("/views/MediaScreen.fxml"));
    	Parent root=mediaLoader.load();
    	MediaScreenController controller=mediaLoader.getController();
    	controller.series=series;
    	controller.pageNum=pageNum+1;
    	controller.setPageNumberText();
    	controller.getMedia();
    	pane.getScene().setRoot(root);
    }

    @FXML
    void previousPage(ActionEvent event) throws IOException {
    	FXMLLoader mediaLoader = new FXMLLoader(getClass().getResource("/views/MediaScreen.fxml"));
    	Parent root=mediaLoader.load();
    	MediaScreenController controller=mediaLoader.getController();
    	controller.series=series;
    	controller.pageNum=pageNum-1;
    	controller.setPageNumberText();
    	controller.getMedia();
    	pane.getScene().setRoot(root);
    }
    
    void givenPage() throws IOException {
    	setPageNum();
    	FXMLLoader mediaLoader = new FXMLLoader(getClass().getResource("/views/MediaScreen.fxml"));
    	Parent root=mediaLoader.load();
    	MediaScreenController controller=mediaLoader.getController();
    	controller.series=series;
    	controller.pageNum=pageNum;
    	controller.setPageNumberText();
    	controller.getMedia();
    	pane.getScene().setRoot(root);
    }
    
    @FXML
    void search(ActionEvent event) throws IOException, ParseException {
    	series=Media.getSeriesFiltered(searchField.getText(), selectedTags);
    	FXMLLoader mediaLoader = new FXMLLoader(getClass().getResource("/views/MediaScreen.fxml"));
    	Parent root=mediaLoader.load();
    	MediaScreenController controller=mediaLoader.getController();
    	controller.series=series;
    	controller.pageNum=1;
    	controller.setPageNumberText();
    	controller.getMedia();
    	pane.getScene().setRoot(root);
    }

}
