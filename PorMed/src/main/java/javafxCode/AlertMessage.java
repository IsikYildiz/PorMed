package javafxCode;

import javafx.scene.control.Alert;

public class AlertMessage {
	
	public static Alert makeWarning(String alertMessage) {
		Alert alert=new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Error");
		alert.setHeaderText(alertMessage);
		return alert;
	}
	
	public static Alert makeSucces(String alertMessage) {
		Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Success");
		alert.setHeaderText(alertMessage);
		return alert;
	}
}
