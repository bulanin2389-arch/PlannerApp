package UI;

import database.TaskDAO;
import Model.Task;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddTaskDialog extends JDialog {
    private JTextField titleField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextArea descArea;
    private CalendarWindow parent;
    private User currentUser;

    public AddTaskDialog(CalendarWindow parent, User user) {
        this.parent = parent;
        this.currentUser = user;
        setTitle("Добавить задачу");
        setModal(true);
        setSize(450, 350);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleField = new JTextField(20);
        dateField = new JTextField(LocalDate.now().toString());
        timeField = new JTextField("12:00");
        descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Название:*"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Дата (ГГГГ-ММ-ДД):"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Время (ЧЧ:ММ):"), gbc);
        gbc.gridx = 1;
        panel.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Описание:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);

        JButton saveBtn = new JButton("Сохранить");
        JButton cancelBtn = new JButton("Отмена");

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveTask());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void saveTask() {
        String title = titleField.getText().trim();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String desc = descArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите название задачи");
            return;
        }

        String datetime = date + " " + time;
        Task task = new Task(0, title, desc, datetime, false, currentUser.getId());
        TaskDAO.addTask(task);

        parent.updateCalendar();
        dispose();
        JOptionPane.showMessageDialog(parent, "Задача добавлена!");
    }
}