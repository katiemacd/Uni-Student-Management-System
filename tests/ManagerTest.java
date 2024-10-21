import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class ManagerTest
{
    private User testManager;
    private User testEnrollStudent;
    private User testLecturerReset;

    @BeforeEach
    void setUp() throws SQLException, NoSuchAlgorithmException
    {
        // Check if the user already exists and delete it if it does
        if (User.isUserExists("testManager"))
        {
            try {
                deleteManager("testManager");
                deleteUser("testManager");
            } catch (SQLException e) {
                fail("Exception thrown during user deletion: " + e.getMessage());
            }
        }
        testManager = new User("testManager", "Carla", "Strathdee", "carla@example.com",
                Date.valueOf("2003-11-21"), "password123", "Manager", false, false);

        try
        {
            testManager.registerUser("testManager", "Carla", "Strathdee", "carla@example.com",
                    Date.valueOf("2003-11-21"), "password123", "Manager");

            assertTrue(User.isUserExists("testManager"));
        }
        catch (NoSuchAlgorithmException | SQLException e)
        {
            fail("Exception thrown during registration: " + e.getMessage());
        }

        testEnrollStudent = new User("testEnrollStudent", "Robbie", "Currie", "robbiecurrie@example.com",
                Date.valueOf("2023-11-22"), "password123", "Student", true, false);

        try
        {
            testEnrollStudent.registerUser("testEnrollStudent", "Robbie", "Currie", "robbiecurrie@example.com",
                    Date.valueOf("2023-11-22"), "password123", "Student");
            Manager.approveUser("testEnrollStudent");
        }
        catch (NoSuchAlgorithmException | SQLException e)
        {
            fail("Exception thrown during student registration: " + e.getMessage());
        }

        testLecturerReset = new User("testLecturerReset", "Robbie", "Currie", "testLecturerReset@example.com",
                Date.valueOf("2023-11-22"), "password123", "Lecturer", true, true);
        try
        {
            testLecturerReset.registerUser("testLecturerReset", "Robbie", "Currie", "testLecturerReset@example.com",
                    Date.valueOf("2023-11-22"), "password123", "Lecturer");
            Manager.approveUser("testEnrollStudent");
        }
        catch (NoSuchAlgorithmException | SQLException e)
        {
            fail("Exception thrown during student registration: " + e.getMessage());
        }

        try {
            Manager.addNewCourse("testCourseName", "testCourseDescription", 1);
            Student.setUpStudent("testEnrollStudent");
            Manager.setUpManager("testManager");
            Manager.addNewModule("testModuleID", "testModuleName", "testModuleDescription");
        } catch (SQLException | NoSuchAlgorithmException e) {
            fail("Exception during test data setup: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Student2 WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, ("testEnrollStudent"));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM User WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testEnrollStudent");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Modules2 WHERE moduleID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testModuleID");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Manager WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testManager");
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
                preparedStatement.setString(1, "testManager");
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
                preparedStatement.setString(1, "testLecturerReset");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            // Assuming the cleanup is successful if no exception is thrown
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Course WHERE courseName = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testCourseName");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
    }

    // Helper method to delete a user by username
    private void deleteUser(String username) throws SQLException
    {
        Connection connection = dbConnect.getMysqlConnection();
        String deleteQuery = "DELETE FROM User WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery))
        {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteManager(String username) throws SQLException
    {
        Connection connection = dbConnect.getMysqlConnection();
        String deleteQuery = "DELETE FROM Manager WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery))
        {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void testGetManagerIDByUsername() {
        try {
            int managerID = Manager.getManagerIDByUsername("testManager");
            // Add assertions based on the expected managerID
            assertTrue(managerID > 0);
        } catch (SQLException e) {
            fail("Exception thrown during testGetManagerIDByUsername: " + e.getMessage());
        }
    }

    @Test
    void testIsManagerExistsWhenManagerExists() {
        try {
            // Assuming there is a manager with the username "existingManager" in the database
            boolean exists = Manager.isManagerExists("testManager");

            assertTrue(exists, "Manager should exist in the database.");

        } catch (SQLException e) {
            fail("Exception thrown during testIsManagerExistsWhenManagerExists: " + e.getMessage());
        }
    }

    @Test
    void testIsManagerExistsWhenManagerDoesNotExist() {
        try {
            // Assuming there is no manager with the username "nonexistentManager" in the database
            boolean exists = Manager.isManagerExists("nonexistentManager");

            assertFalse(exists, "Manager should not exist in the database.");

        } catch (SQLException e) {
            fail("Exception thrown during testIsManagerExistsWhenManagerDoesNotExist: " + e.getMessage());
        }
    }

    @Test
    void testSetUpManager() {
        try {
            // Assuming the manager with the username "testManager" does not exist in the database
            String testManagerUsername = "testManager";

            // Call the method to set up the manager
            Manager.setUpManager(testManagerUsername);

            // Check if the manager now exists in the database
            boolean exists = Manager.isManagerExists(testManagerUsername);

            assertTrue(exists, "Manager should exist in the database after setup.");

        } catch (NoSuchAlgorithmException | SQLException e) {
            fail("Exception thrown during testSetUpManager: " + e.getMessage());
        }
    }

    @Test
    void testAddNewCourse() {
        try {

            // Check if the course now exists in the database
            boolean exists = Manager.courseExists("testCourseName");

            assertTrue(exists, "Course should exist in the database after addition.");

        } catch (SQLException e) {
            fail("Exception thrown during testAddNewCourse: " + e.getMessage());
        }
    }

    @Test
    void testAddNewModule() {
        try {

            // Check if the course now exists in the database
            boolean exists = Manager.moduleExists("testModuleID");

            assertTrue(exists, "Course should exist in the database after addition.");

        } catch (SQLException e) {
            fail("Exception thrown during testAddNewCourse: " + e.getMessage());
        }
    }

    @Test
    void testAssignModuleToCourse() {
        try {
            String testCourseID = Integer.toString(Manager.getCourseID("testCourseName"));
            String testModuleID = Manager.getModuleID("testModuleName");
            System.out.println("Test Module ID: " + testModuleID);
            // Call the method to assign the module to the course
            Manager.assignModuleToCourse(testCourseID, testModuleID);

            // Check if the module is now assigned to the course in the database
            boolean isAssigned = Manager.moduleAssignedToCourse(testModuleID, testCourseID);

            assertTrue(isAssigned, "Module is now assigned to the course.");
        } catch (SQLException e) {
            fail("Exception thrown during testAssignModuleToCourse: " + e.getMessage());
        }
    }


    @Test
    void testUpdateCourseInformation() {
        try {
            // Assuming there is a course with the ID "testCourseID" in the database
            int course = Manager.getCourseID("testCourseName");
            String courseID = Integer.toString(course);

            Manager.updateCourseInformation(courseID, "newCourseDescription");

            // Verify the update by directly querying the database
            String updatedCourseDescription = Manager.getUpdatedCourseDescriptionFromDB(courseID);

            // Add assertions to check if the course information is updated correctly
            assertNotNull(updatedCourseDescription);
            assertEquals("newCourseDescription", updatedCourseDescription);

        } catch (SQLException e) {
            fail("Exception thrown during testUpdateCourseInformation: " + e.getMessage());
        }
    }


    @Test
    void testGetUnapproved() {
        try {
            String unapprovedUsers = Manager.getUnapproved("testManager");

            assertNotNull(unapprovedUsers);
            assertFalse(unapprovedUsers.contains("testManager")); // Replace with an actual unapproved username
            assertFalse(unapprovedUsers.contains("Carla Strathdee")); // Replace with actual first and last names
            assertFalse(unapprovedUsers.contains("approved")); // Ensure that the result does not contain the word "approved"

        } catch (SQLException e) {
            fail("Exception thrown during testGetUnapproved: " + e.getMessage());
        }
    }

    @Test
    void testApproveUser() {
        try {
            String approvedUsers = Manager.getApproved("testManager");

            assertNotNull(approvedUsers);
            assertTrue(approvedUsers.contains("testEnrollStudent")); // Replace with an actual unapproved username


        } catch (SQLException e) {
            fail("Exception thrown during testApproveUser: " + e.getMessage());
        }
    }


    @Test
    void testGetApproved() {
        try {
            String approvedUsers = Manager.getApproved("testManager");

            assertNotNull(approvedUsers);
            assertTrue(approvedUsers.contains("testEnrollStudent"));
            assertTrue(approvedUsers.contains("Robbie Currie"));

        } catch (SQLException e) {
            fail("Exception thrown during testGetUnapproved: " + e.getMessage());
        }
    }

    @Test
    void testGetUnactivated() {
        try {
            String unactivatedUsers = Manager.getUnactivated("testManager");

            assertNotNull(unactivatedUsers);
            assertTrue(unactivatedUsers.contains("testEnrollStudent"));
            assertTrue(unactivatedUsers.contains("Robbie Currie"));

        } catch (SQLException e) {
            fail("Exception thrown during testGetUnactivated: " + e.getMessage());
        }
    }

    @Test
    void testGetActivated() throws SQLException {
        Manager.activateAccount("testEnrollStudent");
        try {
            String activatedUsers = Manager.getActivated("testManager");

            assertNotNull(activatedUsers);
            assertTrue(activatedUsers.contains("testEnrollStudent"));
            assertTrue(activatedUsers.contains("Robbie Currie"));

        } catch (SQLException e) {
            fail("Exception thrown during testGetUnactivated: " + e.getMessage());
        }
    }

    @Test
    void testDeactivateAccount() throws SQLException {
        Manager.activateAccount("testEnrollStudent");
        Manager.deactivateAccount("testEnrollStudent");
        try {
            String activatedUsers = Manager.getActivated("testManager");
            assertFalse(activatedUsers.contains("testEnrollStudent"));

        } catch (SQLException e) {
            fail("Exception thrown during testGetUnactivated: " + e.getMessage());
        }
    }

    @Test
    void testResetStudentAccount() throws SQLException {
        Manager.enrollStudentCourse("testEnrollStudent", 2);
        try {
            Manager.resetAccount("testEnrollStudent");

        } catch (SQLException e) {
            fail("Exception thrown during testResetAccount: " + e.getMessage());
        }
    }

    @Test
    void testResetLecturerAccount() throws SQLException {
        Manager.assignModuleToLecturer("testModuleID", (Lecturer.getLecturerID("testLecturerReset")));
        try {
            Manager.resetAccount("testLecturerReset");

        } catch (SQLException e) {
            fail("Exception thrown during testResetAccount: " + e.getMessage());
        }
    }

    @Test
    void testGetAllAccounts() {
        try {
            String accountDetails = Manager.getAllAccounts("testManager");
            assertNotNull(accountDetails);
            assertTrue(accountDetails.contains("testEnrollStudent"));
        } catch (SQLException e) {
            fail("Exception thrown during testGetAllAccounts: " + e.getMessage());
        }
    }

    @Test
    void testAssignModuleToLecturer() {
        try {
            String moduleID = Manager.getModuleID("testModuleName");
            String lecturerID="3";

            // Call the method to assign the module to the course
            Manager.assignModuleToLecturer(moduleID, lecturerID);

            // Check if the module is now assigned to the course in the database
            boolean isAssigned = Manager.moduleAssignedToLecturer(moduleID, lecturerID);

            assertTrue(isAssigned, "Module should be assigned to the lecturer after assignment.");

        } catch (SQLException e) {
            fail("Exception thrown during testAssignModuleToCourse: " + e.getMessage());
        }
    }

    @Test
    void testEnrollStudentCourse() {
        try {
            // Enroll a student in a course
            Manager.enrollStudentCourse("testEnrollStudent", 2);

            // Verify the enrollment by fetching the updated courseID
            int enrolledCourseID = Integer.parseInt(Manager.getEnrolledCourseIDFromDatabase("testEnrollStudent"));

            // Add assertions to check if the student is enrolled in the course correctly
            assertEquals(2, enrolledCourseID);

        } catch (SQLException e) {
            fail("Exception thrown during testEnrollStudentCourse: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudents() {
        try {
            // Retrieve all students
            ResultSet resultSet = Manager.getAllStudents();

            // Verify that the result set is not null
            assertNotNull(resultSet);

            // Add more specific assertions based on the structure of your Student2 table

        } catch (SQLException e) {
            fail("Exception thrown during testGetAllStudents: " + e.getMessage());
        }
    }

    @Test
    void testIssueDecision() {
        try {
            // Issue a decision to a student
            Manager.issueDecision("testEnrollStudent", "Pass");

            // Verify the decision by fetching the updated decision
            String issuedDecision = Manager.getIssuedDecisionFromDatabase("testEnrollStudent");

            // Add assertions to check if the decision is issued correctly
            assertEquals("Pass", issuedDecision);

        } catch (SQLException e) {
            fail("Exception thrown during testIssueDecision: " + e.getMessage());
        }
    }


    @Test
    void testGetCourseDetails() {
        try {
            // Call the method to get course details
            String courseDetails = Manager.getCourseDetails();

            // Add assertions to check if course details are retrieved correctly
            assertNotNull(courseDetails);
            assertTrue(courseDetails.contains("testCourseName"));
            assertTrue(courseDetails.contains("testCourseDescription"));

        } catch (SQLException e) {
            fail("Exception thrown during testGetCourseDetails: " + e.getMessage());
        }
    }

    @Test
    void testGetModuleDetails() {
        try {
            // Call the method to get module details
            String moduleDetails = Manager.getModuleDetails();

            // Add assertions to check if module details are retrieved correctly
            assertNotNull(moduleDetails);
            assertTrue(moduleDetails.contains("testModuleID"));
            assertTrue(moduleDetails.contains("testModuleName"));
            assertTrue(moduleDetails.contains("testModuleDescription"));


        } catch (SQLException e) {
            fail("Exception thrown during testGetModuleDetails: " + e.getMessage());
        }
    }
    @Test
    public void testGetAllCourses() throws SQLException {
        ResultSet resultSet = Manager.getAllCourses();
        assertNotNull(resultSet);
    }

    @Test
    public void testGetAllModules() throws SQLException {
        ResultSet resultSet = Manager.getAllModules();
        assertNotNull(resultSet);
    }

    @Test
    public void testGetAllLecturers() throws SQLException {
        ResultSet resultSet = Manager.getAllLecturers();
        assertNotNull(resultSet);
    }

    @Test
    public void testGetAllDepartments() throws SQLException {
        ResultSet resultSet = Manager.getAllDepartments();
        assertNotNull(resultSet);
    }
}