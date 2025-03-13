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
import javafx.scene.shape.Circle;
import javafxCode.AlertMessage;
import javafxCode.Bubbles;

public class TagsScreenController {

    @FXML
    private BorderPane pane;
    
    @FXML
    private VBox vbox;

    @FXML
    private Button tagButton;

    @FXML
    private TextField tagName;
    
    @FXML
    private Button backButton;
    
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
        		try {
                    Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
                    pane.getScene().setRoot(root);
                } catch (Exception a) {
                    System.out.println("Error Could Not Open/Find fxml File");
                }
        	}
        	else if(e.getCode()==KeyCode.ENTER) {
        		try {
					addTagFunction();
				} catch (IOException | ParseException e1) {
					e1.printStackTrace();
				}
        	}
        });

        GridPane gridPane = new GridPane();
        gridPane.setMinSize(900, 400);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10); 
        gridPane.setHgap(10);

        List<String> tags = JsonOperations.getTags();
      
        int k = 0;

        for (int i = 0; i <= tags.size() / 6 + 1; i++) {
            for (int j = 0; j < 6; j++) {
                if (k >= tags.size()) {
                    break;
                }
                Label tagLabel = new Label(tags.get(k));
                tagLabel.setMinSize(120, 20);
                tagLabel.setAlignment(Pos.CENTER);
                tagLabel.getStylesheets().add(getClass().getResource("/ui/PorMed.css").toExternalForm());
                tagLabel.getStyleClass().add("boxed-label");
                
                tagLabel.setOnMouseClicked(e->{
                	tagLabel.requestFocus();
                });
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
                
                gridPane.add(tagLabel, j, i);
                k++;
            }
        }
        
        gridPane.setMaxSize(1200, 600);
        vbox.getChildren().addFirst(gridPane);
    }

    @FXML
    void addTag(ActionEvent event) throws FileNotFoundException, IOException, ParseException {
    	addTagFunction();
    }
    
    void addTagFunction() throws IOException, ParseException {
    	Alert alert=null;
    	if(!tagName.getText().isBlank()) {
    		JsonOperations.addNewTag(tagName.getText());
    		Parent root = FXMLLoader.load(getClass().getResource("/views/TagsScreen.fxml"));
            backButton.getScene().setRoot(root);
    		alert=AlertMessage.makeSucces("Succesfully Added Tag!");
        	alert.show();
    	}
    	else {
    		alert=AlertMessage.makeWarning("Please Enter a Name!");
        	alert.show();
    	}
    }
    
    @FXML
    void goBack(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
            backButton.getScene().setRoot(root);
        } catch (Exception e) {
            System.out.println("Error Could Not Open/Find fxml File");
        }
    }

}