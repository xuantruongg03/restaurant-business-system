package restaurant_business_system.db.account;

import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * The AccountDAO class is responsible for performing database operations
 * related to the Account entity.
 * It extends the AbstractDAO class and provides methods for creating, finding,
 * updating, and querying Account objects.
 */
public class AccountDAO {

    private final Jdbi jdbi;

    public AccountDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Creates a new Account object in the database.
     * 
     * @param account The Account object to be created.
     * @return The Account object that was created.
     */
    @SqlUpdate("INSERT INTO accounts (id_account, username, password, role) VALUES (:idAccount, :username, :password, :role)")
    public Account create(Account account) {
        jdbi.useHandle(handle -> handle.createUpdate(
                "INSERT INTO accounts (id_account, username, password, role) VALUES (:idAccount, :username, :password, :role)")
                .bind("idAccount", account.getId())
                .bind("username", account.getUsername())
                .bind("password", account.getPassword())
                .bind("role", account.getRole())
                .execute());
        return account;
    }

    /**
     * Finds an Account object by its username and password.
     * 
     * @param username The username of the Account.
     * @param password The password of the Account.
     * @return The Account object if found, null otherwise.
     */
    public Account findByUsernameAndPassword(String username, String password) {
        Map<String, Object> account = jdbi.withHandle(handle -> handle
                .createQuery("SELECT * FROM accounts WHERE username = :username AND password = :password")
                .bind("username", username)
                .bind("password", password)
                .mapToMap()
                .findFirst()
                .orElse(null));
        if (account != null) {
            return new Account((String) account.get("id_account"), (String) account.get("username"),
                    (String) account.get("password"), (String) account.get("role"));
        } else {
            return null;
        }
    }
}