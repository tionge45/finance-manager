package com.finance.model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Expense {
    private String userLogin;
    private String category;
    private double amount;
    private String description;
    private Date expenseDate;
    private Time expenseTime;

    public Expense(int expenseID, String _userLogin, String _category, double _amount, String _description, Date _expenseDate, Time _expenseTime ){
        this.userLogin = _userLogin;
        this.category = _category;
        this.amount = _amount;
        this.description = _description;
        this.expenseDate = _expenseDate;
        this.expenseTime = _expenseTime;
    }


}
