package com.finance.controller;

import com.finance.database.BudgetDAO;
import com.finance.database.FinanceDatabase;
import com.finance.model.Budget;
import com.finance.service.UserSessionSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BudgetController {

    public Button sideBarBtn;
    public Button addBudgetBtn;
    public Label totalBudgetLabel;
    public Label totalSpentLabel;
    public Label activeBudgetLabel;
    public VBox budgetListContainer;
    public VBox emptyStateBox;
    public Button createBudgetBtn;
    public Button deleteBudgetBtn;
    private SideBarController sideBarController;

    private Connection connection;
    private BudgetDAO budgetDAO;

    private static final Logger LOGGER = Logger.getLogger(BudgetController.class.getName());


    @FXML
    public void initialize(){

        loadBudgetsAndUpdateSummary();
    }


    public void loadBudgetsAndUpdateSummary(){
        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();

        try{
            connection = FinanceDatabase.getConnection();
            budgetDAO = new BudgetDAO(connection);
            List<Budget> budgets = budgetDAO.findByUser(userEmail);

            double totalBudget = 0;
            double totalSpent = 0;
            double activeCount = 0;

            for(Budget b: budgets){
                totalBudget += b.getBudgetAmount();
                totalSpent += b.getBudgetSpent();
                if(b.getStatus() == Budget.Status.ACTIVE){
                    activeCount++;
                }
            }

            totalBudgetLabel.setText("$" + String.format("%.2f", totalBudget));
            totalSpentLabel.setText("$" + String.format("%.2f", totalSpent));
            activeBudgetLabel.setText(String.valueOf(activeCount));

            budgetListContainer.getChildren().clear();

            if (budgets.isEmpty()){
                emptyStateBox.setVisible(true);
                emptyStateBox.setManaged(budgets.isEmpty());
            } else {
                Map<String, List<Budget>> groupedBudgets = budgets.stream()
                        .collect(Collectors.groupingBy(Budget::getCategory));
                for(Map.Entry<String, List<Budget>> entry : groupedBudgets.entrySet()){

                    String category = entry.getKey();
                    List<Budget> categoryBudgets = entry.getValue();

                    Label categoryLabel = new Label("📂 " + category);
                    categoryLabel.getStyleClass().add("category-label");
                    budgetListContainer.getChildren().add(categoryLabel);

                    for (Budget b : categoryBudgets) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budget_item.fxml"));
                            VBox budgetItem = loader.load();

                            BudgetItemController controller = loader.getController();
                            controller.setBudget(b);

                            budgetListContainer.getChildren().add(budgetItem);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                }
                for (Budget b: budgets){
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budget_item.fxml"));
                        VBox budgetItem = loader.load();

                        BudgetItemController controller = loader.getController();
                        controller.setBudget(b);
                        controller.setOnDeleteCallback(this::loadBudgetsAndUpdateSummary);



                        budgetListContainer.getChildren().add(budgetItem);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    @FXML
    public void handleAddBudget() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_budget.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Budget");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadBudgetsAndUpdateSummary();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
