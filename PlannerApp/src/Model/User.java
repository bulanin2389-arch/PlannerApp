package Model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String telegramId;

    public User(int id, String username, String passwordHash, String telegramId) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.telegramId = telegramId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getTelegramId() { return telegramId; }
    public void setTelegramId(String telegramId) { this.telegramId = telegramId; }
}