import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class dbConnectTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() {
        connection = dbConnect.getMysqlConnection();
    }

    @Test
    void testConnectionNotNull() {
        assertNotNull(connection, "Connection should not be null");
    }

    @Test
    void testConnectionIsValid() {
        try {
            assertTrue(connection.isValid(5), "Connection should be valid");
        } catch (SQLException e) {
            fail("SQLException: " + e.getMessage());
        }
    }

}

