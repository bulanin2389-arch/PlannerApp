package database;

import Model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public static void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, remind_at, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getRemindAt());
            pstmt.setInt(4, task.getUserId());
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> getTasksByDate(int userId, int year, int month, int day) {
        List<Task> tasks = new ArrayList<>();
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);
        String sql = "SELECT id, title, description, remind_at, is_notified FROM tasks WHERE user_id = ? AND remind_at LIKE ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, dateStr + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("remind_at"),
                        rs.getInt("is_notified") == 1,
                        userId
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static List<Task> getPendingReminders() {
        List<Task> tasks = new ArrayList<>();
        String now = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String sql = "SELECT t.id, t.title, t.description, t.remind_at, t.user_id, u.telegram_id " +
                "FROM tasks t LEFT JOIN users u ON t.user_id = u.id " +
                "WHERE t.remind_at <= ? AND t.is_notified = 0";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, now);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("remind_at"),
                        false,
                        rs.getInt("user_id")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static void markNotified(int taskId) {
        String sql = "UPDATE tasks SET is_notified = 1 WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> getTasksByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, remind_at, is_notified FROM tasks WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("remind_at"),
                        rs.getInt("is_notified") == 1,
                        userId
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}