package restaurant_business_system.db.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import restaurant_business_system.db.food.FoodDetails;
import restaurant_business_system.db.food.FoodOrder;
import restaurant_business_system.db.order.Order;

/**
 * The BillDAO class is responsible for performing database operations related to bills.
 */
public class BillDAO {
    private final Jdbi jdbi;

    /**
     * Constructs a new BillDAO object with the specified Jdbi instance.
     *
     * @param jdbi the Jdbi instance to be used for database operations
     */
    public BillDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Creates a new bill in the database.
     *
     * @param b the Bill object to be created
     * @return the created Bill object
     */
    @SqlUpdate("INSERT INTO bills (id_bill, id_table, status) VALUES (:idBill, :idTable, :status)")
    public Bill create(Bill b) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("INSERT INTO bills (id_bill, id_table, status) VALUES (:idBill, :idTable, :status)")
                    .bind("idBill", b.getIdBill())
                    .bind("idTable", b.getIdTable())
                    .bind("status", b.getStatus())
                    .execute();
        });
        return b;
    }

    /**
     * Updates the status of a bill in the database.
     *
     * @param idBill the ID of the bill to be updated
     * @param status the new status of the bill
     */
    @SqlUpdate("UPDATE bill SET status = :status WHERE idBill = :idBill")
    public void updateStatus(String idBill, String status) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("UPDATE bill SET status = :status WHERE idBill = :idBill")
                    .bind("idBill", idBill)
                    .bind("status", status)
                    .execute();
        });
    }

    /**
     * Deletes a bill from the database.
     *
     * @param idBill the ID of the bill to be deleted
     */
    @SqlUpdate("DELETE FROM bill WHERE idBill = :idBill")
    public void delete(String idBill) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("DELETE FROM bill WHERE idBill = :idBill")
                    .bind("idBill", idBill)
                    .execute();
        });
    }

    /**
     * Retrieves a bill from the database based on the table ID.
     *
     * @param idTable the ID of the table
     */
    @SqlUpdate("SELECT * FROM bills WHERE id_table = :idTable")
    public void getBillByTable(String idTable) {
        jdbi.useHandle(handle -> {
            handle.createQuery("SELECT * FROM bills WHERE id_table = :idTable")
                    .bind("idTable", idTable)
                    .mapToBean(Bill.class)
                    .list();
        });
    }

    /**
     * Retrieves the details of a bill from the database based on the table ID.
     *
     * @param idTable the ID of the table
     * @return the BillDTO object containing the bill details
     */
    @SqlUpdate("SELECT b.id_table, b.id_bill, b.status, o.id_food, f.name, f.price, o.quantity FROM (bills as b left join orders as o on b.id_bill = o.id_bill) left join foods as f on o.id_food = f.id_food WHERE b.id_bill = :idBill and b.status = 'Open'")
    public BillDTO getBillDetails(String idBill) {
        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery(
                    "SELECT b.id_table, b.id_bill, b.status, o.id_food, f.name, f.price, o.quantity FROM (bills as b left join orders as o on b.id_bill = o.id_bill) left join foods as f on o.id_food = f.id_food WHERE b.id_bill = :idBill and b.status = 'Open'")
                    .bind("idBill", idBill)
                    .mapToMap()
                    .list();
            // Check if the result is empty
            if (results.isEmpty())
                return null;

            if(results.get(0).get("id_bill") != null && results.get(0).get("id_food") == null)
                //tra ve list food rong
                return new BillDTO((String) results.get(0).get("id_table"), new ArrayList<>(), "Open");

            // Create a list of FoodDetail objects
            List<FoodDetails> foods = new ArrayList<>();
            for (Map<String, Object> result : results) {
                String idFood = (String) result.get("id_food");
                String name = (String) result.get("name");
                float price = (float) result.get("price");
                int quantity = (int) result.get("quantity");
                foods.add(new FoodDetails(idFood, name, price, quantity));
            }
            
            return new BillDTO((String) results.get(0).get("id_table"), foods, "Open");
        });

    }

    /**
     * Checks the bill associated with the given table ID.
     *
     * @param idTable the ID of the table to check the bill for
     * @return the ID of the bill if it exists and is open, or null if no bill is found
     */
    public String checkBill(String idTable) {
        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery("SELECT * FROM bills WHERE id_table = :idTable and status = 'Open'")
                    .bind("idTable", idTable)
                    .mapToMap()
                    .list();
            if (results.isEmpty()){
                return null;
            } else {
                return (String) results.get(0).get("id_bill");
            }
        });
    }

    private boolean checkBillExist(String idBill) {
        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery("SELECT * FROM bills WHERE id_bill = :idBill")
                    .bind("idBill", idBill)
                    .mapToMap()
                    .list();
            return !results.isEmpty();
        });
    }

    /**
     * Adds an order to a bill in the database.
     *
     * @param idBill the ID of the bill to add the order to
     * @param idFood the ID of the food item to add to the order
     * @param quantity the quantity of the food item to add to the order
     * @return true if the order was successfully added, false otherwise
     */
    public boolean order(String idBill, List<FoodOrder> foodOrders) {
        return jdbi.withHandle(handle -> {
            // Check if the bill is open
            if(!checkBillExist(idBill))
                return false;

            // Add the order to the database
            for (FoodOrder foodDetail : foodOrders) {
                Order order = new Order(idBill, foodDetail.getIdFood(), foodDetail.getQuantity());
                handle.createUpdate("INSERT INTO orders (id_order, id_bill, id_food, quantity, status) VALUES (:idOrder, :idBill, :idFood, :quantity, :status)")
                        .bind("idOrder", order.getIdOrder())
                        .bind("idBill", idBill)
                        .bind("idFood", order.getIdFood())
                        .bind("quantity", order.getQuantity())
                        .bind("status", order.getStatus())
                        .execute();
            }
            return true;
        });
    }
}
