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
    
    public Bubbles bubbleImages=new Bubbles();
	
	@FXML
	void initialize() {
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
	}


    @FXML
    void copyMedia(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/NewMediaScreen.fxml"));
            copyButton.getScene().setRoot(root);
        } catch (Exception e) {
            System.out.println("Error Could Not Open/Find fxml File");
        }
    }
    
    @FXML
    void exit(ActionEvent event) {
    	System.exit(1);
    }

    @FXML
    void openMedia(ActionEvent event) {
    	try {
    		FXMLLoader mediaLoader = new FXMLLoader(getClass().getResource("/views/MediaScreen.fxml"));
        	Parent root=mediaLoader.load();
        	MediaScreenController controller=mediaLoader.getController();
        	controller.series=Media.getSeries();
        	controller.pageNum=1;
        	controller.checkPageNum();
        	controller.getMedia();
        	openMediaButton.getScene().setRoot(root);
        } catch (Exception e) {
            System.out.println("Error Could Not Open/Find fxml File");
        }
    }

    @FXML
    void openTag(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/TagsScreen.fxml"));
            copyButton.getScene().setRoot(root);
        } catch (Exception e) {
            System.out.println("Error Could Not Open/Find fxml File");
        }
    }
}
