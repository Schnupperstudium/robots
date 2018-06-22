package com.github.schnupperstudium.robots.gui;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.gui.server.ServerWorldObserverController;
import com.github.schnupperstudium.robots.server.Game;
import com.github.schnupperstudium.robots.server.NetworkRobotsServer;
import com.github.schnupperstudium.robots.server.RobotsServer;
import com.github.schnupperstudium.robots.server.event.ServerListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class ServerGUI extends Application implements ServerListener {
	private static final Logger LOG = LogManager.getLogger();
	
	private RobotsServer server;
	
	public ServerGUI() {
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() throws Exception {
		Map<String, String> params = getParameters().getNamed();
		
		int port = NetworkRobotsServer.DEFAULT_PORT;
		if (params.containsKey("port")) {
			port = Integer.parseInt(params.get("port"));
		}
		
		final String mode = params.get("mode");
		if (mode != null && mode.equalsIgnoreCase("local")) {
			// TODO: implement (we need to connect a client right here).
			throw new UnsupportedOperationException();
		} else {
			server = new NetworkRobotsServer(port);
		}
		
		server.getMasterServerListener().registerListener(this);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ServerGUI.fxml"));
		loader.setController(this);
		Parent root = loader.load();
		
		primaryStage.setOnCloseRequest(event -> {
			try {
				stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		primaryStage.setTitle("ServerGUI");
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		server.close();
	}

	@Override
	public void onGameStart(RobotsServer server, Game game) { 
		Platform.runLater(() -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/observerView.fxml"));
				ObserverViewController controller = new ServerWorldObserverController(game);
				loader.setController(controller);
				Parent root = loader.load();
				
				Stage stage = new Stage();
				stage.initModality(Modality.NONE);
				stage.initStyle(StageStyle.DECORATED);
				stage.setTitle("GameObserver");
				stage.setScene(new Scene(root, 800, 600));
				stage.show();
			} catch (IOException e) {
				LOG.catching(e);
			}
		});
	}

	@Override
	public boolean canGameStart(RobotsServer server, Game game) { return true; }

	@Override
	public void onGameEnd(RobotsServer server, Game game) {
		// TODO Auto-generated method stub
		
	}
}
