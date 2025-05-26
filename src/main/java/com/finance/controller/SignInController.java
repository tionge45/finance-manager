package com.finance.controller;

import com.finance.auth.YandexSignIn;
import com.finance.auth.YandexUser;
import com.finance.database.FinanceDatabase;
import com.finance.database.UserDAO;
import com.finance.database.YandexAuthDAO;
import com.finance.model.User;
import com.finance.service.UserSessionSingleton;
import com.finance.utils.SceneSwitcher;
import com.finance.controller.DashboardController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SignInController {

    public StackPane rightPane;
    @FXML private Button signInButton;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    Connection connection = FinanceDatabase.getConnection();

    private final UserDAO userDAO = new UserDAO(connection);
    private final ActionEvent event = new ActionEvent();

    public SignInController() throws SQLException {
    }


    @FXML
    private void handleSignIn() throws IOException, SQLException {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password are required.");
            return;
        }
        boolean authenticated = userDAO.authenticateUser(email, password);
        if (authenticated) {
            User user = userDAO.getUserByEmail(email);
            showAlert("Success", "Login successful!");
            UserSessionSingleton.setLoggedInUser(user);
            System.out.println("Setting logged-in user to: " + user.getUserEmail());
            goToDashboard();

            } else {
                showAlert("Error", "Invalid email or password.");
            }
    }
    @FXML
    private void handleYandexLogin(ActionEvent event) {
        try {
            YandexSignIn yandexSignIn = new YandexSignIn();
            yandexSignIn.loginWithYandex();
            
            YandexUser yandexUser = YandexAuthDAO.findUserByEmail(yandexSignIn.getAuthenticatedEmail());

            if (yandexUser != null) {
                User user = new User(yandexUser.getId(), yandexUser.getLogin(), yandexUser.getEmail());
                UserSessionSingleton.setLoggedInUser(user);
                showAlert("Success", "Yandex Login successful!");
                goToDashboard();
            } else {
                showAlert("Login Error", "User not found after Yandex login.");
            }
        } catch (Exception e) {
            showAlert("Login Error", "Failed to complete Yandex OAuth flow.");
            e.printStackTrace();
        }
    }


    @FXML
    private void goToSignUp(ActionEvent event) {
        try {
            SceneSwitcher.switchScene((Control) event.getSource(), "/fxml/signup.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToDashboard() throws IOException {
        SceneSwitcher.loadMainApp();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
