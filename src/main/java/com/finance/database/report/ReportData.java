package com.finance.database.report;

import java.time.LocalDate;
import java.util.Map;

public class ReportData {
    public final String userEmail;
    public final LocalDate startDate;
    public final LocalDate endDate;
    public final double totalIncome;
    public final double totalExpense;
    public final Map<LocalDate, Double> incomeDailyTotals;
    public final Map<LocalDate, Double> expenseDailyTotals;
    public final Map<String, Double> incomeByCategory;
    public final Map<String, Double> expenseByCategory;

    public ReportData(
            String userEmail,
            LocalDate startDate,
            LocalDate endDate,
            double totalIncome,
            double totalExpense,
            Map<LocalDate, Double> incomeDailyTotals,
            Map<LocalDate, Double> expenseDailyTotals,
            Map<String, Double> incomeByCategory,
            Map<String, Double> expenseByCategory
    ) {
        this.userEmail = userEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.incomeDailyTotals = incomeDailyTotals;
        this.expenseDailyTotals = expenseDailyTotals;
        this.incomeByCategory = incomeByCategory;
        this.expenseByCategory = expenseByCategory;
    }
}
