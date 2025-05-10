package com.finance.database.filters;

import com.finance.model.Transaction;

import java.sql.Date;
import java.util.List;

public interface TransactionFilter {
    List<Transaction> filterByCategory(String userEmail, String category, String transactionType);

    List<Transaction> filterByDateRange(String userEmail, Date startDate, Date endDate, String transactionType);

    List<Transaction> filterByMonth(String userEmail, int year, int month, String transactionType);

    List<Transaction> filterByAmountRange(String userEmail, double minAmount, double maxAmount, String transactionType);

}
