package com.finance.database.filters;

import com.finance.model.Expense;
import com.finance.model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFilter implements TransactionFilter {
    private final Connection connection;

    public ExpenseFilter(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Transaction> filterByCategory(String userEmail, String category, String transactionType) {
        List<Transaction> filteredExpenses = new ArrayList<>();
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
            pstmt.setString(2, transactionType); // Hardcoded as we're filtering expenses
            pstmt.setString(3, category);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                filteredExpenses.add(mapRowToExpense(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter expenses by category", e);
        }
        return filteredExpenses;
    }

    @Override
    public List<Transaction> filterByDateRange(String userEmail, LocalDate startDate, LocalDate endDate, String transactionType) {
        List<Transaction> filteredExpenses = new ArrayList<>();
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
            pstmt.setString(2, transactionType); // Hardcoded as we're filtering expenses
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4,  Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                filteredExpenses.add(mapRowToExpense(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter expenses by date range", e);
        }
        return filteredExpenses;
    }

    @Override
    public List<Transaction> filterByMonth(String userEmail, int year, int month, String transactionType) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return filterByDateRange(userEmail, startDate, endDate, transactionType);
    }

    @Override
    public List<Transaction> filterByAmountRange(String userEmail, double minAmount, double maxAmount, String transactionType) {
        List<Transaction> filteredExpenses = new ArrayList<>();
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
                filteredExpenses.add(mapRowToExpense(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to filter expenses by amount range", e);
        }
        return filteredExpenses;
    }

    private Expense mapRowToExpense(ResultSet rs) throws SQLException {
        Expense e = new Expense(
                rs.getString("category"),
                rs.getDouble("amount"),
                rs.getString("description")
        );
        Timestamp ts = rs.getTimestamp("transaction_timestamp");
        if (ts != null) { e.setTimestamp(ts.toLocalDateTime()); }
        return e;
    }
}