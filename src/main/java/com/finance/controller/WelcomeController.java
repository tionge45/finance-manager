package com.finance.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class WelcomeController {

    public VBox leftPane;
    public StackPane rightPane;
    public ImageView logoImage;
    @FXML private Button signUpButton;
    @FXML private Button signInButton;


    @FXML
    public void initialize() {
        signUpButton.setOnAction(event -> loadPage("/fxml/signup.fxml"));
        signInButton.setOnAction(event -> loadPage("/fxml/signin.fxml"));
    }

    private void loadPage(String fxmlFile) {

        try {
            System.out.println("Loading FXML: " + fxmlFile);
            URL resource = getClass().getResource(fxmlFile);
            System.out.println("Resource URL: " + resource);
            if (resource == null) {
                throw new IllegalStateException("FXML file not found: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
