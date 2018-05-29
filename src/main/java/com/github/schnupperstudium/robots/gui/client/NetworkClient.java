package com.github.schnupperstudium.robots.gui.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.schnupperstudium.robots.NetworkRobotAI;
import com.github.schnupperstudium.robots.client.AbstractAI;
import com.github.schnupperstudium.robots.client.NetworkRobotsClient;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class NetworkClient extends Application {
	private static final Logger LOG = LogManager.getLogger();
	
	private final Map<String, Class<? extends AbstractAI>> aiClasses = new HashMap<>();
	
	@FXML
	private TextField ipField;
	@FXML
	private TextField portField;
	@FXML
	private PasswordField passwordField;
	@FXML	
	private TextField aiNameField;
	@FXML
	private ComboBox<String> aiDropdown;

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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/networkClient.fxml"));
		loader.setController(this);
		Parent root = loader.load();
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Robots -- NetworkClient");
		primaryStage.show();
	}

	@FXML
	public void initialize() {
		for (Class<? extends AbstractAI> aiClass : aiClasses.values())
			aiDropdown.getItems().add(aiClass.getSimpleName());
	}
	
	@FXML
	public void onConnectClick(ActionEvent event) {		
		String host = ipField.getText();
		String port = portField.getText();
		String password = passwordField.getText();
		String aiName = aiNameField.getText();
		String selectedAI = aiDropdown.getSelectionModel().getSelectedItem();
		
		if (host == null || host.isEmpty()) {
			showAlert("host is empty or null");
			return;
		} else if (port == null || port.isEmpty()) {
			showAlert("port is empty or null");
			return;
		} else if (aiName == null || aiName.isEmpty()) {
			showAlert("AiName is empty or null");
			return;
		} else if (selectedAI == null || selectedAI.isEmpty()) {
			showAlert("selectedAI is empty or null");
			return;
		}
		
		try {
			NetworkRobotsClient.connect(host, Integer.parseInt(port));
		} catch (NumberFormatException | IOException e) {
			showAlert(e.getMessage());
		}
		System.out.println("CLICK");
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.show();
	}
}
