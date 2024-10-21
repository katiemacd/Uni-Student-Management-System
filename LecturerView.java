import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.security.NoSuchAlgorithmException;

public class LecturerView extends JFrame
{
    private String username;
    private JButton updatePasswordButton;
    private JButton updateModuleInformationButton;
    private JButton updateModuleMaterialButton;
    private JButton viewEnrolledButton;
    private JButton updateRecordButton;
    private JButton addQualificationButton;

    private JButton logoutButton;
    private JComboBox<String> qualificationComboBox;



    public LecturerView(String username)
    {
        this.username = username;

        setTitle("Lecturer Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1));
        setSize(270, 420);


        updatePasswordButton = new JButton("Update Password");
        updateModuleInformationButton = new JButton("Update Module Information");
        updateModuleMaterialButton = new JButton("Upload Module Materials");
        viewEnrolledButton = new JButton("Display Enrolled Students");
        updateRecordButton = new JButton("Update Exam Records");
        addQualificationButton = new JButton("Add Qualification");

        String[] qualifications = {"PhD", "MSc", "BSc"};
        qualificationComboBox = new JComboBox<>(qualifications);
        logoutButton = new JButton("Logout");

        add(addQualificationButton);
        add(updatePasswordButton);
        add(updateModuleInformationButton);
        add(updateModuleMaterialButton);
        add(viewEnrolledButton);
        add(updateRecordButton);
        add(logoutButton);

        addQualificationButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int option = JOptionPane.showOptionDialog(
                        null,
                        qualificationComboBox,
                        "Choose your qualification",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        null
                );

                if (option == JOptionPane.OK_OPTION)
                {
                    String selectedQualification = (String) qualificationComboBox.getSelectedItem();

                    try
                    {
                        Lecturer.addQualification(username, selectedQualification);
                    }
                    catch (SQLException | NoSuchAlgorithmException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });

        // Update password
        updatePasswordButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String newPassword = JOptionPane.showInputDialog("Please enter your new password:");

                if (newPassword != null && !newPassword.trim().isEmpty())
                {
                    User user = new User();
                    try
                    {
                        user.updatePasswordForUser(username, newPassword);
                    }
                    catch (SQLException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
                else
                {
                    // User closed the dialog without entering a new password
                    JOptionPane.showMessageDialog(null, "Unable to change password. No password entered.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Update module information (name and description)

        updateModuleInformationButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Allow the user to enter new module information
                String newModuleName = JOptionPane.showInputDialog("Enter new module name:");
                String newModuleDescription = JOptionPane.showInputDialog("Enter new module description:");

                Lecturer lecturer = new Lecturer();

                try
                {
                    // Update module information in database
                    lecturer.updateModuleInformation(username, newModuleName, newModuleDescription);
                    JOptionPane.showMessageDialog(null, "Module information updated successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (SQLException ex)
                {
                    // Handle SQLException
                    JOptionPane.showMessageDialog(null, "Error updating module information: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateModuleMaterialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    int semester;
                    String semesterInput = JOptionPane.showInputDialog("Enter Semester (1/2):");
                    try {
                        semester = Integer.parseInt(semesterInput);
                        if (semester != 1 && semester != 2) {
                            JOptionPane.showMessageDialog(null, "Invalid semester. Please enter 1 or 2.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number for semester.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int week;
                    String weekInput = JOptionPane.showInputDialog("Enter Week (1-3):");
                    try {
                        week = Integer.parseInt(weekInput);
                        if (week < 1 || week > 3) {
                            JOptionPane.showMessageDialog(null, "Invalid week. Please enter a value between 1 and 3.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid number for week.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String[] materialTypes = {"lecture", "lab"};
                    JComboBox<String> materialTypeComboBox = new JComboBox<>(materialTypes);
                    int materialTypeResult = JOptionPane.showOptionDialog(null, materialTypeComboBox, "Select Material Type",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (materialTypeResult == JOptionPane.OK_OPTION) {
                        String materialType = (String) materialTypeComboBox.getSelectedItem();

                        UUID uniqueIdentifier = UUID.randomUUID();

                        Lecturer lecturer = new Lecturer();
                        try {
                            lecturer.uploadModuleMaterials(username, selectedFile, semester, week, materialType, uniqueIdentifier);

                            JOptionPane.showMessageDialog(null, "Module materials uploaded successfully",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);

                        } catch (SQLException | IOException ex) {
                            JOptionPane.showMessageDialog(null, "Error uploading module materials: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        viewEnrolledButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Lecturer lecturer = new Lecturer();
                String ID = lecturer.getLecturerID(username);
                String MID = lecturer.getModuleID(ID);
                List<String> studentUsernames = lecturer.getAllStudentInfo(MID);

                // Create a JFrame to display the Swing component
                JFrame frame = new JFrame("Students enrolled in your module:");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Create a JPanel to hold the components
                JPanel panel = new JPanel(new BorderLayout());

                // Create a JLabel for the title
                JLabel titleLabel = new JLabel("Students enrolled in your module:");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(titleLabel, BorderLayout.NORTH);


                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);

                // Add student usernames to the JTextArea
                for (String username : studentUsernames)
                {
                    textArea.append("Student: " + username + "\n");
                }

                // Create a JScrollPane to add scrolling functionality if needed
                JScrollPane scrollPane = new JScrollPane(textArea);

                // Add the scroll pane to the panel
                panel.add(scrollPane, BorderLayout.CENTER);

                // Add the panel to the frame
                frame.getContentPane().add(panel);

                frame.setSize(400, 300);
                frame.setVisible(true);
            }
        });


        updateRecordButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Lecturer lecturer = new Lecturer();
                String ID = lecturer.getLecturerID(username);
                String MID = lecturer.getModuleID(ID);
                List<String> studentUsernames = lecturer.getAllStudentInfo(MID);

                // Create a dialog to select a student
                String selectedStudent = (String) JOptionPane.showInputDialog(
                        null,
                        "Select a student to update exam records:",
                        "Update Exam Record",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        studentUsernames.toArray(),
                        studentUsernames.get(0) // Default selection
                );

                if (selectedStudent != null)
                {
                    // Create a dialog to input lab result, exam result, and overall mark
                    JTextField labResultField = new JTextField();
                    JTextField examResultField = new JTextField();
                    JTextField overallMarkField = new JTextField();

                    Object[] fields = {
                            "Lab Result:", labResultField,
                            "Exam Result:", examResultField,
                            "Overall Mark:", overallMarkField
                    };

                    int result = JOptionPane.showConfirmDialog(
                            null,
                            fields,
                            "Enter Exam Records for " + selectedStudent,
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (result == JOptionPane.OK_OPTION)
                    {
                        try
                        {
                            // Update exam records in the Mark table
                            lecturer.updateExamRecords((selectedStudent),
                                    labResultField.getText(),
                                    examResultField.getText(),
                                    overallMarkField.getText(), MID);

                            // Inform the lecturer that the update was successful
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Exam records updated successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }
                        catch (SQLException ex)
                        {
                            // Handle any SQL exceptions
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Error updating exam records. Please try again.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
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
}



