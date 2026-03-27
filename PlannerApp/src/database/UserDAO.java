package database;

import Model.User;
import utils.PasswordUtils;

import java.sql.*;

public class UserDAO {

    public static User getUserById(int userId) {
        String sql = "SELECT id, username, password, telegram_id FROM users WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("telegram_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserByUsername(String username) {
        String sql = "SELECT id, username, password, telegram_id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("telegram_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserByTelegramId(String telegramId) {
        String sql = "SELECT id, username, password, telegram_id FROM users WHERE telegram_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, telegramId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("telegram_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            String hashed = PasswordUtils.hashPassword(password);
            System.out.println("Регистрация: username=" + username + ", hash=" + hashed);
            pstmt.setString(2, hashed);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public static User loginUser(String username, String password) {
        String sql = "SELECT id, username, password, telegram_id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            String hashed = PasswordUtils.hashPassword(password);
            System.out.println("Вход: username=" + username + ", hash=" + hashed);
            pstmt.setString(2, hashed);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Пользователь найден!");
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("telegram_id")
                );
            } else {
                System.out.println("Пользователь НЕ найден");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateTelegramId(int userId, String telegramId) {
        String sql = "UPDATE users SET telegram_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, telegramId);
            pstmt.setInt(2, userId);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}