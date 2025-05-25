package com.finance.database;

import com.finance.model.Budget;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BudgetDAO {

    private final Connection conn;

    public BudgetDAO(Connection conn) { this.conn = conn; }

    private static final Logger LOGGER = Logger.getLogger(BudgetDAO.class.getName());


    public void insertBudget(Budget b) throws SQLException {
        String sql = """
            INSERT INTO budgets
            (userID, name, category, budgetAmount, budgetSpent,
             startDate, endDate, status)
            VALUES (?,?,?,?,?,?,?,?)""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            mapForInsert(ps, b);
            ps.executeUpdate();
        }
    }

    public List<Budget> findByUser(String userEmail) throws SQLException {
        String findQuery = "SELECT b.budgetID, b.userID, b.name, b.category, b.budgetAmount, b.budgetSpent, b.startDate, " +
                "b.endDate, b.status " +
                "FROM budgets b " +
                "JOIN Users u ON b.userID = u.userID " +
                "WHERE u.userEmail = ? " +
                "AND b.status <> 'ARCHIVED' ORDER BY b.endDate ";

        try (PreparedStatement ps = conn.prepareStatement(findQuery)) {
            ps.setString(1, userEmail);
            return extractList(ps.executeQuery());
        }
    }

    public void updateAmountSpent(int budgetId, double delta) throws SQLException {
        String sql = "UPDATE budgets SET budgetSpent = budgetSpent + ? WHERE budgetID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setInt(2, budgetId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(int budgetId, Budget.Status st) throws SQLException {
        String sql = "UPDATE budgets SET status = ? WHERE budgetID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, st.name());
            ps.setInt(2, budgetId);
            ps.executeUpdate();
        }
    }


    private static void mapForInsert(PreparedStatement ps, Budget b) throws SQLException {
        ps.setInt   (1,  b.getUserID());
        System.out.println("Inserting budget for userID: " + b.getUserID());
        ps.setString(2,  b.getName());
        ps.setString(3,  b.getCategory());
        ps.setDouble(4,  b.getBudgetAmount());
        ps.setDouble(5,  b.getBudgetSpent());
        ps.setDate  (6,  Date.valueOf(b.getStartDate()));
        ps.setDate  (7,  Date.valueOf(b.getEndDate()));
        ps.setString(8,  b.getStatus().name());
    }

    private static List<Budget> extractList(ResultSet rs) throws SQLException {
        List<Budget> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Budget(
                    rs.getInt("budgetID"),
                    rs.getInt("userID"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("budgetAmount"),
                    rs.getDouble("budgetSpent"),
                    rs.getDate("startDate").toLocalDate(),
                    rs.getDate("endDate").toLocalDate(),
                    Budget.Status.valueOf(rs.getString("status"))
            ));
        }
        return list;
    }

    public List<Budget> findByCategory(int userId, String category) throws SQLException {
        String sql = """
        SELECT * FROM budgets
        WHERE userID = ? AND category = ?
        ORDER BY endDate DESC""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();
            List<Budget> result = new ArrayList<>();
            while (rs.next()) result.add(extractBudgetFromResultSet(rs));
            return result;
        }
    }

    public List<Budget> findByDateRange(int userId, LocalDate start, LocalDate end) throws SQLException {
        String sql = """
        SELECT * FROM budgets
        WHERE userID = ? AND startDate >= ? AND endDate <= ?
        ORDER BY endDate DESC""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            List<Budget> result = new ArrayList<>();
            while (rs.next()) result.add(extractBudgetFromResultSet(rs));
            return result;
        }
    }

    public List<Budget> findActiveBudgets(int userId) throws SQLException {
        String sql = """
        SELECT * FROM budgets
        WHERE userID = ? AND status = 'ACTIVE' AND endDate >= CURRENT_DATE
        ORDER BY endDate ASC""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            List<Budget> result = new ArrayList<>();
            while (rs.next()) result.add(extractBudgetFromResultSet(rs));
            return result;
        }
    }

    private Budget extractBudgetFromResultSet(ResultSet rs) throws SQLException {
        return new Budget(
                rs.getInt("goalID"),
                rs.getInt("userID"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("budgetAmount"),
                rs.getDouble("budgetSpent"),
                rs.getDate("startDate").toLocalDate(),
                rs.getDate("endDate").toLocalDate(),
                Budget.Status.valueOf(rs.getString("status"))
        );
    }

    public void deleteBudgetByEmail(String userEmail) throws SQLException {
        String findQuery = "DELETE b.budgetID, b.userID, b.name, b.category, b.budgetAmount, b.budgetSpent, b.startDate, " +
                "b.endDate, b.status " +
                "FROM budgets b " +
                "JOIN Users u ON b.userID = u.userID " +
                "WHERE u.userEmail = ? " +
                "AND b.status <> 'ARCHIVED' ORDER BY b.endDate ";

        try (PreparedStatement ps = conn.prepareStatement(findQuery)) {
            ps.setString(1, userEmail);
            ps.executeUpdate();
        }
    }

    public void deleteBudgetById(int budgetId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE budgetID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            ps.executeUpdate();
        }
    }



    public void updateBudgetStatus(int budgetID){}



}
