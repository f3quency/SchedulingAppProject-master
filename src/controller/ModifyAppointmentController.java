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
import utilities.JDBC;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

/**
 * Class for controlling and modifying appointments.
 * Field names are self explanatory.
 */
public class ModifyAppointmentController implements Initializable {
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
    private static String userNameText;

    private static Appointment appointment;

    /**
     * Initializes and prepopulates fields
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeContactName();
        try {
            initializeAppointmentTextFields();
        } catch (ParseException e) {
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes appointment fields.
     * @throws ParseException
     */
    private void initializeAppointmentTextFields() throws ParseException {
        aCustomerIDTextField.setText(appointment.getCustomerID() + "");
        aDescriptionTextField.setText(appointment.getDescription());
        aIDTextField.setText(appointment.getAppointmentID() + "");
        aTitleTextField.setText(appointment.getTitle());
        aLocationTextField.setText(appointment.getLocation());
        contactNameComboBox.getSelectionModel().select(appointment.getContact());
        aTypeTextField.setText(appointment.getType());
        aStartTextField.setText(appointment.getStartDateTime());
        aEndTextField.setText(appointment.getEndDateTime());
        aUserIDTextField.setText(appointment.getUserID() + "");
    }

    /**
     * Initializes contact name.
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
     * Goes to previous menu without making any change to appointments.
     * @param event
     * @throws IOException
     */
    @FXML
    void cancelButtonClicked(ActionEvent event) throws IOException, SQLException {
        appointment.addAppointmentToDataBase(contactNameComboBox.getValue(), userNameText);

        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        // Setting the scene
        parentScene = FXMLLoader.load(getClass().getResource("/view/SchedulePage.fxml"));
        stage.setScene(new Scene(parentScene));
        stage.setTitle("Scheduling App");
        stage.show();
    }

    /**
     * Method for saving collected information in fields.
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
                appointment.setTitle(aTitleTextField.getText());
                appointment.setDescription(aDescriptionTextField.getText());
                appointment.setLocation(aLocationTextField.getText());
                appointment.setContact(contactNameComboBox.getValue());
                appointment.setType(aTypeTextField.getText());
                appointment.setStartDateTime(aStartTextField.getText());
                appointment.setEndDateTime(aEndTextField.getText());
                appointment.setCustomerID(Integer.parseInt(aCustomerIDTextField.getText()));
                appointment.setUserID(Integer.parseInt(aUserIDTextField.getText()));
                schedulingError = !appointment.addAppointmentToDataBase(contactNameComboBox.getValue(), userNameText);
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
                exception.printStackTrace();
            }

            if(!schedulingError) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                // Setting the scene
                parentScene = FXMLLoader.load(getClass().getResource("/view/SchedulePage.fxml"));
                stage.setScene(new Scene(parentScene));
                stage.setTitle("Scheduling App");
                stage.show();
            }
        }
    }

    /**
     * Helper methods for receiving appointments from other classes.
     * @param recievedAppointment
     */
    public static void receiveAppointment(Appointment recievedAppointment){
        appointment = recievedAppointment;
    }

    /**
     * Helper method for recieving username from other classes.
     * @param rUserNameText
     */
    public static void receiveUserNameText(String rUserNameText){
        userNameText = rUserNameText;
    }
}
