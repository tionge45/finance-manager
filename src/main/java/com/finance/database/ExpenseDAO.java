package com.finance.database;

import com.finance.model.Budget;
import com.finance.model.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ExpenseDAO {

    private Connection connection;

    public ExpenseDAO(Connection connection){
        this.connection = connection;
    }

    public static void insertExpense(String userLogin, String category, double amount, String description, Date expenseDate, Time expenseTime ){
        String sql = "INSERT INTO expenseTable(userLogin, category, amount, description, expenseDate, expenseTime) VALUES (?,?,?,?,?,?)";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, userLogin);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            pstmt.setDate(5, expenseDate);
            pstmt.setTime(6, expenseTime);

            pstmt.executeUpdate();

            Budget.addToCurrentExpenses(userLogin, category, amount);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static List<Expense> getExpenses(String userLogin) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenseTable WHERE userLogin = ?";

        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userLogin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getInt("expenseID"),
                        rs.getString("userLogin"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getDate("expenseDate"),
                        rs.getTime("expenseTime")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }
}
