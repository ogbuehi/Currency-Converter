package com.example.currencyconverter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("currency-converter.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 563, 393);
        stage.setTitle("Currency Converter");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event ->{
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}