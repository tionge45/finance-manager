package com.finance.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Budget {
    private int budgetID;
    private int userID;
    @Nullable
    private String name;
    private String category;
    private double budgetAmount;
    private double budgetSpent;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;

    public enum Status{ACTIVE, EXCEEDED, ARCHIVED}

    public Budget(int budgetID, int userID, @Nullable String name, String category,
                  double budgetAmount, double budgetSpent, LocalDate startDate,
                  LocalDate endDate, Status status){
        this.budgetID = budgetID;
        this.userID = userID;
        this.name = name;
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.budgetSpent = budgetSpent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public double getRemaining(){
        return budgetAmount - budgetSpent;
    }

}
