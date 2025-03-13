package application;
	
import java.io.IOException;

import fileOperations.CopyMedia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class PorMed extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		CopyMedia.CreateMedia();
		FXMLLoader firstScreenLoader = new FXMLLoader(getClass().getResource("/views/MainScreen.fxml"));
        Scene scene = new Scene(firstScreenLoader.load());
        primaryStage.setTitle("PorMed");
        Image icon=new Image("/ui/Logo.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
