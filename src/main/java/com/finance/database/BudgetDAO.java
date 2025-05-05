package com.finance.database;

import com.finance.model.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {
    
    private Connection connection;
    
    public BudgetDAO(Connection connection){
        this.connection = connection;
    }

    public static void insertBudgetGoal(String userLogin, String category, double budgetAmount, Date startDate, Date endDate) {
        String sql = "INSERT INTO budget_goals (userLogin, category, budgetAmount, currentExpenses, startDate, endDate, status) VALUES (?, ?, ?, 0, ?, ?, 'In Progress')";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userLogin);
            pstmt.setString(2, category);
            pstmt.setDouble(3, budgetAmount);
            pstmt.setDate(4, startDate);
            pstmt.setDate(5, endDate);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Budget> getBudgetGoals(String userLogin) {
        List<Budget> goals = new ArrayList<>();
        String sql = "SELECT * FROM budget_goals WHERE userLogin = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userLogin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                goals.add(new Budget(
                        rs.getInt("goalID"),
                        rs.getString("userLogin"),
                        rs.getString("category"),
                        rs.getDouble("budgetAmount"),
                        rs.getDouble("currentExpenses"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

    // Update budget goal
    public static boolean updateBudgetGoal(int goalID, String category, double budgetAmount, Date endDate) {
        String sql = "UPDATE budget_goals SET category = ?, budgetAmount = ?, endDate = ? WHERE goalID = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setDouble(2, budgetAmount);
            pstmt.setDate(3, endDate);
            pstmt.setInt(4, goalID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteBudgetGoal(int goalID) {
        String sql = "DELETE FROM budget_goals WHERE goalID = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, goalID);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addToCurrentExpenses(String userLogin, String category, double amount) {
        String sql = "UPDATE budget_goals SET currentExpenses = currentExpenses + ? WHERE userLogin = ? AND category = ?";

        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setString(2, userLogin);
            pstmt.setString(3, category);

            pstmt.executeUpdate(); // Execute update without returning a boolean
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void checkBudgetStatus(int goalID) {
        String sql = "SELECT  budgetAmount, currentExpenses FROM budget_goals WHERE goalID = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, goalID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double budgetAmount = rs.getDouble("budgetAmount");
                double currentExpenses = rs.getDouble("currentExpenses");
                String status = (currentExpenses > budgetAmount) ? "Exceeded" : "Within Budget";

                String updateSql = "UPDATE budget_goals SET status = ? WHERE goalID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, status);
                    updateStmt.setInt(2, goalID);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
