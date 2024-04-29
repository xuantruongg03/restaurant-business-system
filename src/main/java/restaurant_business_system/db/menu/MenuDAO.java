package restaurant_business_system.db.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import jakarta.ws.rs.ForbiddenException;

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
     * Checks if the specified account is the owner of the restaurant.
     *
     * @param idRestaurant the ID of the restaurant
     * @param idAccount the ID of the account to check
     * @return true if the account is the owner of the restaurant, false otherwise
     */
    private boolean isOwner(String idRestaurant, String idAccount) {
        return jdbi.withHandle(handle -> {
            try {
                String idOwner = handle.createQuery("SELECT id_account FROM restaurants WHERE id_restaurant = :idRestaurant")
                    .bind("idRestaurant", idRestaurant)
                    .mapTo(String.class)
                    .one();
                    return idOwner.equals(idAccount);
            } catch (NoSuchElementException e) { // If the query returns no results (no restaurant with the given ID)
                return false;
            }
        });
    }

    /**
     * Updates an existing menu in the database.
     *
     * @param menu the menu object to be updated
     * @return the updated menu object
     */
    @SqlUpdate("UPDATE menus SET name = :name WHERE id_menu = :idMenu AND id_restaurant = :idRestaurant")
    public Menu update(Menu menu, String idAccount) {
        jdbi.useHandle(handle -> {
            //Check if the menu belongs to the account
            if (!isOwner(menu.getIdRestaurant(), idAccount)) {
                throw new ForbiddenException("You are not the owner of this restaurant.");
            }

            handle.createUpdate("UPDATE menus SET name = :name WHERE id_menu = :idMenu AND id_restaurant = :idRestaurant")
                .bind("idMenu", menu.getIdMenu())
                .bind("name", menu.getName())
                .bind("idRestaurant", menu.getIdRestaurant())
                .execute();
        });
        return menu;
    }  

    /**
     * Deletes a menu from the database.
     *
     * @param idMenu the ID of the menu to be deleted
     */
    @SqlUpdate("DELETE FROM menus WHERE id_menu = :idMenu")
    public void delete(String idMenu, String idAccount) {
        jdbi.useHandle(handle -> {
            //Get id_restaurant from id_menu
            String idRestaurant = handle.createQuery("SELECT id_restaurant FROM menus WHERE id_menu = :idMenu")
                .bind("idMenu", idMenu)
                .mapTo(String.class)
                .one();

            //Check if the menu belongs to the account
            if (!isOwner(idRestaurant, idAccount)) {
                throw new ForbiddenException("You are not the owner of this restaurant.");
            }
            // Delete the food items associated with the menu
            handle.createUpdate("DELETE FROM foods WHERE id_menu = :idMenu")
                .bind("idMenu", idMenu)
                .execute();

            // Delete the menu
            handle.createUpdate("DELETE FROM menus WHERE id_menu = :idMenu")
                .bind("idMenu", idMenu)
                .execute();
        });
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
    public List<MenuDTO> get(String idRestaurant) {
        List<Map<String, Object>> menus = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM menus WHERE id_restaurant = :idRestaurant")
                .bind("idRestaurant", idRestaurant)
                .mapToMap()
                .list());
        List<MenuDTO> menuList = new ArrayList<>();
        for (Map<String, Object> menu : menus) {
            menuList.add(new MenuDTO((String) menu.get("id_menu"), (String) menu.get("name")));
        }
        return menuList;
    }
}
