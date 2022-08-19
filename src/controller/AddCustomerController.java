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
import model.Customer;
import utilities.JDBC;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * This class implements functions to add a new customer
 */
public class AddCustomerController implements Initializable {
    @FXML
    private TextField addressTextField;
    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private ComboBox<String> divisionComboBox;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField postalCodeTextField;

    Stage stage = new Stage();
    Parent parentScene;
    String sqlString;
    private int customerID;
    private static String userNameText;

    /**
     * Initializes required entities
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            sqlString = "SELECT MAX(Customer_ID) FROM customers";
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sqlString);
            ResultSet resultSet = pStatement.executeQuery();
            resultSet.next();  // moving cursor to last row
            //NOTE!! Create and assign new customer ID in SchedulePageController
            customerID = resultSet.getInt("MAX(Customer_ID)") + 1;
        }
        catch(SQLException sqlException){
            System.err.println(sqlException.getMessage());
        }
        idTextField.setText(customerID + "");

        initializeCountry();
    }

    @FXML
    void selectDivisionOnAction(ActionEvent event) {
    }

    /**
     * This method is executed when country is selected. Prepopulates Division.
     * @param event
     */
    @FXML
    void selectedCountryOnAction(ActionEvent event) {
        try {
            initializeDivision();
        } catch (SQLException sqlException) {
            System.err.println(sqlException.getMessage());
        }
    }

    /**
     * Initializes Division
     * @throws SQLException
     */
    private void initializeDivision() throws SQLException {
        String country = countryComboBox.getValue();

        String sql = "SELECT Division "
                + "FROM first_level_divisions, countries "
                + "WHERE first_level_divisions.Country_ID = countries.Country_ID "
                + "AND countries.Country = '" + country + "'";

        PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
        ResultSet resultSet = resultSet = pStatement.executeQuery();
        divisionComboBox.getItems().clear();
        try {
            while (resultSet.next()) {
                divisionComboBox.getItems().add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    /**
     * Helper method to initialize country
     */
    private void initializeCountry() {
        sqlString = "SELECT Country FROM countries";
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
        countryComboBox.getItems().clear();
        try {
            while (resultSet.next()) {
                countryComboBox.getItems().add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * reverts to previous menu without making a change.
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
     * Saves Cutomer if constraints are met.
     * @param event
     * @throws IOException
     */
    @FXML
    void saveButtonClicked(ActionEvent event) throws IOException {
        if(nameTextField.getText().isEmpty() || addressTextField.getText().isEmpty() ||
                postalCodeTextField.getText().isEmpty() || phoneTextField.getText().isEmpty() ||
                countryComboBox.getSelectionModel().isEmpty() || divisionComboBox.getSelectionModel().isEmpty()){
            Alert loginAlert;
            loginAlert = new Alert(Alert.AlertType.ERROR, "Neither text field nor selection box can be empty!");
            loginAlert.show();
        }
        else {
            try {
                Customer customer = new Customer(nameTextField.getText(), addressTextField.getText(),
                        postalCodeTextField.getText(), phoneTextField.getText(),
                        countryComboBox.getValue(), divisionComboBox.getValue(),
                        customerID);
                customer.addCustomerToDataBase(customerID, userNameText);
            } catch (SQLException sqlException) {
                System.err.println(sqlException.getMessage());
                sqlException.printStackTrace();
            }

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            // Setting the scene
            parentScene = FXMLLoader.load(getClass().getResource("/view/SchedulePage.fxml"));
            stage.setScene(new Scene(parentScene));
            stage.setTitle("Scheduling App");
            stage.show();
        }
    }

    /**
     * Helper method to receive user name.
     * @param rUserNametext
     */
    public static void receiveUserNameText(String rUserNametext){
        userNameText = rUserNametext;
    }
}
