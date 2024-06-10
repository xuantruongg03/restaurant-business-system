package restaurant_business_system.db.bill;

public class OrderReturn {
    private String nametable;
    private String idOrder;

    public OrderReturn(String nametable, String idOrder) {
        this.nametable = nametable;
        this.idOrder = idOrder;
    }

    public String getNametable() {
        return nametable;
    }

    public String getIdOrder() {
        return idOrder;
    }
}