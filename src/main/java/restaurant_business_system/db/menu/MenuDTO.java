package restaurant_business_system.db.menu;

public class MenuDTO {
    private String idMenu;
    private String name;

    public MenuDTO(String idMenu, String name) {
        this.idMenu = idMenu;
        this.name = name;
    }


    public String getIdMenu() {
        return idMenu;
    }

    public String getName() {
        return name;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public void setName(String name) {
        this.name = name;
    }
}
