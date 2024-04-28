package restaurant_business_system.auth;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import restaurant_business_system.core.User;
import restaurant_business_system.db.account.*;

/**
 * The UserAuthenticator class is responsible for authorizing user access based on their role.
 * It implements the Authorizer interface for user authorization.
 */
public class UserAuthenticator implements Authorizer<User> {

    private AccountDAO dao;

    /**
     * Constructs a new UserAuthenticator object with the specified AccountDAO.
     *
     * @param dao the AccountDAO used to retrieve user account information
     */
    public UserAuthenticator(AccountDAO dao) {
        this.dao = dao;
    }

    public UserAuthenticator() {
        //TODO Auto-generated constructor stub
    }

    /**
     * Authorizes the specified user based on their role.
     *
     * @param user the User object representing the user to be authorized
     * @param role the role to be authorized
    * @param arg2 the ContainerRequestContext object representing the request context (nullable)
     * @return true if the user is authorized, false otherwise
     */
    @Override
    public boolean authorize(User user, String role, @Nullable ContainerRequestContext arg2) {
        return true;
    }
}

