package com.finance.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class Transaction {

    protected String category;
    protected double amount;
    protected String description;

    protected LocalDateTime timestamp;

    protected abstract String getType();

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


}
