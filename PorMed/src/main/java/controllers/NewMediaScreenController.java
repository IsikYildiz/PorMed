package controllers;

import java.io.File;
import java.util.List;
import org.json.simple.parser.ParseException;
import fileOperations.CopyMedia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafxCode.AlertMessage;
import javafxCode.Bubbles;

public class NewMediaScreenController {
	
	@FXML
    private Button backButton;

    @FXML
    private Button directoryButton;

    @FXML
    private Button fileButton;

    @FXML
    private TextField mediaName;
    
    @FXML
    private BorderPane pane;
    
    @FXML
	void initialize() {
		List<Circle> bubbles=Bubbles.getBubbles();
		for(Circle a:bubbles) {
			pane.getChildren().add(0,a);
		}
		
		pane.setOnKeyPressed(e->{
        	if(e.getCode()==KeyCode.ESCAPE) {
        		try {
                    Parent root = FXMLLoader.load(getClass().getResource("/views/MainScreen.fxml"));
                    backButton.getScene().setRoot(root);
                } catch (Exception a) {
                    System.out.println("Error Could Not Open/Find fxml File");
                }
        	}
        });
	}

    @FXML
    void chooseDirectory(ActionEvent event) throws ParseException {
    	Alert alert=null;
    	if(!mediaName.getText().isBlank()) {
    		DirectoryChooser chooser=new DirectoryChooser();
    		chooser.setTitle("Select a directory");
    		File selectedFile = chooser.showDialog(fileButton.getScene().getWindow());
    		String message=CopyMedia.CreateVideo(mediaName.getText(),selectedFile.toPath());
    		if(message.equalsIgnoreCase("Success")) {
        		alert=AlertMessage.makeSucces(message);
        	}
        	else {
        		alert=AlertMessage.makeWarning(message);
        	}
        	alert.show();
    	}
    	else {
    		alert=AlertMessage.makeWarning("Please Enter a Name!");
        	alert.show();
    	}
    }

    @FXML
    void chooseFile(ActionEvent event) throws ParseException {
    	Alert alert=null;
    	if(!mediaName.getText().isBlank()) {
    		FileChooser chooser=new FileChooser();
        	chooser.setTitle("Select a video");
        	File selectedFile = chooser.showOpenDialog(fileButton.getScene().getWindow());
        	String message=CopyMedia.CreateVideo(mediaName.getText(),selectedFile.toPath());
        	if(message.equalsIgnoreCase("Success")) {
        		alert=AlertMessage.makeSucces(message);
        	}
        	else {
        		alert=AlertMessage.makeWarning(message);
        	}
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
