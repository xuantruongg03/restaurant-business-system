package restaurant_business_system.api;

import io.dropwizard.auth.Auth;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.core.User;
import restaurant_business_system.db.bill.Bill;
import restaurant_business_system.db.bill.BillDAO;

@Path("/bill")
public class BillAPI {
    private final BillDAO dao;

    public BillAPI(BillDAO dao) {
        this.dao = dao;
    }

    @POST
    @Path("/create")
    public Response createBill(@Auth User u, Bill b) {
        if (u != null) {
            dao.create(b);
            return Response.ok().build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
