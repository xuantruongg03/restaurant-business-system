package restaurant_business_system.db.account;

public class AccountDTO {
    private String id;

    private String username;

    private String role;

    private String name;

    private String phone;

    private String status;

    private String idRestaurant;

    public AccountDTO(String idAccount, String username, String name, String phone, String role, String status, String idRestaurant) {
        this.id = idAccount;
        this.username = username;
        this.role = role;
        this.name = name;
        this.status = status;
        this.phone = phone;
        this.idRestaurant = idRestaurant;
    }

    public AccountDTO() {}

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }
    
    public String getIdRestaurant() {
        return idRestaurant;
    }
}
