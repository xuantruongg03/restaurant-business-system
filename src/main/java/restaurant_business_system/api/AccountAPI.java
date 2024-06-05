package restaurant_business_system.api;

import java.util.Collections;

import jakarta.ws.rs.POST;

import jakarta.ws.rs.Path;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.db.account.Account;
import restaurant_business_system.db.account.AccountDAO;
import restaurant_business_system.exceptions.AccountException;
import restaurant_business_system.exceptions.PhoneNumberException;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountAPI {
    private final String ACCOUNT_SID = System.getenv("ACCOUNT_SID");
    private final String AUTH_TOKEN = System.getenv("ACCOUNT_TOKEN");
    private final String PHONE = System.getenv("PHONE");

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Account account) {
        try {
            Account a = new Account(account.getUsername(), account.getPassword(), account.getName(),
                    account.getPhone());
            dao.create(a);
            return Response.ok("OK").type(MediaType.APPLICATION_JSON).build();
        } catch (AccountException e) {
            // Account already exists
            return Response.status(Response.Status.CONFLICT)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (PhoneNumberException e) {
            // Phone number issues (exists or invalid)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Collections.singletonMap("error", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            // General error handling
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("error", "An unexpected error occurred"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("/sendOTP")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendOTP(String phoneNumber){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber(phoneNumber), // Số điện thoại nhận
                new PhoneNumber(PHONE), // Số điện thoại gửi của Twilio
                "Hello nhan nhan!" // Nội dung tin nhắn
        ).create();

        message.getSid();
        return Response.ok("OK").type(MediaType.APPLICATION_JSON).build();
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

}
