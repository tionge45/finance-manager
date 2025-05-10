package com.finance.database.Calculator;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class IncomeCalculations implements Calculations {
    private final Connection connection;

    public IncomeCalculations(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Map<String, Double> calculateByCategory(String userEmail) {
        String sql = """
            SELECT t.category, SUM(t.amount) AS total
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Income'
            GROUP BY t.category""";

        Map<String, Double> totals = new HashMap<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                totals.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate income by category", e);
        }
        return totals;
    }

    @Override
    public double calculateForCurrentWeek(String userEmail) {
        String sql = """
            SELECT SUM(t.amount)
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Income'
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
            AND t.type = 'Income'
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
            AND t.type = 'Income'
            AND DATE(t.transaction_timestamp) BETWEEN ? AND ?""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate income for date range", e);
        }
    }

    @Override
    public Map<LocalDate, Double> calculateDailyTotals(String userEmail, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT DATE(t.transaction_timestamp) AS day, SUM(t.amount) AS daily_total
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = 'Income'
            AND DATE(t.transaction_timestamp) BETWEEN ? AND ?
            GROUP BY DATE(t.transaction_timestamp)
            ORDER BY day""";

        Map<LocalDate, Double> dailyTotals = new HashMap<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                dailyTotals.put(
                        rs.getDate("day").toLocalDate(),
                        rs.getDouble("daily_total")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate daily income totals", e);
        }
        return dailyTotals;
    }

    private double executeSumQuery(String userEmail, String sql) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute income sum query", e);
        }
    }
}