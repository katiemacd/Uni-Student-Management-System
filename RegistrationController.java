import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationController {
    private UserRegistrationView view;
    private User model;

    public RegistrationController(UserRegistrationView view, User model) {
        this.view = view;
        this.model = model;
        this.view.setRegisterButtonListener(new RegisterButtonListener());
    }

    class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get user input from the view
                String username = view.getUsername();
                String firstName = view.getFirstName();
                String surname = view.getSurname();
                String email = view.getEmail();
                String DOB = view.getDOB();
                String password = view.getPassword();
                String role = view.getRole();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                if (username.isEmpty() || firstName.isEmpty() || surname.isEmpty() ||
                        email.isEmpty() || DOB.isEmpty() || password.isEmpty()) {
                    view.showError("All fields must be filled out. Registration failed.");
                    return;
                }

                if (!view.isValidEmail(email)) {
                    view.showError("Invalid email address. Please enter a valid email.");
                    return;
                }

                if (model.isUserExists(username)) {
                    view.showError("Username already exists. Please choose a different username.");
                    return;
                }

                if (model.isEmailExists(email)) {
                    view.showError("Email is already registered. Please use a different email.");
                    return;
                }

                try {
                    java.util.Date parsedDate = dateFormat.parse(DOB);
                    java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                    model.registerUser(username, firstName, surname, email, sqlDate, password, role);
                    JOptionPane.showMessageDialog(null, "Registration successful!");

                    view.clearFields();
                } catch (ParseException ex) {
                    view.showError("Invalid date format. Please enter a valid date.");
                }
            } catch (NoSuchAlgorithmException | SQLException ex) {
                view.showError("Registration failed. Please try again.");
            }
        }
    }
}