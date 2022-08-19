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
 * This class helps modify costumer info, by collecting changes and checking constraints.
 */
public class ModifyCustomerController implements Initializable {
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

    private static Customer customer;

    /**
     * Prepopulates customer info.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addressTextField.setText(customer.getAddress());
        idTextField.setText(customer.getCustomerID() + "");
        nameTextField.setText(customer.getCustomerName());
        phoneTextField.setText(customer.getPhoneNumber());
        postalCodeTextField.setText(customer.getPostalCode());

        customerID = customer.getCustomerID();

        initializeCountry();
        countryComboBox.getSelectionModel().select(customer.getCountry());
        selectedCountryOnAction(new ActionEvent());
        divisionComboBox.getSelectionModel().select(customer.getDivision());
    }

    /**
     * Activates when country is selected.
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
     * Initializes division
     * @throws SQLException
     */
    private void initializeDivision() throws SQLException {
        String country = countryComboBox.getValue();

        String sql = "SELECT Division "
                + "FROM first_level_divisions, countries "
                + "WHERE first_level_divisions.Country_ID = countries.Country_ID "
                + "AND countries.Country = '" + country + "'";

        PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
        ResultSet resultSet = pStatement.executeQuery();
        divisionComboBox.getItems().clear();
        try {
            while (resultSet.next()) {
                divisionComboBox.getItems().add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    public void selectDivisionOnAction(ActionEvent actionEvent) {
    }

    /**
     * Saves Customer if constraints are met.
     * @param event
     * @throws IOException
     */
    public void saveButtonClicked(ActionEvent event) throws IOException {
        if(nameTextField.getText().isEmpty() || addressTextField.getText().isEmpty() ||
                postalCodeTextField.getText().isEmpty() || phoneTextField.getText().isEmpty() ||
                countryComboBox.getSelectionModel().isEmpty() || divisionComboBox.getSelectionModel().isEmpty()){
            Alert loginAlert;
            loginAlert = new Alert(Alert.AlertType.ERROR, "Neither text field nor selection box can be empty!");
            loginAlert.show();
        }
        else {
            try {
                customer.setCustomerName(nameTextField.getText());
                customer.setAddress(addressTextField.getText());
                customer.setPostalCode(postalCodeTextField.getText());
                customer.setPhoneNumber(phoneTextField.getText());
                customer.setCountry(countryComboBox.getValue());
                customer.setDivision(divisionComboBox.getValue());

                customer.updateCustomerToDataBase(customerID, userNameText);
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
     * Goes to previous menu without making any change.
     * @param event
     * @throws IOException
     */
    public void cancelButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        // Setting the scene
        parentScene = FXMLLoader.load(getClass().getResource("/view/SchedulePage.fxml"));
        stage.setScene(new Scene(parentScene));
        stage.setTitle("Scheduling App");
        stage.show();
    }

    /**
     * Helper method for initializing country
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
     * Helper method. Name is self explanatory.
     * @param recievedCustomer
     */
    public static void receiveCustomer(Customer recievedCustomer){
        customer = recievedCustomer;
    }

    /**
     * Helper method
     * @param rUserNametext
     */
    public static void receiveUserNameText(String rUserNametext){
        userNameText = rUserNametext;
    }
}
