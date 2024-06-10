package restaurant_business_system.api;

import io.dropwizard.auth.Auth;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.core.User;
import restaurant_business_system.db.account.Account;
import restaurant_business_system.db.account.AccountDAO;
import restaurant_business_system.db.account.AccountEdit;
import restaurant_business_system.exception.AccountExistsException;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountAPI {
    private AccountDAO dao;

    public AccountAPI(AccountDAO dao) {
        this.dao = dao;
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Account account) {
        Account a = new Account(account.getUsername(), account.getPassword());
        try {
        if(dao.create(a) != null) {
            return Response.ok("OK").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
            
        } catch (Exception e) {
            if (e instanceof AccountExistsException) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Account a) {
        Account account = dao.findByUsernameAndPassword(a.getUsername(), a.getPassword());
        if (account != null) {
            return Response.ok(account).type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Path("edit")
    @POST
    public Response edit(@Auth User u, AccountEdit account) {
        if(u != null) {

        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
