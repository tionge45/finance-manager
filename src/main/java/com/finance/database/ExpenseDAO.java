package com.finance.database;

import com.finance.model.Expense;
import com.finance.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ExpenseDAO implements Transactionable {

    private Connection connection;

    public ExpenseDAO(Connection connection){
        this.connection = connection;
    }


    @Override
    public void insertTransaction(Transaction transaction, String userEmail) {
        Expense expense = (Expense) transaction;

        String getUserIdQuery = "SELECT userID FROM Users WHERE userEmail = ?";
        String insertQuery = "INSERT INTO transactions(type, category, amount, description, userID) VALUES (?, ?, ?, ?)";

        try(
            PreparedStatement userIdStmt = connection.prepareStatement(getUserIdQuery);
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery)
        ){
            userIdStmt.setString(1, userEmail);
            ResultSet rs = userIdStmt.executeQuery();
            if(rs.next()){
                int userID = rs.getInt("userID");


                insertStmt.setString(1, "Expense");
                insertStmt.setString(2, expense.getCategory());
                insertStmt.setDouble(3,expense.getAmount());
                insertStmt.setString(4, expense.getDescription());
                insertStmt.setInt(5, userID);

                insertStmt.executeUpdate();
                System.out.println("Expense inserted successfully.");

            } else {
                System.out.println("User not found. Expense not inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to insert expense");
        }


    }

    @Override
    public List<Transaction> getTransactionByUser(String userEmail) throws SQLException {

        List<Transaction> expenses = new ArrayList<>();

        String getQuery = "SELECT t.category, t.amount, t.transaction_timestamp" +
                " FROM transactions t " +
                "JOIN Users u ON t.userID = u.userID " +
                "WHERE u.userEmail = ? " +
                "AND t.type = 'Expense' " +
                "ORDER BY t.transaction_timestamp DESC";

        try(PreparedStatement ps = connection.prepareStatement(getQuery)){
            ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){

                String category = rs.getString("category");
                double amount = rs.getDouble( "amount");
                String description = rs.getString("description");
                Timestamp timestamp = rs.getTimestamp("transaction_timestamp");

                Expense expense = new Expense(category, amount, description);
                if (timestamp != null) {
                    expense.setTimestamp(timestamp.toLocalDateTime());
                }

                expenses.add(expense);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return expenses;

    }

}
