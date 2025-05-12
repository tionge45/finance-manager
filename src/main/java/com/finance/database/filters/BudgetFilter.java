package com.finance.database.filters;

import com.finance.model.Budget;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BudgetFilter {
    private final Connection connection;

    public BudgetFilter(Connection connection) {
        this.connection = connection;
    }

    public List<Budget> filterByCategory(String userEmail, String category) {
        List<Budget> filteredBudgets = new ArrayList<>();
        String sql = """
            SELECT b.goalID, b.userID, b.category, b.budgetAmount,
                   b.currentExpenses, b.startDate, b.endDate, b.status
            FROM budgets b
            JOIN Users u ON b.userID = u.userID
            WHERE u.userEmail = ?
            AND b.category = ?
            ORDER BY b.endDate DESC""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setString(2, category);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                filteredBudgets.add(new Budget(
                        rs.getInt("goalID"),
                        rs.getString("userID"),
                        rs.getString("category"),
                        rs.getDouble("budgetAmount"),
                        rs.getDouble("currentExpenses"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter budgets by category", e);
        }
        return filteredBudgets;
    }

    public List<Budget> filterByDateRange(String userEmail, LocalDate startDate, LocalDate endDate) {
        List<Budget> filteredBudgets = new ArrayList<>();
        String sql = """
            SELECT b.goalID, b.userID, b.category, b.budgetAmount, 
                   b.currentExpenses, b.startDate, b.endDate, b.status
            FROM budgets b
            JOIN Users u ON b.userID = u.userID
            WHERE u.userEmail = ?
            AND b.startDate >= ?
            AND b.endDate <= ?
            ORDER BY b.endDate DESC""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                filteredBudgets.add(new Budget(
                        rs.getInt("goalID"),
                        rs.getString("userID"),
                        rs.getString("category"),
                        rs.getDouble("budgetAmount"),
                        rs.getDouble("currentExpenses"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter budgets by date range", e);
        }
        return filteredBudgets;
    }

    public List<Budget> filterActiveBudgets(String userEmail) {
        List<Budget> activeBudgets = new ArrayList<>();
        String sql = """
            SELECT b.goalID, b.userID, b.category, b.budgetAmount, 
                   b.currentExpenses, b.startDate, b.endDate, b.status
            FROM budgets b
            JOIN Users u ON b.userID = u.userID
            WHERE u.userEmail = ?
            AND b.status = 'ACTIVE'
            AND b.endDate >= CURDATE()
            ORDER BY b.endDate ASC""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                activeBudgets.add(new Budget(
                        rs.getInt("goalID"),
                        rs.getString("userID"),
                        rs.getString("category"),
                        rs.getDouble("budgetAmount"),
                        rs.getDouble("currentExpenses"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter active budgets", e);
        }
        return activeBudgets;
    }
}