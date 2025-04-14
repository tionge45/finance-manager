package com.finance.model;

import com.finance.database.FinanceDatabase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Budget {
    private int goalID;
    private String userLogin;
    private String category;
    private double budgetAmount;
    private double currentExpenses;
    private Date startDate;
    private Date endDate;
    private String status;

    public Budget(int _goalID, String _userLogin, String _category, double _budgetAmount, double _currentExpenses, Date _startDate, Date _endDate, String _status) {
        this.goalID = _goalID;
        this.userLogin = _userLogin;
        this.category = _category;
        this.budgetAmount = _budgetAmount;
        this.currentExpenses = _currentExpenses;
        this.startDate = _startDate;
        this.endDate = _endDate;
        this.status = _status;
    }


    public static void addToCurrentExpenses(String userLogin, String category, double amount) {
    }
}
