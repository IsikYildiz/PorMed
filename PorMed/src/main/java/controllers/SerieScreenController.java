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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafxCode.Bubbles;

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
    private GridPane videoGrid;
    
    public Bubbles bubbleImages=new Bubbles();
    
    public List<Path> videos;
    
    public Path serie;
    
    public Parent mediaRoot;
    
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
        		try {
        			pane.getScene().setRoot(mediaRoot);	
                } catch (Exception a) {
                    System.out.println("Error Could Not Open/Find fxml File");
                }
        	}
        });
        
        rating.setOnMouseClicked(e->{
        	rating.requestFocus();
        });
        rating.setOnKeyPressed(e->{
        	if(rating.isFocused() && e.getCode()==KeyCode.ENTER) {
        		
        	}
        });
    }
    
    void setProperties() {
    	serieName.setText(serie.getFileName().toString());
    	
    	videoGrid.setAlignment(Pos.CENTER);
    	videoGrid.setVgap(3); 
    	videoGrid.setHgap(3);
    	
    	for(Path video:videos) {
        	Label videoLabel=new Label(video.getFileName().toString());
        	videoLabel.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
        	videoLabel.getStyleClass().add("video-label");
        	videoGrid.getChildren().add(videoLabel);
        }
    }
    
    @FXML
    void goBack(ActionEvent event) {
    	try {
    		pane.getScene().setRoot(mediaRoot);
        } catch (Exception a) {
            System.out.println("Error Could Not Open/Find fxml File");
        }
    }

}
