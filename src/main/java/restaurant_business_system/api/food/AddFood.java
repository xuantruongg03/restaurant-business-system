package restaurant_business_system.api.food;

import java.util.Optional;

import com.codahale.metrics.annotation.Timed;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import restaurant_business_system.db.food.Food;
import restaurant_business_system.db.food.FoodDAO;

@Path("/add-food")
public class AddFood {
    public AddFood() {
    }

    /**
     * Adds a new food item to the menu.
     *
     * @param menu_id the ID of the menu to which the food item will be added
     *                (optional)
     * @return a string indicating the result of the operation ("Success" or
     *         "Failed")
     */
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON) // This annotation is used to specify the MIME media types of representations a can consume and process.
    @Produces(MediaType.APPLICATION_JSON)
    public String addFood(@QueryParam("menu_id") Optional<String> menu_id) {
        FoodDAO foodDAO = new FoodDAO(null);
        if (foodDAO.create(new Food()) != null) {
            return "Success";
        }
        return "Failed";
    }
}
