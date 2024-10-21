public class driver {
    public static void main(String[] args) {
        User model = new User();
        UserLoginView view = new UserLoginView();
        LoginController Login = new LoginController(view, model);
        view.setVisible(true);
    }
}
