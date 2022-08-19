package model;

import javafx.scene.control.Alert;
import utilities.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Appointment object
 */
public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String startDateTime;
    private String endDateTime;
    private int customerID;
    private int userID;

    /**
     * Appointment constructor
     * @param appointmentID
     * @param title
     * @param description
     * @param location
     * @param contact
     * @param type
     * @param startDateTime
     * @param endDateTime
     * @param customerID
     * @param userID
     */
    public Appointment(int appointmentID, String title, String description, String location, String contact,
                       String type, String startDateTime, String endDateTime, int customerID, int userID){

        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.customerID = customerID;
        this.userID = userID;
    }

    /**
     * Deletes appointment object.
     * @param selectedAppointment
     */
    public static void deleteAppointment(Appointment selectedAppointment) {
        String sqlString;
        try{
            sqlString = "DELETE FROM appointments WHERE Appointment_ID = " +
                    selectedAppointment.getAppointmentID() + ";";
            System.out.println(sqlString);      // For debugging
            // Executing first statement (sqlString)
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sqlString);
            pStatement.executeUpdate();
        }
        catch(SQLException sqlException){
            System.err.println(sqlException.getMessage());
        }
    }

    /**
     * returns appointment object.
     * @return
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    /**
     * returns title.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * gets description
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * returns description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return
     */
    public String getContact() {
        return contact;
    }

    /**
     * sets contact
     * @param contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * gets type
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * sets type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * gets start date time.
     * @return
     */
    public String getStartDateTime() {
        return startDateTime;
    }

    /**
     * sets start date time.
     * @param startDateTime
     */
    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * gets enddatetime
     * @return
     */
    public String getEndDateTime() {
        return endDateTime;
    }

    /**
     * sets endatetime
     * @param endDateTime
     */
    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * Getter
     * @return
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * setter
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     *
     * @return
     */
    public int getUserID() {
        return userID;
    }

    /**
     *
     * @param userID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Adds appointment ot database if required constraints are met.
     * @param contactName
     * @param userNameText
     * @return
     * @throws SQLException
     */
    public boolean addAppointmentToDataBase (String contactName, String userNameText) throws SQLException {
        String sqlString;
        String addUserStartD = "", addUserEndD = "";

        // Below codes are to check if appointment conflicts with database or business hours
        String sql = "SELECT appointments.Start, appointments.End " + "FROM appointments;";
        try {
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sql);
            ResultSet resultSet = pStatement.executeQuery();

            // Declaring needed variables below.
            Date parsedStartDate;
            Date parsedEndDate;
            Calendar startCalendar;
            Calendar endCalendar;
            Calendar userEndCalendar;
            Calendar userStartCalendar;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // To set formatter to user's time zone.
            formatter.setTimeZone(TimeZone.getDefault());

            userEndCalendar = Calendar.getInstance();
            userStartCalendar = Calendar.getInstance();
            parsedStartDate = formatter.parse(startDateTime);  // to get user start time in terms of date
            parsedEndDate = formatter.parse(endDateTime);      // so I can use it to create Calender object
            userStartCalendar.setTime(parsedStartDate);
            userEndCalendar.setTime(parsedEndDate);

            System.out.println("Parsed Start Date: " + parsedStartDate);

            // Creating user start and end date using LocalDate (As shown in lecture)
            LocalDate startLD = LocalDate.of(userStartCalendar.get(Calendar.YEAR), // Added 1 to month because month starts from 0
                    userStartCalendar.get(Calendar.MONTH) + 1, userStartCalendar.get(Calendar.DAY_OF_MONTH));
            LocalDate endLD = LocalDate.of(userEndCalendar.get(Calendar.YEAR),  // Added 1 to month because month starts from 0
                    userEndCalendar.get(Calendar.MONTH) + 1, userEndCalendar.get(Calendar.DAY_OF_MONTH));
            // Creating user start and end time using LocalTime objects (As shown in lecture)
            LocalTime startLT = LocalTime.of(userStartCalendar.get(Calendar.HOUR_OF_DAY),
                    userStartCalendar.get(Calendar.MINUTE), userStartCalendar.get(Calendar.SECOND));
            LocalTime endLT = LocalTime.of(userEndCalendar.get(Calendar.HOUR_OF_DAY),
                    userEndCalendar.get(Calendar.MINUTE), userEndCalendar.get(Calendar.SECOND));
            // Creating user start and end local date and time
            System.out.println("Start LocalDate: " + LocalDateTime.of(startLD, startLT));
            LocalDateTime startLDT = LocalDateTime.of(startLD, startLT);
            LocalDateTime endLDT = LocalDateTime.of(endLD, endLT);
            // Setting userZoneID to user's zone ID
            ZoneId userZoneID = ZoneId.systemDefault();
            // creating user start and end zoned date and time
            ZonedDateTime  userStartZDT = ZonedDateTime.of(startLDT, userZoneID);
            ZonedDateTime  userEndZDT = ZonedDateTime.of(endLDT, userZoneID);
            // parsing and exempting unneeded characters like 'T' and 'Z'
            addUserStartD = userStartZDT.toInstant().toString().replace('T',
                    ' ').replace("Z", "");
            addUserEndD = userEndZDT.toInstant().toString().replace('T',
                    ' ').replace("Z", "");
            parsedStartDate = formatter.parse(addUserStartD);
            parsedEndDate = formatter.parse(addUserEndD);
            userStartCalendar.setTime(parsedStartDate);
            userEndCalendar.setTime(parsedEndDate);

            // Below print statements are for debugging
            System.out.println("Sys startcalendgettime: " + userStartCalendar.getTime());
            System.out.println("Original HR of day: " + startDateTime + ", HR of day: " +
                    userStartCalendar.get(Calendar.HOUR_OF_DAY)); // For testing

            if((userStartCalendar.get(Calendar.HOUR_OF_DAY) > 3 &&
                    userStartCalendar.get(Calendar.HOUR_OF_DAY) < 13) ||
                    (userEndCalendar.get(Calendar.HOUR_OF_DAY) < 13 &&
                            userEndCalendar.get(Calendar.HOUR_OF_DAY) > 3)){

                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Appointment is outside business hours!");
                alert.show();

                return false;
            }

            while (resultSet.next()) {
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // To set formatter to UTC time zone.
                formatter.setTimeZone(TimeZone.getDefault());
                parsedStartDate = formatter.parse(resultSet.getString("appointments.Start"));
                parsedEndDate = formatter.parse(resultSet.getString("appointments.End"));
                startCalendar = Calendar.getInstance();
                endCalendar = Calendar.getInstance();
                startCalendar.setTime(parsedStartDate);
                endCalendar.setTime(parsedEndDate);

                System.out.println("\n Y: " + (userStartCalendar.get(Calendar.YEAR) + " == " +
                        startCalendar.get(Calendar.YEAR)));
                System.out.println("M: " + (userStartCalendar.get(Calendar.MONTH) + "==" +
                        startCalendar.get(Calendar.MONTH)));
                System.out.println("D: " + (userStartCalendar.get(Calendar.DAY_OF_MONTH) + "==" +
                        startCalendar.get(Calendar.DAY_OF_MONTH)));
                System.out.println("H: " + (userStartCalendar.get(Calendar.HOUR_OF_DAY) + "==" +
                        startCalendar.get(Calendar.HOUR_OF_DAY)));


                if(userStartCalendar.get(Calendar.YEAR) == startCalendar.get(Calendar.YEAR) &&
                        userStartCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH) &&
                        userStartCalendar.get(Calendar.DAY_OF_MONTH) == startCalendar.get(Calendar.DAY_OF_MONTH)){
                    if((userStartCalendar.get(Calendar.HOUR_OF_DAY) >= startCalendar.get(Calendar.HOUR_OF_DAY) &&
                            userStartCalendar.get(Calendar.HOUR_OF_DAY) <= endCalendar.get(Calendar.HOUR_OF_DAY)) ||
                            (userEndCalendar.get(Calendar.HOUR_OF_DAY) >= startCalendar.get(Calendar.HOUR_OF_DAY) &&
                                    userEndCalendar.get(Calendar.HOUR_OF_DAY) <= endCalendar.get(Calendar.HOUR_OF_DAY))){
                        if((userStartCalendar.get(Calendar.MINUTE) >= startCalendar.get(Calendar.MINUTE) &&
                                userStartCalendar.get(Calendar.MINUTE) <= endCalendar.get(Calendar.MINUTE)) ||
                                (userEndCalendar.get(Calendar.MINUTE) >= startCalendar.get(Calendar.MINUTE) &&
                                        userEndCalendar.get(Calendar.MINUTE) <= endCalendar.get(Calendar.MINUTE))) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "Appointment conflicts with another appointment!");
                            alert.show();
                            return false;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        DateTimeFormatter dateTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDate = LocalDateTime.now();
        String dateNow = dateTF.format(currentDate);
        System.out.println(dateNow + " | " + currentDate);
        sqlString = "SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + contactName + "';";
        PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sqlString);
        ResultSet resultSet = pStatement.executeQuery();
        resultSet.next();
        int contactID = resultSet.getInt("Contact_ID");

        System.out.println("Contact ID: " + contactID); // For testing
        System.out.println("startDate=> " + addUserStartD + " | " + "endDate=> " + addUserEndD);

        sqlString = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, " +
                "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, " +
                "User_ID, Contact_ID) \n" +
                "VALUES("+appointmentID+", '"+title+"', '"+description+"', '"+location+"', '"+type+"', '" +
                addUserStartD+"', '"+ addUserEndD +"', '"+dateNow+"', '"+ userNameText+"', '"+dateNow+"', '" +
                userNameText + "', " + customerID + ", " + userID + ", " + contactID + ");";

        pStatement = JDBC.myConnection.prepareStatement(sqlString);
        pStatement.executeUpdate();

        return true;
    }
}
