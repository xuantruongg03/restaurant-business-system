package restaurant_business_system.db.table;

import restaurant_business_system.resources.GenerateID;

public class Table {
    private String idTable;
    private String idRestaurant;
    // private String name;
    private String status;

    public Table(String idTable, String idRestaurant, String status) {
        this.idTable = idTable;
        this.idRestaurant = idRestaurant;
        this.status = status;
    }

    public Table(String idRestaurant) {
        this.idTable = GenerateID.generateUniqueID();
        this.idRestaurant = idRestaurant;
        this.status = "Available";
    }

    public Table() {
    }

    public String getIdTable() {
        return idTable;
    }

    public void setIdTable(String idTable) {
        this.idTable = idTable;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
