package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class PorMed extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader firstScreenLoader = new FXMLLoader(getClass().getResource("/views/MainScreen.fxml"));
        Scene scene = new Scene(firstScreenLoader.load());
        primaryStage.setTitle("PorMed");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
