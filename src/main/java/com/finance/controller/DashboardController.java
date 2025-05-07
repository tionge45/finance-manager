package com.finance.controller;

import com.finance.database.ExpenseDAO;
import com.finance.database.FinanceDatabase;
import com.finance.database.IncomeDAO;
import com.finance.model.Expense;
import com.finance.model.Income;
import com.finance.model.Transaction;
import com.finance.model.User;
import com.finance.service.UserSessionSingleton;
import com.finance.utils.SceneSwitcher;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardController {


    @FXML
    private Button sideBarBtn;
    @FXML
    private Label totalBudgetLabel;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private Label totalIncomeLabel;

    @FXML
    private LineChart<Number, Number> totalBudgetGraph;
    @FXML
    private LineChart<Number, Number> totalSpentGraph;
    @FXML
    private LineChart<Number, Number> totalIncomeGraph;


    @FXML
    private TextField transactionAmountField;
    @FXML
    private TextField transactionCategoryField;
    @FXML
    private ChoiceBox<String> transactionTypeBox;
    private SideBarController sideBarController;
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Double> transactionAmountColumn;
    @FXML
    private TableColumn<Transaction, String> transactionCategoryColumn;
    @FXML
    private TableColumn<Transaction, String> transactionDateColumn;
    @FXML
    private TableColumn<Transaction, String> transactionTypeColumn;
    private ObservableList<Transaction> transactionData = FXCollections.observableArrayList();


    private Connection connection;
    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;


    @FXML
    public void initialize() {
        try {
            connection = FinanceDatabase.getConnection();
            incomeDAO = new IncomeDAO(connection);
            expenseDAO = new ExpenseDAO(connection);

            configureTableColumns();  //Binding columns to actual fields
            transactionTable.setItems(transactionData);

            loadTransactions();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadTransactions() {
        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();

        try {
            List<Transaction> income = incomeDAO.getTransactionByUser(userEmail);
            List<Transaction> expenses = expenseDAO.getTransactionByUser(userEmail);

            List<Transaction> allTransactions = new ArrayList<>();
            allTransactions.addAll(income);
            allTransactions.addAll(expenses);

            System.out.println("Loaded transactions: " + allTransactions.size());
            transactionData.setAll(allTransactions);
            transactionTable.refresh();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void configureTableColumns() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        transactionDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTimestamp().format(formatter))
        );

        transactionTypeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType())
        );

        transactionAmountColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getAmount()).asObject()
        );

        transactionCategoryColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory())
        );
    }


    public void setSideBarController(SideBarController sideBarController) {
        this.sideBarController = sideBarController;
        sideBarController.welcomeMessage();
    }

    public void handleAddTransaction(ActionEvent event) {
        String type = transactionTypeBox.getValue();
        String category = transactionCategoryField.getText();
        double amount;
        try {
            amount = Double.parseDouble(transactionAmountField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();
        System.out.println("Trying to insert for user: " + userEmail);
        if ("Income".equals(type)) {
            Income income = new Income(category, amount, "");  // Use form description if available
            incomeDAO.insertTransaction(income, userEmail);
        } else if ("Expense".equals(type)) {
            Expense expense = new Expense(category, amount, "");  // Use form description if available
            expenseDAO.insertTransaction(expense, userEmail);
        } else {
            System.err.println("Transaction type not selected.");
            return;
        }

        // Refresh Table
        loadTransactions();

        // Clear input fields
        transactionCategoryField.clear();
        transactionAmountField.clear();
        transactionTypeBox.setValue(null);

    }


    @FXML
    private void handleToggleSidebar() {
        if (sideBarController != null) {
            sideBarController.toggleSidebar();
        }
    }
}




