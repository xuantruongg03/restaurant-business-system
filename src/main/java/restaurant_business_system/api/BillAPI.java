package restaurant_business_system.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.db.bill.Bill;
import restaurant_business_system.db.bill.BillDAO;
import restaurant_business_system.db.bill.BillDTO;
import restaurant_business_system.db.food.FoodOrder;

class OrderRequest {
    private String idBill;
    private List<FoodOrder> foodOrders;

    public OrderRequest(){}

    public OrderRequest(String idBill, List<FoodOrder> foodOrders) {
        this.idBill = idBill;
        this.foodOrders = foodOrders;
    }

    // getters and setters
    public String getIdBill() {
        return idBill;
    }

    public void setIdBill(String idBill) {
        this.idBill = idBill;
    }

    public List<FoodOrder> getFoodOrders() {
        return foodOrders;
    }

    public void setFoodOrders(List<FoodOrder> foodOrders) {
        this.foodOrders = foodOrders;
    }
}

@Path("/bills")
@Produces(MediaType.APPLICATION_JSON)
public class BillAPI {
    private final BillDAO dao;
    private final Logger LOGGER = LoggerFactory.getLogger(BillAPI.class);

    public BillAPI(BillDAO dao) {
        this.dao = dao;
    }

    private void notifyClients() {
        // Notify clients that the bill for the table has been updated
        
    }

    /**
     * Create a new bill.
     * 
     * @param b the bill to create
     * @return a response indicating the success of the operation
     */
    @POST
    @Path("/checkOrCreate")
    public Response checkOrCreate(Bill b) {
        // Check if a bill already exists for the table
        String bill = dao.checkBill(b.getIdTable());
        if (bill != null) {
            return Response.ok(bill).build();
        }

        // Create a new bill
        Bill nb = new Bill(b.getIdTable());
        if (dao.create(nb) != null) {
            notifyClients();
            return Response.ok(nb.getIdBill()).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Get the details of a bill.
     * 
     * @param idBill the id of the bill
     * @return a response containing the bill details
     */
    @GET
    @Path("/get")
    public Response getBill(@QueryParam("idBill") String idBill){
        BillDTO bill = dao.getBillDetails(idBill);
        if (bill != null) {
            return Response.ok(bill).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("order")
    public Response order(OrderRequest o) {
        LOGGER.info("Ordering food for bill {}", o.getIdBill());
        LOGGER.info("Food orders: {}", o.getFoodOrders());
        if (dao.order(o.getIdBill(), o.getFoodOrders())) {
            notifyClients();
            return Response.ok("OK").build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }

    @POST
    @Path("close")
    public Response close(@QueryParam("idBill") String idBill) {
        if (dao.closeBill(idBill)) {
            notifyClients();
            return Response.ok("OK").build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }
}
