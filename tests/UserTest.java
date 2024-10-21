import org.junit.jupiter.api.*;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static User testUser;

    @BeforeEach
    void setUp() throws SQLException, NoSuchAlgorithmException {
        // Initialize a test user with sample data
        testUser = new User("testUser", "John", "Doe", "john.doe@example.com",
                Date.valueOf("1990-01-01"), "password123", "Student", true, true);
        testUser.registerUser("testUser", "John", "Doe", "john.doe@example.com",
                Date.valueOf("1990-01-01"), "password123", "Student");
    }

    @AfterEach
    void cleanUp() {
        try {
            Connection connection = dbConnect.getMysqlConnection();
            String deleteQuery = "DELETE FROM User WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, "testUser");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Exception thrown during cleanup: " + e.getMessage());
        }
    }
    @Test
    void getUsername() {
        assertEquals("testUser", testUser.getUsername());
    }

    @Test
    void setUsername() {
        testUser.setUsername("testUser");
        assertEquals("testUser", testUser.getUsername());
    }

    @Test
    void getFirstName() {
        assertEquals("John", testUser.getFirstName());
    }

    @Test
    void setFirstName() {
        testUser.setFirstName("Jane");
        assertEquals("Jane", testUser.getFirstName());
    }

    @Test
    void getSurname() {
        assertEquals("Doe", testUser.getSurname());
    }

    @Test
    void setSurname() {
        testUser.setSurname("Doe");
        assertEquals("Doe", testUser.getSurname());
    }

    @Test
    void getEmail() {
        assertEquals("john.doe@example.com", testUser.getEmail());
    }

    @Test
    void setEmail() {
        testUser.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", testUser.getEmail());
    }

    @Test
    void getDOB() {
        assertEquals(Date.valueOf("1990-01-01"), testUser.getDOB());
    }

    @Test
    void setDOB() {
        testUser.setDOB(Date.valueOf("1990-01-01"));
        assertEquals(Date.valueOf("1990-01-01"), testUser.getDOB());
    }

    @Test
    void getPassword() {
        assertEquals("password123", testUser.getPassword());
    }

    @Test
    void setPassword() {
        testUser.setPassword("password123");
        assertEquals("password123", testUser.getPassword());
    }

    @Test
    void getRole() {
        assertEquals("Student", testUser.getRole());
    }

    @Test
    void setRole() {
        testUser.setRole("Student");
        assertEquals("Student", testUser.getRole());
    }

    @Test
    void isUserExists() throws SQLException {
        assertTrue(User.isUserExists("testUser"));
        assertFalse(User.isUserExists("nonexistentUser"));
    }

    @Test
    void isEmailExists() throws SQLException {
        assertTrue(User.isEmailExists("john.doe@example.com"));
        assertFalse(User.isEmailExists("nonexistent@example.com"));
    }

    @Test
    void isUserApproved() throws SQLException {
        assertFalse(User.isUserApproved("testUser"));
        assertFalse(User.isUserApproved("nonexistentUser"));
    }

    @Test
    void getPasswordForUser() {
        try {
            String password = testUser.getPasswordForUser("testUser");
            assertNotNull(password);
        } catch (SQLException e) {
            fail("Exception thrown during password retrieval: " + e.getMessage());
        }
    }

    @Test
    void updatePasswordForUser() {
        try {
            testUser.updatePasswordForUser("testUser", "newPassword789");
            String updatedPassword = testUser.getPasswordForUser("testUser");
            assertEquals("newPassword789", updatedPassword);
        } catch (SQLException e) {
            fail("Exception thrown during password update: " + e.getMessage());
        }
    }

    @Test
    void getUserRole() {
        assertEquals("Student", testUser.getUserRole("testUser"));
        assertNull(testUser.getUserRole("nonexistentUser"));
    }

    @Test
    void testGetUserByUsername() throws SQLException {
        Connection connection = dbConnect.getMysqlConnection();
        User resultUser = User.getUserByUsername("testUser");

        assertNotNull(resultUser, "User not found");

        assertEquals("testUser", resultUser.getUsername());
        assertEquals("John", resultUser.getFirstName());
        assertEquals("Doe", resultUser.getSurname());
        assertEquals("john.doe@example.com", resultUser.getEmail());
        assertEquals(java.sql.Date.valueOf("1990-01-01"), resultUser.getDOB());
        assertEquals("password123", resultUser.getPassword());
        assertEquals("Student", resultUser.getRole());
    }

    @Test
    void testIsUserActivated() {
        try {
            String testUsername = "testUser";
            Manager.activateAccount(testUsername);

            boolean isActivated = User.isUserActivated(testUsername);

            assertTrue(isActivated, "User should be activated.");

            Manager.deactivateAccount(testUsername);

            isActivated = User.isUserActivated(testUsername);

            assertFalse(isActivated, "User should not be activated.");

        } catch (SQLException e) {
            fail("Exception thrown during testIsUserActivated: " + e.getMessage());
        }
    }
}

