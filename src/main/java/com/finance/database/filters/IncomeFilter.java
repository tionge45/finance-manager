package com.finance.database.filters;

import com.finance.database.FinanceDatabase;
import com.finance.model.Income;
import com.finance.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeFilter implements TransactionFilter {
    private final Connection connection;

    public IncomeFilter(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Transaction> filterByCategory(String userEmail, String category, String transactionType) {
        List<Transaction> filteredIncome = new ArrayList<>();
        String sql = """
            SELECT t.amount, t.category, t.description, t.transaction_timestamp
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = ?
            AND t.category = ?
            ORDER BY t.transaction_timestamp DESC""";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, userEmail);
                pstmt.setString(2, transactionType);
                pstmt.setString(3, category);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    filteredIncome.add(new Income(
                            rs.getString("category"),
                            rs.getDouble("amount"),
                            rs.getString("description")
                    ));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to filter income by category", e);
            }
            return filteredIncome;
    }

    @Override
    public List<Transaction> filterByDateRange(String userEmail, Date startDate, Date endDate, String transactionType) {
        List<Transaction> filteredIncome = new ArrayList<>();
        String sql = """
            SELECT t.amount, t.category, t.description, t.transaction_timestamp
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = ?
            AND DATE(t.transaction_timestamp) BETWEEN ? AND ?
            ORDER BY t.transaction_timestamp DESC""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setString(2, transactionType);
            pstmt.setDate(3, startDate);
            pstmt.setDate(4, endDate);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                filteredIncome.add(new Income(
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter income by date range", e);
        }
        return filteredIncome;
    }

    @Override
    public List<Transaction> filterByMonth(String userEmail, int year, int month, String transactionType) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return filterByDateRange(userEmail, Date.valueOf(startDate), Date.valueOf(endDate), transactionType);
    }

    @Override
    public List<Transaction> filterByAmountRange(String userEmail, double minAmount, double maxAmount, String transactionType) {
        List<Transaction> filteredIncome = new ArrayList<>();
        String sql = """
            SELECT t.amount, t.category, t.description, t.transaction_timestamp
            FROM transactions t
            JOIN Users u ON t.userID = u.userID
            WHERE u.userEmail = ?
            AND t.type = ?
            AND t.amount BETWEEN ? AND ?
            ORDER BY t.transaction_timestamp DESC""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setString(2, transactionType);
            pstmt.setDouble(3, minAmount);
            pstmt.setDouble(4, maxAmount);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                filteredIncome.add(new Income(
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter income by amount range", e);
        }
        return filteredIncome;
    }
}
