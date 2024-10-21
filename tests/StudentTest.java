import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    private User testStudent;
    private User testLecturer;

    @BeforeEach
    void setUp() throws SQLException, NoSuchAlgorithmException, IOException {
        testStudent = new User("testStudent", "John", "Doe", "john.doe@example.com",
                Date.valueOf("1990-01-01"), "password123", "Student", false, false);
        try {
            testStudent.registerUser("testStudent", "John", "Doe", "john.doe@example.com",
                    Date.valueOf("1990-01-01"), "password123", "Student");

            assertTrue(User.isUserExists("testStudent"));
        } catch (NoSuchAlgorithmException | SQLException e) {
            fail("Exception thrown during registration: " + e.getMessage());
        }

        try {
            Student.setUpStudent("testStudent");
        } catch (SQLException | NoSuchAlgorithmException e) {
            fail("Exception during test data setup: " + e.getMessage());
        }

        testLecturer = new User("testLecturer2", "Jane", "Smith", "jane.smith@example.com",
                Date.valueOf("1990-01-01"), "password123", "Lecturer", false, false);

        try {
            testLecturer.registerUser("testLecturer2", "Jane", "Smith", "jane.smith@example.com",
                    Date.valueOf("1990-01-01"), "password123", "Lecturer");

            assertTrue(User.isUserExists("testLecturer2"));
        } catch (NoSuchAlgorithmException | SQLException e) {
            fail("Exception thrown during registration: " + e.getMessage());
        }

        try {
            Lecturer.setUpLecturer("testLecturer2");
            Manager.addNewModule("testModule", "testModule", "testModuleDescription");
            Manager.assignModuleToLecturer("testModule", Lecturer.getLecturerID("testLecturer2"));
        } catch (SQLException | NoSuchAlgorithmException e) {
            fail("Exception during test data setup: " + e.getMessage());
        }

        Manager.issueDecision("testStudent", "Pass");
        Manager.enrollStudentCourse("testStudent", 2);
        Lecturer.updateExamRecords("John Doe", "90", "80", "85", "CS210");
        UUID uniqueIdentifier = UUID.randomUUID();
        Lecturer.uploadModuleMaterials("testLecturer2", new File("testing.txt"), 2, 2, "lecture", uniqueIdentifier);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Marks2 WHERE studentID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                int studentID = Student.getIDByUsername("testStudent");
                preparedStatement.setInt(1, studentID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Student2 WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testStudent");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM User WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testStudent");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM moduleMaterial WHERE module_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testModule");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Modules2 WHERE moduleID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testModule");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM Lecturer2 WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testLecturer2");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM User WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testLecturer2");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
    }

    @Test
    void isStudentExists() {
        try {
            assertTrue(Student.isStudentExists("testStudent"));
            assertFalse(Student.isStudentExists("nonexistentStudent"));
        } catch (SQLException e) {
            fail("Exception thrown during isStudentExists test: " + e.getMessage());
        }
    }

    @Test
    void getIDByUsername() {
        try {
            int studentID = Student.getIDByUsername("testStudent");
            assertTrue(studentID > 0);
        } catch (SQLException e) {
            fail("Exception thrown during getIDByUsername test: " + e.getMessage());
        }
    }

    @Test
    void setUpStudent() {
        try {
            Student.setUpStudent("testStudent");
            assertTrue(Student.isStudentExists("testStudent"));
        } catch (NoSuchAlgorithmException | SQLException e) {
            fail("Exception thrown during setUpStudent test: " + e.getMessage());
        }
    }

    @Test
    void getStudentCourse() {
        try {
            String courseDetails = Student.getStudentCourse("testStudent");
            assertNotNull(courseDetails);
        } catch (SQLException e) {
            fail("Exception thrown during getStudentCourse test: " + e.getMessage());
        }
    }

    @Test
    void getCourseDetails() {
        try {
            String courseDetails = Student.getCourseDetails(2);
            assertNotNull(courseDetails);
            assertTrue(courseDetails.contains("Course ID"));
            assertTrue(courseDetails.contains("Course Name"));
            assertTrue(courseDetails.contains("Course Description"));
            assertTrue(courseDetails.contains("Department ID"));

        } catch (SQLException e) {
            fail("Exception thrown during getCourseDetails test: " + e.getMessage());
        }
    }

    @Test
    void testGetModuleDetails() {
        try {
            String moduleDetails = Student.getModuleDetails(2);
            assertNotNull(moduleDetails);
            assertTrue(moduleDetails.contains("Module ID"));
            assertTrue(moduleDetails.contains("Module Name"));
            assertTrue(moduleDetails.contains("Module Description"));
        } catch (SQLException e) {
            fail("Exception thrown during testGetModuleDetails test: " + e.getMessage());
        }
    }

    @Test
    void testGetModuleMarks() {
        try {
            // Assuming the username "testStudent" exists
            String markDetails = Student.getModuleMarks("testStudent");
            assertNotNull(markDetails);
            assertTrue(markDetails.contains("Module ID"));
            assertTrue(markDetails.contains("Lab Result"));
            assertTrue(markDetails.contains("Exam Result"));
            assertTrue(markDetails.contains("Overall Mark"));
        } catch (SQLException e) {
            fail("Exception thrown during testGetModuleMarks test: " + e.getMessage());
        }
    }

    @Test
    void getDecision() {
        try {
            String decision = Student.getDecision("testStudent");
            assertNotNull(decision);
            assertTrue(decision.contains("Pass"));
        } catch (SQLException e) {
            fail("Exception thrown during getDecision test: " + e.getMessage());
        }
    }

    @Test
    void testDownloadModuleMaterials() throws IOException
    {
        Student student = new Student();
        String moduleID = "CS207";
        int semester = 1;
        int week = 1;
        String destinationPath = "C:\\" + moduleID + ".extension";

        try
        {
            student.downloadModuleMaterials(moduleID, destinationPath, semester, week, "lecture");

            File downloadedLectureFile = new File(destinationPath);
            assertTrue(downloadedLectureFile.exists());

            downloadedLectureFile.delete();
        }
        catch (SQLException e)
        {
            fail("Exception thrown during testDownloadModuleMaterials (lecture): " + e.getMessage());
        }

        try
        {
            student.downloadModuleMaterials(moduleID, destinationPath, semester, week, "lab");

            File downloadedLabFile = new File(destinationPath);
            assertTrue(downloadedLabFile.exists());

            downloadedLabFile.delete();
        }
        catch (SQLException e)
        {
            fail("Exception thrown during testDownloadModuleMaterials (lab): " + e.getMessage());
        }
    }

    @Test
    void testGetMaterialDetailsForStudent()
    {
        try
        {
            String details = Student.getMaterialDetailsForStudent("testStudent");

            assertTrue(details.contains("Module ID"));
            assertTrue(details.contains("Module Name"));
            assertTrue(details.contains("Semester"));
            assertTrue(details.contains("Week"));
            assertTrue(details.contains("Lecture Material: Available") || details.contains("Lecture Material: Not available"));
            assertTrue(details.contains("Lab Material: Available") || details.contains("Lab Material: Not available"));
        }
        catch (SQLException e)
        {
            fail("Exception thrown during test: " + e.getMessage());
        }

    }

    @Test
    void testGetModuleIDsForCourse() {
        try {
            List<String> moduleIDs = Student.getModuleIDsForCourse(2);
            assertNotNull(moduleIDs);
            assertTrue(moduleIDs.size() > 0);

            // Test for a course ID that doesn't exist
            List<String> emptyModuleIDs = Student.getModuleIDsForCourse(999);
            assertNotNull(emptyModuleIDs);
            assertEquals(0, emptyModuleIDs.size());
        } catch (SQLException e) {
            fail("Exception thrown during testGetModuleIDsForCourse test: " + e.getMessage());
        }
    }

    @Test
    void testGetCourseID() {
        try {
            int courseID = Student.getCourseID("testStudent");
            assertTrue(courseID > 0);

            // Test for a username that doesn't exist
            assertThrows(SQLException.class, () -> Student.getCourseID("nonexistentStudent"));
        } catch (SQLException e) {
            fail("Exception thrown during testGetCourseID test: " + e.getMessage());
        }
    }
}


