package it.unicam.cs.mdp2024.formula1game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
        primaryStage.setTitle("Formula 1 Game");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(850);  // Canvas width (800) + margini
        primaryStage.setMinHeight(700); // Canvas height (600) + spazio per bottoni e margini
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
