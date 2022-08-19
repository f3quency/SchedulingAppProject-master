package model;

import utilities.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * represents customer object
 */
public class Customer {
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private int customerID;
    private String country;
    private String division;
    private int divisionID;
    PreparedStatement pStatement;

    /**
     * This constructor creates a customer object with the parameters below.
     * @param customerName
     * @param address
     * @param postalCode
     * @param phoneNumber
     * @param country
     * @param division
     * @param customerID
     */
    public Customer(String customerName, String address, String postalCode, String phoneNumber,
                    String country, String division, int customerID){
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.division = division;
        this.customerID = customerID;
    }

    /**
     * updates customer address
     * @param address
     */
    public void setAddress(String address) throws SQLException {this.address = address;}

    /**
     * updates customer name
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * updates customers phone number
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * updates customer postal code
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Updates customer country
     * @param country
     */
    public void setCountry(String country) {this.country = country;}

    /**
     * Updates customer division
     * @param division
     */
    public void setDivision(String division) {this.division = division;}

    public void addAppointment(Appointment appointment){

    }

    /**
     * adds customer to database
     * @param customerID
     * @param userNameText
     * @throws SQLException
     */
    public void addCustomerToDataBase(int customerID, String userNameText) throws SQLException {
        String sqlString;

        DateTimeFormatter dateTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDate = LocalDateTime.now();
        String dateNow = dateTF.format(currentDate);
        System.out.println(dateNow + " | " + currentDate);
        sqlString = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + this.division + "';";
        pStatement = JDBC.myConnection.prepareStatement(sqlString);
        ResultSet resultSet = pStatement.executeQuery();
        resultSet.next();
        divisionID = resultSet.getInt("Division_ID");

        System.out.println(customerID+", '"+customerName+"', '"+address+"', '"+postalCode+"', '"+phoneNumber+"', '" +
                dateNow+"', '"+ userNameText +"', '"+dateNow+"', '"+ userNameText+"', "+divisionID+");"); // For testing

        sqlString = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, " +
                "Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) \n" +
                "VALUES("+customerID+", '"+customerName+"', '"+address+"', '"+postalCode+"', '"+phoneNumber+"', '" +
                dateNow+"', '"+ userNameText +"', '"+dateNow+"', '"+ userNameText+"', "+divisionID+");";

        pStatement = JDBC.myConnection.prepareStatement(sqlString);
        pStatement.executeUpdate();
    }

    /**
     * deletes customer
     * @param customer
     */
    public static void deleteCustomer(Customer customer){
        String sqlString;
        String sqlString2;
        try{
            sqlString = "DELETE FROM appointments WHERE Customer_ID = '" + customer.getCustomerID() + "';";
            sqlString2 = "DELETE FROM customers WHERE Customer_ID = '" + customer.getCustomerID() + "';";
            System.out.println(sqlString);      // For debugging
            // Executing first statement (sqlString)
            PreparedStatement pStatement = JDBC.myConnection.prepareStatement(sqlString);
            pStatement.executeUpdate();

            // Executing second statement (sqlString2)
            pStatement = JDBC.myConnection.prepareStatement(sqlString2);
            pStatement.executeUpdate();
        }
        catch(SQLException sqlException){
            System.err.println(sqlException.getMessage());
        }
    }

    /**
     * gets customer ID.
     * @return
     */
    public int getCustomerID(){
        return customerID;
    }

    /**
     * gets customer name
     * @return
     */
    public String getCustomerName(){
        return customerName;
    }

    /**
     * gets address
     * @return
     */
    public String getAddress(){
        return address;
    }

    /**
     *
     * @return
     */
    public String getPostalCode(){
        return postalCode;
    }  // gets postal code

    /**
     *
     * @return
     */
    public String getPhoneNumber(){
        return phoneNumber;
    } // gets phone number

    /**
     * gets country
     * @return
     */
    public String getCountry(){
        return country;
    }

    /**
     * gets division
     * @return
     */
    public String getDivision(){
        return division;
    }

    /**
     * updates customer to database
     * @param customerID
     * @param userNameText
     * @throws SQLException
     */
    public void updateCustomerToDataBase(int customerID, String userNameText) throws SQLException {
        String sqlString;

        DateTimeFormatter dateTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDate = LocalDateTime.now();
        String dateNow = dateTF.format(currentDate);
        System.out.println(dateNow + " | " + currentDate);

        sqlString = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + this.division + "';";
        pStatement = JDBC.myConnection.prepareStatement(sqlString);
        ResultSet resultSet = pStatement.executeQuery();
        resultSet.next();
        divisionID = resultSet.getInt("Division_ID");

        System.out.println(customerID+", '"+customerName+"', '"+address+"', '"+postalCode+"', '"+phoneNumber+"', '" +
                dateNow+"', '"+ userNameText +"', '"+dateNow+"', '"+ userNameText+"', "+divisionID+");"); // For testing

        sqlString = "UPDATE customers \n" +
                "SET Customer_Name = '"+customerName+"', Address = '"+address+"', Postal_Code = '"+postalCode+
                "', Phone = '"+phoneNumber+"', Last_Update = '"+dateNow+"', Last_Updated_By = '"+userNameText+
                "', Division_ID = "+divisionID+" WHERE Customer_ID = " + customerID + ";";

        pStatement = JDBC.myConnection.prepareStatement(sqlString);
        pStatement.executeUpdate();
    }
}
