package com.finance.model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Expense extends Transaction {
    private String category;
    private double amount;
    private String description;

    public Expense(String _category, double _amount, String _description){
        this.category = _category;
        this.amount = _amount;
        this.description = _description;

    }

    @Override
    protected String getType() {
        return "Expense";
    }
}
