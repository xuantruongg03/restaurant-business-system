package restaurant_business_system.db.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;

import restaurant_business_system.db.food.FoodDetails;
import restaurant_business_system.db.food.FoodDetails2;
import restaurant_business_system.db.food.FoodOrderDTO;
import restaurant_business_system.db.food.FoodOrderDTO2;
import restaurant_business_system.db.order.FoodOrder;
import restaurant_business_system.db.order.Order;
import restaurant_business_system.resources.GenerateID;

/**
 * The BillDAO class is responsible for performing database operations related
 * to bills.
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

            if (results.get(0).get("id_bill") != null && results.get(0).get("id_food") == null)
                // tra ve list food rong
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
     * @return the ID of the bill if it exists and is open, or null if no bill is
     *         found
     */
    public String checkBill(String idTable) {
        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle
                    .createQuery("SELECT * FROM bills WHERE id_table = :idTable and status = 'Open'")
                    .bind("idTable", idTable)
                    .mapToMap()
                    .list();
            if (results.isEmpty()) {
                return null;
            } else {
                return (String) results.get(0).get("id_bill");
            }
        });
    }

    private boolean checkBillExist(String idBill) {
        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery("SELECT * FROM bills WHERE id_bill = :idBill and status = 'Open'")
                    .bind("idBill", idBill)
                    .mapToMap()
                    .list();
            return !results.isEmpty();
        });
    }

    /**
     * Adds an order to a bill in the database.
     *
     * @param idBill   the ID of the bill to add the order to
     * @param idFood   the ID of the food item to add to the order
     * @param quantity the quantity of the food item to add to the order
     * @return true if the order was successfully added, false otherwise
     */
    public boolean order(String idBill, List<FoodOrder> foodOrders) {
        return jdbi.withHandle(handle -> {
            // Check if the bill is open
            if (!checkBillExist(idBill))
                return false;

            // Add the order to the database
            for (FoodOrder foodDetail : foodOrders) {
                Order order = new Order(idBill, foodDetail.getIdFood(), foodDetail.getQuantity());
                handle.createUpdate(
                        "INSERT INTO orders (id_order, id_bill, id_food, quantity, status) VALUES (:idOrder, :idBill, :idFood, :quantity, :status)")
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

    public OrderReturn orderByFood(String idBill, String idFood, int quantity) {
        return (OrderReturn) jdbi.withHandle(handle -> {
            // Check if the bill is open
            if (!checkBillExist(idBill))
                return null;

            // Check if the quantity is valid
            if(quantity <= 0)
                    return null;

            // Add the order to the database
            Order order = new Order(idBill, idFood, quantity);
            handle.createUpdate(
                    "INSERT INTO orders (id_order, id_bill, id_food, quantity, status) VALUES (:idOrder, :idBill, :idFood, :quantity, :status)")
                    .bind("idOrder", order.getIdOrder())
                    .bind("idBill", idBill)
                    .bind("idFood", order.getIdFood())
                    .bind("quantity", order.getQuantity())
                    .bind("status", order.getStatus())
                    .execute();

            String nameTable = handle.createQuery("SELECT name_table from tables where id_table = (SELECT id_table from bills where id_bill = :idBill)")
                    .bind("idBill", idBill)
                    .mapTo(String.class)
                    .one();

            return new OrderReturn(nameTable, order.getIdOrder());
        });
    }
    /**
     * 
     * @param idRestaurant
     * @return List<FoodOrderDTO>
     *         [
        *         {
                      idTable: "1",
            *         idBill: "1",
            *         foods: [
                        *         {
                            *         idFood: "1",
                            *         name: "Bún bò Huế",
                            *         image: "bunbohue.jpg"
                                      quantity: 2,
                                      price: 50000
                        *         },
                        *         {
                            *         idFood: "2",
                            *         name: "Bún riêu",
                            *         image: "bunrieu.jpg"
                                      quantity: 1,
                                      price: 40000
                        *         }
        *                  ]
        *         total: 140000
        *         }
     */

    public List<FoodOrderDTO> getAllFoodOrders(String idRestaurant) {
        String sqlGetBill = "SELECT id_bill, tables.id_table, tables.name_table FROM tables inner join bills on bills.id_table = tables.id_table WHERE id_restaurant = :idRestaurant and bills.status = 'Open'";
        String sqlGetOrder = "SELECT id_food, quantity, status FROM orders WHERE id_bill = :idBill";
        String sqlGetFoodDetail = "SELECT id_food, name, image, price FROM foods WHERE id_food = :idFood";

        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery(sqlGetBill).bind("idRestaurant", idRestaurant)
                    .mapToMap().list();
            List<FoodOrderDTO> orders = new ArrayList<>();
            for (Map<String, Object> result : results) {
                String idBill = (String) result.get("id_bill");
                String nameTable = (String) result.get("name_table");
                String idTable = (String) result.get("id_table");
                FoodOrderDTO order = new FoodOrderDTO(idTable, idBill, nameTable);
                List<Map<String, Object>> orderResults = handle.createQuery(sqlGetOrder).bind("idBill", idBill)
                        .mapToMap().list();
                for (Map<String, Object> orderResult : orderResults) {
                    String idFood = (String) orderResult.get("id_food");
                    int quantity = (int) orderResult.get("quantity");
                    List<Map<String, Object>> foodResults = handle.createQuery(sqlGetFoodDetail).bind("idFood", idFood)
                            .mapToMap().list();
                    for (Map<String, Object> foodResult : foodResults) {
                        String name = (String) foodResult.get("name");
                        String image = (String) foodResult.get("image");
                        float price = (float) foodResult.get("price");
                        order.addFoodDetails(new FoodDetails(idFood, name, price, image, quantity));
                    }
                }
                order.calculateTotal();
                orders.add(order);
            }
            return orders;
        });
    }

    public List<FoodOrderDTO2> getAllOrderClient(String idBill) {
        String sqlGetBill = "SELECT tables.id_table, tables.name_table FROM tables inner join bills on bills.id_table = tables.id_table WHERE id_bill = :idBill and bills.status = 'Open'";
        String sqlGetOrder = "SELECT id_food, quantity, status, payment FROM orders WHERE id_bill = :idBill and payment = 'Paid'";
        String sqlGetFoodDetail = "SELECT id_food, name, image, price FROM foods WHERE id_food = :idFood";

        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery(sqlGetBill).bind("idBill", idBill)
                    .mapToMap().list();
            List<FoodOrderDTO2> orders = new ArrayList<>();
            for (Map<String, Object> result : results) {
                String nameTable = (String) result.get("name_table");
                String idTable = (String) result.get("id_table");
                FoodOrderDTO2 order = new FoodOrderDTO2(idTable, idBill, nameTable);
                List<Map<String, Object>> orderResults = handle.createQuery(sqlGetOrder).bind("idBill", idBill)
                        .mapToMap().list();
                for (Map<String, Object> orderResult : orderResults) {
                    String idFood = (String) orderResult.get("id_food");
                    int quantity = (int) orderResult.get("quantity");
                    String isPaid = (String) orderResult.get("payment");
                    List<Map<String, Object>> foodResults = handle.createQuery(sqlGetFoodDetail).bind("idFood", idFood)
                            .mapToMap().list();
                    for (Map<String, Object> foodResult : foodResults) {
                        String name = (String) foodResult.get("name");
                        String image = (String) foodResult.get("image");
                        float price = (float) foodResult.get("price");
                        order.addFoodDetails(new FoodDetails2(idFood, name, price, image, quantity, isPaid));
                    }
                }
                order.calculateTotal();
                orders.add(order);
            }
            return orders;
        });
    }

    public float totalBill(String idBill) {
        return jdbi.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery(
                    "SELECT f.price, o.quantity FROM orders as o inner join foods as f on o.id_food = f.id_food WHERE o.id_bill = :idBill and o.payment = 'Not Paid'")
                    .bind("idBill", idBill)
                    .mapToMap()
                    .list();
            float total = 0;
            for (Map<String, Object> result : results) {
                float price = (float) result.get("price");
                int quantity = (int) result.get("quantity");
                total += price * quantity;
            }
            return total;
        });
    }

    public ObjectPayment getPaymentFood(String idOrder) {
        //Check if the order is paid
        String status = jdbi.withHandle(handle -> {
            return handle.createQuery("SELECT payment FROM orders WHERE id_order = :idOrder")
                    .bind("idOrder", idOrder)
                    .mapTo(String.class)
                    .one();
        });
        if(status.equals("Paid"))
            return null;

        return jdbi.withHandle(handle -> {
            // Get the total price of the order
            List<Map<String, Object>> results = handle.createQuery(
                    "SELECT f.price, o.quantity FROM orders as o inner join foods as f on o.id_food = f.id_food WHERE o.id_order = :idOrder and o.payment = 'Not Paid'")
                    .bind("idOrder", idOrder)
                    .mapToMap()
                    .list();
            float total = 0;
            for (Map<String, Object> result : results) {
                float price = (float) result.get("price");
                int quantity = (int) result.get("quantity");
                total += price * quantity;
            }
            // Get the name of the food
            String nameFood = handle.createQuery("SELECT f.name FROM orders as o inner join foods as f on o.id_food = f.id_food WHERE o.id_order = :idOrder")
                    .bind("idOrder", idOrder)
                    .mapTo(String.class)
                    .one();
            // Generate a unique code
            String code = GenerateID.generateUniqueID();
            // Update the code in the database
            handle.createUpdate("UPDATE orders SET code = :code WHERE id_order = :idOrder")
                    .bind("code", code)
                    .bind("idOrder", idOrder)
                    .execute();
            return new ObjectPayment(nameFood, total, 1, code);
        });
    }

    public boolean completePayment(String idBill) {
        try {
            // Pay all the orders
            jdbi.withHandle(handle -> {
                return handle.createUpdate("UPDATE orders SET payment = 'Paid' WHERE id_bill = :idBill")
                        .bind("idBill", idBill)
                        .execute();
            });

            // Close the bill
            jdbi.withHandle(handle -> {
                return handle.createUpdate("Update bills set status = 'Close' where id_bill = :idBill")
                        .bind("idBill", idBill)
                        .execute();
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completePaymentFood(String idOrder, String code) {
        try {
            // Check if the code is correct
            String codeDB = jdbi.withHandle(handle -> {
                return handle.createQuery("SELECT code FROM orders WHERE id_order = :idOrder")
                        .bind("idOrder", idOrder)
                        .mapTo(String.class)
                        .one();
            });
            if (!codeDB.equals(code))
                return false;
            
            // Pay the order
            jdbi.withHandle(handle -> {
                return handle.createUpdate("UPDATE orders SET payment = 'Paid' WHERE id_order = :idOrder")
                        .bind("idOrder", idOrder)
                        .execute();
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
