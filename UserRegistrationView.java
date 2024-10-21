import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserRegistrationView extends JFrame {
    private JTextField usernameField;
    private JTextField firstNameField;
    private JTextField surnameField;
    private JTextField emailField;
    private JTextField DOBField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton loginButton;

    public UserRegistrationView() {
        // Set up the main frame
        setTitle("User Registration");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Adjust the size of the frame
        setSize(270, 420);


        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel firstnameLabel = new JLabel("First Name:");
        firstNameField = new JTextField(20);

        JLabel surnameLabel = new JLabel("Surname:");
        surnameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email: ");
        emailField = new JTextField(20);

        JLabel DOBLabel = new JLabel("Date of Birth:");
        DOBField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"Student", "Lecturer"};
        roleComboBox = new JComboBox<>(roles);

        registerButton = new JButton("Register");

        loginButton = new JButton("Log In");

        // Add components to the frame
        add(usernameLabel);
        add(usernameField);
        add(firstnameLabel);
        add(firstNameField);
        add(surnameLabel);
        add(surnameField);
        add(emailLabel);
        add(emailField);
        add(DOBLabel);
        add(DOBField);
        add(passwordLabel);
        add(passwordField);
        add(roleLabel);
        add(roleComboBox);
        add(registerButton);
        add(loginButton);

        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        loginButton.addActionListener(new ActionListener() {
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

    void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    void clearFields() {
        usernameField.setText("");
        firstNameField.setText("");
        surnameField.setText("");
        emailField.setText("");
        DOBField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
    }

    public void setRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getFirstName() {
        return firstNameField.getText();
    }

    public String getSurname() {
        return surnameField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getDOB() {
        return DOBField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getRole() {
        return (String) roleComboBox.getSelectedItem();
    }

    boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserRegistrationView registrationView = new UserRegistrationView();
            registrationView.setVisible(true);
        });
    }
}