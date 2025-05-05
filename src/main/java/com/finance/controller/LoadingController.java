package com.finance.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import com.finance.utils.SceneSwitcher;

public class LoadingController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void initialize() {
        Task<Void> loadingTask;
        loadingTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30); // simulate loading time
                    updateProgress(i, 100);
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(loadingTask.progressProperty());

        loadingTask.setOnSucceeded(event -> {
            // After loading, switch to welcome screen
            try {
                SceneSwitcher.switchScene(null, "/fxml/welcome.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        new Thread(loadingTask).start();
    }
}
