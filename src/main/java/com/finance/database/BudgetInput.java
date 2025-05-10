package com.finance.database;

import com.finance.database.FinanceDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class BudgetInput {

    public static void addBudgetGoal() {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = FinanceDatabase.getConnection()) {
            System.out.println("=== Add New Budget Goal ===");

            System.out.print("Enter your user login (userName): ");
            String userLogin = scanner.nextLine();

            System.out.print("Enter category (e.g., Food, Transport): ");
            String category = scanner.nextLine();

            System.out.print("Enter budget amount: ");
            double budgetAmount = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter start date (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter end date (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            String sql = "INSERT INTO budget_goals (userLogin, category, budgetAmount, startDate, endDate) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userLogin);
                stmt.setString(2, category);
                stmt.setDouble(3, budgetAmount);
                stmt.setDate(4, java.sql.Date.valueOf(startDate));
                stmt.setDate(5, java.sql.Date.valueOf(endDate));

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("✅ Budget goal added successfully!");
                } else {
                    System.out.println("❌ Failed to add budget goal.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Input error: " + e.getMessage());
        }
    }
}
