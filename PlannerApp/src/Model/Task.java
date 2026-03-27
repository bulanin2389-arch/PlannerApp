package Model;

public class Task {
    private int id;
    private String title;
    private String description;
    private String remindAt;
    private boolean isNotified;
    private int userId;

    public Task(int id, String title, String description, String remindAt, boolean isNotified, int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.remindAt = remindAt;
        this.isNotified = isNotified;
        this.userId = userId;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRemindAt() { return remindAt; }
    public void setRemindAt(String remindAt) { this.remindAt = remindAt; }
    public boolean isNotified() { return isNotified; }
    public void setNotified(boolean notified) { isNotified = notified; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTime() {
        if (remindAt != null && remindAt.length() >= 16) {
            return remindAt.substring(11, 16);
        }
        return "";
    }
    public String getDate() {
        if (remindAt != null && remindAt.length() >= 10) {
            return remindAt.substring(0, 10);
        }
        return "";
    }
}