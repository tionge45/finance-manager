package com.finance.controller;

import com.finance.model.User;
import com.finance.service.UserSessionSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SideBarController {

    public Button transactionHistoryBtn;
    public Button insightsBtn;
    @FXML
    private Button exitBtn, dashboardBtn, incomeBtn, budgetBtn, reportBtn, logOutBtn ;
    @FXML
    private ImageView userAvatar;
    @FXML
    Label welcomeLabel;
    @FXML private VBox sidebar;

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize(){
        sidebar.setVisible(false);
        sidebar.setManaged(false);

        try{
            loadPage("/fxml/dashboard.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void toggleSidebar() {
        sidebar.setVisible(!sidebar.isVisible());
        sidebar.setManaged(sidebar.isVisible());
    }

    private void loadPage(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Node node = loader.load();
        contentPane.getChildren().setAll(node);

        // Set controller reference if needed
        if (loader.getController() instanceof DashboardController dashboardController) {
            dashboardController.setSideBarController(this);
        }
        else if (loader.getController() instanceof InsightsController insightsController){
            insightsController.setSideBarController(this);
        }
        else if(loader.getController() instanceof BudgetController budgetController){
            budgetController.setSideBarController(this);
        }
        else if(loader.getController() instanceof ReportController reportController){
            reportController.setSideBarController(this);
        }
    }


    public void handleDashboard(ActionEvent event) throws IOException {
        System.out.println("Dashboard button clicked!");
        loadPage("/fxml/dashboard.fxml");

    }

    public void handleInsights(ActionEvent event) throws IOException {
        System.out.println("Insights button clicked!");
        loadPage("/fxml/insights.fxml");
    }

    public void handleBudget(ActionEvent event) throws IOException {
        System.out.println("Budget button clicked!");
        loadPage("/fxml/budget.fxml" );
    }

    public void handleReport(ActionEvent event) throws IOException {
        System.out.println("Report button clicked!");
        loadPage("/fxml/report.fxml" );
    }

    public void handleLogout() throws IOException {
        UserSessionSingleton.clear();
        loadPage("/fxml/welcome.fxml");
    }

    public void handleExit(ActionEvent event) {
        System.exit(0);
    }



    public void welcomeMessage() {
        User user = UserSessionSingleton.getLoggedInUser();
        if (user != null) {
            String username = user.getUserName();
            if (username != null && !username.isEmpty()) {
                welcomeLabel.setText("Welcome, " + user.getUserName() + "!");
            } else {
                welcomeLabel.setText("Welcome!");
            }
        } else {
            welcomeLabel.setText("Welcome, Guest!");
        }
    }


}
