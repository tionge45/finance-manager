package com.finance.controller;

import com.finance.database.BudgetDAO;
import com.finance.database.FinanceDatabase;
import com.finance.model.Budget;
import com.finance.service.BudgetService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class BudgetItemController {
    public Label nameLabel;
    public Label amountLabel;
    public Label categoryLabel;
    public Label dateLabel;
    public Label statusLabel;
    public Label remainingLabel;
    public Label messageLabel;

    private Connection connection = FinanceDatabase.getConnection();
    private BudgetDAO budgetDAO = new BudgetDAO(connection);
    private final BudgetService budgetService = new BudgetService(budgetDAO);
    public Button deleteButton;
    private Budget budget;
    private Runnable onDeleteCallback;

    private static final Logger LOGGER = Logger.getLogger(BudgetItemController.class.getName());

    public BudgetItemController() throws SQLException {
    }

    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }

    public void setBudget(Budget b) {
        this.budget = b;
        nameLabel.setText(b.getName() != null ? b.getName() : "Untitled");
        amountLabel.setText(String.format("$%.2f", b.getBudgetAmount()));
        categoryLabel.setText("📂 " + b.getCategory());
        dateLabel.setText("📅 " + b.getStartDate() + " → " + b.getEndDate());
        statusLabel.setText("📌 " + b.getStatus().name());
        remainingLabel.setText("💵 " + String.format("%.2f", b.getRemaining()) + " left");

        messageLabel.setText(budgetService.statusMessage(b));
    }

    @FXML
    private void handleDeleteBudget(ActionEvent event) {

        if (budget == null) {
            LOGGER.info("Budget is not set");
            return;
        }

        int budgetID = budget.getBudgetID();
        LOGGER.info("Deleting budget ID: " + budgetID);

        try {
            connection = FinanceDatabase.getConnection();
            budgetDAO = new BudgetDAO(connection);
            budgetDAO.deleteBudgetById(budgetID);


            if (onDeleteCallback != null) {
                onDeleteCallback.run();
            }

            LOGGER.info("Deleting budget ID: " + budgetID);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void onDeleteCallback(){}
}
