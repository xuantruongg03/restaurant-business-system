package restaurant_business_system.db.restaurant;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * The RestaurantDAO class provides methods to interact with the database for restaurant-related operations.
 */
public class RestaurantDAO{
    private final Jdbi jdbi;

    /**
     * Constructs a new RestaurantDAO object with the given Jdbi instance.
     * @param jdbi The Jdbi instance to use for database operations.
     */
    public RestaurantDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Creates a new restaurant in the database.
     * @param restaurant The restaurant object to create.
     * @return The created restaurant object.
     */
    @SqlUpdate("INSERT INTO restaurants (id_restaurant, name, id_account, status) VALUES (:idRestaurant, :name, :idAccount, :status)")
    public Restaurant create(Restaurant restaurant) {
        jdbi.useHandle(handle -> handle.createUpdate("INSERT INTO restaurants (id_restaurant, name, id_account, status) VALUES (:idRestaurant, :name, :idAccount, :status)")
                .bind("idRestaurant", restaurant.getId())
                .bind("name", restaurant.getName())
                .bind("idAccount", restaurant.getIdAccount())
                .bind("status", restaurant.getStatus())
                .execute());
        return restaurant;
    }
    
    /**
     * Deletes a restaurant from the database.
     * @param restaurant The restaurant object to delete.
     */
    @SqlUpdate("DELETE FROM restaurants WHERE id_restaurant = :id")
    public void delete(Restaurant restaurant) {
        jdbi.useHandle(handle -> handle.createUpdate("DELETE FROM restaurants WHERE id_restaurant = :id")
                .bind("id", restaurant.getId())
                .execute());
    }

    /**
     * Updates an existing restaurant in the database.
     * @param restaurant The restaurant object to update.
     * @return The updated restaurant object.
     */
    @SqlUpdate("UPDATE restaurants SET name = :name, id_account = :idAccount, status = :status WHERE id_restaurant = :id")
    public Restaurant update(Restaurant restaurant) {
        jdbi.useHandle(handle -> handle.createUpdate("UPDATE restaurants SET name = :name, id_account = :idAccount, status = :status WHERE id_restaurant = :id")
                .bind("id", restaurant.getId())
                .bind("name", restaurant.getName())
                .bind("idAccount", restaurant.getIdAccount())
                .bind("status", restaurant.getStatus())
                .execute());
        return restaurant;
    }

    /**
     * Retrieves a restaurant from the database by its ID.
     * @param id The ID of the restaurant to retrieve.
     * @return The restaurant object with the specified ID.
     */
    @SqlUpdate("SELECT * FROM restaurants WHERE id_account = :id")
    public Restaurant get(int id) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM restaurants WHERE id_account = :id")
                .bind("id", id)
                .mapToBean(Restaurant.class)
                .findFirst()
                .orElse(null));
    }
}
