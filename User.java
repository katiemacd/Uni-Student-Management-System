import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class User {
    private  String username;
    private String firstName;
    private String surname;
    private String email;
    private Date DOB;
    private String password;
    private String role;
    private Boolean approved;

    private Boolean active;

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getSurname(){
        return surname;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public Date getDOB(){
        return DOB;
    }

    public void setDOB(Date DOB){
        this.DOB = DOB;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }

    public User(String username, String firstName, String surname, String email,Date DOB, String password, String role, Boolean approved, Boolean active){
        this.username = username;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.DOB = DOB;
        this.password = password;
        this.role = role;
        this.approved = approved;
        this.active = active;

    }
    public User () {
    }
    static Connection sqlConn = dbConnect.getMysqlConnection();

    public void registerUser(String username, String firstName, String surname,
                             String email, Date DOB, String password, String role) throws NoSuchAlgorithmException, SQLException {
        Connection connection = sqlConn;
        String insertQuery = "INSERT INTO User (username, first_name, last_name, email, DOB, password, role, approved, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, firstName);
        preparedStatement.setString(3, surname);
        preparedStatement.setString(4, email);
        preparedStatement.setDate(5, DOB);
        preparedStatement.setString(6, password);
        preparedStatement.setString(7, role);
        preparedStatement.setBoolean(8, false);
        preparedStatement.setBoolean(9, false);
        preparedStatement.executeUpdate();
    }

    public static boolean isUserExists(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT COUNT(*) FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false; // Return false by default if an error occurs
    }

    public static boolean isEmailExists(String email) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT COUNT(*) FROM User WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static boolean isUserApproved(String username) throws SQLException {
        Connection connection = sqlConn;
        boolean approved = false;
        String query = "SELECT approved FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int approvedSQL = resultSet.getInt("approved");
                    if(approvedSQL == 1) {
                        approved = true;
                    }
                }
            }
        } return approved;
    }

    public static boolean isUserActivated(String username) throws SQLException {
        Connection connection = sqlConn;
        boolean approved = false;
        String query = "SELECT active FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int approvedSQL = resultSet.getInt("active");
                    if(approvedSQL == 1) {
                        approved = true;
                    }
                }
            }
        } return approved;
    }



    public String getPasswordForUser(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT password FROM User WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("password");
                }
            }
        }
        return null; // Return null by default if the username is not found or an error occurs
    }

    public void updatePasswordForUser(String username, String newPassword) throws SQLException
    {
        Connection connection = sqlConn;
        String query = "UPDATE User SET password = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0)
            {
                System.out.println("Password updated successfully");
            }
            else
            {
                System.out.println("User not found");
            }
        }
    }

    public static String getUserRole(String username) {
        String userRole = null;

        // Establish a database connection (You need to set up your database connection)
        Connection connection = sqlConn;
        String sql = "SELECT role FROM User WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userRole = resultSet.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userRole;
    }


    public static User getUserByUsername(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM User WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    Date dob = resultSet.getDate("DOB");
                    String password = resultSet.getString("password");
                    String role = resultSet.getString("role");
                    boolean approved = resultSet.getBoolean("approved");
                    boolean active = resultSet.getBoolean("active");

                    return new User(username, firstName, lastName, email, dob, password, role, approved, active);
                }
            }
        }
        return null;
    }
}


