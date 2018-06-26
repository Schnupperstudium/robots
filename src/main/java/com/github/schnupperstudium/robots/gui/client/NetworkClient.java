package com.github.schnupperstudium.robots.gui.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.NetworkRobotsClient;
import com.github.schnupperstudium.robots.server.GameInfo;
import com.github.schnupperstudium.robots.server.Level;
import com.github.schnupperstudium.robots.server.NetworkRobotsServer;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NetworkClient extends Application {
	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final long UPDATE_GAMES_TIMER = 10000;
	private static final long UPDATE_LEVELS_TIMER = 60000;
	private static final Random RANDOM = new Random();
	private static final String[] DEFAULT_AI_NAMES = new String[] {
			"CodeSmacker", "SuboptimalPrime", "Spriteless", "RobotRick", "CyberMorty", "MortalWombat",
			"Codebeauty", "Memoryleak", "Phoenix", "ConsciousCortana", "SeriousSiri", "AskingAlexa",
			"FaceMcComputy", "HAL2018", "RobotMcRobotFace", "OhLookAWall", "SchnupperAI", "LudicrousSpeed",
			"CookieBreaker", "HashDefender", "BugStopper", "BitRebel", "DaemonByte", "BitsN'Bytes",
			"JavaMayhem", "EtherBot", "ByteHog", "HugsForBugs", "HippyTrojan", "BabesGotBytes",
			"SystemOfABit", "BootstrapParadox", "Hackover", "BugHunter", "BitsEnd", "FlowBitTheory",
			"PurpleByteRiot", "BinaryBomber", "CtrlAltWin", "GreyCellInterface", "BitArchitecture",
			"InsertCodeHere", "ByteMechanic", "SpacePenguin", "NoEvolution", "ThnksFrThRAM", "Centuries64",
			"ThnksFrThMmrs", "WasSollDas?", "SyntaxTerror", "BillyNoTalent", "RAMSpaghetti", "PlugAndPray",
			"DebugMe", "KernelPanic", "BunteSeiteDerMacht", "LutzFaust", "LeekBot", "LaserRaptor", "\"Laser\"",
			"OststadtAI", "Jebediah", "Valentina", "Trevor", "DootWithTheSickness", "Marvin", "DeepThought",
			"Herobrine", "Kickapoo", "QuestioningAlexandria", "MegaRobotMaster3000", "Dobby", "Smeagol", "Knorke",
			"ButterRobot", "WhatIsMyPurpose?", "GetSchwifty", "SeeSharp", "Awesome-O", "R2D2", "FullSteamSpaceMachine",
			"30SecondsToWall", "DerGrät", "IBims1Robot", "RobyWanKenoby", "LukeSkybotter", "RobaFett"
	};
	
	private static final Logger LOG = LogManager.getLogger();
	
	private final Map<String, Class<? extends AbstractAI>> aiClasses = new HashMap<>();
		
	private final AnimationTimer updateLevelsTimer = new AnimationTimer() {
		private long lastUpdateMs = 0;
		
		@Override
		public void handle(long now) {
			long time = now / 1000000;
			if (time - lastUpdateMs > UPDATE_LEVELS_TIMER) {
				lastUpdateMs = time;
				Platform.runLater(() -> updateLevelsView());
			}
		}		
	};
	private final AnimationTimer updateGamesTimer = new AnimationTimer() {
		private long lastUpdateMs = 0;
		
		@Override
		public void handle(long now) {
			long time = now / 1000000;
			if (time - lastUpdateMs > UPDATE_GAMES_TIMER) {
				lastUpdateMs = time;
				Platform.runLater(() -> updateGameViews());
			}
		}		
	};
	
	/** if a parameter is supplied the server is started with this ui. */
	private NetworkRobotsServer server;
	private NetworkRobotsClient client;
	
	@FXML
	private ListView<Level> levelView;
	@FXML
	private TextField gameName;
	@FXML
	private PasswordField gamePassword;
	
	@FXML
	private ListView<GameInfo> gameView;
	@FXML
	private ComboBox<Class<? extends AbstractAI>> aiDropdown;
	@FXML
	private TextField aiName;
	@FXML
	private PasswordField aiPassword;
	
	@FXML
	private ListView<GameInfo> observeView;
	@FXML
	private PasswordField observePassword;
	
	public NetworkClient() {
		InputStream is = getClass().getResourceAsStream("/ais.txt");
		Scanner scanner = new Scanner(is);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty() || line == null)
				continue;
			
			try {
				Class<?> clazz = Class.forName(line);
				if (clazz != null && AbstractAI.class.isAssignableFrom(clazz)) {
					Class<? extends AbstractAI> aiClass = clazz.asSubclass(AbstractAI.class);
					aiClasses.put(aiClass.getSimpleName(), aiClass);
				}
			} catch (ClassNotFoundException e) {
				LOG.catching(e);
			}
		}
		scanner.close();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client.fxml"));
		loader.setController(this);
		Parent root = loader.load();
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Robots -- NetworkClient");
		primaryStage.show();			
	}

	@FXML
	public void initialize() throws Exception {	
		aiDropdown.setCellFactory(view -> new ListCell<Class<? extends AbstractAI>>() {
			 @Override
		    protected void updateItem(Class<? extends AbstractAI> item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null) {
		            setText(null);
		        } else {
		            setText(item.getSimpleName());
		        }
		    }
		});
		aiDropdown.setButtonCell(new ListCell<Class<? extends AbstractAI>>() {
			@Override
			protected void updateItem(Class<? extends AbstractAI> item, boolean empty) {				
				super.updateItem(item, empty);
				
				if (empty || item == null) {
		            setText(null);
		        } else {
		            setText(item.getSimpleName());
		        }
			}
		});
		for (Class<? extends AbstractAI> aiClass : aiClasses.values())
			aiDropdown.getItems().add(aiClass);
		
		Map<String, String> params = getParameters().getNamed();		
		int port = NetworkRobotsServer.DEFAULT_PORT;
		if (params.get("port") != null)
			port = Integer.parseInt(params.get("port"));

		if (params.get("server") != null && params.get("server").equalsIgnoreCase("true")) {
			server = new NetworkRobotsServer(port);
		}
		
		String host = DEFAULT_HOST;
		if (params.get("host") != null)
			host = params.get("host");
		
		LOG.info("Connecting to {}:{}", host, port);
		client = NetworkRobotsClient.connect(host, port);
		
		levelView.setCellFactory(view -> new ListCell<Level>() {
			 @Override
		    protected void updateItem(Level item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null || item.getName() == null) {
		            setText(null);
		        } else {
		            setText(item.getName() + " (" + item.getMapLocation() + ")");
		        }
		    }
		});
		gameView.setCellFactory(view -> new ListCell<GameInfo>() {
			 @Override
		    protected void updateItem(GameInfo item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null || item.getName() == null) {
		            setText(null);
		        } else {
		        	setText(item.getName() + "(" + item.getLevel().getName() + ") " + (item.hasPassword() ? "YES" : "NO"));
		        }
			 }
	    });		
		observeView.setCellFactory(view -> new ListCell<GameInfo>() {
			 @Override
		    protected void updateItem(GameInfo item, boolean empty) {
		        super.updateItem(item, empty);

		        if (empty || item == null || item.getName() == null) {
		            setText(null);
		        } else {
		            setText(item.getName() + "(" + item.getLevel().getName() + ") " + (item.hasPassword() ? "YES" : "NO"));
		        }
			 }
	    });
		
		updateLevelsView();
		updateGameViews();
		
		updateGamesTimer.start();
		updateLevelsTimer.start();
	}
	
	private void updateLevelsView() {
		levelView.getItems().clear();
		List<Level> levels = client.listLevels();
		for (Level level : levels)
			levelView.getItems().add(level);
	}
	
	private void updateGameViews() {
		final GameInfo selectedGameView = gameView.getSelectionModel().getSelectedItem();
		final GameInfo selectedObserveView = observeView.getSelectionModel().getSelectedItem();
		gameView.getItems().clear();
		observeView.getItems().clear();
		
		List<GameInfo> infos = client.listGames();
		for (GameInfo info : infos) {
			gameView.getItems().add(info);
			observeView.getItems().add(info);
			
			if (selectedGameView != null && selectedGameView.getUUID() == info.getUUID()) {
				gameView.getSelectionModel().select(info);
			}
			if (selectedObserveView != null && selectedObserveView.getUUID() == info.getUUID()) {
				observeView.getSelectionModel().select(info);
			}
		}
	}
	
	@FXML
	public void startGameClicked(ActionEvent event) {		
		Level level = levelView.getSelectionModel().getSelectedItem();
		String name = gameName.getText();
		String password = gamePassword.getText();
		
		if (level == null) {
			showAlert("Bitte ein Level auswählen!");
			return;
		} else if (name == null || name.isEmpty()) {
			showAlert("Bitte einen Namen angeben!");
			return;
		}
		
		client.getServerInterface().startGame(name, level.getName(), password);
		updateGameViews();
	}
	
	@FXML
	public void connectAIClicked(ActionEvent event) {
		GameInfo game = gameView.getSelectionModel().getSelectedItem();
		Class<? extends AbstractAI> aiClass = aiDropdown.getValue();
		String name = aiName.getText();
		String password = aiPassword.getText();
		
		if (game == null) {
			showAlert("Bitte ein Spiel auswählen!");
			return;
		} else if (aiClass == null) {
			showAlert("Bitte eine KI auswählen!");
			return;
		} else if (name == null || name.isEmpty()) {
			name = DEFAULT_AI_NAMES[RANDOM.nextInt(DEFAULT_AI_NAMES.length)];
		}
		
		final AbstractAI ai = client.spawnAI(game.getUUID(), name, password, aiClass);
		if (ai == null) {
			showAlert(name + " konnte dem Spiel nicht beitreten!");
			LOG.error("Failed to spawn {} in game {}:{}", name, game.getName(), game.getUUID());
			return;
		}
		
		ClientAIObserverViewController observer = new ClientAIObserverViewController(client, ai);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/observerView.fxml"));
		loader.setController(observer);
		try {
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.NONE);
			stage.initStyle(StageStyle.DECORATED);
			stage.setTitle("Robots -- ClientAIObserver");
			stage.setOnHidden(e -> observer.shutdown());
			stage.setScene(new Scene(root, 800, 600));
			stage.show();
		} catch (IOException e) {
			LOG.catching(e);
			showAlert("AI konnte nicht geladen werden :(");
			return;
		}
	}
	
	@FXML
	public void observeGameClicked(ActionEvent event) {
		GameInfo selectedGame = observeView.getSelectionModel().getSelectedItem();
		if (selectedGame == null) {
			showAlert("Bitte ein Spiel auswählen!");
			return;
		}		
		
		ClientWorldObserverController observer = new ClientWorldObserverController(client, selectedGame.getUUID());
		
		final boolean spawned = client.spawnObserver(selectedGame.getUUID(), observePassword.getText(), observer);
		if (spawned) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/observerView.fxml"));
			loader.setController(observer);
			try {
				Parent root = loader.load();
				Stage stage = new Stage();
				stage.initModality(Modality.NONE);
				stage.initStyle(StageStyle.DECORATED);
				stage.setTitle("Robots -- ClientGameObserver");
				stage.setOnHidden(e -> observer.shutdown());
				stage.setScene(new Scene(root, 800, 600));
				stage.show();
			} catch (IOException e) {
				LOG.catching(e);
				showAlert("Observer konnte nicht geladen werden :(");
				return;
			}
		} else {
			showAlert("Passwort war falsch!");
		}
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		// close the server if we started one :)
		if (server != null)			
			server.close();
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.show();
	}
}
