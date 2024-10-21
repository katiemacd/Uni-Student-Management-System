import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lecturer extends User {
    public static String getLecturerID(String username)
    {
        String lecturerID = null; // Default value if not found
        try
        {
            // SQL query to select lecturerID based on username
            String sql = "SELECT lecturerID FROM Lecturer2 WHERE username = ?";
            // Prepare the SQL statement
            try (PreparedStatement statement = sqlConn.prepareStatement(sql))
            {
                // Set the parameter in the prepared statement
                statement.setString(1, username);
                // Execute the query
                try (ResultSet resultSet = statement.executeQuery())
                {
                    // Check if a result was found
                    if (resultSet.next())
                    {
                        // Retrieve the lecturerID from the result set
                        lecturerID = resultSet.getString("lecturerID");
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            // Handle any SQL exceptions
            ex.printStackTrace();
        }
        return lecturerID;
    }

    public static String getModuleID(String lecturerID)
    {
        String moduleID = null; // Default value if not found
        try
        {
            // SQL query to select moduleID based on lecturerID
            String sql = "SELECT moduleID FROM Modules2 WHERE lecturerID = ?";
            // Prepare the SQL statement
            try (PreparedStatement statement = sqlConn.prepareStatement(sql))
            {
                // Set parameter in prepared statement
                statement.setString(1, lecturerID);
                // Execute the query
                try (ResultSet resultSet = statement.executeQuery())
                {
                    // Check if a result was found
                    if (resultSet.next())
                    {
                        // Retrieve the moduleID from the result set
                        moduleID = resultSet.getString("moduleID");
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            // Handle any SQL exceptions
            ex.printStackTrace();
        }
        return moduleID;
    }

    public static void updateModuleInformation(String username, String newModuleName, String newModuleDescription) throws SQLException
    {
        Connection connection = sqlConn;
        String query = "UPDATE Modules2 SET moduleName = ?, moduleDescription = ? WHERE lecturerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, newModuleName);
            statement.setString(2, newModuleDescription);
            statement.setString(3, getLecturerID(username));

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0)
            {
                System.out.println("Module information updated successfully");
            }
            else
            {
                System.out.println("Failed to update module information");
            }
        }
    }

    public static void uploadModuleMaterials(String username, File file, int semester, int week, String materialType, UUID uniqueIdentifier) throws SQLException, IOException {
        Connection connection = sqlConn;
        String query;

        if ("lecture".equals(materialType)) {
            query = "INSERT INTO moduleMaterial (Unique_Identifier, module_ID, Semester, Week, Lecture_Material) " +
                    "VALUES (?, (SELECT moduleID FROM Modules2 WHERE lecturerID = ? LIMIT 1), ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE Lecture_Material = VALUES(Lecture_Material)";
        } else if ("lab".equals(materialType)) {
            query = "INSERT INTO moduleMaterial (Unique_Identifier, module_ID, Semester, Week, Lab_Material) " +
                    "VALUES (?, (SELECT moduleID FROM Modules2 WHERE lecturerID = ? LIMIT 1), ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE Lab_Material = VALUES(Lab_Material)";
        } else {
            throw new IllegalArgumentException("Material type must be 'lecture' or 'lab': " + materialType);
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            statement.setString(1, uniqueIdentifier.toString());
            statement.setString(2, getLecturerID(username));
            statement.setInt(3, semester);
            statement.setInt(4, week);
            statement.setBytes(5, fileContent);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Module materials uploaded successfully");
            } else {
                System.out.println("No modules found for the lecturer in the specified week");
            }
        }
    }

    static String getFullNameByUsername(String username)
    {
        try
        {
            // SQL query to select first and last name for a specific username
            String sql = "SELECT first_name, last_name FROM User WHERE username = ?";
            // Prepare the SQL statement
            try (PreparedStatement statement = sqlConn.prepareStatement(sql))
            {
                // Set parameter in prepared statement
                statement.setString(1, username);
                // Execute the query
                try (ResultSet resultSet = statement.executeQuery())
                {
                    // Process the result set and return the full name
                    if (resultSet.next())
                    {
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        return firstName + " " + lastName;
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            // Handle any SQL exceptions
            ex.printStackTrace();
        }
        return ""; // Return an empty string if no match is found
    }

    static String getUsernameByFullName(String fullName)
    {
        try
        {
            // Split the full name into first name and last name
            String[] names = fullName.split("\\s+");
            String firstName = names[0];
            String lastName = names.length > 1 ? names[1] : "";

            // SQL query to select username for a specific full name
            String sql = "SELECT username FROM User WHERE first_name = ? AND last_name = ?";

            // Prepare the SQL statement
            try (PreparedStatement statement = sqlConn.prepareStatement(sql))
            {
                // Set the parameters in the prepared statement
                statement.setString(1, firstName);
                statement.setString(2, lastName);

                // Execute the query
                try (ResultSet resultSet = statement.executeQuery())
                {
                    // Process the result set and return the username
                    if (resultSet.next())
                    {
                        return resultSet.getString("username");
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            // Handle any SQL exceptions
            ex.printStackTrace();
        }
        return ""; // Return an empty string if no match is found
    }

    public static List<String> getAllStudentInfo(String moduleID) {
        List<String> studentInfoList = new ArrayList<>();
        try {
            String courseIDQuery = "SELECT courseID FROM Modules2 WHERE moduleID = ?";

            // Prepare the SQL statement
            try (PreparedStatement courseIDStatement = sqlConn.prepareStatement(courseIDQuery)) {
                // Set parameter in prepared statement
                courseIDStatement.setString(1, moduleID);

                // Execute the query to get courseID
                try (ResultSet courseIDResultSet = courseIDStatement.executeQuery()) {
                    if (courseIDResultSet.next()) {
                        int courseID = courseIDResultSet.getInt("courseID");

                        // SQL query to select student usernames for a specific course
                        String studentQuery = "SELECT username FROM Student2 WHERE courseID = ?";

                        // Prepare the SQL statement
                        try (PreparedStatement studentStatement = sqlConn.prepareStatement(studentQuery)) {
                            // Set parameter in prepared statement
                            studentStatement.setInt(1, courseID);

                            // Execute the query
                            try (ResultSet resultSet = studentStatement.executeQuery()) {
                                // Process the result set and add student names and info to the list
                                while (resultSet.next()) {
                                    String username = resultSet.getString("username");
                                    String fullName = getFullNameByUsername(username);
                                    studentInfoList.add(fullName);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            // Handle any SQL exceptions
            ex.printStackTrace();
        }
        return studentInfoList;
    }


    public static void updateExamRecords(String studentUsername, String labResult, String examResult, String overallMark, String moduleID) throws SQLException {
        try {
            // Get the studentID and markID for the given username
            String username = getUsernameByFullName(studentUsername);
            String studentID = getStudentIDByUsername(username);
            String markID = getMarkIDByStudentID(studentID);

            // Check if the record already exists in the Mark table
            if (markID != null) {
                // If it exists, update the existing record
                updateExistingRecord(studentID, moduleID, labResult, examResult, overallMark);
            } else {
                // If it doesn't exist, insert a new record
                insertNewRecord(studentID, moduleID, labResult, examResult, overallMark);
            }
        } catch (SQLException ex) {
            // Handle any SQL exceptions
            throw ex;
        }
    }


    // Method to get studentID based on username
    static String getStudentIDByUsername(String studentUsername) throws SQLException
    {
        String sql = "SELECT studentID FROM Student2 WHERE username = ?";
        try (PreparedStatement statement = sqlConn.prepareStatement(sql))
        {
            statement.setString(1, studentUsername);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return resultSet.getString("studentID");
                }
                else
                {
                    throw new SQLException("Student not found with username: " + studentUsername);
                }
            }
        }
    }

    // Method to get markID based on studentID
    private static String getMarkIDByStudentID(String studentID) throws SQLException
    {
        String sql = "SELECT MarkID FROM Marks2 WHERE studentID = ?";
        try (PreparedStatement statement = sqlConn.prepareStatement(sql))
        {
            statement.setString(1, studentID);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return resultSet.getString("markID");
                }
                else
                {
                    return null; // Indicates that the record doesn't exist
                }
            }
        }
    }


    // Method to update an existing record in the Marks2 table
    private static void updateExistingRecord(String studentID, String moduleID, String labResult, String examResult, String overallMark) throws SQLException {
        String existingMarkID = getMarkIDByStudentIDAndModuleID(studentID, moduleID);

        if (existingMarkID != null) {
            // If it exists, update the existing record
            String sqlUpdate = "UPDATE Marks2 SET labResult = ?, examResult = ?, overallMarks = ? " +
                    "WHERE MarkID = ?";
            try (PreparedStatement statement = sqlConn.prepareStatement(sqlUpdate)) {
                statement.setString(1, labResult);
                statement.setString(2, examResult);
                statement.setString(3, overallMark);
                statement.setString(4, existingMarkID);
                statement.executeUpdate();
            }
        } else {
            // If it doesn't exist, insert a new record
            insertNewRecord(studentID, moduleID, labResult, examResult, overallMark);
        }
    }

    // Method to get markID based on studentID and moduleID
    private static String getMarkIDByStudentIDAndModuleID(String studentID, String moduleID) throws SQLException {
        String sql = "SELECT MarkID FROM Marks2 WHERE studentID = ? AND moduleID = ?";
        try (PreparedStatement statement = sqlConn.prepareStatement(sql)) {
            statement.setString(1, studentID);
            statement.setString(2, moduleID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("MarkID");
                } else {
                    return null; // Indicates that the record doesn't exist
                }
            }
        }
    }

    // Method to insert a new record into the Marks2 table
    private static void insertNewRecord(String studentID, String moduleID, String labResult, String examResult, String overallMark) throws SQLException {
        String sqlInsert = "INSERT INTO Marks2 (studentID, moduleID, labResult, examResult, overallMarks) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statementInsert = sqlConn.prepareStatement(sqlInsert)) {
            statementInsert.setString(1, studentID);
            statementInsert.setString(2, moduleID);
            statementInsert.setString(3, labResult);
            statementInsert.setString(4, examResult);
            statementInsert.setString(5, overallMark);
            statementInsert.executeUpdate();
        }
    }

    public static boolean isLecturerExists(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT COUNT(*) FROM Lecturer2 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static void setUpLecturer(String username) throws NoSuchAlgorithmException, SQLException {
        Connection connection = sqlConn;
        String insertQuery = "INSERT INTO Lecturer2 (username) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
    }

    public static void addQualification(String username, String qualification) throws NoSuchAlgorithmException, SQLException {
        Connection connection = sqlConn;
        String addQualificationQuery = "UPDATE Lecturer2 SET qualification = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(addQualificationQuery)) {
            preparedStatement.setString(1, qualification);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        }
    }
}
