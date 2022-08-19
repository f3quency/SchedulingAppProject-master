package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.util.*;

/**
 * Basically main menu. Integrates customer and appointments using separate
 * segments but related info. Names of fields below are informative.
 */
public class SchedulePageController implements Initializable {
    @FXML
    private TableColumn<Customer, String> cAddressCol;
    @FXML
    private TableColumn<Customer, Integer> cIDCol;
    @FXML
    private TableColumn<Customer, String> cNameCol;
    @FXML
    private TableColumn<Customer, String> cPhoneNumberCol;
    @FXML
    private TableColumn<Customer, String> cPostalCodeCol;
    @FXML
    private TableColumn<Customer, String> division;
    @FXML
    private TableColumn<Customer, String> country;
    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private RadioButton monthButton;

    private static ObservableList<Customer> customers = FXCollections.observableArrayList();
    private static ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private static String userNameText = null;
    private static boolean login = false;

    @FXML
    private TableColumn<Appointment, String> aContactCol;
    @FXML
    private TableColumn<Appointment, Integer> aCustomerIDCol;
    @FXML
    private TableColumn<Appointment, String> aDescriptionCol;
    @FXML
    private TableColumn<Appointment, String> aEndDateTimeCol;
    @FXML
    private TableColumn<Appointment, Integer> aIDCol;
    @FXML
    private TableColumn<Appointment, String> aLocationCol;
    @FXML
    private TableColumn<Appointment, String> aStartDateTimeCol;
    @FXML
    private TableColumn<Appointment, String> aTitleCol;
    @FXML
    private TableColumn<Appointment, String> aTypeCol;
    @FXML
    private TableColumn<Appointment, Integer> aUserIDCol;
    @FXML
    private TableView<Appointment> appointmentTableView;

    /**
     * Initilalizes Appointment and Customer, et al.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        cIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        cNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        cPhoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        cPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        country.setCellValueFactory(new PropertyValueFactory<>("country"));
        division.setCellValueFactory(new PropertyValueFactory<>("division"));

        String sql = "SELECT customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, " +
                "customers.Customer_ID, first_level_divisions.Division, countries.Country\n" +
                "FROM customers, first_level_divisions, countries\n" +
                "WHERE customers.Division_ID = first_level_divisions.Division_ID AND \n" +
                "first_level_divisions.Country_ID = countries.Country_ID;";
        try {
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
            ResultSet resultSet = pStatement.executeQuery();

            customers.clear();          // To ensure customer data is not replicated.

            while(resultSet.next()) {
                System.out.println(resultSet.getString("Customer_Name"));  // For debugging purpose
                customers.add(new Customer(resultSet.getString("customers.Customer_Name"),
                        resultSet.getString("customers.Address"),
                        resultSet.getString("customers.Postal_Code"),
                        resultSet.getString("customers.Phone"),
                        resultSet.getString("countries.Country"),
                        resultSet.getString("first_level_divisions.Division"),
                        resultSet.getInt("customers.Customer_ID")));
            }

        } catch (SQLException sqlException) {
            System.err.println(sqlException.getMessage());
        }

        customerTableView.setItems(customers);

        initializeAppointments();

        if(!login) {
            generateInformationF();
            login = true;
        }
    }

    /**
     * Helper method to generate information asked in question A(3)[f].
     */
    private void generateInformationF() {
        String sql = "SELECT Type, Count(*), MONTH(Start) \n" +
                " FROM appointments \n" +
                " GROUP BY Type, MONTH(Start);";
        try {
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
            ResultSet resultSet = pStatement.executeQuery();
            String displayApNum = "";
            boolean noAppointments = true;

            while(resultSet.next()){
                displayApNum = displayApNum + "\n" + "Type: " + resultSet.getString("Type") +
                        ", Month: " + resultSet.getString("MONTH(Start)") + ", appointments: " +
                        resultSet.getString("Count(*)");
                noAppointments = false;
            }
            if(noAppointments){
                displayApNum = "No appointments.";
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, displayApNum);
            alert.setTitle("Total customers appointments by type and month");
            alert.show();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Initializes appointments.
     */
    private void initializeAppointments() {
        aIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        aTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        aDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        aLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        aContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        aTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        aStartDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        aEndDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        aCustomerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        aUserIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));

        String sql = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, " +
                "appointments.Location, contacts.Contact_Name, contacts.Email, appointments.Type, " +
                "appointments.Start, appointments.End, appointments.Customer_ID, appointments.User_ID \n" +
                "FROM appointments, contacts \n" +
                "WHERE appointments.Contact_ID = contacts.Contact_ID;";

        try {
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
            ResultSet resultSet = pStatement.executeQuery();
            boolean appointComingUp = false;

            appointments.clear();          // To ensure customer data is not replicated.

            while(resultSet.next()) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // To set formatter to UTC time zone.
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parsedStartDate = formatter.parse(resultSet.getString("appointments.Start"));
                Date parsedEndDate = formatter.parse(resultSet.getString("appointments.End"));
                Calendar startCalendar = Calendar.getInstance();
                Calendar endCalendar = Calendar.getInstance();
                startCalendar.setTime(parsedStartDate);
                endCalendar.setTime(parsedEndDate);

                // To set formatter to User time zone.
                formatter.setTimeZone(TimeZone.getDefault());
                String startUserTime = formatter.format(startCalendar.getTime());
                String endUserTime = formatter.format(endCalendar.getTime());
                //For debugging
                /*System.out.println(" [start] UTC TZ: " + resultSet.getString("appointments.Start") +
                        " | UserTZ: " + startUserTime);
                System.out.println(" [end] UTC TZ: " + resultSet.getString("appointments.End") +
                        " | UserTZ: " + endUserTime);*/

                Calendar now = Calendar.getInstance();
                now.setTime(new Date());

                if(monthButton.isSelected()){
                    if(startCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            startCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)){
                        appointments.add(new Appointment(resultSet.getInt("appointments.Appointment_ID"),
                                resultSet.getString("appointments.Title"),
                                resultSet.getString("appointments.Description"),
                                resultSet.getString("appointments.Location"),
                                resultSet.getString("contacts.Contact_Name"),
                                resultSet.getString("appointments.Type"),
                                startUserTime,
                                endUserTime,
                                resultSet.getInt("appointments.Customer_ID"),
                                resultSet.getInt("appointments.User_ID")));
                    }
                }
                else{
                    if(startCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            startCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                            startCalendar.get(Calendar.DAY_OF_MONTH) <= (now.get(Calendar.DAY_OF_MONTH) + 6)){
                        appointments.add(new Appointment(resultSet.getInt("appointments.Appointment_ID"),
                                resultSet.getString("appointments.Title"),
                                resultSet.getString("appointments.Description"),
                                resultSet.getString("appointments.Location"),
                                resultSet.getString("contacts.Contact_Name"),
                                resultSet.getString("appointments.Type"),
                                startUserTime,
                                endUserTime,
                                resultSet.getInt("appointments.Customer_ID"),
                                resultSet.getInt("appointments.User_ID")));
                    }
                }

                // Check if there is an appointment within 15 minutes of user login
                if(!login && startCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        startCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        startCalendar.get(Calendar.DAY_OF_MONTH) <= (now.get(Calendar.DAY_OF_MONTH))){

                    System.out.println("startCalend: " + startCalendar.get(Calendar.MINUTE) + ", " +
                            "nowCalend: " + (now.get(Calendar.MINUTE) + 15)); // Testing purpose

                    if(startCalendar.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY) &&
                            startCalendar.get(Calendar.MINUTE) <= (now.get(Calendar.MINUTE) + 15) &&
                            startCalendar.get(Calendar.MINUTE) >= now.get(Calendar.MINUTE)){
                        appointComingUp = true;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                "Appointment ID: " + resultSet.getInt("appointments.Appointment_ID") +
                                        ", Date and Time: " + startUserTime + ", is coming up!");
                        alert.show();
                    }
                }
            }
            if(!login && !appointComingUp){
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "There are no upcoming appointments.");
                alert.show();
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        appointmentTableView.setItems(appointments);
    }

    /**
     * Adds new customer.
     * @param event
     * @throws IOException
     */
    @FXML
    void addNewCustomer(ActionEvent event) throws IOException {
        AddCustomerController.receiveUserNameText(userNameText);
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        // Setting the scene
        Parent parentScene = FXMLLoader.load(getClass().getResource("/view/AddCustomerPage.fxml"));
        stage.setScene(new Scene(parentScene));
        stage.setTitle("Customer");
        stage.show();
    }

    /**
     * Deletes customer
     * @param event
     */
    @FXML
    void deleteCustomer(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you want to delete this customer?");
        Optional<ButtonType> confirmButton = alert.showAndWait();

        if(confirmButton.isPresent() && confirmButton.get() == ButtonType.OK){
            Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
            Appointment associatedAppointment = null;

            for(Appointment a : appointments){
                if(a.getCustomerID() == selectedCustomer.getCustomerID()){
                    associatedAppointment = a;
                }
            }

            if(!associatedAppointment.equals(null)){
                appointments.remove(associatedAppointment);
            }

            Customer.deleteCustomer(selectedCustomer);
            customers.remove((selectedCustomer));
            alert = new Alert(Alert.AlertType.INFORMATION, "Customer is successfully deleted.");
            alert.show();
        }
    }

    /**
     * updates customer.
     * @param event
     * @throws IOException
     */
    @FXML
    void updateCustomer(ActionEvent event) throws IOException {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if(selectedCustomer != null) {
            ModifyCustomerController.receiveCustomer(selectedCustomer);
            ModifyCustomerController.receiveUserNameText(userNameText);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            // Setting the scene
            Parent parentScene = FXMLLoader.load(getClass().getResource("/view/ModifyCustomerPage.fxml"));
            stage.setScene(new Scene(parentScene));
            stage.setTitle("Customer");
            stage.show();
        }
    }

    /**
     * Helper method.
     * @param rUserNameText
     */
    public static void recieveUserNameText(String rUserNameText){
        userNameText = rUserNameText;
    }

    /**
     * Shows only appointments for the month.
     * @param event
     */
    @FXML
    void monthButtonSelected(ActionEvent event) {
        initializeAppointments();
    }

    /**
     * Shows only appointments for the week.
     * @param event
     */
    @FXML
    void weekButtonSelected(ActionEvent event) {
        initializeAppointments();
    }

    /**
     * Loads add appointment page.
     * @param event
     * @throws IOException
     */
    @FXML
    void addAppointmentClicked(ActionEvent event) throws IOException {
        AddAppointmentController.receiveUserNameText(userNameText);
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        // Setting the scene
        Parent parentScene = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage.setScene(new Scene(parentScene));
        stage.setTitle("Appointment");
        stage.show();
    }

    /**
     * Deletes appointments.
     * @param event
     */
    @FXML
    void deleteAppointmentClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you want to delete this appointment?");
        Optional<ButtonType> confirmButton = alert.showAndWait();

        if(confirmButton.isPresent() && confirmButton.get() == ButtonType.OK){
            Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();
            Appointment.deleteAppointment(selectedAppointment);
            appointments.remove((selectedAppointment));

            alert = new Alert(Alert.AlertType.INFORMATION, "Appointment (ID: " +
                    selectedAppointment.getAppointmentID() + " | Type: " + selectedAppointment.getType() +
                    ") has been successfully deleted.");
            alert.show();
        }
    }

    /**
     * Loads modify appointments page.
     * @param event
     * @throws IOException
     */
    @FXML
    void updateAppointmentClicked(ActionEvent event) throws IOException {
        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();
        Appointment.deleteAppointment(selectedAppointment);
        appointments.remove((selectedAppointment));

        if(selectedAppointment != null) {
            ModifyAppointmentController.receiveAppointment(selectedAppointment);
            ModifyAppointmentController.receiveUserNameText(userNameText);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            // Setting the scene
            Parent parentScene = FXMLLoader.load(getClass().getResource("/view/ModifyAppointment.fxml"));
            stage.setScene(new Scene(parentScene));
            stage.setTitle("Appointment");
            stage.show();
        }
    }

    /**
     * Loads contacts schedule page.
     * @param event
     */
    @FXML
    void contactsSchedule(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("../view/ContactsSchedule.fxml"));
            stage.setTitle("");
            stage.setScene(new Scene(root, 800, 450));
            stage.setX(250);
            stage.setY(5);
            stage.show();
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Error loading contacts Schedule page: " + e.getMessage() + "!");  // For debugging
        }
    }
}
