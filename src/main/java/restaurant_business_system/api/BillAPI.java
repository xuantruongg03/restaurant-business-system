package restaurant_business_system.api;

import java.io.IOException;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Auth;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.core.User;
import restaurant_business_system.db.bill.Bill;
import restaurant_business_system.db.bill.BillDAO;
import restaurant_business_system.db.bill.BillDTO;
import restaurant_business_system.db.bill.ObjectPayment;
import restaurant_business_system.db.bill.OrderReturn;
import restaurant_business_system.db.food.FoodOrderDTO;
import restaurant_business_system.db.food.FoodOrderDTO2;

class OrderRequest {
    private String idBill;
    private String idRestaurant;
    // private List<FoodOrder> foodOrders;
    private String idFood;
    private int quantity;

    public OrderRequest() {
    }

    public OrderRequest(String idBill, String idRestaurant, String idFood, int quantity) {
        this.idBill = idBill;
        this.idRestaurant = idRestaurant;
        this.idFood = idFood;
        this.quantity = quantity;
    }

    // getters and setters
    public String getIdBill() {
        return idBill;
    }

    public void setIdBill(String idBill) {
        this.idBill = idBill;
    }
    
    public String getIdFood() {
        return idFood;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public int getQuantity() {
        return quantity;
    }
}

class Url {
    private String url;

    public Url(String url) {
        this.url = url;
    }

    // getters and setters
    public String getUrl() {
        return url;
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

    /**
     * Notifies clients that the bill for the table has been updated.
     * 
     * @param idRestaurant the ID of the restaurant
     * @param message the message to be sent to the clients
     * @return 1 if the message was successfully sent, -1 if the client was not found, 0 if there was an error sending the message
     */
    private int notifyClients(String idRestaurant, String message) {
        // Notify clients that the bill for the table has been updated
        try {
            int statusCode = sendMessageToSocketServer(idRestaurant, message);
            if (statusCode == HttpStatus.SC_OK) {
                return 1;
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                LOGGER.error("Client not found");
                return -1;
            } else {
                LOGGER.error("Failed to send message to client");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
            // boolean noti = notifyClients();
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
    public Response getBill(@QueryParam("idBill") String idBill) {
        BillDTO bill = dao.getBillDetails(idBill);
        if (bill != null) {
            return Response.ok(bill).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Processes an order request and returns a response.
     *
     * @param o the order request containing the bill ID and food orders
     * @return a response indicating the success or failure of the order request
     */
    @POST
    @Path("order")
    public Response order(OrderRequest o) {
        LOGGER.info("Ordering food for bill {}", o.getIdBill());
        LOGGER.info("Food orders: {}", o.getIdFood());
        LOGGER.info("ID Restaurant: {}", o.getIdRestaurant());

        OrderReturn rs = dao.orderByFood(o.getIdBill(), o.getIdFood(), o.getQuantity());
        if (rs != null) {
            int rsNoti = notifyClients(o.getIdRestaurant(), "Bàn " + rs.getNametable()+ " có một đơn hàng mới!");
            if(rsNoti == 1) {
                return Response.ok(rs).build();
            } else if (rsNoti == -1) {
                // return Response.status(Response.Status.NOT_FOUND).build();
                return Response.ok(rs).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }

    /**
     * Retrieves all food orders for a given restaurant.
     *
     * @param u            the authenticated user
     * @param idRestaurant the ID of the restaurant
     * @return a Response object containing the list of food orders if the user is
     *         authenticated,
     *         otherwise returns an UNAUTHORIZED response
     */
    @GET
    @Path("/get-all-order")
    public Response getAllOrder(@Auth User u, @QueryParam("idRestaurant") String idRestaurant) {
        if (u != null) {
            List<FoodOrderDTO> orders = dao.getAllFoodOrders(idRestaurant);
            return Response.ok(orders).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/get-all-order-client")
    public Response getAllOrderClient(@QueryParam("idBill") String idBill) {
        if (idBill != null) {
            List<FoodOrderDTO2> orders = dao.getAllOrderClient(idBill);
            return Response.ok(orders).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/payment-all-bill")
    public Response payment(@QueryParam("idBill") String idBill) {
        if(idBill != null) {
            float price = dao.totalBill(idBill);
            String partnerCode = "MOMO";
            String desUrl = "http://localhost:3000/api/v1/bills/complete-payment?idBill=" + idBill;
            String des = "Thanh toán hóa đơn";
            String payUrl = createMethodPayment(price, partnerCode, desUrl, des);
            return Response.ok(payUrl).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/payment-one-food")
    public Response paymentFood(@QueryParam("idOrder") String idOrder) {
        if(idOrder != null) {
            ObjectPayment payment = dao.getPaymentFood(idOrder);
            if(payment == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            String partnerCode = "MOMO";
            String desUrl = "http://localhost:8082/bills/complete-payment-food?idOrder=" + idOrder + "&code=" + payment.getCode();
            String desc = "Thanh toan mon an \nTen mon: " + payment.getNameFood() + "\nSo luong: " + payment.getQuantity();
            String payUrl = createMethodPayment(payment.getPrice(), partnerCode, desUrl, desc);
            if(payUrl != null && !payUrl.isEmpty()) {
                Url url = new Url(payUrl);
                return Response.ok(url).build();
            } else {
                return Response.status(Response.Status.EXPECTATION_FAILED).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("complete-payment-food")
    public Response completePaymentFood(@QueryParam("idOrder") String idOrder, @QueryParam("code") String code, @QueryParam("resultCode") String resultCode) {
        // Logger LOGGER = LoggerFactory.getLogger(BillAPI.class);
        // LOGGER.info("idOrder: " + idOrder);
        // LOGGER.info("code: " + code);
        // LOGGER.info("resultCode: " + resultCode);
        if(idOrder != null && code != null && resultCode.equals("0")) {
            boolean rs = dao.completePaymentFood(idOrder, code);
            if(rs) {
                return Response.ok("Thanh toan thanh cong! Vui long quay tro lai trang truoc.").build();
            }
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("complete-payment")
    public Response completePayment(@QueryParam("idBill") String idBill) {
        if(idBill != null) {
            boolean rs = dao.completePayment(idBill);
            if(rs) {
                return Response.ok("OK").build();
            }
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @SuppressWarnings("deprecation") // Suppress deprecation warning for EntityUtils.consume
    private String createMethodPayment(float price, String partnerCode, String desUrl, String des) {
        String payUrl = "";
        String url = "http://localhost:3000/api/v1/momo/payment-request";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");

        String json = String.format("{\"price\":\"%s\", \"partnerCode\":\"%s\", \"desUrl\":\"%s\", \"des\":\"%s\"}", price, partnerCode, desUrl, des);
        StringEntity entity = new StringEntity(json);
        post.setEntity(entity);

        try (CloseableHttpResponse response = client.execute(post)) {
            String responseBody;
            try {
                responseBody = EntityUtils.toString(response.getEntity());
                LOGGER.info("Response url: " + responseBody);
                payUrl = responseBody;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            EntityUtils.consume(response.getEntity());
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payUrl;
    }

    @SuppressWarnings("deprecation") // Suppress deprecation warning for EntityUtils.consume
    private int sendMessageToSocketServer(String clientId, String message) throws IOException {
        String url = "http://localhost:3000/api/v1/socket/send-message";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");

        String json = String.format("{\"clientId\":\"%s\", \"message\":\"%s\"}", clientId, message);
        StringEntity entity = new StringEntity(json);
        post.setEntity(entity);

        CloseableHttpResponse response = client.execute(post);
        int statusCode = response.getCode();
        LOGGER.info("Response code: " + statusCode);
        EntityUtils.consume(response.getEntity());
        client.close();

        return statusCode;
    }
}
