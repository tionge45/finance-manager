package com.finance.controller;

import com.finance.database.ExpenseDAO;
import com.finance.database.IncomeDAO;
import com.finance.database.filters.ExpenseFilter;
import com.finance.database.filters.TransactionFilter;
import com.finance.database.FinanceDatabase;
import com.finance.model.Transaction;
import com.finance.service.UserSessionSingleton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class InsightsController {

    public Button sideBarBtn;
    @FXML private ChoiceBox<String> timeRangeChoiceBox;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private Button applyCustomRangeButton;
    @FXML private PieChart incomeExpensePie;
    @FXML private Label netIncomeLabel;

    private IncomeDAO incomeDAO;
    private ExpenseDAO expenseDAO;
    private TransactionFilter expenseFilter;
    private SideBarController sideBarController;

    @FXML
    public void initialize() {
        try {
            Connection conn = FinanceDatabase.getConnection();
            incomeDAO   = new IncomeDAO(conn);
            expenseDAO  = new ExpenseDAO(conn);
            expenseFilter = new ExpenseFilter(conn);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        initializeGraph();
        timeRangeChoiceBox.setValue("This Month");
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

        timeRangeChoiceBox.getSelectionModel().select("This Week");

    }


    private void updateChartForRange(String range) {
        LocalDate today = LocalDate.now();
        LocalDate from, to;

        switch (range) {
            case "Today"     -> { from = today; to = today; }
            case "This Week" -> { from = today.minusDays(today.getDayOfWeek().getValue()-1); to = today; }
            case "This Month"-> { from = today.withDayOfMonth(1); to = today; }
            case "This Year" -> { from = today.withDayOfYear(1); to = today; }
            default          -> { return; }
        }
        updateChartForCustomRange(from, to);
    }

    private void updateChartForCustomRange(LocalDate start, LocalDate end) {
        String email = UserSessionSingleton.getLoggedInUser().getUserEmail();

        CompletableFuture
                .supplyAsync(() -> fetchTotals(email, start, end),
                        Executors.newSingleThreadExecutor())          // ★ explicit pool (optional)
                .thenAccept(totals -> Platform.runLater(
                        () -> renderPie(totals[0], totals[1])));
    }

    private double[] fetchTotals(String email,
                                 LocalDate start,
                                 LocalDate end) {
        try {
            List<Transaction> incomes = incomeDAO.getTransactionByUser(email);
            List<Transaction> expenses =
                    expenseFilter.filterByDateRange(email, start, end, "Expense");

            double incomeTotal = incomes.stream()
                    .filter(t -> t.getTimestamp() != null)          // ★ NPE guard
                    .filter(t -> {
                        LocalDate d = t.getTimestamp().toLocalDate();
                        return !d.isBefore(start) && !d.isAfter(end);
                    })
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double expenseTotal = expenses.stream()
                    .filter(t -> t.getTimestamp() != null)          // ★ guard
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            return new double[] { incomeTotal, expenseTotal };
        } catch (Exception ex) {
            ex.printStackTrace();
            return new double[] { 0, 0 };
        }
    }

    private void renderPie(double income, double expense) {
        var data = FXCollections.<PieChart.Data>observableArrayList();
        if (income  > 0) data.add(new PieChart.Data("Income",  income));
        if (expense > 0) data.add(new PieChart.Data("Expense", expense));

        incomeExpensePie.setData(data);

        double net = income - expense;
        netIncomeLabel.setText(String.format("Net Income: %s%.2f",
                net >= 0 ? "+" : "-", Math.abs(net)));
        Platform.runLater(() -> {
            for(PieChart.Data d : data){
                d.getNode().setStyle("-fx-pie-color: " + (
                        d.getName().equalsIgnoreCase("Income") ?  "#3498db" : "#2c3e50"
                        ) + ";");
            }
        });
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
