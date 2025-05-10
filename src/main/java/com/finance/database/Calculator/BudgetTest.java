package com.finance.database.Calculator;

public class BudgetTest {
    public static void main(String[] args) {
        int goalID = 1; // Use a valid goalID from your `budget_goals` table
        String userLogin = "Joshua"; // Use a valid userLogin from your `Users` table
        String category = "transport"; // Use a category used in the `transactions` and `budget_goals`

        // Fetch and display individual budget-related values
        System.out.println("Budget Amount: " + BudgetCalculations.getBudgetAmount(goalID));
        System.out.println("Current Expenses in Category: " +
                BudgetCalculations.getCurrentExpenses(userLogin, category));
        System.out.println("Percentage Spent: " + BudgetCalculations.getPercentageSpent(goalID) + "%");
        System.out.println("Days Remaining: " + BudgetCalculations.getDaysRemaining(goalID));
        System.out.println("Remaining Budget: " + BudgetCalculations.getRemainingBudget(goalID));
        System.out.println("Average Spending Allowed per Day: " +
                BudgetCalculations.getAverageSpendingAllowed(goalID));

        // Final status message
        System.out.println("Status Message: " + BudgetCalculations.getBudgetStatusMessage(goalID));
    }
}
