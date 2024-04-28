package restaurant_business_system.db.menu;

import restaurant_business_system.resources.GenerateID;

/**
 * The Menu class represents a menu in the system.
 */
public class Menu {
    private String idMenu;
    private String idRestaurant;
    private String name;

    public Menu(String idRestaurant, String name) {
        this.idMenu = GenerateID.generateUniqueID();
        this.idRestaurant = idRestaurant;
        this.name = name;
    }

    public Menu(String idMenu, String idRestaurant, String name) {
        this.idMenu = idMenu;
        this.idRestaurant = idRestaurant;
        this.name = name;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public String getName() {
        return name;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public void setName(String name) {
        this.name = name;
    }
}
