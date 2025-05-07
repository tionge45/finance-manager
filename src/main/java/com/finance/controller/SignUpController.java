package com.finance.controller;

import com.finance.database.FinanceDatabase;
import com.finance.database.UserDAO;
import com.finance.model.User;
import com.finance.service.UserSessionSingleton;
import com.finance.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable{

    @FXML
    private TextField usernameField, emailField;
    @FXML
    private Button signInButton, signUpButton;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML private Label statusLabel;

    private Connection connection;
    private UserDAO userDAO;


    @FXML
    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }
        if (userDAO.isValidEmail(email)) {
            showAlert("Error", "Invalid email format.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }
        try {
            userDAO.addUser(username, email, password);
            showAlert("Success", "Account created successfully!");

            userDAO = new UserDAO(connection);
            boolean authenticated = userDAO.authenticateUser(email, password);
            if (authenticated){
                User user = userDAO.getUserByEmail(email);
                UserSessionSingleton.setLoggedInUser(user);
                goToDashboard();
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            connection = FinanceDatabase.getConnection();
            userDAO = new UserDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not connect to the database.");
        }
    }

    @FXML
    private void goToSignIn(ActionEvent event) {
        try {
            SceneSwitcher.switchScene((Control) event.getSource(), "/fxml/signin.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
