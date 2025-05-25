package com.finance.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class ReportController {

    public Button sideBarBtn;
    private SideBarController sideBarController;

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
