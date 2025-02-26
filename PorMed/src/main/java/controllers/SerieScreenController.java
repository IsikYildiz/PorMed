package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.json.simple.parser.ParseException;
import fileOperations.Media;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafxCode.Bubbles;

public class SerieScreenController {

    @FXML
    private BorderPane pane;

    @FXML
    private VBox vbox;
    
    @FXML
    void initialize() throws FileNotFoundException, IOException, ParseException {
        List<Circle> bubbles = Bubbles.getBubbles();
        for (Circle a : bubbles) {
            pane.getChildren().addFirst(a);
        }
        
        pane.setOnKeyPressed(e->{
        	if(e.getCode()==KeyCode.ESCAPE) {
        		try {
                    Parent root = FXMLLoader.load(getClass().getResource("/views/MediaScreen.fxml"));
                    pane.getScene().setRoot(root);
                } catch (Exception a) {
                    System.out.println("Error Could Not Open/Find fxml File");
                }
        	}
        });
        
        List<Path> videos=Media.getVideoPaths();
        
        for(Path video:videos) {
        	Label videoLabel=new Label(video.getFileName().toString());
        	vbox.getChildren().add(videoLabel);
        }
    }

}
