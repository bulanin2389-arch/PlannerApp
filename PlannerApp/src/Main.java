
import database.DatabaseManager;
import telegram.TelegramBot;
import UI.LoginWindow;
import utils.ReminderScheduler;

public class Main {
    public static void main(String[] args) {
        // Инициализация базы данных
        DatabaseManager.initDatabase();

        // Запуск фоновых напоминаний
        ReminderScheduler.start();

        // Запуск Telegram бота (если есть токен)
        TelegramBot.start();

        // Открытие окна входа
        javax.swing.SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}