package controllers;

import java.util.List;

import fileOperations.Media;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
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

	
	@FXML
	void initialize() {
		List<Circle> bubbles=Bubbles.getBubbles();
		for(Circle a:bubbles) {
			pane.getChildren().add(0,a);
		}
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
