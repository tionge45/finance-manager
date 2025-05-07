package com.finance.utils;

import com.finance.controller.DashboardController;
import com.finance.controller.SideBarController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneSwitcher {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(Control control, String fxmlPath) throws IOException {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneSwitcher.class.getResource(fxmlPath)));
            Stage stage = (control != null) ?
                    (Stage) control.getScene().getWindow() :
                    primaryStage;

            Scene currentScene = (control != null) ?
                    control.getScene() :
                    (stage.getScene() != null ? stage.getScene() : new Scene(root));

            Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            stage.setScene(newScene);
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
            throw new RuntimeException("Scene switch failed", e);
        }
    }

    public static void loadMainApp() throws IOException {

        //Sidebar has its own contentPane and handles loading the dashboard
        FXMLLoader sidebarLoader = new FXMLLoader(SceneSwitcher.class.getResource("/fxml/sidebar.fxml"));
        Parent sidebarRoot = sidebarLoader.load();

        primaryStage.setScene(new Scene(sidebarRoot));
    }


}
