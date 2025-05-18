/*package com.finance;

import com.finance.database.report.PDFReportGenerator;
import com.finance.database.report.ReportData;
import com.finance.database.report.ReportDataCollector;
import com.finance.database.report.ReportType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ReportMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt for login
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/financedb?useSSL=false",
                "root",
                "root"
        )) {
            // Authenticate user
            if (authenticateUser(conn, email, password)) {
                System.out.println("Invalid login credentials. Exiting.");
                return;
            }

            // Prompt for report type
            System.out.println("\nWhich report do you want to generate?");
            System.out.println("1 - Income Report");
            System.out.println("2 - Expense Report");
            System.out.println("3 - Combined Report");
            System.out.print("Enter choice (1/2/3): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Prompt for report date range
            System.out.print("Enter start date (yyyy-MM-dd): ");
            String startDateStr = scanner.nextLine();

            System.out.print("Enter end date (yyyy-MM-dd): ");
            String endDateStr = scanner.nextLine();

            // Convert strings to LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            // Prepare collector and generator
            ReportDataCollector collector = new ReportDataCollector(conn);
            PDFReportGenerator generator = new PDFReportGenerator(collector);

            // Collect report data
            ReportData data = collector.collectReportData(email, startDate, endDate);

            // Output path for PDF file (you can customize the path/filename)
            String outputPath;
            switch (choice) {
                case 1 -> outputPath = "IncomeReport_" + email + ".pdf";
                case 2 -> outputPath = "ExpenseReport_" + email + ".pdf";
                case 3 -> outputPath = "CombinedReport_" + email + ".pdf";
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }

            // Generate report based on choice
            switch (choice) {
                case 1 -> generator.generateIncomeReport(data, outputPath);
                case 2 -> generator.generateExpenseReport(data, outputPath);
                case 3 -> generator.generateCombinedReport(data, outputPath);
            }

        } catch (SQLException e) {
            System.err.println("Database connection or query failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private static boolean authenticateUser(Connection conn, String email, String password) throws SQLException {
        String query = "SELECT hashedPassword FROM Users WHERE userEmail = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("hashedPassword");
                    return storedPassword.equals(password); // For real apps, use hashed password checking
                }
            }
        }
        return false;
    }
}
*/