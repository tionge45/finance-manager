package com.finance.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString

public class Income extends Transaction {

    private String category; //a.k.a category => Business, Salary, etc
    private double amount;
    private String description;

    public Income(String category, double amount, String description) {
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public String getType(){
        return "Income";
    }

}