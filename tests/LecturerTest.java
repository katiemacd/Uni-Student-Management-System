import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LecturerTest {

    private User testLecturer;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;


    @BeforeEach
    void setUp() throws SQLException, NoSuchAlgorithmException {
        if (User.isUserExists("testLecturer")) {
            try {
                deleteUser("testLecturer");
            } catch (SQLException e) {
                fail("Exception thrown during user deletion: " + e.getMessage());
            }
        }

        testLecturer = new User("testLecturer", "Jane", "Smith", "jane.smith@example.com",
                Date.valueOf("1990-01-01"), "password123", "Lecturer", false, false);

        try {
            testLecturer.registerUser("testLecturer", "Jane", "Smith", "jane.smith@example.com",
                    Date.valueOf("1990-01-01"), "password123", "Lecturer");

            assertTrue(User.isUserExists("testLecturer"));
        } catch (NoSuchAlgorithmException | SQLException e) {
            fail("Exception thrown during registration: " + e.getMessage());
        }

        try {
            Lecturer.setUpLecturer("testLecturer");
            insertModule("testLecturer", "Sample Module", "Sample Module Description");
        } catch (SQLException | NoSuchAlgorithmException e) {
            fail("Exception during test data setup: " + e.getMessage());
        }
    }

    private void insertModule(String username, String moduleName, String moduleDescription) throws SQLException {
        Connection connection = dbConnect.getMysqlConnection();
        int lecturerID = Integer.parseInt(Lecturer.getLecturerID(username));
        String moduleID = "CS111";
        int courseID = 2;
        String insertQuery = "INSERT INTO Modules2 (moduleID, moduleName, moduleDescription, lecturerID, courseID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Set the lecturerID in the prepared statement
            preparedStatement.setString(1, moduleID);
            // Set the module name in the prepared statement
            preparedStatement.setString(2, moduleName);
            // Set the module description in the prepared statement
            preparedStatement.setString(3, moduleDescription);
            preparedStatement.setInt(4, lecturerID);
            preparedStatement.setInt(5, courseID);

            // Execute the insert query
            preparedStatement.executeUpdate();
        }
    }

    // Helper method to delete a user by username
    private void deleteUser(String username) throws SQLException {
        Connection connection = dbConnect.getMysqlConnection();
        String deleteQuery = "DELETE FROM User WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }
    }


    @AfterEach
    void cleanUp() {
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM moduleMaterial WHERE module_ID = ? AND semester = ? AND week = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "CS111");
                preparedStatement.setInt(2, 1);
                preparedStatement.setInt(3, 2);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Marks2 WHERE studentID = ? AND moduleID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, Lecturer.getStudentIDByUsername("ellenhazlett"));
                preparedStatement.setString(2, "CS111");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Modules2 WHERE lecturerID IN (SELECT lecturerID FROM Lecturer2 WHERE username = ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testLecturer");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            // Assuming the cleanup is successful if no exception is thrown
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Lecturer2 WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testLecturer");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            // Assuming the cleanup is successful if no exception is thrown
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM User WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testLecturer");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
    }


    @Test
    void getLecturerID() {
        // Assuming a lecturer with username "testLecturer" exists
        String lecturerID = Lecturer.getLecturerID("testLecturer");
        assertNotNull(lecturerID);
        assertTrue(lecturerID.length() > 0);

    }

    @Test
    void getModuleID() {
        // Assuming a lecturer with username "testLecturer" exists
        String lecturerID = Lecturer.getLecturerID("testLecturer");
        String moduleID = Lecturer.getModuleID(lecturerID);
        assertNotNull(moduleID);
        assertTrue(moduleID.length() > 0);
    }


    @Test
    void testUpdateModuleInformation() {
        // Redirect System.out to capture printed messages
        System.setOut(new PrintStream(outContent));

        try {
            Lecturer.updateModuleInformation("testLecturer", "NewModule", "NewModuleDescription");

            // Check if the expected message is printed
            assertEquals("Module information updated successfully" + System.lineSeparator(), outContent.toString());

        } catch (SQLException e) {
            fail("Exception thrown during testUpdateModuleInformation: " + e.getMessage());
        } finally {
            // Reset System.out to the original PrintStream
            System.setOut(originalOut);
        }
    }


    @Test
    void testUploadModuleMaterials()
    {
        try
        {
            File file = new File("testing.txt");
            int semester = 1;
            int week = 2;
            UUID uniqueIdentifier = UUID.randomUUID();

            Lecturer.uploadModuleMaterials("testLecturer", file, semester, week, "lecture", uniqueIdentifier);
            int lectureRowsUpdated = checkIfMaterialsUploaded("testLecturer", semester, week, "lecture", uniqueIdentifier);
            assertTrue(lectureRowsUpdated > 0, "Lecture materials should be uploaded successfully");

            Lecturer.uploadModuleMaterials("testLecturer", file, semester, week, "lab", uniqueIdentifier);
            int labRowsUpdated = checkIfMaterialsUploaded("testLecturer", semester, week, "lab", uniqueIdentifier);
            assertTrue(labRowsUpdated > 0, "Lab materials should be uploaded successfully");
        }
        catch (SQLException | IOException e)
        {
            fail("Exception thrown during uploadModuleMaterials test: " + e.getMessage());
        }
    }

    private int checkIfMaterialsUploaded(String lecturerUsername, int semester, int week, String materialType, UUID uniqueIdentifier) throws SQLException
    {
        String query;
        if ("lecture".equals(materialType))
        {
            query = "SELECT COUNT(*) FROM moduleMaterial WHERE Unique_Identifier = ? AND semester = ? AND week = ? AND Lecture_Material IS NOT NULL";
        }
        else if ("lab".equals(materialType))
        {
            query = "SELECT COUNT(*) FROM moduleMaterial WHERE Unique_Identifier = ? AND semester = ? AND week = ? AND Lab_Material IS NOT NULL";
        }
        else
        {
            throw new IllegalArgumentException("Material type must be 'lecture' or 'lab': " + materialType);
        }

        try (PreparedStatement statement = User.sqlConn.prepareStatement(query))
        {
            statement.setString(1, uniqueIdentifier.toString());
            statement.setInt(2, semester);
            statement.setInt(3, week);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    @Test
    void testGetFullNameByUsername() {

        // Call the method and get the result
        String actualFullName = Lecturer.getFullNameByUsername("testLecturer");
        String expectedFullName = "Jane Smith";

        assertNotNull(actualFullName);
        assertEquals(expectedFullName, actualFullName);
    }


    @Test
    void testGetUsernameByFullName() {

        // Call the method and get the result
        String actualUsername = Lecturer.getUsernameByFullName("Jane Smith");
        String expectedUsername = "testLecturer";

        assertNotNull(actualUsername);
        assertEquals(expectedUsername, actualUsername);
    }


    @Test
    void getAllStudentInfo() {
        List<String> studentInfoList = Lecturer.getAllStudentInfo("CS111");
        System.out.println(studentInfoList);
        assertNotNull(studentInfoList);
        assertTrue(studentInfoList.size() > 0);
    }


    @Test
    void updateExamRecords() {
        try {
            // Assuming a student with username "testStudent" exists
            Lecturer.updateExamRecords("Ellen Hazlett", "90", "80", "85", "CS111");

            String labResult = getLabResult("ellenhazlett", "CS111");
            String examResult = getExamResult("ellenhazlett", "CS111");
            String overallMark = getOverallMark("ellenhazlett", "CS111");

            assertEquals("90.0", labResult);
            assertEquals("80.0", examResult);
            assertEquals("85.0", overallMark);
        } catch (SQLException e) {
            fail("Exception thrown during updateExamRecords test: " + e.getMessage());
        }
    }


    @Test
    void isLecturerExists() {
        try {
            assertTrue(Lecturer.isLecturerExists("testLecturer"));
            assertFalse(Lecturer.isLecturerExists("nonexistentLecturer"));
        } catch (SQLException e) {
            fail("Exception thrown during isLecturerExists test: " + e.getMessage());
        }
    }

    @Test
    void setUpLecturer() {
        try {
            Lecturer.setUpLecturer("testLecturer");
            assertTrue(Lecturer.isLecturerExists("testLecturer"));
        } catch (NoSuchAlgorithmException | SQLException e) {
            fail("Exception thrown during setUpLecturer test: " + e.getMessage());
        }
    }


    @Test
    void addQualification() {
        try {
            // Assuming a lecturer with username "testLecturer" exists
            Lecturer.addQualification("testLecturer", "Ph.D. in Computer Science");

            // Verify the update
            String qualification = getQualification("testLecturer");
            assertEquals("Ph.D. in Computer Science", qualification);
        } catch (NoSuchAlgorithmException | SQLException e) {
            fail("Exception thrown during addQualification test: " + e.getMessage());
        }
    }



    private String getLabResult(String studentUsername, String moduleID) throws SQLException {
        Connection connection = dbConnect.getMysqlConnection();
        String sql = "SELECT labResult FROM Marks2 WHERE studentID = ? AND moduleID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Lecturer.getStudentIDByUsername(studentUsername));
            statement.setString(2, moduleID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("labResult");
                }
            }
        }
        return null;
    }

    private String getExamResult(String studentUsername, String moduleID) throws SQLException {
        Connection connection = dbConnect.getMysqlConnection();
        String sql = "SELECT examResult FROM Marks2 WHERE studentID = ? AND moduleID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Lecturer.getStudentIDByUsername(studentUsername));
            statement.setString(2, moduleID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("examResult");
                }
            }
        }
        return null;
    }

    private String getOverallMark(String studentUsername, String moduleID) throws SQLException {
        Connection connection = dbConnect.getMysqlConnection();
        String sql = "SELECT overallMarks FROM Marks2 WHERE studentID = ? AND moduleID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Lecturer.getStudentIDByUsername(studentUsername));
            statement.setString(2, moduleID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("overallMarks");
                }
            }
        }
        return null;
    }

    private String getQualification(String lecturerUsername) throws SQLException {
        Connection connection = dbConnect.getMysqlConnection();
        String sql = "SELECT qualification FROM Lecturer2 WHERE lecturerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Lecturer.getLecturerID(lecturerUsername));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("qualification");
                }
            }
        }
        return null;
    }

}