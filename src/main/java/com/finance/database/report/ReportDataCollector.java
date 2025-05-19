package com.finance.database.report;

import com.finance.database.Calculator.IncomeCalculations;
import com.finance.database.Calculator.ExpenseCalculations;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Map;

public class ReportDataCollector {

    private final IncomeCalculations incomeCalc;
    private final ExpenseCalculations expenseCalc;

    public ReportDataCollector(Connection connection) {
        this.incomeCalc = new IncomeCalculations(connection);
        this.expenseCalc = new ExpenseCalculations(connection);
    }

    public ReportData collectReportData(String userEmail, LocalDate startDate, LocalDate endDate) {
        double totalIncome = incomeCalc.calculateForDateRange(userEmail, startDate, endDate);
        double totalExpense = expenseCalc.calculateForDateRange(userEmail, startDate, endDate);

        Map<LocalDate, Double> incomeDaily = incomeCalc.calculateDailyTotals(userEmail, startDate, endDate);
        Map<LocalDate, Double> expenseDaily = expenseCalc.calculateDailyTotals(userEmail, startDate, endDate);

        Map<String, Double> incomeByCategory = incomeCalc.calculateByCategory(userEmail);
        Map<String, Double> expenseByCategory = expenseCalc.calculateByCategory(userEmail);

        return new ReportData(
                userEmail,
                startDate,
                endDate,
                totalIncome,
                totalExpense,
                incomeDaily,
                expenseDaily,
                incomeByCategory,
                expenseByCategory
        );
    }
}
