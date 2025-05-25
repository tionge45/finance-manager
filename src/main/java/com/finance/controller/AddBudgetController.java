package com.finance.controller;

import com.finance.database.BudgetDAO;
import com.finance.database.FinanceDatabase;
import com.finance.database.UserDAO;
import com.finance.model.Budget;
import com.finance.model.User;
import com.finance.service.BudgetService;
import com.finance.service.UserSessionSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddBudgetController {


    public TextField nameField;
    public TextField amountField;
    public TextField categoryField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private Connection conn = FinanceDatabase.getConnection();
    private final BudgetDAO dao = new BudgetDAO(conn);
    private BudgetService budgetService;

    public AddBudgetController() throws SQLException {

    }

    @FXML
    public void handleSaveBudget(ActionEvent event) {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            double amount = Double.parseDouble(amountField.getText());
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            UserSessionSingleton.getInstance();
            User currentUser = UserSessionSingleton.getLoggedInUser();
            budgetService = new BudgetService(dao);

            Budget b = new Budget();

            b.setName(name);
            b.setCategory(category);
            b.setBudgetAmount(amount);
            b.setBudgetSpent(0);
            b.setStartDate(startDate);
            b.setEndDate(endDate);
            b.setUserID(currentUser.getUserID());
            b.setStatus(Budget.Status.ACTIVE);

            budgetService.insertBudget(b);

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
