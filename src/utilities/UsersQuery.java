package utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Helps mostly with sql login query.
 */
public abstract class UsersQuery {

    /**
     * Created this method incase we need to add additional users in the future.
     * Hence, method currently returns zero as a default. Also, parameter is empty since this method is
     * currently redundant.
     * @return
     */
    public static int insert(){
        return 0;
    }

    /**
     * This method helps select a user ID and password, to help compare user login credentials with the database
     * user credentials (User_ID and Password).
     */
    public static boolean login(String userName, String userPassword) {
        String sql = "SELECT * FROM users WHERE User_Name = '" + userName + "' AND Password = '" + userPassword + "';";
        PreparedStatement pStatement = null;
        try {
            pStatement = JDBC.myConnection.prepareStatement(sql);
        } catch (SQLException sqlException) {
            System.err.println("Login Prepared statement error");
        }
        ResultSet resultSet = null;
        try {
            resultSet = pStatement.executeQuery();
        } catch (SQLException sqlException) {
            System.err.println("Login ExecuteQuery error!");
        }
        System.out.println("Successfully executed login query!");
        boolean loginSuccess = false;
        boolean resultHasNext = false;
        try {
            resultHasNext = resultSet.next();
        } catch (SQLException sqlException) {
            System.err.println("LoginQuery resultSet.next() error!");
        }

        System.out.println("result Has next: " + resultHasNext);

        if (resultHasNext) {
            loginSuccess = true;
            System.out.println("UserID: " + userPassword + " | " + "UserPassword: " + userPassword); // For debugging purpose.
        }

        System.out.println("Login Success: " + loginSuccess);
        return loginSuccess;
    }
}
