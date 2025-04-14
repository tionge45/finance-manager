package com.finance.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchScene(Node sourceNode, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
