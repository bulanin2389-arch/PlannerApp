package telegram;

import database.UserDAO;
import Model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TelegramBot {
    private static final String BOT_TOKEN = "8687554769:AAGdB1UQ3esS2LPnFFWjDTOnHj43QpYOXUM";

    private static Thread botThread;
    private static int lastUpdateId = 0;

    public static void start() {
        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            System.out.println("Telegram бот не запущен: нет токена");
            return;
        }

        botThread = new Thread(() -> {
            System.out.println("Telegram бот запущен, жду сообщения...");
            while (true) {
                try {
                    String url = "https://api.telegram.org/bot" + BOT_TOKEN +
                            "/getUpdates?offset=" + (lastUpdateId + 1) + "&timeout=30";

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(35000);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        parseUpdates(response.toString());
                    }
                } catch (Exception e) {

                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        });
        botThread.setDaemon(true);
        botThread.start();
    }

    private static void parseUpdates(String json) {
        int msgStart = json.indexOf("\"message\"");
        if (msgStart == -1) return;

        int chatIdStart = json.indexOf("\"chat\"", msgStart);
        int idStart = json.indexOf("\"id\"", chatIdStart);
        int idEnd = json.indexOf(",", idStart);
        if (idEnd == -1) idEnd = json.indexOf("}", idStart);

        String chatId = "";
        try {
            chatId = json.substring(idStart + 5, idEnd).trim();
        } catch (Exception e) {
            return;
        }

        int textStart = json.indexOf("\"text\"", msgStart);
        if (textStart == -1) return;

        int textQuoteStart = json.indexOf("\"", textStart + 6);
        int textQuoteEnd = json.indexOf("\"", textQuoteStart + 1);

        String text = "";
        try {
            text = json.substring(textQuoteStart + 1, textQuoteEnd);
        } catch (Exception e) {
            return;
        }

        int updateIdStart = json.indexOf("\"update_id\"");
        if (updateIdStart != -1) {
            int updateIdEnd = json.indexOf(",", updateIdStart);
            try {
                lastUpdateId = Integer.parseInt(json.substring(updateIdStart + 12, updateIdEnd).trim());
            } catch (NumberFormatException e) {}
        }

        handleCommand(chatId, text);
    }

    private static void handleCommand(String chatId, String text) {
        if (text.equals("/start")) {
            sendMessage(chatId, "🤖 Привет! Я бот-помощник.\n\n" +
                    "📌 Команды:\n" +
                    "/link <логин> - привязать аккаунт из приложения\n" +
                    "/add <название> <ГГГГ-ММ-ДД ЧЧ:ММ> - добавить задачу\n\n" +
                    "Пример: /link ivan\n" +
                    "Пример: /add Встреча 2026-03-25 15:00");
            return;
        }

        if (text.startsWith("/link")) {
            String[] parts = text.split(" ");
            if (parts.length == 2) {
                String username = parts[1];
                User user = UserDAO.getUserByUsername(username);
                if (user != null) {
                    UserDAO.updateTelegramId(user.getId(), chatId);
                    sendMessage(chatId, "✅ Аккаунт \"" + username + "\" привязан!\nТеперь вы будете получать напоминания.");
                } else {
                    sendMessage(chatId, "❌ Пользователь \"" + username + "\" не найден.\nСначала зарегистрируйтесь в приложении.");
                }
            } else {
                sendMessage(chatId, "❌ Неправильный формат.\nИспользуй: /link твой_логин");
            }
            return;
        }

        if (text.startsWith("/add")) {
            String rest = text.substring(4).trim();
            int firstSpace = rest.indexOf(" ");
            if (firstSpace > 0) {
                String title = rest.substring(0, firstSpace);
                String datetime = rest.substring(firstSpace + 1);

                User user = UserDAO.getUserByTelegramId(chatId);
                if (user != null) {
                    sendMessage(chatId, "✅ Задача \"" + title + "\" добавлена на " + datetime);
                } else {
                    sendMessage(chatId, "❌ Сначала привяжите аккаунт командой /link");
                }
            } else {
                sendMessage(chatId, "❌ Формат: /add Название 2026-03-25 15:00");
            }
            return;
        }

        sendMessage(chatId, "❌ Неизвестная команда.\nОтправь /start для списка команд.");
    }

    public static void sendMessage(String chatId, String text) {
        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) return;

        try {
            String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            String params = "chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8");

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes("UTF-8"));
                os.flush();
            }

            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("Ошибка отправки: " + e.getMessage());
        }
    }
}