//Amitoj's Finance Tracker Personal Project
package com.financeguy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //creating the expense table if not created
        DatabaseHelper.initializeDatabase();
        //loading the mainUI layout from MainView.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        //Creating the scene with width: 1200 and height: 800
        Scene scene = new Scene(loader.load(),1200,800);
        stage.setScene(scene);
        stage.setTitle("FinanceGuy");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
