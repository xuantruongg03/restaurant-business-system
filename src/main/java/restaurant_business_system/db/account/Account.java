package restaurant_business_system.db.account;

import restaurant_business_system.resources.GenerateID;

public class Account {
    private String idAccount;

    private String username;

    private String password;

    private String role;

    private String name;

    private String phone;

    /**
     * Constructs a new Account object.
     */
    public Account(String username, String password, String name, String phone) {
        idAccount = GenerateID.generateUniqueID();
        this.username = username;
        this.password = password;
        this.role = "user";
        this.name = name;
        this.phone = phone;
    }

    public Account(String id, String username, String password,String name, String phone, String role) {
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

    public String getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
