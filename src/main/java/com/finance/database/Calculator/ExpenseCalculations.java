package com.finance.database.Calculator;

import com.finance.database.FinanceDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ExpenseCalculations implements Calculations {
    private final Connection connection;

    public ExpenseCalculations(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Map<String, Double> calculateByCategory(String userEmail) {
        Map<String, Double> categoryTotals = new HashMap<>();
        String sql = """
            SELECT t.category, SUM(t.amount) AS total
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Expense'
            GROUP BY t.category""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                categoryTotals.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate expenses by category", e);
        }
        return categoryTotals;
    }

    @Override
    public double calculateForCurrentWeek(String userEmail) {
        String sql = """
            SELECT SUM(t.amount)
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Expense'
            AND YEARWEEK(t.transaction_timestamp, 1) = YEARWEEK(CURDATE(), 1)""";

        return executeSumQuery(userEmail, sql);
    }

    @Override
    public double calculateForCurrentMonth(String userEmail) {
        String sql = """
            SELECT SUM(t.amount)
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Expense'
            AND MONTH(t.transaction_timestamp) = MONTH(CURDATE())
            AND YEAR(t.transaction_timestamp) = YEAR(CURDATE())""";

        return executeSumQuery(userEmail, sql);
    }

    @Override
    public double calculateForDateRange(String userEmail, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT SUM(t.amount)
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Expense'
            AND DATE(t.transaction_timestamp) BETWEEN ? AND ?""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate expenses for date range", e);
        }
    }

    @Override
    public Map<LocalDate, Double> calculateDailyTotals(String userEmail, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> dailyTotals = new HashMap<>();
        String sql = """
            SELECT DATE(t.transaction_timestamp) AS day, SUM(t.amount) AS total
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Expense'
            AND DATE(t.transaction_timestamp) BETWEEN ? AND ?
            GROUP BY DATE(t.transaction_timestamp)
            ORDER BY day""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                dailyTotals.put(rs.getDate("day").toLocalDate(), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate daily expense totals", e);
        }
        return dailyTotals;
    }

    private double executeSumQuery(String userEmail, String sql) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute expense sum query", e);
        }
    }
}