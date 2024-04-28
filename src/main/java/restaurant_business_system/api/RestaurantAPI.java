package restaurant_business_system.api;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.core.User;
import restaurant_business_system.db.restaurant.Restaurant;
import restaurant_business_system.db.restaurant.RestaurantDAO;

@Path("/restaurant")
@Produces(MediaType.APPLICATION_JSON)
public class RestaurantAPI {
    private RestaurantDAO dao;

    public RestaurantAPI(RestaurantDAO dao) {
        this.dao = dao;
    }

    /**
        * Adds a new restaurant to the system.
        *
        * @param u The authenticated user.
        * @param r The restaurant to be added.
        * @return A response indicating the success or failure of the operation.
        */
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response addRestaurant(@Auth User u, Restaurant r) {
        if (u != null) {
            Restaurant re = new Restaurant(r.getName(), u.getId());
            if (dao.create(re) != null) {
                return Response.ok("OK").build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
