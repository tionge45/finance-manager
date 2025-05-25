package com.finance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FinanceDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/financedb";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException{
      if(connection == null || connection.isClosed()){
          connection = DriverManager.getConnection(URL, USER, PASSWORD);
      }
      return connection;
    }

    public static void createTables(){

        String usersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "userID INT AUTO_INCREMENT PRIMARY KEY,"
                + "userName VARCHAR(255) NOT NULL UNIQUE,"
                + "hashedPassword VARCHAR(255) NOT NULL,"
                + "userEmail VARCHAR(255) NOT NULL UNIQUE"
                + ")";

        String transactionTable = "CREATE TABLE IF NOT EXISTS transactions ("
                +"transactionID INT AUTO_INCREMENT PRIMARY KEY,"
                +"type ENUM('Income', 'Expense') NOT NULL,"
                +"category VARCHAR(255),"
                +"amount DOUBLE PRECISION,"
                +"description TEXT,"
                +"transaction_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," // For extraction => DATE(transaction_timestamp) or TIME(transaction_timestamp).
                +"userID INT,"
                +"FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
                +")";

        String budgetTable = "CREATE TABLE IF NOT EXISTS budgets ("
                +"budgetID INT AUTO_INCREMENT PRIMARY KEY,"
                +"userID INT NOT NULL,"
                +"name VARCHAR(80) NOT NULL,"
                +"category VARCHAR(255) NOT NULL,"
                +"budgetAmount DOUBLE PRECISION NOT NULL,"
                +"budgetSpent DOUBLE PRECISION NOT NULL DEFAULT 0,"
                +"startDate DATE NOT NULL,"
                +"endDate DATE NOT NULL,"
                +"status ENUM('ACTIVE', 'EXCEEDED', 'ARCHIVED') DEFAULT 'ACTIVE',"
                +"FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
                +")";



        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()){
            statement.execute(usersTable);
            System.out.println("Users table successfully created.");

            statement.execute(transactionTable);
            System.out.println("Transaction table successfully created.");

            statement.execute(budgetTable);
            System.out.println("Budget table successfully created.");

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


}
