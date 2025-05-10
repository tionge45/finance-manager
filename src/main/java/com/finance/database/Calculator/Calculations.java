package com.finance.database.Calculator;

import java.time.LocalDate;
import java.util.Map;

public interface Calculations {
    Map<String, Double> calculateByCategory(String userEmail);
    double calculateForCurrentWeek(String userEmail);
    double calculateForCurrentMonth(String userEmail);
    double calculateForDateRange(String userEmail, LocalDate startDate, LocalDate endDate);
    Map<LocalDate, Double> calculateDailyTotals(String userEmail, LocalDate startDate, LocalDate endDate);
}