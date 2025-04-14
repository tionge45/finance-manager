package com.finance.controller;

import com.finance.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label incomeLabel, expenseLabel, budgetLabel;
    @FXML
    private Hyperlink logOutLabel;


    public void initialize() {
        incomeLabel.setText("Total Income: $0");
        expenseLabel.setText("Total Expenses: $0");
        budgetLabel.setText("Budget Left: $0");
    }

    public void handleLogout() throws IOException {
        SceneSwitcher.switchScene(logOutLabel, "/fxml/welcome.fxml");
    }
}
