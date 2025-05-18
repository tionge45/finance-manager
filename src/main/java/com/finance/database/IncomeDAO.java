package com.finance.database;

import com.finance.model.Income;
import com.finance.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;




public class IncomeDAO implements Transactionable {

    private final Connection connection;

    public IncomeDAO(Connection connection){
        this.connection = connection;
    }

    private static final Logger LOGGER = Logger.getLogger(IncomeDAO.class.getName());


    @Override
    public  void insertTransaction(Transaction transaction, String userEmail) {
        Income income = (Income) transaction;

        String getUserIdQuery = "SELECT userID FROM Users WHERE userEmail = ?";
        String insertQuery = "INSERT INTO transactions(type, category, amount, description, userID) VALUES (?, ?, ?, ?, ?)";

        try(
            PreparedStatement userIdStmt = connection.prepareStatement(getUserIdQuery);
            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        ){
            userIdStmt.setString(1, userEmail);
            ResultSet rs = userIdStmt.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");

                insertStmt.setString(1, "Income");
                insertStmt.setString(2, income.getCategory());
                insertStmt.setDouble(3, income.getAmount());
                insertStmt.setString(4, income.getDescription());
                insertStmt.setInt(5, userID);

                insertStmt.executeUpdate();
                LOGGER.info("Income inserted successfully.");
                System.out.println("Inserted income for: " + userEmail);


            } else {
                System.out.println("User not found. Expense not inserted.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Transaction> getTransactionByUser(String userEmail) throws SQLException {

        List<Transaction> income = new ArrayList<>();

        String getQuery = "SELECT t.category, t.amount, t.transaction_timestamp, t.description " +
                " FROM transactions t " +
                "JOIN Users u ON t.userID = u.userID " +
                "WHERE u.userEmail = ? " +
                "AND t.type = 'Income' " +
                "ORDER BY t.transaction_timestamp DESC";

        try(PreparedStatement ps = connection.prepareStatement(getQuery)){
            ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){

                String category = rs.getString("category");
                double amount = rs.getDouble( "amount");
                String description = rs.getString("description");
                Timestamp timestamp = rs.getTimestamp("transaction_timestamp");

                Income userIncome = new Income(category, amount, description);
                if (timestamp != null) {
                    userIncome.setTimestamp(timestamp.toLocalDateTime());
                }

                income.add(userIncome);
                System.out.println("Fetched " + income.size() + " incomes for " + userEmail);

            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return income;
    }


}
