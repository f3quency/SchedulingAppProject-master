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
import model.MyPrintInterface;
import utilities.JDBC;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Prepopulates contacts and their respective appointment fields.
 */
public class ContactsScheduleController implements Initializable {
    @FXML
    private TableColumn<Appointment, Integer> appointmentIDCol;
    @FXML
    private TableColumn<Appointment, String> contactNameCol;
    @FXML
    private TableColumn<Appointment, Integer> customerIDCol;
    @FXML
    private TableColumn<Appointment, String> descriptionCol;
    @FXML
    private TableColumn<Appointment, String> endDateCol;
    @FXML
    private TableColumn<Appointment, String> startDateCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableView<Appointment> contactTableView;

    private static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    @FXML
    private Label numOfContactsLabel;
    int totalNumOfConts = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        String sql = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, " +
                "contacts.Contact_Name, appointments.Type, appointments.Location, " +
                "appointments.Start, appointments.End, appointments.Customer_ID, appointments.User_ID\n" +
                "FROM appointments, contacts \n" +
                "WHERE appointments.Contact_ID = contacts.Contact_ID;";

        try {
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
            ResultSet resultSet = pStatement.executeQuery();

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
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        contactTableView.setItems(appointments);

        sql = "SELECT COUNT(*) FROM contacts;";
        try {
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
            ResultSet resultSet = pStatement.executeQuery();

            resultSet.next();
            totalNumOfConts = resultSet.getInt("COUNT(*)");
            // Used lambda expression below to concat and set num of contacts, string.
            MyPrintInterface message = s -> "Total Number of Contacts in database: " + s;
            numOfContactsLabel.setText(message.myMessage(totalNumOfConts + ""));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * goes back to main menu aka previous screen.
     * @param event
     * @throws IOException
     */
    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        // Setting the scene
        Parent parentScene = FXMLLoader.load(getClass().getResource("/view/SchedulePage.fxml"));
        stage.setScene(new Scene(parentScene));
        stage.setTitle("Scheduling App");
        stage.show();
    }
}
