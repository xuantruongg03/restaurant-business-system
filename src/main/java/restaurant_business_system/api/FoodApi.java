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
import restaurant_business_system.db.food.Food;
import restaurant_business_system.db.food.FoodDAO;

@Path("/food")
@Produces(MediaType.APPLICATION_JSON)
public class FoodApi {
    private FoodDAO dao;

    public FoodApi(FoodDAO dao) {
        this.dao = dao;
    }

    @POST
    @Path("/create")
    public Response addFood(@Auth User u, Food food) {
        if(food.getIdMenu() == null || food.getName() == null || food.getImage() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Food f = new Food(food.getIdMenu(), food.getName(), food.getPrice(), food.getImage());
        if (dao.create(f) != null) {
            return Response.ok("OK").build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/get")
    public Response getFoods(@QueryParam("idMenu") String idMenu){
        return Response.ok(dao.findByMenu(idMenu)).build();
    }
}
