package UI;

import database.TaskDAO;
import Model.Task;
import Model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class CalendarWindow extends JFrame {
    private User currentUser;
    private JTable calendarTable;
    private DefaultTableModel tableModel;
    private JLabel monthLabel;
    private int currentYear;
    private int currentMonth;

    public CalendarWindow(User user) {
        this.currentUser = user;
        setTitle("Планировщик - " + user.getUsername());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        LocalDate now = LocalDate.now();
        currentYear = now.getYear();
        currentMonth = now.getMonthValue();

        initUI();
        updateCalendar();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton addTaskBtn = new JButton("+ Добавить задачу");
        addTaskBtn.setBackground(new Color(100, 200, 100));

        controlPanel.add(prevBtn);
        controlPanel.add(monthLabel);
        controlPanel.add(nextBtn);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(addTaskBtn);

        prevBtn.addActionListener(e -> changeMonth(-1));
        nextBtn.addActionListener(e -> changeMonth(1));
        addTaskBtn.addActionListener(e -> new AddTaskDialog(this, currentUser).setVisible(true));

        tableModel = new DefaultTableModel(6, 7) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        tableModel.setColumnIdentifiers(days);

        calendarTable = new JTable(tableModel);
        calendarTable.setRowHeight(80);
        calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());

        calendarTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = calendarTable.getSelectedRow();
                int col = calendarTable.getSelectedColumn();
                if (row >= 0 && col >= 0) {
                    Object val = tableModel.getValueAt(row, col);
                    if (val != null) {
                        String valStr = val.toString();
                        String dayStr = valStr.split("\n")[0].trim();
                        try {
                            int day = Integer.parseInt(dayStr);
                            new TaskListDialog(CalendarWindow.this, currentUser, currentYear, currentMonth, day).setVisible(true);
                        } catch (NumberFormatException ex) {
                        }
                    }
                }
            }
        });

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(calendarTable), BorderLayout.CENTER);
        add(mainPanel);
    }

    private void changeMonth(int delta) {
        LocalDate date = LocalDate.of(currentYear, currentMonth, 1).plusMonths(delta);
        currentYear = date.getYear();
        currentMonth = date.getMonthValue();
        updateCalendar();
    }

    public void updateCalendar() {
        monthLabel.setText(String.format("%d年 %d月", currentYear, currentMonth));

        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        int daysInMonth = firstDay.lengthOfMonth();
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                tableModel.setValueAt("", i, j);
            }
        }

        int day = 1;
        int row = 0;
        int col = firstDayOfWeek - 1;

        while (day <= daysInMonth) {
            List<Task> tasks = TaskDAO.getTasksByDate(currentUser.getId(), currentYear, currentMonth, day);
            String display = String.valueOf(day);
            if (!tasks.isEmpty()) {
                display = day + "\n• " + tasks.get(0).getTitle();
                if (tasks.size() > 1) {
                    display += " +" + (tasks.size() - 1);
                }
            }
            tableModel.setValueAt(display, row, col);
            day++;
            col++;
            if (col >= 7) {
                col = 0;
                row++;
            }
        }
    }

    private class CalendarCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setVerticalAlignment(SwingConstants.TOP);

            if (value != null) {
                String valStr = value.toString();
                if (valStr.contains("•")) {
                    label.setText("<html>" + valStr.replace("\n", "<br>") + "</html>");
                    label.setForeground(new Color(0, 100, 0));
                } else if (valStr.matches("\\d+")) {
                    label.setText(valStr);
                    label.setFont(label.getFont().deriveFont(Font.BOLD, 12));
                    label.setForeground(Color.BLACK);
                }
            }

            if (column == 6 && value != null && !value.toString().contains("•")) {
                label.setForeground(Color.RED);
            }

            return label;
        }
    }
}