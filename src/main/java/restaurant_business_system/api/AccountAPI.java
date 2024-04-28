package restaurant_business_system.api;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.db.account.Account;
import restaurant_business_system.db.account.AccountDAO;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountAPI {
    private AccountDAO dao;

    public AccountAPI(AccountDAO dao) {
        this.dao = dao;
    }

    /**
     * Registers a new account.
     *
     * @param account the account to be registered
     *                When user submit, the account object will be created and
     *                passed to the create method in the AccountDAO class.
     * @return the response containing the created account
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response register(Account account) {
        Account createdAccount = dao.create(account);
        if(createdAccount != null) {
            return Response.ok("OK").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // @POST
    // @Path("/login")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @UnitOfWork
    // public Response login(Account a) {
    //     Account account = dao.findByUsernameAndPassword(a.getUsername(), a.getPassword());
    //     if (account != null) {
    //         return Response.ok(account).type(MediaType.APPLICATION_JSON).build();
    //     } else {
    //         return Response.status(Response.Status.UNAUTHORIZED).build();
    //     }
    // }

    @POST
    @Path("/logout")
    public void logout() {
    }

}