package com.finance.database;

import com.finance.model.Expense;
import com.finance.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class ExpenseDAO implements Transactionable {

    private Connection connection;

    public ExpenseDAO(Connection connection){
        this.connection = connection;
    }

    private static final Logger LOGGER = Logger.getLogger(ExpenseDAO.class.getName());

    @Override
    public void insertTransaction(Transaction transaction, String userEmail) {
        Expense expense = (Expense) transaction;

        String getUserIdQuery = "SELECT userID FROM Users WHERE userEmail = ?";
        String insertQuery = "INSERT INTO transactions(type, category, amount, description, userID) VALUES (?, ?, ?, ?, ?)";

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
                insertStmt.setString(4, String.valueOf(expense.getDescription()));
                insertStmt.setInt(5, userID);

                insertStmt.executeUpdate();
                LOGGER.info("Expense inserted successfully.");
                System.out.println("Inserted expense for: " + userEmail);

            } else {
                LOGGER.info("User not found. Expense not inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.info("Failed to insert expense");
        }


    }

    @Override
    public List<Transaction> getTransactionByUser(String userEmail) throws SQLException {

        List<Transaction> expenses = new ArrayList<>();

        String getQuery = "SELECT t.category, t.amount, t.transaction_timestamp, t.description" +
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
                System.out.println("Fetched " + expenses.size() + " expenses for " + userEmail);

            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return expenses;

    }

}
