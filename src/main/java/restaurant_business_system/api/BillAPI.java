package restaurant_business_system.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.db.bill.Bill;
import restaurant_business_system.db.bill.BillDAO;
import restaurant_business_system.db.bill.BillDTO;

@Path("/bills")
public class BillAPI {
    private final BillDAO dao;

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
            return Response.ok(nb.getIdBill()).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("/get")
    public Response getBill(@QueryParam("idBill") String idBill){
        BillDTO bill = dao.getBillDetails(idBill);
        if (bill != null) {
            return Response.ok(bill).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
