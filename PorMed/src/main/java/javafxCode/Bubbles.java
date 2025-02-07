package javafxCode;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration; 

public class Bubbles {
	final static int xAxis[]= {150,900,650,360,1200};
	final static int yAxis[]= {70,150,340,480,600};
	
	public static List<Circle> getBubbles() {
		List<Circle> bubbles=new ArrayList<>();
		for(int i=0;i<5;i++) {
			Circle cir=new Circle();
			cir.setLayoutX(xAxis[i]);
			cir.setLayoutY(yAxis[i]);
			cir.setRadius(80);
			cir.setFill(Color.DEEPSKYBLUE);
			cir.setStroke(Color.MEDIUMBLUE);
			cir.setStrokeWidth(2);
			cir.setOpacity(0.5);
			
			FadeTransition fade=makeFade();
			fade.setNode(cir);
			fade.play();
			
			TranslateTransition move=makeTranslate();
			move.setToY(50);
			move.setNode(cir);
			move.play();
			
			bubbles.add(cir);
		}
		return bubbles;
	}
	
	public static FadeTransition makeFade() {
		FadeTransition fade=new FadeTransition(Duration.seconds(8));
		fade.setFromValue(0.5); 
		fade.setToValue(0.0); 
		fade.setCycleCount(Animation.INDEFINITE);
		fade.setAutoReverse(false);
		return fade;
	}
	
	public static TranslateTransition makeTranslate() {
		TranslateTransition move=new TranslateTransition(Duration.seconds(8));
		move.setCycleCount(Animation.INDEFINITE);
		move.setAutoReverse(false);
		return move;
	}
}
