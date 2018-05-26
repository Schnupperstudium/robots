package com.github.schnupperstudium.robots;

import com.github.schnupperstudium.robots.entity.Robot;
import com.github.schnupperstudium.robots.gui.Texture;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CanvasTest extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group root = new Group();
		Scene s = new Scene(root, 800, 600, Color.BLACK);
		
		final Canvas canvas = new Canvas(800, 600);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		 
		gc.drawImage(Texture.getTexture(new Robot("as")), 0, 0);
		
//		gc.setFill(Color.BLUE);
//		gc.fillRect(0, 0, canvas.getHeight(), canvas.getWidth());
		 
		root.getChildren().add(canvas);
		
		primaryStage.setScene(s);
		primaryStage.show();
	}
}
