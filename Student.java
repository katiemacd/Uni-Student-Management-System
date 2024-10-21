import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    public static boolean isStudentExists(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT COUNT(*) FROM Student2 WHERE username = ?";
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

    public static int getIDByUsername(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT studentID FROM Student2 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("studentID");
                } else {
                    throw new SQLException("Student with username " + username + " not found");
                }
            }
        }
    }
    public static void setUpStudent(String username) throws NoSuchAlgorithmException, SQLException {
        Connection connection = sqlConn;
        String insertQuery = "INSERT INTO Student2 (username) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
    }

    public static String getStudentCourse(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT courseID FROM Student2 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int courseID = resultSet.getInt("courseID");
                    return getCourseDetails(courseID);
                } else {
                    return "No course enrolled";
                }
            }
        }
    }

    public static List<String> getModuleIDsForCourse(int courseID) throws SQLException {
        Connection connection = sqlConn;
        List<String> moduleIDs = new ArrayList<>();
        String query = "SELECT moduleID FROM Modules2 WHERE courseID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String moduleID = resultSet.getString("moduleID");
                    moduleIDs.add(moduleID);
                }
            }
        }
        return moduleIDs;
    }

    public static int getCourseID(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT courseID FROM Student2 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("courseID");
                } else {
                    throw new SQLException("Student with username " + username + " not found");
                }
            }
        }
    }

    public void downloadModuleMaterials(String moduleID, String destinationPath, int semester, int week, String materialType) throws SQLException, IOException
    {
        Connection connection = sqlConn;

        String columnName = "Lecture_Material";
        if ("lab".equals(materialType))
        {
            columnName = "Lab_Material";
        }

        String query = "SELECT " + columnName + " FROM moduleMaterial WHERE module_ID = ? AND Semester = ? AND Week = ?";

        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, moduleID);
            statement.setInt(2, semester);
            statement.setInt(3, week);

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    byte[] fileContent = resultSet.getBytes(columnName);

                    try (FileOutputStream fos = new FileOutputStream(new File(destinationPath)))
                    {
                        fos.write(fileContent);
                    }
                    System.out.println("Module materials downloaded successfully to: " + destinationPath);
                }
                else
                {
                    System.out.println("Module not found for the specified semester and week");
                }
            }
        }
    }

    static String getCourseDetails(int courseID) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Course WHERE courseID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int ID = resultSet.getInt("courseID");
                    String courseName = resultSet.getString("courseName");
                    String courseDescription = resultSet.getString("courseDescription");
                    int departmentID = resultSet.getInt("departmentID");
                    return "Course ID: " + ID + "\nCourse Name: " + courseName +
                            "\nCourse Description: " + courseDescription + "\nDepartment ID: " + departmentID +
                            "\n\n" + getModuleDetails(ID);
                } else {
                    return "No course details found";
                }
            }
        }
    }

    static String getModuleDetails(int courseID) throws SQLException
    {
        Connection connection = sqlConn;
        String query = "SELECT * FROM Modules2 WHERE courseID = ?";
        StringBuilder moduleDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, courseID);
            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    String ID = resultSet.getString("moduleID");
                    String moduleName = resultSet.getString("moduleName");
                    String moduleDescription = resultSet.getString("moduleDescription");

                    moduleDetails.append("Module ID: ").append(ID).append("\n")
                            .append("Module Name: ").append(moduleName).append("\n")
                            .append("Module Description: ").append(moduleDescription).append("\n");
                    moduleDetails.append("\n");
                }
            }
        }
        if (moduleDetails.length() > 0)
        {
            return moduleDetails.toString();
        }
        else
        {
            return "No modules found for the course";
        }
    }

    public static String getMaterialDetailsForStudent(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT M.moduleID, M.moduleName, C.departmentID, MM.Semester, MM.Week, MM.Lecture_Material, MM.Lab_Material " +
                "FROM Modules2 M " +
                "JOIN moduleMaterial MM ON M.moduleID = MM.module_ID " +
                "JOIN Course C ON M.courseID = C.courseID " +
                "JOIN Student2 S ON S.courseID = C.courseID " +
                "WHERE S.username = ?";

        StringBuilder materialDetails = new StringBuilder();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String moduleID = resultSet.getString("moduleID");
                    String moduleName = resultSet.getString("moduleName");
                    int semester = resultSet.getInt("Semester");
                    int week = resultSet.getInt("Week");
                    byte[] lectureMaterial = resultSet.getBytes("Lecture_Material");
                    byte[] labMaterial = resultSet.getBytes("Lab_Material");

                    materialDetails.append("Module ID: ").append(moduleID).append("\n")
                            .append("Module Name: ").append(moduleName).append("\n")
                            .append("Semester: ").append(semester).append("\n")
                            .append("Week: ").append(week).append("\n");

                    if (lectureMaterial != null) {
                        materialDetails.append("Lecture Material: Available\n");
                    } else {
                        materialDetails.append("Lecture Material: Not available\n");
                    }

                    if (labMaterial != null) {
                        materialDetails.append("Lab Material: Available\n");
                    } else {
                        materialDetails.append("Lab Material: Not available\n");
                    }

                    materialDetails.append("\n");
                }
            }
        }

        if (materialDetails.length() > 0) {
            return materialDetails.toString();
        } else {
            return "No modules or materials found for the student";
        }
    }

    static String getModuleMarks(String username) throws SQLException {
        Connection connection = sqlConn;
        int ID = getIDByUsername(username);
        String query = "SELECT * FROM Marks2 WHERE studentID = ?";
        StringBuilder markDetails = new StringBuilder();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String moduleID = resultSet.getString("moduleID");
                    int labResult = resultSet.getInt("labResult");
                    int examResult = resultSet.getInt("examResult");
                    int overallMarks = resultSet.getInt("overallMarks");

                    markDetails.append("Module ID: ").append(moduleID).append("\n")
                            .append("Lab Result: ").append(labResult).append("\n")
                            .append("Exam Result: ").append(examResult).append("\n")
                            .append("Overall Mark: ").append(overallMarks).append("\n\n");
                }
            }
        }
        if (markDetails.length() > 0) {
            return markDetails.toString();
        } else {
            return "No marks found for this student";
        }
    }

    public static String getDecision(String username) throws SQLException {
        Connection connection = sqlConn;
        String query = "SELECT decision FROM Student2 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("decision");
                } else {
                    return "No decision awarded";
                }
            }
        }
    }
}





