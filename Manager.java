import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class Manager extends User {
    public static int getManagerIDByUsername(String username) throws SQLException
    {
        Connection connection = sqlConn;
        String query = "SELECT managerID FROM Manager WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return resultSet.getInt("managerID");
                }
            }
        }
        throw new SQLException("Manager not found for username: " + username);
    }


    public static boolean isManagerExists(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT COUNT(*) FROM Manager WHERE username = ?";
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

    public static boolean courseExists (String courseName) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT COUNT(*) FROM Course WHERE courseName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static boolean moduleExists (String moduleID) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT COUNT(*) FROM Modules2 WHERE moduleID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, moduleID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setUpManager(String username) throws NoSuchAlgorithmException, SQLException {
        Connection connection = sqlConn;
        String insertQuery = "INSERT INTO Manager (username) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
    }
    public static void addNewCourse(String courseName, String courseDescription, int departmentID) throws SQLException {
        if (courseExists(courseName)) {
            System.out.println("Course already exists");
            throw new SQLException("Course already exists");
        }

        String insertQuery = "INSERT INTO Course (courseName, courseDescription, departmentID) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, courseName);
            preparedStatement.setString(2, courseDescription);
            preparedStatement.setInt(3, departmentID);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("New course added successfully");
            } else {
                System.out.println("Failed to add new course");
            }
        }
    }

    public static void addNewModule(String moduleID, String moduleName, String moduleDescription) throws SQLException {
        /*
        if (moduleExists(moduleID)) {
            return;
        }
        */

        String insertQuery = "INSERT INTO Modules2 (moduleID, moduleName, moduleDescription) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, moduleID);
            preparedStatement.setString(2, moduleName);
            preparedStatement.setString(3, moduleDescription);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("New module added successfully");
            } else {
                System.out.println("Failed to add new module");
            }
        }
    }


    public static void assignModuleToCourse(String courseID, String moduleID) throws SQLException
    {
        // Check if the module is already assigned to the course
        if (moduleAssignedToCourse(moduleID, courseID))
        {
            System.out.println("Module is already assigned to the course");
            return; // Return without updating the database
        }

        if (!moduleExists(moduleID))
        {
            System.out.println("Module not found. Unable to assign to course.");
            return;
        }

        String updateQuery = "UPDATE Modules2 SET courseID = ? WHERE moduleID = ?";
        try (PreparedStatement updateStatement = sqlConn.prepareStatement(updateQuery))
        {
            updateStatement.setString(1, courseID);
            updateStatement.setString(2, moduleID);

            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0)
            {
                System.out.println("Module assigned to course successfully");
            }
            else
            {
                System.out.println("Failed to assign module to course");
            }
        }
    }

    static boolean moduleAssignedToCourse(String moduleID, String courseID) throws SQLException {
        // Query to check if a module is assigned to a course
        String query = "SELECT COUNT(*) FROM Modules2 WHERE moduleID = ? AND courseID = ?";

        try (PreparedStatement statement = sqlConn.prepareStatement(query)) {
            statement.setString(1, moduleID);
            statement.setString(2, courseID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    static boolean moduleAssignedToLecturer(String moduleID, String lecturerID) throws SQLException {
        // Query to check if a module is assigned to a course
        String query = "SELECT COUNT(*) FROM Modules2 WHERE moduleID = ? AND lecturerID = ?";

        try (PreparedStatement statement = sqlConn.prepareStatement(query)) {
            statement.setString(1, moduleID);
            statement.setString(2, lecturerID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static void updateCourseInformation(String courseID, String newCourseDescription) throws SQLException
    {
        String updateQuery = "UPDATE Course SET courseDescription = ? WHERE courseID = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(updateQuery))
        {
            preparedStatement.setString(1, newCourseDescription);
            preparedStatement.setString(2, courseID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0)
            {
                System.out.println("Course information updated successfully");
            }
            else
            {
                System.out.println("Failed to update course information");
            }
        }

    }

    static String getUpdatedCourseDescriptionFromDB(String courseID) throws SQLException {
        String query = "SELECT courseDescription FROM Course WHERE courseID = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(query)) {
            preparedStatement.setString(1, courseID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("courseDescription");
                }
            }
        }
        return null;
    }

    public static String getApproved(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM User WHERE approved = ? AND username != ?";
        StringBuilder moduleDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, 1);
            statement.setString(2, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String usernameToApprove = resultSet.getString("username");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    int approved = resultSet.getInt("approved");
                    moduleDetails.append(usernameToApprove).append("\n").append(firstName).append(" ").append(lastName).append("\n\n");
                }
            }
        }
        if (moduleDetails.length() > 0) {
            return moduleDetails.toString();
        } else {
            return "No modules found for the course";
        }
    }

    public static String getUnapproved(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM User WHERE approved = ? AND username != ?";
        StringBuilder moduleDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, 0);
            statement.setString(2, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String unapprovedUsername = resultSet.getString("username");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    int approved = resultSet.getInt("approved");
                    moduleDetails.append(unapprovedUsername).append("\n").append(firstName).append(" ").append(lastName).append("\n\n");
                }
            }
        }
        if (moduleDetails.length() > 0) {
            return moduleDetails.toString();
        } else {
            return "No unapproved users found";
        }
    }

    public static void approveUser(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "UPDATE User SET approved = 1 WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public static String getUnactivated(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM User WHERE active = ? AND username != ?";
        StringBuilder moduleDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, 0);
            statement.setString(2, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String unactiveUsername = resultSet.getString("username");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    int approved = resultSet.getInt("approved");
                    int active = resultSet.getInt("active");
                    moduleDetails.append(unactiveUsername).append("\n").append(firstName).append(" ").append(lastName).append("\n\n");
                }
            }
        }
        if (moduleDetails.length() > 0) {
            return moduleDetails.toString();
        } else {
            return "No inactive accounts found";
        }
    }


    public static void activateAccount(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "UPDATE User SET active = 1 WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public static String getActivated(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM User WHERE active = ? AND username != ?";
        StringBuilder moduleDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, 1);
            statement.setString(2, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String activeUsername = resultSet.getString("username");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    int approved = resultSet.getInt("approved");
                    int active = resultSet.getInt("active");
                    moduleDetails.append(activeUsername).append("\n").append(firstName).append(" ").append(lastName).append("\n\n");
                }
            }
        }
        if (moduleDetails.length() > 0) {
            return moduleDetails.toString();
        } else {
            return "No activated accounts found";
        }
    }

    public static void deactivateAccount(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "UPDATE User SET active = 0 WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public static String getAllAccounts(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM User WHERE username != ?";
        StringBuilder moduleDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String accountUsername = resultSet.getString("username");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    int approved = resultSet.getInt("approved");
                    int active = resultSet.getInt("active");
                    moduleDetails.append(accountUsername).append("\n").append(firstName).append(" ").append(lastName).append("\n\n");
                }
            }
        }
        if (moduleDetails.length() > 0) {
            return moduleDetails.toString();
        } else {
            return "No activated accounts found";
        }
    }

    public static void resetAccount(String username) throws SQLException {
        String role = getUserRole(username);
        switch (role) {
            case "Student":
                resetStudentAccount(username);
                break;
            case "Lecturer":
                resetLecturerAccount(username);
                break;
            default:
                System.out.println("Unsupported role for account reset");
                break;
        }
    }

    private static void resetStudentAccount(String username) throws SQLException {
        Connection connection = sqlConn;
        int ID = Student.getIDByUsername(username);
        String studentMarksResetQuery = "DELETE FROM Marks2 WHERE studentID = ?";
        try (PreparedStatement statement = connection.prepareStatement(studentMarksResetQuery)) {
            statement.setInt(1, ID);
            statement.executeUpdate();
        }

        String studentAccountResetQuery = "DELETE FROM Student2 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(studentAccountResetQuery)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    private static void resetLecturerAccount(String username) throws SQLException {
        Connection connection = sqlConn;
        String ID = Lecturer.getLecturerID(username);
        String lecturerModuleResetQuery = "UPDATE Modules2 SET lecturerID = NULL WHERE lecturerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(lecturerModuleResetQuery)) {
            statement.setString(1, ID);
            statement.executeUpdate();
        }

        String studentAccountResetQuery = "DELETE FROM Lecturer2 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(studentAccountResetQuery)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public static void assignModuleToLecturer(String moduleID, String lecturerID) throws SQLException {
        // Check if the module is already assigned to a lecturer
        if (moduleAssignedToLecturer(moduleID, lecturerID)) {
            System.out.println("Module is already assigned to the lecturer");
            return; // Return without updating the database
        }

        String assignModuleQuery = "UPDATE Modules2 SET lecturerID = ? WHERE moduleID = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(assignModuleQuery)) {
            preparedStatement.setString(1, lecturerID);
            preparedStatement.setString(2, moduleID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Module assigned to lecturer successfully");
            } else {
                System.out.println("Failed to assign module to lecturer");
            }
        }
    }

    static boolean isEnrolled(String username, int courseID) throws SQLException
    {
        String checkEnrollmentQuery = "SELECT COUNT(*) FROM Student2 WHERE username = ? AND courseID = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(checkEnrollmentQuery))
        {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, courseID);

            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static void enrollStudentCourse(String username, int courseID) throws SQLException
    {
        if (isEnrolled(username, courseID))
        {
            System.out.println("Student is already enrolled in the course");
            return;
        }

        String query = "UPDATE Student2 SET courseID = ? WHERE username = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(query)) {
            preparedStatement.setInt(1, courseID);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Student enrolled successfully");
            } else {
                System.out.println("Failed to enroll student");
            }
        }
    }



    static String getEnrolledCourseIDFromDatabase(String username) throws SQLException {
        String query = "SELECT * FROM Student2 WHERE username = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("courseID");
                }
            }
        }
        return null;
    }

    public static ResultSet getAllStudents() throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Student2";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

    public static ResultSet getAllCourses() throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Course";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }
    public static ResultSet getAllModules() throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Modules2";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

    public static ResultSet getAllLecturers() throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Lecturer2";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

    public static ResultSet getAllDepartments() throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Department";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

    public static void issueDecision(String username, String decision) throws SQLException
    {
        String issueAwardQuery = "UPDATE Student2 SET decision = ? WHERE username = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(issueAwardQuery))
        {
            preparedStatement.setString(1, decision);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        }
    }


    static String getIssuedDecisionFromDatabase(String username) throws SQLException {
        String query = "SELECT decision FROM Student2 WHERE username = ?";

        try (PreparedStatement preparedStatement = sqlConn.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("decision");
                }
            }
        }
        return null;
    }



    public static int getCourseID(String courseName) throws SQLException
    {
        Connection connection = sqlConn;
        int courseID = -1;

        String query = "SELECT courseID FROM Course WHERE courseName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    courseID = resultSet.getInt("courseID");
                }
            }
        }
        return courseID;
    }

    public static String getModuleID(String moduleName) throws SQLException
    {
        Connection connection = sqlConn;
        String courseID = null;

        String query = "SELECT moduleID FROM Modules2 WHERE moduleName = ?";
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, moduleName);
            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    courseID = resultSet.getString("moduleID");
                }
            }
        }
        return courseID;
    }

    static String getCourseDetails() throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Course";

        StringBuilder result = new StringBuilder();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int ID = resultSet.getInt("courseID");
                    String courseName = resultSet.getString("courseName");
                    String courseDescription = resultSet.getString("courseDescription");
                    int departmentID = resultSet.getInt("departmentID");

                    // Append details for each course to the StringBuilder
                    result.append("Course ID: ").append(ID).append("\nCourse Name: ").append(courseName)
                            .append("\nCourse Description: ").append(courseDescription)
                            .append("\nDepartment ID: ").append(departmentID).append("\n\n");
                }
            }
        }
        if (result.length() > 0) {
            return result.toString();
        } else {
            return "No courses found";
        }
    }

    private static boolean hasMaterials(ResultSet resultSet) throws SQLException
    {
        return resultSet.getBytes("material") != null;
    }

    static String getModuleDetails() throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Modules2";
        StringBuilder moduleDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String ID = resultSet.getString("moduleID");
                    String moduleName = resultSet.getString("moduleName");
                    String moduleDescription = resultSet.getString("moduleDescription");
                    String material = resultSet.getString("material");

                    moduleDetails.append("Module ID: ").append(ID).append("\n")
                            .append("Module Name: ").append(moduleName).append("\n")
                            .append("Module Description: ").append(moduleDescription).append("\n");

                    if (hasMaterials(resultSet)) {
                        // Display a message for downloading materials
                        moduleDetails.append("Module Materials: ")
                                .append("Only students can view module materials.")
                                .append("\n");
                    } else {
                        moduleDetails.append("Module Materials: No materials available").append("\n");
                    }

                    moduleDetails.append("\n");
                }
                if (moduleDetails.length() > 0) {
                    return moduleDetails.toString();
                } else {
                    return "No modules found";
                }
            }
        }
    }
}
