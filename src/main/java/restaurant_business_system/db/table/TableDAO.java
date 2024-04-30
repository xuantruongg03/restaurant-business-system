package restaurant_business_system.db.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import jakarta.ws.rs.ForbiddenException;

public class TableDAO {
    private final Jdbi jdbi;

    public TableDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    private boolean isOwner(String idTable, String idAccount) {
        return jdbi.withHandle(handle -> {
            // Get idRestaurant from idTable
            Optional<String> idRestaurant = handle
                    .createQuery("SELECT id_restaurant FROM tables WHERE id_table = :idTable")
                    .bind("idTable", idTable)
                    .mapTo(String.class)
                    .findOne();
            if (idRestaurant.isEmpty()) {
                return false;
            }
            // Check if idAccount is the owner of idRestaurant
            Optional<String> idAccountRS = handle
                    .createQuery("SELECT id_account FROM restaurants WHERE id_restaurant = :idRestaurant")
                    .bind("idRestaurant", idRestaurant)
                    .mapTo(String.class)
                    .findOne();
            if (idAccountRS.isEmpty()) {
                return false;
            }
            return idAccount.equals(idAccountRS.get());
        });
    }

    private boolean isOwnerRestaurant(String idRestaurant, String idAccount) {
        return jdbi.withHandle(handle -> {
            Optional<String> idAccountRS = handle
                    .createQuery("SELECT id_account FROM restaurants WHERE id_restaurant = :idRestaurant")
                    .bind("idRestaurant", idRestaurant)
                    .mapTo(String.class)
                    .findOne();
            return idAccountRS.isPresent() && idAccount.equals(idAccountRS.get());
        });
    }

    @SqlUpdate("INSERT INTO tables (id_table, id_restaurant, status) VALUES (:idTable, :idRestaurant, :status)")
    public Table create(Table t, String idAccount) {
        //Check if idRestaurant is owned by idAccount
        if (!isOwnerRestaurant(t.getIdRestaurant(), idAccount)) {
            throw new ForbiddenException("You are not the owner of the restaurant");
        }

        jdbi.useHandle(handle -> {
            handle.createUpdate(
                    "INSERT INTO tables (id_table, id_restaurant, status) VALUES (:idTable, :idRestaurant, :status)")
                    .bind("idTable", t.getIdTable())
                    .bind("idRestaurant", t.getIdRestaurant())
                    .bind("status", t.getStatus())
                    .execute();
        });
        return t;
    }

    @SqlUpdate("DELETE FROM tables WHERE id_table = :idTable")
    public void delete(String idTable, String idAccount) {
        // Check if the user is the owner of the table
        if (!isOwner(idTable, idAccount)) {
            throw new ForbiddenException("You are not the owner of the table");
        }

        jdbi.useHandle(handle -> {
            handle.createUpdate("DELETE FROM tables WHERE id_table = :idTable")
                    .bind("idTable", idTable)
                    .execute();
        });
    }

    @SqlUpdate("SELECT * FROM tables WHERE id_restaurant = :idRestaurant")
    public List<TableDTO> get(String idRestaurant, String idAccount) {
        //Get id account from id restaurant
        String idAccountRS = jdbi.withHandle(handle -> handle.createQuery("SELECT id_account FROM restaurants WHERE id_restaurant = :idRestaurant")
                        .bind("idRestaurant", idRestaurant)
                        .mapTo(String.class)
                        .one());
        
        //Check if idAccount is the owner of idRestaurant
        if (idAccountRS.isEmpty() || !idAccount.equals(idAccountRS)) {
            throw new ForbiddenException("You are not the owner of the restaurant");
        }

        List<Map<String, Object>> result = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM tables WHERE id_restaurant = :idRestaurant")
                        .bind("idRestaurant", idRestaurant)
                        .mapToMap()
                        .list());
        List<TableDTO> tables = new ArrayList<>();
        for (Map<String, Object> r : result) {
            tables.add(new TableDTO((String) r.get("id_table"), (String) r.get("status")));
        }
        return tables;
    }
}
