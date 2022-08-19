package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import utilities.JDBC;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * This class is a controller for add appointment button
 */
public class AddAppointmentController implements Initializable {
    @FXML
    private ComboBox<String> contactNameComboBox;
    @FXML
    private TextField aCustomerIDTextField;
    @FXML
    private TextField aDescriptionTextField;
    @FXML
    private TextField aEndTextField;
    @FXML
    private TextField aIDTextField;
    @FXML
    private TextField aLocationTextField;
    @FXML
    private TextField aStartTextField;
    @FXML
    private TextField aTitleTextField;
    @FXML
    private TextField aTypeTextField;
    @FXML
    private TextField aUserIDTextField;

    Stage stage = new Stage();
    Parent parentScene;
    String sqlString;
    private int appointmentID;
    private static String userNameText;

    /**
     * this method initializes appointment ID and Contact Name
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAppointmentID();
        initializeContactName();
    }

    /**
     * Helper method to initialize contact name
     */
    private void initializeContactName() {
        sqlString = "SELECT Contact_Name FROM contacts";
        PreparedStatement pStatement = null;
        try {
            pStatement = JDBC.myConnection.prepareStatement(sqlString);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        ResultSet resultSet = null;
        try {
            resultSet = pStatement.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        contactNameComboBox.getItems().clear();
        try {
            while (resultSet.next()) {
                contactNameComboBox.getItems().add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Helper method to initialize appointment ID
     */
    private void initializeAppointmentID() {
        try{
            sqlString = "SELECT MAX(Appointment_ID) FROM appointments";
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sqlString);
            ResultSet resultSet = pStatement.executeQuery();
            resultSet.next();  // moving cursor to last row
            //NOTE!! Create and assign new appointment ID in SchedulePageController
            appointmentID = resultSet.getInt("MAX(Appointment_ID)") + 1;
        }
        catch(SQLException sqlException){
            System.err.println(sqlException.getMessage());
        }
        aIDTextField.setText(appointmentID + "");
    }

    /**
     * Returns to previous menu if cancel button is clicked.
     * @param event
     * @throws IOException
     */
    @FXML
    void cancelButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        // Setting the scene
        parentScene = FXMLLoader.load(getClass().getResource("/view/SchedulePage.fxml"));
        stage.setScene(new Scene(parentScene));
        stage.setTitle("Scheduling App");
        stage.show();
    }

    /**
     * Saves the appointment if there are no overlaps or appointment is within business hours.
     * @param event
     * @throws IOException
     */
    @FXML
    void saveButtonClicked(ActionEvent event) throws IOException {
        boolean schedulingError = false;
        if(aTitleTextField.getText().isEmpty() || aDescriptionTextField.getText().isEmpty() ||
                aLocationTextField.getText().isEmpty() || contactNameComboBox.getSelectionModel().isEmpty() ||
                aTypeTextField.getText().isEmpty() || aStartTextField.getText().isEmpty() ||
                aEndTextField.getText().isEmpty() || aCustomerIDTextField.getText().isEmpty() ||
                aUserIDTextField.getText().isEmpty()){
            Alert loginAlert;
            loginAlert = new Alert(Alert.AlertType.ERROR, "Neither text field nor selection box can be empty!");
            loginAlert.show();
        }
        else {
            try {
                Appointment appointment = new Appointment(appointmentID,
                        aTitleTextField.getText(), aDescriptionTextField.getText(), aLocationTextField.getText(),
                        contactNameComboBox.getValue(), aTypeTextField.getText(),
                        aStartTextField.getText(), aEndTextField.getText(),
                        Integer.parseInt(aCustomerIDTextField.getText()),
                        Integer.parseInt(aUserIDTextField.getText()));
                schedulingError = !appointment.addAppointmentToDataBase(contactNameComboBox.getValue(), userNameText);
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
                exception.printStackTrace();
            }

            if(!schedulingError){
                stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                // Setting the scene
                parentScene = FXMLLoader.load(getClass().getResource("/view/SchedulePage.fxml"));
                stage.setScene(new Scene(parentScene));
                stage.setTitle("Scheduling App");
                stage.show();
            }
        }
    }

    /**
     * Helper method to receive userName text from other classes.
     * @param rUserNameText
     */
    public static void receiveUserNameText(String rUserNameText){
        userNameText = rUserNameText;
    }
}
