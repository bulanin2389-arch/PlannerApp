package UI;

import database.UserDAO;
import Model.User;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginWindow() {
        setTitle("Планировщик - Вход");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(new JLabel("Логин:"));
        panel.add(usernameField);
        panel.add(new JLabel("Пароль:"));
        panel.add(passwordField);

        JButton loginBtn = new JButton("Вход");
        JButton registerBtn = new JButton("Регистрация");

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = UserDAO.loginUser(username, password);
        if (user != null) {
            dispose();
            new CalendarWindow(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Неверный логин или пароль");
        }
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля");
            return;
        }

        if (UserDAO.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Регистрация успешна!");
        } else {
            JOptionPane.showMessageDialog(this, "Пользователь уже существует");
        }
    }
}