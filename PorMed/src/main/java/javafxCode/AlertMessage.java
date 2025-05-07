package javafxCode;

import javafx.scene.control.Alert;

// Kullanıcıya farklı türde bildiriler gösteren sınıftır.
public class AlertMessage {
	
	// Uyarı mesajı verir.
	public static Alert makeWarning(String alertMessage) {
		Alert alert=new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Error");
		alert.setHeaderText(alertMessage);
		return alert;
	}
	
	// "Başarılı" mesajı verir.
	public static Alert makeSucces(String alertMessage) {
		Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Success");
		alert.setHeaderText(alertMessage);
		return alert;
	}
}
