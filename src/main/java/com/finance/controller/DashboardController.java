package com.finance.controller;

import com.finance.model.User;
import com.finance.service.UserSessionSingleton;
import com.finance.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

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
    private TableView<?> transactionTable;
    @FXML
    private TableColumn<?, ?> transactionDateColumn;
    @FXML
    private TableColumn<?, ?> transactionTypeColumn;
    @FXML
    private TableColumn<?, ?> transactionAmountColumn;
    @FXML
    private TableColumn<?, ?> transactionCategoryColumn;

    @FXML
    private TextField transactionAmountField;
    @FXML
    private TextField transactionCategoryField;
    @FXML
    private ChoiceBox<String> transactionTypeBox;
    private SideBarController sideBarController;
    @FXML
    private BorderPane sideBarInclude;


    @FXML
    public void initialize() {
        transactionTypeBox.setItems(FXCollections.observableArrayList("Income", "Expense"));

        XYChart.Series<Number, Number> spentSeries = new XYChart.Series<>();
        spentSeries.setName("Spent");
        spentSeries.getData().add(new XYChart.Data<>(1, 200));
        spentSeries.getData().add(new XYChart.Data<>(2, 250));
        totalSpentGraph.getData().add(spentSeries);

    }

    public void setSideBarController(SideBarController sideBarController) {
        this.sideBarController = sideBarController;
        sideBarController.welcomeMessage();
    }

    public void handleAddTransaction(ActionEvent event) {
    }


    @FXML
    private void handleToggleSidebar() {
        if (sideBarController != null) {
            sideBarController.toggleSidebar();
        }
    }
}




