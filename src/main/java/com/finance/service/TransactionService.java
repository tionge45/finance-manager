package com.finance.service;

import com.finance.database.ExpenseDAO;
import com.finance.database.IncomeDAO;
import com.finance.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final IncomeDAO incomeDAO;
    private final ExpenseDAO expenseDAO;

    public TransactionService(Connection connection) throws SQLException {
        this.incomeDAO = new IncomeDAO(connection);
        this.expenseDAO = new ExpenseDAO(connection);
    }

    public ObservableList<Transaction> loadTransactions() throws SQLException, SQLException {
        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();

        List<Transaction> income = incomeDAO.getTransactionByUser(userEmail);
        List<Transaction> expenses = expenseDAO.getTransactionByUser(userEmail);

        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        allTransactions.addAll(income);
        allTransactions.addAll(expenses);
        return allTransactions;
    }
}
