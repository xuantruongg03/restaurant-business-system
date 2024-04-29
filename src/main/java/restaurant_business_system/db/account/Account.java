package restaurant_business_system.db.account;

import restaurant_business_system.resources.GenerateID;

public class Account {
    private String idAccount;

    private String username;

    private String password;

    private String role;

    /**
     * Constructs a new Account object.
     */
    public Account(String username, String password) {
        idAccount = GenerateID.generateUniqueID();
        this.username = username;
        this.password = password;
        this.role = "user";
    }

    public Account(String id, String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        idAccount = id;
    }

    public Account() {}

    public Account(String id) {
        idAccount = id;
    }

    public String getId() {
        return idAccount;
    }

    public void setId(String id) {
        this.idAccount = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
