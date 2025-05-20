package com.finance.controller;

import com.finance.database.ExpenseDAO;
import com.finance.database.IncomeDAO;
import com.finance.database.filters.ExpenseFilter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.Connection;

public class BudgetController {

    private SideBarController sideBarController;

    @FXML
    public void initialize(){

    }


    public void setSideBarController(SideBarController sideBarController) {
        this.sideBarController = sideBarController;
        sideBarController.welcomeMessage();
    }

    @FXML
    private void handleToggleSidebar() {
        if (sideBarController != null) {
            sideBarController.toggleSidebar();
        }
    }
}
