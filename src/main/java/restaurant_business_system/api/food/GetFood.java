package restaurant_business_system.api.food;

import java.util.List;
import java.util.Optional;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import restaurant_business_system.db.food.Food;
import restaurant_business_system.db.food.FoodDAO;

/**
 * The GetFood class is a resource that represents a collection of products
 * in the restaurant business system.
 */
@Path("/get-foods")
/*
 * This annotation is used to specify the MIME media types of representations a can produce and send back to the client.
 */
@Produces(MediaType.APPLICATION_JSON) 
public class GetFood {

    public GetFood() {
    }

    /**
     * Retrieves all products from the menu.
     *
     * @param menu_id an optional parameter to filter products by menu ID
     * @return a string representation of the retrieved products
     */
    @GET
    @Timed
    @JsonProperty
    public String getAll(@QueryParam("menu_id") Optional<String> menu_id) {
        /*
         * Call the FoodADO.findAll method to retrieve all products from the menu.
         */
        FoodDAO foodDAO = new FoodDAO(null);
        List<Food> foods = foodDAO.findAll(menu_id.orElse("1"));

        // Return a string representation of the retrieved products.
        return foods.toString();
    }
}
