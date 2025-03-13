package javafxCode;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration; 

public class Bubbles{
	public int xAxis;
	public int yAxis;
	
	public ImageView getBubble() {
		Image bubble=new Image("/ui/Bubble.png");
		xAxis=(int) (50+Math.random()*1330);
		yAxis=(int) (20+Math.random()*630);
		ImageView bubbleImage=new ImageView(bubble);
		bubbleImage.setLayoutX(xAxis);
		bubbleImage.setLayoutY(yAxis);
		bubbleImage.maxWidth(130);
		bubbleImage.maxHeight(130);
		bubbleImage.setFitWidth(130);
		bubbleImage.setFitHeight(130);
		
		FadeTransition fade=makeFade();
		fade.setNode(bubbleImage);
		fade.play();
		
		TranslateTransition move=makeTranslate();
		move.setToY(50);
		move.setNode(bubbleImage);
		move.play();
		
		return bubbleImage;
	}
	
	public FadeTransition makeFade() {
		FadeTransition fade=new FadeTransition(Duration.seconds(4));
		fade.setFromValue(0); 
		fade.setToValue(1); 
		fade.setCycleCount(2);
		fade.setAutoReverse(true);
		return fade;
	}
	
	public TranslateTransition makeTranslate() {
		TranslateTransition move=new TranslateTransition(Duration.seconds(8));
		move.setCycleCount(1);
		move.setAutoReverse(false);
		return move;
	}
}
