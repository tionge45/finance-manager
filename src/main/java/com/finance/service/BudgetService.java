package com.finance.service;

import com.finance.database.BudgetDAO;
import com.finance.model.Budget;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class BudgetService {

    private final BudgetDAO dao;

    public BudgetService(BudgetDAO dao) {
        this.dao = dao;
    }

    public double percentageSpent(Budget b) {
        if (b.getBudgetAmount() == 0) return 0;
        return (b.getBudgetSpent() / b.getBudgetAmount()) * 100;
    }

    public long daysRemaining(Budget b) {
        return ChronoUnit.DAYS.between(LocalDate.now(), b.getEndDate());
    }

    public String statusMessage(Budget b) {
        double pct = percentageSpent(b);
        long daysLeft = daysRemaining(b);
        double remaining = b.getRemaining();

        if (pct >= 100) return "⚠️ Over budget!";
        if (pct >= 90) return "🔶 You’ve used 90%+ of your budget.";
        if (pct >= 75) return "🟡 You’ve used 75%+ of your budget.";
        if (daysLeft <= 3 && remaining > 0) return "⌛ " + daysLeft + " day(s) left. Stay within budget.";
        if (pct < 50 && daysLeft <= 3) return "✅ You're well under budget with " + daysLeft + " day(s) left.";
        return "✅ You're on track.";
    }

    public void registerExpense(int budgetId, double amount) throws SQLException {
        dao.updateAmountSpent(budgetId, amount);

        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();

        Optional<Budget> bOpt = dao.findByUser(userEmail)
                .stream()
                .filter(b -> b.getBudgetID() == budgetId)
                .findFirst();

        if (bOpt.isPresent()) {
            Budget b = bOpt.get();
            Budget.Status newStatus = b.getBudgetSpent() > b.getBudgetAmount()
                    ? Budget.Status.EXCEEDED
                    : Budget.Status.ACTIVE;
            dao.updateStatus(budgetId, newStatus);
        }
    }

    public List<Budget> getAllBudgetsForUser() throws SQLException {
        UserSessionSingleton.getInstance();
        String email = UserSessionSingleton.getLoggedInUser().getUserEmail();
        return dao.findByUser(email);
    }

    public void insertBudget(Budget b) throws SQLException {
        dao.insertBudget(b);
    }

}
