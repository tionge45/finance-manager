package com.finance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FinanceDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/financedb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException{
      if(connection == null || connection.isClosed()){
          connection = DriverManager.getConnection(URL, USER, PASSWORD);
      }
      return connection;
    }

    public static void createTables(){
        String usersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "userID VARCHAR(50) PRIMARY KEY," // could be a phone number
                + "userName VARCHAR(255) NOT NULL,"
                + "hashedPassword VARCHAR(255) NOT NULL,"
                + "userEmail VARCHAR(255) NOT NULL UNIQUE"
                + ")";

        String transactionTable = "CREATE TABLE IF NOT EXISTS transactions ("
                +"transactionID INT AUTO_INCREMENT PRIMARY KEY,"
                +"transactionName VARCHAR(255) NOT NULL,"
                +"amount DECIMAL (20,2),"
                +"description TEXT,"
                +"transactionDate DATE NOT NULL,"
                +"transactionTime TIME NOT NULL,"
                +"userID VARCHAR(50),"
                +"FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
                +")";

        String incomeTable = "CREATE TABLE IF NOT EXISTS income("
                +"incomeID INT AUTO_INCREMENT PRIMARY KEY,"
                +"business DECIMAL(10,2) DEFAULT 0.00,"
                +"salary DECIMAL(10,2) DEFAULT 0.00,"
                +"userID VARCHAR(50),"
                +"FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
                + ")";

        String expenseTable = "CREATE TABLE IF NOT EXISTS expense ("
                +"expenseID INT AUTO_INCREMENT PRIMARY KEY,"
                +"groceries DECIMAL(10,2) DEFAULT 0.00,"
                +"utilities DECIMAL(10,2) DEFAULT 0.00,"
                +"debt DECIMAL(10,2) DEFAULT 0.00,"
                +"health DECIMAL(10,2) DEFAULT 0.00,"
                +"transportation DECIMAL(10,2) DEFAULT 0.00,"
                +"userID VARCHAR(50),"
                +"FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE"
                + ")";

        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()){
            statement.execute(usersTable);
            System.out.println("Users table successfully created.");

            statement.execute(transactionTable);
            System.out.println("Transaction table successfully created.");

            statement.execute(incomeTable);
            System.out.println("Income table successfully created.");

            statement.execute(expenseTable);
            System.out.println("Expense table successfully created.");

        } catch (SQLException e){
            e.printStackTrace();;
        }
    }


}
