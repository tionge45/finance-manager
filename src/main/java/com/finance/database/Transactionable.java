package com.finance.database;

import com.finance.model.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface Transactionable {

    void insertTransaction(Transaction transaction, String userEmail);

    List<Transaction> getTransactionByUser(String userLogin) throws SQLException;

}
