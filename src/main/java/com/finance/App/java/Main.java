package com.finance.App.java;

import com.finance.database.FinanceDatabase;
import com.finance.utils.SceneSwitcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
       // FinanceDatabase.createTables();
        SceneSwitcher.setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/loading.fxml")));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Loading...");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
