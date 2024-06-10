package restaurant_business_system.db.bill;

public class ObjectPayment {
    private String nameFood;
    private float price;
    private int quantity;
    private String code;

    public ObjectPayment(String nameFood, float price, int quantity, String code) {
        this.nameFood = nameFood;
        this.price = price;
        this.quantity = quantity;
        this.code = code;
    }

    public String getNameFood() {
        return nameFood;
    }

    public float getPrice() {
        return price;
    }
    
    public int getQuantity() {
        return quantity;
    }

    public String getCode() {
        return code;
    }
}