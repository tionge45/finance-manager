package com.finance.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Income {
    private int incomeID;
    private String userLogin;
    private double salary;
    private double investment;
    private double business;

    public Income(int _incomeID, String _userLogin, double _salary, double _investment, double _business) {
        this.incomeID = _incomeID;
        this.userLogin = _userLogin;
        this.salary = _salary;
        this.investment = _investment;
        this.business = _business;
    }

}