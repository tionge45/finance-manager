package com.finance.controller;

import com.finance.database.FinanceDatabase;
import com.finance.database.UserDAO;
import com.finance.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {


    public TextField usernameField;
    public Button signInButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML private Button signUpBotton;
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
        if (!email.matches("^[a-zA-Z0–9._%+-]+@[a-zA-Z0–9.-]+\\.[a-zA-Z]{2,}$")) {
            showAlert("Error", "Invalid email format.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }
        try {
            connection = FinanceDatabase.getConnection();
            userDAO = new UserDAO(connection);
            userDAO.addUser(username, email, password);
            showAlert("Success", "Account created successfully!");

            userDAO = new UserDAO(connection);
            boolean authenticated = userDAO.authenticateUser(email, password);
            if (authenticated){
                goToDashboard();
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void goToDashboard() throws IOException {
        SceneSwitcher.switchScene(signUpBotton, "/fxml/dashboard.fxml");
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
            SceneSwitcher.switchScene((Node) event.getSource(), "/fxml/signin.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
