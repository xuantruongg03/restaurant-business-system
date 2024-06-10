package restaurant_business_system.db.food;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ForbiddenException;

/**
 * The FoodDAO class is responsible for performing database operations related
 * to food entities.
 */
public class FoodDAO {
    private final Jdbi jdbi;
    private final Logger LOOGER = LoggerFactory.getLogger(FoodDAO.class);

    /**
     * Constructs a new FoodDAO object.
     *
     * @param jdbi the Jdbi instance used for database operations
     */
    public FoodDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Checks if the given account is the owner of the specified menu.
     *
     * @param idMenu    the ID of the menu
     * @param idAccount the ID of the account
     * @return true if the account is the owner of the menu, false otherwise
     */
    private boolean isOwner(String idMenu, String idAccount) {
        return jdbi.withHandle(h -> {
            // Get the restaurant ID from the menu ID
            Optional<String> idRestaurant = h.createQuery("SELECT id_restaurant FROM menus WHERE id_menu = :idMenu")
                    .bind("idMenu", idMenu)
                    .mapTo(String.class)
                    .findOne(); // FindOne return 1 result and save it in Optional
            if (idRestaurant.isEmpty()) {
                return false;
            }
            // Get the account ID from the restaurant ID
            Optional<String> id = h
                    .createQuery("SELECT id_account FROM restaurants WHERE id_restaurant = :idRestaurant")
                    .bind("idRestaurant", idRestaurant)
                    .mapTo(String.class)
                    .findOne();
            if (id.isEmpty()) {
                return false;
            }
            return id.get().equals(idAccount);
        });
    }

    /**
     * Creates a new food in the database.
     *
     * @param food the food object to be created
     * @return the created food object
     */
    public Food create(Food food, String idAccount) {
        LOOGER.info("Creating food with ID: " + food.getIdFood() + " in menu with ID: " + food.getIdMenu()
                + " for account with ID: " + idAccount);
        jdbi.useHandle(handle -> {
            // Check if the user is the owner of the menu
            if (!isOwner(food.getIdMenu(), idAccount)) {
                throw new ForbiddenException("You are not the owner of the menu");
            }

            handle.createUpdate(
                    "INSERT INTO foods (id_food, id_menu, name, price, image) VALUES (:idFood, :idMenu, :name, :price, :image)")
                    .bind("idFood", food.getIdFood())
                    .bind("idMenu", food.getIdMenu())
                    .bind("name", food.getName())
                    .bind("price", food.getPrice())
                    .bind("image", food.getImage())
                    .execute();
        });
        return food;
    }

    /**
     * Updates an existing food in the database.
     *
     * @param food the food object to be updated
     * @return the updated food object
     */
    public Food update(Food food, String idAccount) {
        // Check if the user is the owner of the menu
        if (!isOwner(food.getIdMenu(), idAccount)) {
            throw new ForbiddenException("You are not the owner of the menu");
        }

        jdbi.useHandle(handle -> {
            handle
                    .createUpdate(
                            "UPDATE foods SET name = :name, price = :price, image = :image WHERE id_food = :idFood")
                    .bind("idFood", food.getIdFood())
                    .bind("name", food.getName())
                    .bind("price", food.getPrice())
                    .bind("image", food.getImage())
                    .execute();
        });
        return food;
    }

    /**
     * Deletes a food from the database.
     *
     * @param food the food object to be deleted
     */
    public void delete(String idFood, String idAccount) {
        jdbi.useHandle(handle -> {
            // Get the menu ID from the food ID
                Map<String, Object> resultList = jdbi
                    .withHandle(h -> h.createQuery("SELECT id_menu FROM foods WHERE id_food = :idFood")
                        .bind("idFood", idFood)
                        .mapToMap()
                        .one());
                String idMenu = (String) resultList.get("id_menu");

                // Check if the user is the owner of the menu
                if (!isOwner(idMenu, idAccount)) {
                throw new ForbiddenException("You are not the owner of the menu");
                }

                handle.createUpdate("DELETE FROM foods WHERE id_food = :idFood")
                    .bind("idFood", idFood)
                    .execute();
        });
    }

    /**
     * Deletes all foods associated with a menu from the database.
     *
     * @param idMenu the ID of the menu
     */
    @SqlUpdate("DELETE FROM foods WHERE id_menu = :idMenu")
    public void deleteAll(String idMenu, String idAccount) {
        jdbi.useHandle(handle -> {
            // Check if the user is the owner of the menu
            if (!isOwner(idMenu, idAccount)) {
                throw new ForbiddenException("You are not the owner of the menu");
            }

            handle.createUpdate("DELETE FROM foods WHERE id_menu = :idMenu")
                    .bind("idMenu", idMenu)
                    .execute();
        });
    }

    /**
     * Retrieves all foods associated with a menu from the database.
     *
     * @param idMenu the ID of the menu
     * @return a list of FoodDTO objects representing the foods
     */
    public List<FoodDTO> findByMenu(String idMenu) {
        List<Map<String, Object>> results = jdbi
                .withHandle(handle -> handle.createQuery("SELECT * FROM foods WHERE id_menu = :idMenu and (select status from menus where id_menu = :idMenu) = 'active'")
                        .bind("idMenu", idMenu)
                        .mapToMap()
                        .list());
        List<FoodDTO> foods = new ArrayList<>();
        for (Map<String, Object> result : results) {
            FoodDTO food = new FoodDTO((String) result.get("id_food"), (String) result.get("name"),
                    (Float) result.get("price"), (String) result.get("image"));
            foods.add(food);
        }
        return foods;
    }

    /**
     * Retrieves a food by its ID from the database.
     *
     * @param idFood the ID of the food
     * @return the FoodDTO object representing the food
     */
    public FoodDTO findById(String idFood) {
        List<Map<String, Object>> resultList = jdbi.withHandle(
                handle -> handle.createQuery("SELECT id_food, name, price, image FROM foods WHERE id_food = :idFood")
                        .bind("idFood", idFood)
                        .mapToMap()
                        .list());

        // If the result list is empty, return null
        if (resultList.isEmpty()) {
            return null;
        }

        // Otherwise, get the first result (since we expect only one row for a specific
        // id)
        Map<String, Object> result = resultList.get(0);
        return new FoodDTO(
                (String) result.get("id_food"),
                (String) result.get("name"),
                (Float) result.get("price"),
                (String) result.get("image"));
    }
}
