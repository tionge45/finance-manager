package com.finance.database.Calculator;

import com.finance.database.FinanceDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BudgetCalculations {

    public static double getBudgetAmount(int goalID) {
        String sql = "SELECT budgetAmount FROM budget_goals WHERE goalID = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("budgetAmount");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private static String getUserLogin(int goalID) {
        String sql = "SELECT userLogin FROM budget_goals WHERE goalID = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("userLogin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getCategory(int goalID) {
        String sql = "SELECT category FROM budget_goals WHERE goalID = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("category");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double getCurrentExpenses(String userLogin, String category) {
        String sql = "SELECT SUM(amount) AS total FROM transactions " +
                "WHERE type = 'Expense' AND userID = " +
                "(SELECT userID FROM Users WHERE userName = ?) AND category = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userLogin);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static double getCurrentExpenses(int goalID) {
        String userLogin = getUserLogin(goalID);
        String category = getCategory(goalID);
        if (userLogin != null && category != null) {
            return getCurrentExpenses(userLogin, category);
        }
        return 0.0;
    }

    public static double getPercentageSpent(int goalID) {
        double budget = getBudgetAmount(goalID);
        double spent = getCurrentExpenses(goalID);
        return budget == 0 ? 0 : (spent / budget) * 100;
    }

    public static long getDaysRemaining(int goalID) {
        String sql = "SELECT endDate FROM budget_goals WHERE goalID = ?";
        try (Connection conn = FinanceDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Date endDate = rs.getDate("endDate");
                return ChronoUnit.DAYS.between(LocalDate.now(), endDate.toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isOverBudget(int goalID) {
        return getCurrentExpenses(goalID) > getBudgetAmount(goalID);
    }

    public static double getRemainingBudget(int goalID) {
        return getBudgetAmount(goalID) - getCurrentExpenses(goalID);
    }

    public static double getAverageSpendingAllowed(int goalID) {
        double remainingBudget = getRemainingBudget(goalID);
        long daysLeft = getDaysRemaining(goalID);
        return daysLeft > 0 ? remainingBudget / daysLeft : 0;
    }

    public static String getBudgetStatusMessage(int goalID) {
        double percentageSpent = getPercentageSpent(goalID);
        long daysRemaining = getDaysRemaining(goalID);
        boolean overBudget = isOverBudget(goalID);
        double remaining = getRemainingBudget(goalID);
        double dailyAverage = getAverageSpendingAllowed(goalID);

        if (overBudget) {
            return "⚠️ You are over budget.";
        } else if (percentageSpent >= 90) {
            return "⚠️ You’ve spent over 90% of your budget. Be very cautious.";
        } else if (percentageSpent >= 75) {
            return "🔶 You’ve spent over 75% of your budget. Monitor spending closely.";
        } else if (daysRemaining <= 3 && remaining > 0) {
            return "⌛ Only " + daysRemaining + " day(s) left. Try to stay within the remaining budget.";
        } else if (percentageSpent < 50 && daysRemaining < 3) {
            return "✅ You’re under budget with only " + daysRemaining + " day(s) left.";
        } else {
            return "✅ You are on track. Keep monitoring your spending.";
        }
    }
}
