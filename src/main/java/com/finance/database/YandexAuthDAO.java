package com.finance.database;

import com.finance.auth.YandexUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class YandexAuthDAO {

    public static YandexUser findUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM Users WHERE userEmail = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("userId");
                String login = rs.getString("userName");
                String userEmail = rs.getString("userEmail");
                return new YandexUser(id, login, userEmail);
            }
        }
        return null;
    }

    public static void saveYandexUser(YandexUser user) throws SQLException {
        YandexUser existingUser = findUserByEmail(user.getEmail());
        if (existingUser != null) {
            System.out.println("User already exists. Login success.");
            return;
        }

        String insert = "INSERT INTO Users (userName, hashedPassword, userEmail) VALUES (?, ?, ?)";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {

            stmt.setString(1, user.getLogin());
            stmt.setString(2, "oauth_yandex");
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();
            System.out.println("New Yandex user inserted into database.");
        }
    }
}
