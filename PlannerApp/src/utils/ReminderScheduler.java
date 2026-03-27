package utils;

import database.TaskDAO;
import database.UserDAO;
import Model.Task;
import Model.User;
import telegram.TelegramBot;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderScheduler {
    private static Timer timer;

    public static void start() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    List<Task> reminders = TaskDAO.getPendingReminders();
                    for (Task task : reminders) {
                        User user = UserDAO.getUserById(task.getUserId());
                        if (user != null && user.getTelegramId() != null && !user.getTelegramId().isEmpty()) {
                            String message = "🔔 НАПОМИНАНИЕ!\n\n" +
                                    "Задача: " + task.getTitle() + "\n" +
                                    "Время: " + task.getRemindAt();
                            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                                message += "\nОписание: " + task.getDescription();
                            }
                            TelegramBot.sendMessage(user.getTelegramId(), message);
                            TaskDAO.markNotified(task.getId());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reminder error: " + e.getMessage());
                }
            }
        }, 0, 60000);

        System.out.println("Система напоминаний запущена");
    }
    public static void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}