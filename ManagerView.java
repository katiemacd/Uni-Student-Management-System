import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ManagerView extends JFrame {
    private String username;
    private JButton updatePasswordButton;
    private JButton viewSignUpWorkflowButton;
    private JButton approveUsersButton;
    private JButton LECTURER_assignModuleButton;
    private JButton enrolStudentButton;
    private JButton issueDecisionButton;
    private JButton addNewCourseButton;
    private JButton addModuleButton;
    private JButton COURSE_assignModuleButton;
    private JButton displayCourseDetailsButton;
    private JButton displayModuleDetailsButton;
    private JButton updateCourseInformationButton;
    private JButton activateAccountButton;
    private JButton deactivateAccountButton;
    private JButton resetAccountButton;

    private JButton logoutButton;

    public ManagerView(String username) {
        this.username = username;

        setTitle("Manager Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(16, 1));
        setSize(270, 420);


        updatePasswordButton = new JButton("Update password");
        viewSignUpWorkflowButton = new JButton("View users awaiting approval");
        approveUsersButton = new JButton("Approve user");
        LECTURER_assignModuleButton = new JButton("Assign module to lecturer");
        enrolStudentButton = new JButton("Enroll student");
        issueDecisionButton = new JButton("Issue student decision");
        addNewCourseButton = new JButton("Add new course");
        addModuleButton = new JButton("Add new module");
        COURSE_assignModuleButton = new JButton("Assign module to course");
        displayCourseDetailsButton = new JButton("Display course details");
        displayModuleDetailsButton = new JButton("Display module details");
        updateCourseInformationButton = new JButton("Update course information");
        activateAccountButton = new JButton("Activate a users account");
        deactivateAccountButton = new JButton("De-Activate a users account");
        resetAccountButton = new JButton("Reset a users account");
        logoutButton = new JButton("Logout");

        add(updatePasswordButton);
        add(viewSignUpWorkflowButton);
        add(approveUsersButton);
        add(LECTURER_assignModuleButton);
        add(enrolStudentButton);
        add(issueDecisionButton);
        add(addNewCourseButton);
        add(addModuleButton);
        add(COURSE_assignModuleButton);
        add(displayCourseDetailsButton);
        add(displayModuleDetailsButton);
        add(updateCourseInformationButton);
        add(activateAccountButton);
        add(deactivateAccountButton);
        add(resetAccountButton);
        add(logoutButton);

        updatePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newPassword = JOptionPane.showInputDialog("Please enter your new password:");

                User user = new User();

                try {
                    user.updatePasswordForUser(username, newPassword);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        viewSignUpWorkflowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String unapprovedStudents = Manager.getUnapproved(username);
                    JOptionPane.showMessageDialog(null, "All Unproved Users:\n\n" + unapprovedStudents);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        approveUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String unapprovedStudents = Manager.getUnapproved(username);
                    String[] users = unapprovedStudents.split("\n\n");
                    JButton[] approvalButtons = new JButton[users.length];
                    JPanel buttonPanel = new JPanel(new GridLayout(users.length, 2));
                    for (int i = 0; i < users.length; i++) {
                        String[] userInfo = users[i].split("\n");
                        String username = userInfo[0].trim();
                        JTextArea userTextArea = new JTextArea(users[i]);
                        userTextArea.setEditable(false);
                        buttonPanel.add(userTextArea);
                        buttonPanel.add(new JLabel(" "));
                        approvalButtons[i] = new JButton("Approve");
                        approvalButtons[i].addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    Manager.approveUser(username);
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                        buttonPanel.add(approvalButtons[i]);
                    }
                    JOptionPane.showMessageDialog(null, buttonPanel, "Unapproved Users: ", JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        activateAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String approvedStudent = Manager.getUnactivated(username);
                    String[] users = approvedStudent.split("\n\n");
                    JButton[] activateButton = new JButton[users.length];
                    JPanel buttonPanel = new JPanel(new GridLayout(users.length, 2));
                    for (int i = 0; i < users.length; i++) {
                        String[] userInfo = users[i].split("\n");
                        String username = userInfo[0].trim();
                        JTextArea userTextArea = new JTextArea(users[i]);
                        userTextArea.setEditable(false);
                        buttonPanel.add(userTextArea);
                        buttonPanel.add(new JLabel(" "));
                        activateButton[i] = new JButton("Activate");
                        activateButton[i].addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    Manager.activateAccount(username);
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                        buttonPanel.add(activateButton[i]);
                    }
                    JOptionPane.showMessageDialog(null, buttonPanel, "Unactivated Accounts: ", JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        deactivateAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String approvedStudent = Manager.getActivated(username);
                    String[] users = approvedStudent.split("\n\n");
                    JButton[] deactivateButton = new JButton[users.length];
                    JPanel buttonPanel = new JPanel(new GridLayout(users.length, 2));
                    for (int i = 0; i < users.length; i++) {
                        String[] userInfo = users[i].split("\n");
                        String username = userInfo[0].trim();
                        JTextArea userTextArea = new JTextArea(users[i]);
                        userTextArea.setEditable(false);
                        buttonPanel.add(userTextArea);
                        buttonPanel.add(new JLabel(" "));
                        deactivateButton[i] = new JButton("De-Activate");
                        deactivateButton[i].addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    Manager.deactivateAccount(username);
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                        buttonPanel.add(deactivateButton[i]);
                    }
                    JOptionPane.showMessageDialog(null, buttonPanel, "Activated Accounts: ", JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        resetAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String unactivatedStudent = Manager.getAllAccounts(username);
                    String[] users = unactivatedStudent.split("\n\n");
                    JButton[] resetButton = new JButton[users.length];
                    JPanel buttonPanel = new JPanel(new GridLayout(users.length, 2));
                    for (int i = 0; i < users.length; i++) {
                        String[] userInfo = users[i].split("\n");
                        String username = userInfo[0].trim();
                        JTextArea userTextArea = new JTextArea(users[i]);
                        userTextArea.setEditable(false);
                        buttonPanel.add(userTextArea);
                        buttonPanel.add(new JLabel(" "));
                        resetButton[i] = new JButton("Reset Account");
                        resetButton[i].addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    Manager.resetAccount(username);
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                        buttonPanel.add(resetButton[i]);
                    }
                    JOptionPane.showMessageDialog(null, buttonPanel, "All Accounts that can be reset: ", JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        addNewCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Retrieve a list of available departments
                    ResultSet departmentList = Manager.getAllDepartments();

                    List<String> departments = new ArrayList<>();
                    while (departmentList.next()) {
                        departments.add(departmentList.getString("departmentID"));
                    }

                    // Show a dropdown to select the department
                    String selectedDepartment = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a department:",
                            "Add New Course",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            departments.toArray(new String[0]),
                            departments.get(0) // Default selection
                    );

                    if (selectedDepartment != null) {
                        String courseName = JOptionPane.showInputDialog("Enter new course name:");
                        String courseDescription = JOptionPane.showInputDialog("Enter course description:");

                        try {
                            Manager manager = new Manager();
                            manager.addNewCourse(courseName, courseDescription, Integer.parseInt(selectedDepartment));
                            JOptionPane.showMessageDialog(
                                    null,
                                    "New course added successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Error adding new course: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            "Error retrieving departments: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        addModuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Manager manager = new Manager();
                String moduleID = JOptionPane.showInputDialog("Enter new module ID");
                String moduleName = JOptionPane.showInputDialog("Enter new module name:");
                String moduleDescription = JOptionPane.showInputDialog("Enter module description:");

                try {
                    manager.addNewModule(moduleID, moduleName, moduleDescription);
                    JOptionPane.showMessageDialog(null, "New module added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error adding new module: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        COURSE_assignModuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Manager manager = new Manager();

                try {
                    // Retrieve a list of available courses and modules
                    ResultSet courseList = Manager.getAllCourses();
                    ResultSet moduleList = Manager.getAllModules();

                    List<String> courses = new ArrayList<>();
                    while (courseList.next()) {
                        courses.add(courseList.getString("courseName"));
                    }

                    List<String> modules = new ArrayList<>();
                    while (moduleList.next()) {
                        modules.add(moduleList.getString("moduleID"));
                    }

                    // Show dropdowns to select course and module
                    String courseName = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a course:",
                            "Assign Module to Course",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            courses.toArray(new String[0]),
                            courses.get(0) // Default selection
                    );

                    String moduleID = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a module:",
                            "Assign Module to Course",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            modules.toArray(new String[0]),
                            modules.get(0) // Default selection
                    );

                    String courseID =Integer.toString(Manager.getCourseID(courseName));

                    if (courseID != null && moduleID != null) {
                        try {
                            // Check if the module is already assigned to the course
                            if (manager.moduleAssignedToCourse(moduleID, courseID)) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Module is already assigned to the course",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            } else {
                                // If not assigned, proceed to assign the module
                                manager.assignModuleToCourse(courseID, moduleID);
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Module assigned to course successfully",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Error assigning module to course: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            "Error retrieving courses or modules: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        LECTURER_assignModuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Manager manager = new Manager();

                try {
                    // Retrieve a list of available modules and lecturers
                    ResultSet moduleList = Manager.getAllModules();
                    ResultSet lecturerList = Manager.getAllLecturers();

                    List<String> modules = new ArrayList<>();
                    while (moduleList.next()) {
                        modules.add(moduleList.getString("moduleID"));
                    }

                    List<String> lecturers = new ArrayList<>();
                    while (lecturerList.next()) {
                        lecturers.add(lecturerList.getString("username"));
                    }


                    // Show dropdowns to select module and lecturer
                    String moduleID = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a module:",
                            "Assign Lecturer to Module",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            modules.toArray(new String[0]),
                            modules.get(0) // Default selection
                    );

                    String lecturerID = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a lecturer:",
                            "Assign Lecturer to Module",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            lecturers.toArray(new String[0]),
                            lecturers.get(0) // Default selection
                    );

                    if (moduleID != null && lecturerID != null) {
                        try {
                            // Check if the module is already assigned to the lecturer
                            if (manager.moduleAssignedToLecturer(moduleID, Lecturer.getLecturerID(lecturerID))) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Module is already assigned to the lecturer",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            } else {
                                // If not assigned, proceed to assign the lecturer
                                manager.assignModuleToLecturer(moduleID, Lecturer.getLecturerID(lecturerID));
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Lecturer assigned to module successfully",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Error assigning lecturer to module: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            "Error retrieving modules or lecturers: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        updateCourseInformationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Retrieve a list of available courses
                    ResultSet courseList = Manager.getAllCourses();

                    List<String> courses = new ArrayList<>();
                    while (courseList.next()) {
                        courses.add(courseList.getString("courseName"));
                    }

                    // Show a dropdown to select the course
                    String selectedCourse = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a course:",
                            "Update Course Information",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            courses.toArray(new String[0]),
                            courses.get(0) // Default selection
                    );

                    if (selectedCourse != null) {
                        String newCourseDescription = JOptionPane.showInputDialog("Enter new course description:");
                        String selected = Integer.toString(Manager.getCourseID(selectedCourse));

                        try {
                            Manager manager = new Manager();
                            manager.updateCourseInformation(selected, newCourseDescription);
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Course information updated successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Error updating course information: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            "Error retrieving courses: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        enrolStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get a list of student usernames
                    ResultSet studentUsernames = Manager.getAllStudents();
                    List<String> usernameList = new ArrayList<>();
                    while (studentUsernames.next()) {
                        usernameList.add(studentUsernames.getString("username"));
                    }

                    // Convert the list to an array for the JOptionPane
                    String[] usernamesArray = usernameList.toArray(new String[0]);

                    // Show a dialog to select a student
                    String selectedStudent = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a student to enroll:",
                            "Enroll Student",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            usernamesArray,
                            usernamesArray[0] // Default selection
                    );

                    if (selectedStudent != null) {
                        try {
                            // Get a list of course IDs
                            ResultSet allCourses = Manager.getAllCourses();
                            List<String> coursesList = new ArrayList<>();
                            while (allCourses.next()) {
                                coursesList.add(allCourses.getString("courseName"));
                            }

                            // Convert the list to an array for the JOptionPane
                            String[] coursesArray = coursesList.toArray(new String[0]);

                            // Show a dialog to select a course
                            String selectedCourse = (String) JOptionPane.showInputDialog(
                                    null,
                                    "Select a course:",
                                    "Enroll Student",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    coursesArray,
                                    coursesArray[0] // Default selection
                            );


                            if (selectedCourse != null) {
                                try {
                                    // Attempt to parse the selectedCourse to an int
                                    int courseID = Manager.getCourseID(selectedCourse);

                                    // Show a confirmation dialog
                                    int result = JOptionPane.showConfirmDialog(
                                            null,
                                            "Enroll Student " + selectedStudent + " in Course: " + selectedCourse + "?",
                                            "Enroll Student",
                                            JOptionPane.OK_CANCEL_OPTION
                                    );

                                    if (result == JOptionPane.OK_OPTION) {
                                        try {
                                            // Check if the student is already enrolled in the course
                                            if (Manager.isEnrolled(selectedStudent, courseID)) {
                                                JOptionPane.showMessageDialog(
                                                        null,
                                                        "Student is already enrolled in the course",
                                                        "Error",
                                                        JOptionPane.ERROR_MESSAGE
                                                );
                                            } else {
                                                // Enroll the student in the course
                                                Manager.enrollStudentCourse(selectedStudent, courseID);
                                                JOptionPane.showMessageDialog(
                                                        null,
                                                        "Student enrolled!",
                                                        "Success",
                                                        JOptionPane.INFORMATION_MESSAGE
                                                );
                                            }
                                        } catch (SQLException ex) {
                                            // Handle any SQL exceptions during enrollment
                                            ex.printStackTrace();
                                            JOptionPane.showMessageDialog(
                                                    null,
                                                    "Could not enroll student",
                                                    "Error",
                                                    JOptionPane.ERROR_MESSAGE
                                            );
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                    // Handle the case where the selected course is not a valid integer
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Invalid Course ID. Please select a valid course.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                        } catch (SQLException ex) {
                            // Handle any SQL exceptions during course selection
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Could not retrieve courses",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } catch (SQLException ex) {
                    // Handle any SQL exceptions during student selection
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            null,
                            "Could not retrieve student usernames",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });


        issueDecisionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ResultSet studentUsernames = Manager.getAllStudents();
                    List<String> usernameList = new ArrayList<>();
                    while (studentUsernames.next()) {
                        usernameList.add(studentUsernames.getString("username"));
                    }
                    String[] usernamesArray = usernameList.toArray(new String[0]);

                    String selectedStudent = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a student to award decision:",
                            "Issue Decision",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            usernamesArray,
                            usernamesArray[0]
                    );

                    if (selectedStudent != null) {
                        String moduleMarksDetails = Student.getModuleMarks(selectedStudent);
                        if (!moduleMarksDetails.equals("No marks found for this student")) {
                            displayDecisionDialog(selectedStudent, moduleMarksDetails);
                        } else {
                            // Handle the case where there are no modules found for the course
                            JOptionPane.showMessageDialog(
                                    null,
                                    "No marks found for this student",
                                    "No Marks",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        displayCourseDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String allCourseDetails = Manager.getCourseDetails();
                    JOptionPane.showMessageDialog(null, "All Course Details :\n\n" + allCourseDetails);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        displayModuleDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String allModuleDetails = Manager.getModuleDetails();
                    JOptionPane.showMessageDialog(null, "All module details :\n\n" + allModuleDetails);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UserLoginView loginView = new UserLoginView();

                User model = new User();
                LoginController loginController = new LoginController(loginView, model);

                dispose();

                loginView.setVisible(true);
            }
        });
    }

    private void displayDecisionDialog(String studentUsername, String moduleMarksDetails) {
        JTextArea marksTextArea = new JTextArea(moduleMarksDetails);
        marksTextArea.setEditable(false);

        JComboBox<String> decisionComboBox = new JComboBox<>();
        decisionComboBox.addItem("Pass");
        decisionComboBox.addItem("Resit");
        decisionComboBox.addItem("Withdraw");

        Object[] fields = {
                "Module Marks Details:", marksTextArea,
                "Select Decision:", decisionComboBox,
        };

        int result = JOptionPane.showConfirmDialog(
                null,
                fields,
                "Issue Decision for " + studentUsername,
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedDecision = (String) decisionComboBox.getSelectedItem();
            processDecision(studentUsername, selectedDecision);
        }
    }

    private void processDecision(String studentUsername, String decision) {
        try {
            Manager.issueDecision(studentUsername, decision);
            JOptionPane.showMessageDialog(
                    null,
                    "Decision issued successfully for " + studentUsername,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error issuing decision: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        }
    }
}

