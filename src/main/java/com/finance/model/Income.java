//Доход
package com.finance.model;

import com.finance.database.FinanceDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Income {
    private int incomeID;
    private String userLogin;
    private double salary;
    private double investment;
    private double business;

    public Income(int _incomeID, String _userLogin, double _salary, double _investment, double _business) {
        this.incomeID = _incomeID;
        this.userLogin = _userLogin;
        this.salary = _salary;
        this.investment = _investment;
        this.business = _business;
    }

    public int getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(int incomeID) {
        this.incomeID = incomeID;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getInvestment() {
        return investment;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    public double getBusiness() {
        return business;
    }

    public void setBusiness(double business) {
        this.business = business;
    }

    public static void insertIncome(String userLogin, double salary, double investment, double business) {
        String sql = "INSERT INTO Income(userLogin, salary, investment, business) VALUES (?, ?, ?, ?)";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userLogin);
            pstmt.setDouble(2, salary);
            pstmt.setDouble(3, investment);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Income> getIncome(String userLogin) {
        List<Income> incomeList = new ArrayList<>();
        String sql = "SELECT * FROM Income WHERE userLogin = ?";

        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userLogin);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                incomeList.add(new Income(
                        rs.getInt("incomeID"),
                        rs.getString("userLogin"),
                        rs.getDouble("salary"),
                        rs.getDouble("investment"),
                        rs.getDouble("business")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomeList;
    }
    }