import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class StudentView extends JFrame {
    private String username;
    private JButton updatePasswordButton;
    private JButton downloadMaterialButton;
    private JButton viewCourseButton;
    private JButton viewModuleMaterialButton;
    private JButton viewDecisionButton;
    private JButton viewModuleMarksButton;

    private JButton logoutButton;

    public StudentView(String username) {
        this.username = username;

        setTitle("Student Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1));
        setSize(270, 420);

        updatePasswordButton = new JButton("Update password");
        downloadMaterialButton = new JButton("Download Module Material");
        viewCourseButton = new JButton("View Enrolled Course");
        viewModuleMaterialButton = new JButton("View Module Material");
        viewDecisionButton = new JButton("View Decision");
        viewModuleMarksButton = new JButton("View Module Marks");
        logoutButton = new JButton("Logout");

        add(updatePasswordButton);
        add(downloadMaterialButton);
        add(viewCourseButton);
        add(viewModuleMaterialButton);
        add(viewModuleMarksButton);
        add(viewDecisionButton);
        add(logoutButton);

        // update password
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

        downloadMaterialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int courseID = Student.getCourseID(username);

                    List<String> moduleIDsList = Student.getModuleIDsForCourse(courseID);

                    ArrayList<String> moduleIDs = new ArrayList<>(moduleIDsList);

                    if (moduleIDs.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No modules found for the enrolled course", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JComboBox<String> moduleComboBox = new JComboBox<>(moduleIDs.toArray(new String[0]));

                    int moduleResult = JOptionPane.showOptionDialog(null, moduleComboBox, "Select Module:",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (moduleResult != JOptionPane.OK_OPTION) {
                        return; // User canceled
                    }

                    String selectedModule = (String) moduleComboBox.getSelectedItem();
                    String moduleID = selectedModule.trim();

                    String[] semesterOptions = {"1", "2"};
                    JComboBox<String> semesterComboBox = new JComboBox<>(semesterOptions);
                    int semesterResult = JOptionPane.showOptionDialog(null, semesterComboBox, "Select Semester:",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (semesterResult != JOptionPane.OK_OPTION) {
                        return;
                    }

                    String semester = (String) semesterComboBox.getSelectedItem();

                    String[] weekOptions = {"1", "2", "3"};
                    JComboBox<String> weekComboBox = new JComboBox<>(weekOptions);
                    int weekResult = JOptionPane.showOptionDialog(null, weekComboBox, "Select Week:",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (weekResult != JOptionPane.OK_OPTION) {
                        return;
                    }

                    String week = (String) weekComboBox.getSelectedItem();

                    String[] materialTypes = {"lecture", "lab"};
                    JComboBox<String> materialTypeComboBox = new JComboBox<>(materialTypes);
                    int materialTypeResult = JOptionPane.showOptionDialog(null, materialTypeComboBox, "Select Material Type:",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (materialTypeResult != JOptionPane.OK_OPTION) {
                        return;
                    }

                    String materialType = (String) materialTypeComboBox.getSelectedItem();

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Choose download destination");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    int userSelection = fileChooser.showSaveDialog(null);//doing this for a push

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        String destinationPath = selectedFile.getAbsolutePath() + File.separatorChar + moduleID + "_" + materialType + ".extension";

                        if (!selectedFile.exists()) {
                            selectedFile.mkdirs();
                        }

                        Student student = new Student();

                        try {
                            student.downloadModuleMaterials(moduleID, destinationPath, Integer.parseInt(semester), Integer.parseInt(week), materialType);

                            JOptionPane.showMessageDialog(null, "Module materials downloaded successfully",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (SQLException | IOException ex) {
                            JOptionPane.showMessageDialog(null, "Error downloading module materials: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error fetching enrolled course: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        viewCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String studentCourseDetails = Student.getStudentCourse(username);
                    JOptionPane.showMessageDialog(null, "Enrolled Course Details:\n" + studentCourseDetails);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        viewModuleMaterialButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    String studentMaterialDetails = Student.getMaterialDetailsForStudent(username);
                    JOptionPane.showMessageDialog(null, "Enrolled Module Materials:\n" + studentMaterialDetails);
                }
                catch (SQLException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });

        viewModuleMarksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String moduleMarksDetails = Student.getModuleMarks(username);

                    if (!moduleMarksDetails.equals("No marks found for this student")) {
                        // Display the module marks details
                        JOptionPane.showMessageDialog(null, "Module Marks Details:\n\n" + moduleMarksDetails);
                    } else {
                        // Handle the case where there are no modules found for the course
                        JOptionPane.showMessageDialog(null, "No marks found for this student", "No Modules", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        viewDecisionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String decision = Student.getDecision(username);

                    if (decision != null) {
                        if (decision.equals("Pass")) {
                            JOptionPane.showMessageDialog(null, "Congratulations! You have passed.");
                        } else if (decision.equals("Resit")) {
                            JOptionPane.showMessageDialog(null, "You need to resit some modules. Please check your marks.");
                        } else if (decision.equals("Withdraw")) {
                            JOptionPane.showMessageDialog(null, "You have withdrawn from the course.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Unknown decision: " + decision);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No decision available.");
                    }
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
}
