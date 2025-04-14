package com.finance.controller;

import com.finance.database.FinanceDatabase;
import com.finance.database.UserDAO;
import com.finance.utils.SceneSwitcher;
import com.finance.controller.DashboardController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SignInController {

    @FXML private Button signInButton;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    Connection connection = FinanceDatabase.getConnection();

    private final UserDAO userDAO = new UserDAO(connection);
    private final DashboardController dashboardController = new DashboardController();
    private final ActionEvent event = new ActionEvent();

    public SignInController() throws SQLException {
    }

    @FXML
    private void handleSignIn() throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password are required.");
            return;
        }

        boolean authenticated = userDAO.authenticateUser(email, password);

        if (authenticated) {
                showAlert("Success", "Login successful!");
                goToDashboard();
            } else {
                showAlert("Error", "Invalid email or password.");
            }

    }

    @FXML
    private void goToSignUp(ActionEvent event) {
        try {
            SceneSwitcher.switchScene((Node) event.getSource(), "/fxml/signup.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToDashboard() throws IOException {
        SceneSwitcher.switchScene(signInButton, "/fxml/dashboard.fxml");
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
