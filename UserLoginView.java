import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserLoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    public UserLoginView() {
        // Set up the main frame
        setTitle("User Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setSize(270, 420);
        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");

        registerButton = new JButton("Register");


        // Add components to the frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerButton); // Add the button to the frame

        // Register button action
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                // Call the registration method of your AuthenticationService here

                // Clear the fields after registration
                usernameField.setText("");
                passwordField.setText("");

            }
        });



        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UserRegistrationView registrationView = new UserRegistrationView();

                User model = new User();
                RegistrationController registrationController = new RegistrationController(registrationView, model);

                dispose();

                registrationView.setVisible(true);
            }
        });


    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void showLoginSuccessMessage() {
        JOptionPane.showMessageDialog(this, "Login successful!");
    }
    public void showUserNotApproved() {
        JOptionPane.showMessageDialog(this, "Login failed! User not yet approved.");
    }
    public void showUserNotActivated() {JOptionPane.showMessageDialog(this, "Login failed! Account is not activated.");}
    public void showLoginErrorMessage() {
        JOptionPane.showMessageDialog(this, "Login failed. Please try again.");
    }


}

