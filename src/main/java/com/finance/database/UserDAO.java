package com.finance.database;

import com.finance.model.User;
import com.finance.utils.PasswordUtils;

import java.sql.*;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean isValidEmail(String email) {
        return !email.matches("^[a-zA-Z0–9._%+-]+@[a-zA-Z0–9.-]+\\.[a-zA-Z]{2,}$");
    }


    //adds new User to Users table
    public User addUser(String userName, String userEmail, String rawPassword) throws SQLException {

        if (userName == null || userName.isEmpty() ||
                rawPassword == null || rawPassword.isEmpty() ||
                userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException
                    ("All fields must be non-null and non-empty.");
        }
        if (isValidEmail(userEmail)){
            throw new IllegalArgumentException("Invalid email format");
        }

        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        String query = "INSERT INTO Users(userName, hashedPassword, userEmail) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userName);
            ps.setString(2, hashedPassword);
            ps.setString(3, userEmail);
            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    int userID = rs.getInt(1);
                    return new User(userID, userName, userEmail, hashedPassword);
                } else {
                    throw new SQLException("Failed to retrieve generated userID");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new IllegalArgumentException("Email address is already " +
                        "associated with another account");
            } else {
                throw new SQLException("Database error: " + e.getMessage(), e);
            }
        }
    }

    //searches for User by Email
    public User getUserByEmail(String userEmail) throws SQLException {
        String query = "SELECT userID, userName, hashedPassword, userEmail FROM Users WHERE userEmail = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("userID");
                String userName = rs.getString("userName");
                String hashedPassword = rs.getString("hashedPassword");
                return new User(userID, userName, userEmail, hashedPassword);

            }
        }
        return null;
    }

    public boolean authenticateUser(String userEmail, String rawPassword) {
        String query = "SELECT hashedPassword FROM Users WHERE userEmail = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return PasswordUtils.checkPassword(rawPassword, rs.getString("hashedPassword"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public void updateUserPassword(String userEmail, String oldRawPassword, String newRawPassword) {
        String query = "SELECT hashedPassword FROM Users WHERE userEmail = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (!PasswordUtils.checkPassword(oldRawPassword, rs.getString("hashedPassword"))) {
                    System.out.println("Incorrect old password.");
                    return;
                }

                String updateQuery = "UPDATE Users SET hashedPassword = ? WHERE userEmail = ?";
                try (PreparedStatement updatePS = connection.prepareStatement(updateQuery)) {
                    updatePS.setString(1, PasswordUtils.hashPassword(newRawPassword));
                    updatePS.setString(2, userEmail);
                    int rowsAffected = updatePS.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("User password updated: " + userEmail);
                    } else {
                        System.out.println("Failed to update password.");
                    }
                }
            } else {
                System.out.println("User does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateUserName(String userEmail, String newUserName) {
        String updateQuery = "UPDATE Users SET userName = ? WHERE userEmail = ?";

        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, newUserName);
            ps.setString(2, userEmail);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Username updated: " + userEmail);
            } else {
                System.out.println("Failed to update username. User might not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteUser(String userEmail, String plainPassword) {
        String selectQuery = "SELECT hashedPassword FROM Users WHERE userEmail = ?";
        String deleteQuery = "DELETE FROM Users WHERE userEmail = ?";

        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
            ps.setString(1, userEmail);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("hashedPassword");

                if (!PasswordUtils.checkPassword(plainPassword, storedHashedPassword)) {
                    System.out.println("Incorrect password. Cannot delete user.");
                    return;
                }

                try (PreparedStatement deletePS = connection.prepareStatement(deleteQuery)) {
                    deletePS.setString(1, userEmail);
                    int rows = deletePS.executeUpdate();

                    if (rows > 0) {
                        System.out.println("User successfully deleted.");
                    } else {
                        System.out.println("Failed to delete user.");
                    }
                }
            } else {
                System.out.println("User does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}