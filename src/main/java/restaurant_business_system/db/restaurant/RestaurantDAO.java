package restaurant_business_system.db.restaurant;


import org.jdbi.v3.core.Jdbi;

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
    public Restaurant create(Restaurant restaurant) {
        jdbi.useHandle(handle -> handle.createUpdate("INSERT INTO restaurants (id_restaurant, name, status) VALUES (:idRestaurant, :name, :status)")
                .bind("idRestaurant", restaurant.getIdRestaurant())
                .bind("name", restaurant.getName())
                .bind("status", restaurant.getStatus())
                .execute());
        if(updateRestaurant(restaurant.getIdRestaurant(),restaurant.getIdAccount())){
            return null;
        }
        return restaurant;
    }
    
    /**
     * Deletes a restaurant from the database.
     * @param restaurant The restaurant object to delete.
     */
    // @SqlUpdate("DELETE FROM restaurants WHERE id_account = :id and id_restaurant = :idRestaurant")
    public void delete(String id, String idRestaurant) {
        jdbi.useTransaction(handle -> {
            //Get the menu id associated with the restaurant
            String idMenu = handle.createQuery("SELECT id_menu FROM menus WHERE id_restaurant = :idRestaurant")
                .bind("idRestaurant", idRestaurant)
                .mapTo(String.class)
                .one();

            //Delete all food items associated with the restaurant
            handle.createUpdate("DELETE FROM foods WHERE id_menu = :idMenu")
                .bind("idMenu", idMenu)
                .execute();

            // Delete all menu items associated with the restaurant
            handle.createUpdate("DELETE FROM menus WHERE id_restaurant = :idRestaurant")
                .bind("idRestaurant", idRestaurant)
                .execute();
    
            // Delete the restaurant
            handle.createUpdate("DELETE FROM restaurants WHERE id_restaurant = :idRestaurant")
                .bind("id", id)
                .bind("idRestaurant", idRestaurant)
                .execute();
        });
    }

    /**
     * Updates name an existing restaurant in the database.
     * @param restaurant The restaurant object to update.
     * @return The updated restaurant object.
     */
    public Restaurant updateName(Restaurant restaurant) {
        jdbi.useHandle(handle -> handle.createUpdate("UPDATE restaurants SET name = :name WHERE id_restaurant = :id")
                .bind("id", restaurant.getIdRestaurant())
                .bind("name", restaurant.getName())
                .execute());
        return restaurant;
    }



    public boolean updateRestaurant(String idRes, String idAcc) {
        return jdbi.withHandle(handle -> {
            handle.createUpdate("UPDATE accounts SET id_restaurant = :idRes WHERE id_account = :idAccount")
                    .bind("idRes", idRes)
                    .bind("idAccount", idAcc)
                    .execute();
            return true;
        });
    }
}