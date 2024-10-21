import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class LoginController {
    private UserLoginView view;
    private User model;

    public LoginController(UserLoginView view, User model) {
        this.view = view;
        this.model = model;
        view.setLoginButtonListener(new LoginButtonListener());
    }

    class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get user input from the view
                String username = view.getUsername();
                String password = view.getPassword();

                // Check if the username exists in the database
                if (User.isUserExists(username)) {
                    // Get the stored password for the username from the database
                    String storedPassword = model.getPasswordForUser(username);
                    // Check if the entered password matches the stored password
                    if (storedPassword.equals(password)) {
                        String userRole = User.getUserRole(username);
                        if (User.isUserApproved(username)) {
                            if (User.isUserActivated(username)) {
                                if ("Student".equals(userRole)) {
                                    // Open the student view
                                    view.showLoginSuccessMessage();
                                    StudentView student = new StudentView(username);

                                    view.dispose();
                                    // Display the registration view
                                    student.setVisible(true);
                                    if (!Student.isStudentExists(username)) {
                                        Student.setUpStudent(username);
                                    }
                                }

                                if ("Lecturer".equals(userRole)) {
                                    // Open the student view
                                    view.showLoginSuccessMessage();
                                    LecturerView lecturer = new LecturerView(username);

                                    view.dispose();
                                    // Display the registration view
                                    lecturer.setVisible(true);
                                    if (!Lecturer.isLecturerExists(username)) {
                                        Lecturer.setUpLecturer(username);
                                    }
                                }

                                if ("Manager".equals(userRole)) {
                                    // Open the student view
                                    view.showLoginSuccessMessage();
                                    ManagerView manager = new ManagerView(username);

                                    view.dispose();
                                    // Display the registration view
                                    manager.setVisible(true);
                                    if (!Manager.isManagerExists(username)) {
                                        Manager.setUpManager(username);
                                    }
                                }
                            }
                            else {
                            view.showUserNotActivated();

                            }
                        } else {
                            view.showUserNotApproved();
                        }
                    } else {
                        view.showLoginErrorMessage();
                    }
                } else {
                    view.showLoginErrorMessage();
                }
            } catch (SQLException ex) {
                // Handle any database-related exceptions (e.g., show an error message)
                view.showLoginErrorMessage();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
