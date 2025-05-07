package com.finance.model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;


@Getter
@Setter
@ToString
public class Expense extends Transaction {
    private String category;
    private double amount;
    @Nullable
    private String description;

    public Expense(String _category, double _amount, @Nullable String  _description){
        this.category = _category;
        this.amount = _amount;
        this.description = _description;
    }

    @Override
    public String getType() {
        return "Expense";
    }
}
