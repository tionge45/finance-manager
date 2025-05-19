package com.finance.controller;

import com.finance.database.ExpenseDAO;
import com.finance.database.FinanceDatabase;
import com.finance.database.IncomeDAO;
import com.finance.database.filters.ExpenseFilter;
import com.finance.model.Expense;
import com.finance.model.Income;
import com.finance.model.Transaction;
import com.finance.service.UserSessionSingleton;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {


    public CategoryAxis dateAxis;
    @FXML
    private  ChoiceBox<String> timeRangeChoiceBox;
    @FXML
    private  DatePicker startDatePicker, endDatePicker;
    @FXML
    private  Button applyCustomRangeButton;
    @FXML
    private Button sideBarBtn;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private LineChart<String, Number> totalSpentGraph;



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
    private ExpenseFilter expenseFilter;


    @FXML
    public void initialize() {
        try {
            connection = FinanceDatabase.getConnection();
            incomeDAO = new IncomeDAO(connection);
            expenseDAO = new ExpenseDAO(connection);
            expenseFilter = new ExpenseFilter(connection);
            initializeGraph();
            configureTableColumns();  //Binding columns to actual fields
            transactionTable.setItems(transactionData);
            loadTransactions();
            updateChartForRange("This Week");
            timeRangeChoiceBox.setValue("This Week");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void initializeGraph(){

        startDatePicker.setVisible(false);
        endDatePicker.setVisible(false);
        applyCustomRangeButton.setVisible(false);

        timeRangeChoiceBox.getSelectionModel().
                selectedItemProperty().
                addListener
                        ((obs, oldVal, newVal) -> {
                            if ("Custom Range".equals(newVal)){
                                startDatePicker.setVisible(true);
                                endDatePicker.setVisible(true);
                                applyCustomRangeButton.setVisible(true);
                            } else {

                                startDatePicker.setVisible(false);
                                endDatePicker.setVisible(false);
                                applyCustomRangeButton.setVisible(false);
                                updateChartForRange(newVal);
                            }

                        });

                applyCustomRangeButton.setOnAction( E -> {
                    LocalDate start = startDatePicker.getValue();
                    LocalDate end = endDatePicker.getValue();
                    if (start != null && end != null && !end.isBefore(start)){
                        updateChartForCustomRange(start, end);
                        timeRangeChoiceBox.setValue("Custom Range");//to be implemented
                    } else {
                        System.out.println("NOTHING"); // To be done later
                    }
                });

    }

    private void updateChartForRange(String range){
        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = new ArrayList<>();

        switch (range){
            case "Today" ->
                    transactions = expenseFilter.filterByDateRange(userEmail, today, today, "Expense");

            case "This Week" -> {
                LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
                transactions = expenseFilter.filterByDateRange(
                        userEmail, startOfWeek, today, "Expense");
            }
            case "This Month" -> {
                transactions = expenseFilter.filterByMonth(userEmail, today.getYear(), today.getMonthValue(), "Expense");
            }
            case "This Year" -> {
                LocalDate startOfYear = LocalDate.of(today.getYear(), 1, 1);
                transactions = expenseFilter.filterByDateRange(
                        userEmail, startOfYear, today, "Expense");
            }
        }
        updateChart(transactions);
    }

    private void updateChart(List<Transaction> transactions) {

        // clears previous data
        totalSpentGraph.getData().clear();

        if (transactions.isEmpty()) {
            totalSpentLabel.setText("Total Spent: $0.00");
            return;
        }

        //aggregates expenses from one day into one sum
        Map<LocalDate, Double> dailyTotals = new HashMap<>();
        for (Transaction t : transactions) {
            if(t.getTimestamp() == null){
                System.err.println("Transaction without timestamp: " + t);
                continue;
            }
            LocalDate day = t.getTimestamp().toLocalDate();
            dailyTotals.merge(day, t.getAmount(), Double::sum);
        }

        //builds a linechart series
        //data point on graph = (day, total), (x, y)
        XYChart.Series<String, Number> spentSeries = new XYChart.Series<>();
        spentSeries.setName("Expenses");

        //sorts left→right chronologically
        //formats each date entry
        dailyTotals.keySet().stream()
                .sorted()
                .forEach(day -> {
                    double total = dailyTotals.get(day);

                    String label = day.format(DateTimeFormatter.ofPattern("dd MMM"));
                    spentSeries.getData().add(new XYChart.Data<>(label, total));
                });

        //display graph
        totalSpentGraph.getData().add(spentSeries);

        double grandTotal = dailyTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        totalSpentLabel.setText(String.format("Total Spent: $%.2f", grandTotal));


    }

    public void updateChartForCustomRange(LocalDate start, LocalDate end) {
        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();
        List<Transaction> transactions = expenseFilter.filterByDateRange(
                userEmail, start, end, "Expense");
        updateChart(transactions);
    }

    public void loadTransactions() {
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
            income.setTimestamp(LocalDateTime.now());
            incomeDAO.insertTransaction(income, userEmail);
        } else if ("Expense".equals(type)) {
            Expense expense = new Expense(category, amount, "");  // Use form description if available
            expense.setTimestamp(LocalDateTime.now());
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




