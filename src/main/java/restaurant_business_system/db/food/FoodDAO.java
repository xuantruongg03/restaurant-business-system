package restaurant_business_system.db.food;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * The FoodDAO class is responsible for performing database operations related to food entities.
 */
public class FoodDAO {
    private final Jdbi jdbi;

    /**
     * Constructs a new FoodDAO object.
     *
     * @param jdbi the Jdbi instance used for database operations
     */
    public FoodDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Creates a new food in the database.
     *
     * @param food the food object to be created
     * @return the created food object
     */
    @SqlUpdate("INSERT INTO foods (id_food, id_menu, name, price, image) VALUES (:idFood, :idMenu, :name, :price, :image)")
    public Food create(Food food) {
        jdbi.useHandle(handle -> handle.createUpdate(
                "INSERT INTO foods (id_food, id_menu, name, price, image) VALUES (:idFood, :idMenu, :name, :price, :image)")
                .bind("idFood", food.getIdFood())
                .bind("idMenu", food.getIdMenu())
                .bind("name", food.getName())
                .bind("price", food.getPrice())
                .bind("image", food.getImage())
                .execute());
        return food;
    }

    /**
     * Updates an existing food in the database.
     *
     * @param food the food object to be updated
     * @return the updated food object
     */
    @SqlUpdate("UPDATE foods SET name = :name, price = :price, image = :image WHERE id_food = :idFood")
    public Food update(Food food) {
        jdbi.useHandle(handle -> handle
                .createUpdate("UPDATE foods SET name = :name, price = :price, image = :image WHERE id_food = :idFood")
                .bind("idFood", food.getIdFood())
                .bind("name", food.getName())
                .bind("price", food.getPrice())
                .bind("image", food.getImage())
                .execute());
        return food;
    }

    /**
     * Deletes a food from the database.
     *
     * @param food the food object to be deleted
     */
    @SqlUpdate("DELETE FROM foods WHERE id_food = :idFood")
    public void delete(Food food) {
        jdbi.useHandle(handle -> handle.createUpdate("DELETE FROM foods WHERE id_food = :idFood")
                .bind("idFood", food.getIdFood())
                .execute());
    }

    /**
     * Deletes all foods associated with a menu from the database.
     *
     * @param idMenu the ID of the menu
     */
    @SqlUpdate("DELETE FROM foods WHERE id_menu = :idMenu")
    public void deleteByMenu(String idMenu) {
        jdbi.useHandle(handle -> handle.createUpdate("DELETE FROM foods WHERE id_menu = :idMenu")
                .bind("idMenu", idMenu)
                .execute());
    }

    /**
     * Retrieves all foods associated with a menu from the database.
     *
     * @param idMenu the ID of the menu
     * @return a list of FoodDTO objects representing the foods
     */
    @SqlUpdate("SELECT * FROM foods WHERE id_menu = :idMenu")
    public List<FoodDTO> findByMenu(String idMenu) {
        List<Map<String, Object>> results = jdbi
                .withHandle(handle -> handle.createQuery("SELECT * FROM foods WHERE id_menu = :idMenu")
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
}
