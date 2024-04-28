package restaurant_business_system.db.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * The MenuDAO class provides methods to interact with the menus table in the database.
 */
public class MenuDAO {
    private final Jdbi jdbi;

    /**
     * Constructs a new MenuDAO object.
     *
     * @param jdbi the Jdbi instance used to interact with the database
     */
    public MenuDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Creates a new menu in the database.
     *
     * @param menu the menu object to be created
     * @return the created menu object
     */
    @SqlUpdate("INSERT INTO menus (id_menu, name, id_restaurant) VALUES (:idMenu, :name, :idRestaurant)")
    public Menu create(Menu menu) {
        jdbi.useHandle(handle -> handle.createUpdate("INSERT INTO menus (id_menu, name, id_restaurant) VALUES (:idMenu, :name, :idRestaurant)")
                .bind("idMenu", menu.getIdMenu())
                .bind("name", menu.getName())
                .bind("idRestaurant", menu.getIdRestaurant())
                .execute());
        return menu;
    }

    /**
     * Updates an existing menu in the database.
     *
     * @param menu the menu object to be updated
     * @return the updated menu object
     */
    @SqlUpdate("UPDATE menus SET name = :name, id_restaurant = :idRestaurant WHERE id_menu = :idMenu")
    public Menu update(Menu menu) {
        jdbi.useHandle(handle -> handle.createUpdate("UPDATE menus SET name = :name, id_restaurant = :idRestaurant WHERE id_menu = :idMenu")
                .bind("idMenu", menu.getIdMenu())
                .bind("name", menu.getName())
                .bind("idRestaurant", menu.getIdRestaurant())
                .execute());
        return menu;
    }   

    /**
     * Deletes a menu from the database.
     *
     * @param idMenu the ID of the menu to be deleted
     */
    @SqlUpdate("DELETE FROM menus WHERE id_menu = :idMenu")
    public void delete(String idMenu) {
        jdbi.useHandle(handle -> handle.createUpdate("DELETE FROM menus WHERE id_menu = :idMenu")
                .bind("idMenu", idMenu)
                .execute());
    }

    /**
     * Deletes all menus from the database.
     */
    @SqlUpdate("DELETE FROM menus")
    public void deleteAll() {
        jdbi.useHandle(handle -> handle.createUpdate("DELETE FROM menus").execute());
    }

    /**
     * Gets a menu from the database.
     *
     * @param idRestaurant the ID of the menu to get
     * @return the menu object
     */
    @SqlUpdate("SELECT * FROM menus WHERE id_restaurant = :idRestaurant")
    public List<Menu> get(String idRestaurant) {
        List<Map<String, Object>> menus = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM menus WHERE id_restaurant = :idRestaurant")
                .bind("idRestaurant", idRestaurant)
                .mapToMap()
                .list());
        List<Menu> menuList = new ArrayList<>();
        for (Map<String, Object> menu : menus) {
            menuList.add(new Menu((String) menu.get("id_menu"), (String) menu.get("name"), (String) menu.get("id_restaurant")));
        }
        return menuList;
    }
}
