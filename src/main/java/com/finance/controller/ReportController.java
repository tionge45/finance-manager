package com.finance.controller;

import com.finance.database.FinanceDatabase;
import com.finance.database.report.PDFReportGenerator;
import com.finance.database.report.ReportData;
import com.finance.database.report.ReportDataCollector;
import com.finance.database.report.ReportType;
import com.finance.service.UserSessionSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;


public class ReportController {

    public Button sideBarBtn;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox reportTypeComboBox;
    public Button viewBtn;
    public ScrollPane reportScroll;
    public VBox reportContent;
    public Button generateBtn;
    private SideBarController sideBarController;

    private PDFReportGenerator pdfReportGenerator;
    private ReportDataCollector reportDataCollector;

    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = FinanceDatabase.getConnection();
            setUpReportTab(connection);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUpReportTab(Connection connection){
        reportDataCollector = new ReportDataCollector(connection);
        pdfReportGenerator = new PDFReportGenerator(reportDataCollector);

    }


    @FXML
    private void handleViewReport()  {

        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();

        reportContent.getChildren().clear();

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        String type = (String) reportTypeComboBox.getValue();

        if (start == null || end == null || type == null) {
            showAlert("Please select a date range and report type.");
            return;
        }

        ReportData data = reportDataCollector.collectReportData(userEmail, start, end);

        Text header = new Text(type + " Report (" + start + " to " + end + ")");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        reportContent.getChildren().add(header);

        if (type.equals("INCOME")) {
            data.incomeByCategory.forEach((k, v) -> reportContent.getChildren().add(
                    new Label(k + ": $" + String.format("%.2f", v))));
            reportContent.getChildren().add(new Label("Total Income: $" + data.totalIncome));

        } else if (type.equals("EXPENSE")) {
            data.expenseByCategory.forEach((k, v) -> reportContent.getChildren().add(
                    new Label(k + ": $" + String.format("%.2f", v))));
            reportContent.getChildren().add(new Label("Total Expense: $" + data.totalExpense));

        } else if (type.equals("COMBINED")) {
            reportContent.getChildren().add(new Label("Income:"));
            data.incomeByCategory.forEach((k, v) -> reportContent.getChildren().add(
                    new Label("  " + k + ": $" + String.format("%.2f", v))));
            reportContent.getChildren().add(new Label("Total Income: $" + data.totalIncome));

            reportContent.getChildren().add(new Label(""));

            reportContent.getChildren().add(new Label("Expenses:"));
            data.expenseByCategory.forEach((k, v) -> reportContent.getChildren().add(
                    new Label("  " + k + ": $" + String.format("%.2f", v))));
            reportContent.getChildren().add(new Label("Total Expense: $" + data.totalExpense));
        }
    }

    @FXML
    private void handleGenerateReport() {

        UserSessionSingleton.getInstance();
        String userEmail = UserSessionSingleton.getLoggedInUser().getUserEmail();

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        String type = (String) reportTypeComboBox.getValue();

        if (start == null || end == null || type == null) {
            showAlert("Please select a date range and report type.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(getWindow());

        if (file != null) {
            pdfReportGenerator.generateReport(userEmail, start, end, ReportType.valueOf(type), file.getAbsolutePath());
            showAlert("Report generated successfully!");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private Window getWindow() {
        return sideBarBtn.getScene().getWindow();
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

