package com.finance.database;

import com.finance.model.Income;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class IncomeDAO {

    private Connection connection;

    public IncomeDAO(Connection connection){
        this.connection = connection;
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
