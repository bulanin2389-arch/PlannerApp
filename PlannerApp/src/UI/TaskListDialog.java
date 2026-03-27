package UI;

import database.TaskDAO;
import Model.Task;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TaskListDialog extends JDialog {
    public TaskListDialog(CalendarWindow parent, User user, int year, int month, int day) {
        setTitle("Задачи на " + year + "-" + month + "-" + day);
        setModal(true);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        List<Task> tasks = TaskDAO.getTasksByDate(user.getId(), year, month, day);

        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет задач на этот день");
            dispose();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📅 ЗАДАЧИ НА ").append(year).append("-").append(month).append("-").append(day).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            sb.append(i + 1).append(". ").append(t.getTitle());
            if (t.getTime() != null && !t.getTime().isEmpty()) {
                sb.append(" ⏰ ").append(t.getTime());
            }
            sb.append("\n");
            if (t.getDescription() != null && !t.getDescription().isEmpty()) {
                sb.append("   📝 ").append(t.getDescription()).append("\n");
            }
            sb.append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(450, 350));

        add(scroll);
    }
}