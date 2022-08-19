package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.JDBC;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * start that kick starts the app.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle rb = ResourceBundle.getBundle("controller/Nat", Locale.getDefault());;
        Parent root = FXMLLoader.load(getClass().getResource("../view/LoginPage.fxml"));

        if(Locale.getDefault().getLanguage().equals("fr")){
            primaryStage.setTitle(rb.getString("Scheduling_App"));
        }
        else {
            primaryStage.setTitle("Scheduling App");
        }
        primaryStage.setScene(new Scene(root, 350, 350));
        primaryStage.show();
    }

    /**
     * Main method. Thus, program execution starts from this main thread.
     * @param args
     */
    public static void main(String[] args) {
        // creates database connection before launch is called.
        JDBC.openDBConnection();

        // launches GUI
        launch(args);

        // Closes database connection.
        JDBC.closeDBConnection();
    }
}
