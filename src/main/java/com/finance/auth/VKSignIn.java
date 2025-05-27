/*
package com.finance.auth;

import javafx.application.Application; // 53620705
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VKSignIn extends Application {
    private static final String CLIENT_ID = "53620884";
    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        String authUrl = String.format(
                "https://oauth.vk.com/authorize?client_id=%s&display=page&redirect_uri=%s&scope=email&response_type=token&v=5.131",
                CLIENT_ID, REDIRECT_URI
        );

        webEngine.load(authUrl);

        // Add listener to detect redirect and extract access_token
        webEngine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            if (newLocation.contains("access_token=")) {
                String token = extractTokenFromUrl(newLocation);
                if (token != null) {
                    System.out.println("Access Token: " + token);
                    fetchUserInfo(token);
                    stage.close(); // Close after login
                }
            }
        });

        stage.setScene(new Scene(webView, 800, 600));
        stage.setTitle("VK Sign-In");
        stage.show();
    }


    public String extractTokenFromUrl(String url) {
        String fragment = url.split("#")[1];
        String[] params = fragment.split("&");
        for (String param : params) {
            if (param.startsWith("access_token=")) {
                return param.split("=")[1];
            }
        }
        return null;
    }

    public void fetchUserInfo(String token) {
        try {
            URL url = new URL("https://api.vk.com/method/users.get?fields=first_name,last_name&access_token=" + token + "&v=5.131");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream response = conn.getInputStream();
            String json = new String(response.readAllBytes(), StandardCharsets.UTF_8);

            JSONObject responseObject = new JSONObject(json).getJSONArray("response").getJSONObject(0);
            String firstName = responseObject.getString("first_name");
            String lastName = responseObject.getString("last_name");

            saveToDatabase(firstName, lastName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToDatabase(String firstName, String lastName) {
        String url = "jdbc:mysql://localhost:3306/financedb";
        String user = "financedb";
        String password = "financedb";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "first_name VARCHAR(255), " +
                    "last_name VARCHAR(255))";
            conn.createStatement().execute(createTableSQL);

            String insertSQL = "INSERT INTO users (first_name, last_name) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.executeUpdate();
            }

            System.out.println("User saved: " + firstName + " " + lastName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}

*/