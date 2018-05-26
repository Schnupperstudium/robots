package com.github.schnupperstudium.robots.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLUtils {

	private static final String FXML_FILENAME_FORMAT = "/fxml/%s.fxml";

	public static <T extends Node> T loadFxRoot(final T root) throws IOException {
		return loadFxRoot(root, root.getClass());
	}

	public static <T extends Node> T loadFxRoot(final T root, final Class<? extends Node> rootClass)
			throws IOException {
		return loadFxRoot(root, getFxmlFilename(rootClass));
	}

	public static <T extends Node> T loadFxRoot(final T root, final String fxml)
			throws IOException {
		FXMLLoader loader = new FXMLLoader(root.getClass().getResource(fxml), getResourceBundle());
		loader.setRoot(root);
		loader.setController(root);
		return loader.load();
	}

	public static <T extends Node> T loadFxController(final Object controller) throws IOException {
		return loadFxController(controller, controller.getClass());
	}

	public static <T extends Node> T loadFxController(final Object controller,
			final Class<?> controllerClass) throws IOException {
		return loadFxController(controller, getFxmlFilename(controllerClass));
	}

	public static <T extends Node> T loadFxController(final Object controller, final String fxml)
			throws IOException {
		FXMLLoader loader =
				new FXMLLoader(controller.getClass().getResource(fxml), getResourceBundle());
		loader.setController(controller);
		return loader.load();
	}

	private static String getFxmlFilename(final Class<?> clazz) {
		return String.format(FXML_FILENAME_FORMAT, clazz.getSimpleName());
	}

	private static ResourceBundle getResourceBundle() {
		return ResourceBundleManager.get().getResourceBundle();
	}

	public static <S, T> void installXTooltips(final List<Series<S, T>> chartData) {
		for (Series<S, T> series : chartData) {
			for (Data<S, T> data : series.getData()) {
				Tooltip.install(data.getNode(), new Tooltip(data.getXValue().toString()));
			}
		}
	}

	public static <S, T> void installYTooltips(final List<Series<S, T>> chartData) {
		for (Series<S, T> series : chartData) {
			for (Data<S, T> data : series.getData()) {
				Tooltip.install(data.getNode(), new Tooltip(data.getYValue().toString()));
			}
		}
	}

	private FXMLUtils() {
		// utility class
	}

	private static final class ResourceBundleManager {

		private static ResourceBundleManager INSTANCE = null;

		public static ResourceBundleManager get() {
			if (INSTANCE == null) {
				synchronized (ResourceBundleManager.class) {
					if (INSTANCE == null) {
						INSTANCE = new ResourceBundleManager();
					}
				}
			}
			return INSTANCE;
		}

		private final ResourceBundle resourceBundle;

		public ResourceBundleManager() {
			if (Thread.currentThread().getContextClassLoader().getResource("bundles") != null) {
				resourceBundle = ResourceBundle.getBundle("bundles.gui");
			} else {
				resourceBundle = null;
			}
		}

		public ResourceBundle getResourceBundle() {
			return resourceBundle;
		}
	}
}
