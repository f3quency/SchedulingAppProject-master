package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.MyPrintInterface;
import utilities.UsersQuery;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Login Controller class
 * @author Febechukwu Megwalu
 */
public class LoginPageController implements Initializable {
    ResourceBundle rb;
    boolean loginState = false;

    @FXML
    private Button loginButton;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField passwordText;

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField userNameText;

    @FXML
    private Label zoneIDLabel;

    /**
     * Initializes this controller class
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MyPrintInterface message = z -> "" + z;         // Used lambda expression to printout user zone
        zoneIDLabel.setText(message.myMessage(ZoneId.systemDefault().toString()));
        try{
            rb = ResourceBundle.getBundle("controller/Nat", Locale.getDefault());
            System.out.println("Login");  // For debugging
            // The logic below checks if the default language is french
            if(Locale.getDefault().getLanguage().equals("fr")){
                loginButton.setText(rb.getString("Login"));
                passwordLabel.setText(rb.getString("Password"));
                userNameLabel.setText(rb.getString("User_Name"));
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * If the user inputs the right login combinations and credentials, this event handler directs the user
     * to the next page of the app.
     * @param event
     */
    @FXML
    void loginButtonClicked(ActionEvent event) {
        try{
            System.out.println(userNameText.getText() + " " + "Password:" + passwordText.getText()); // For debugging
            loginState = UsersQuery.login(userNameText.getText(), passwordText.getText());
        }
        catch(Exception e){
            Alert userAlert;
            System.err.println("Wrong Input Error: " + e.getMessage() + "!");         // For debugging
            if(Locale.getDefault().getLanguage().equals("fr")) {
                userAlert = new Alert(Alert.AlertType.ERROR, rb.getString("User_ID_must_be_an_integer!"));
            }
            else {
                userAlert = new Alert(Alert.AlertType.ERROR, "User ID must be an integer! " + e.getMessage());
            }
            userAlert.show();
        }
        if(loginState){
            try {
                SchedulePageController.recieveUserNameText(userNameText.getText());
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("../view/SchedulePage.fxml"));
                if(Locale.getDefault().getLanguage().equals("fr")){
                    stage.setTitle(rb.getString("Scheduling_App"));
                }
                else {
                    stage.setTitle("Scheduling App");
                }
                stage.setScene(new Scene(root, 800, 450));
                stage.setX(250);
                stage.setY(5);
                stage.show();
            }catch(Exception e){
                System.err.println("Error loading next page: " + e.getMessage() + "!");         // For debugging
            }
        }
        else{
            Alert loginAlert;
            if(Locale.getDefault().getLanguage().equals("fr")) {
                loginAlert = new Alert(Alert.AlertType.ERROR, rb.getString("Wrong_Username_or_Password!"));
            }
            else{
                loginAlert = new Alert(Alert.AlertType.ERROR, "Wrong Username or Password!");
            }
            loginAlert.show();
        }
        printLoginAttempt(loginState);
    }

    /**
     * Appends logged information every time a login attempt is made.
     * @param loginState
     */
    private void printLoginAttempt(boolean loginState) {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream("login_activity.txt",true));
            //out.txt will appear in the project's root directory under NetBeans projects
            //Note that Notepad will not display the following lines on separate lines
            pw.append("Username: " + userNameText.getText() + "\n");
            pw.append("Password: " + passwordText.getText() + "\n");
            pw.append("Date and Time: " + now.getTime() + "\n");
            pw.append("Attempt success: " + loginState + "\n\n");
            pw.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
