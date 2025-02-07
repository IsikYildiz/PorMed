package controllers;

import java.util.List;

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
    private Button openCollectionsButton;

    @FXML
    private Button openMediaButton;

    @FXML
    private Button openRatingsButton;

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
    void openCollections(ActionEvent event) {

    }

    @FXML
    void openMedia(ActionEvent event) {

    }

    @FXML
    void openRatings(ActionEvent event) {

    }

    @FXML
    void openTag(ActionEvent event) {

    }
}
