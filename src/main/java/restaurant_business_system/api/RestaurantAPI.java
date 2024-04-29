package restaurant_business_system.api;

import io.dropwizard.auth.Auth;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.core.User;
import restaurant_business_system.db.restaurant.Restaurant;
import restaurant_business_system.db.restaurant.RestaurantDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/restaurant")
@Produces(MediaType.APPLICATION_JSON)
public class RestaurantAPI {
    private RestaurantDAO dao;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantAPI.class);

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
    public Response addRestaurant(@Auth User u, Restaurant r) {
        if (u != null) {
            Restaurant re = new Restaurant(r.getName(), u.getId());
            if (dao.create(re) != null) {
                return Response.ok("OK").build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/get")
    public Response getRestaurant(@Auth User u) {
        if (u != null) {
            return Response.ok(dao.get(u.getId())).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/update-name")
    public Response updateNameRestaurant(@Auth User u, Restaurant r) {
        if (u != null) {
            LOGGER.info("ID: " + r.getIdRestaurant());
            LOGGER.info("New name: " + r.getName());
            if (dao.updateName(r) != null) {
                return Response.ok("OK").build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    //Chưa xóa được
    @POST
    @Path("/delete")
    public Response deleteRestaurant(@Auth User u, Restaurant r){
        if (u != null) {
            dao.delete(u.getId(), r.getIdRestaurant());
            return Response.ok("OK").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
