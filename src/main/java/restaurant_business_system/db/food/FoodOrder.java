package restaurant_business_system.db.food;

public class FoodOrder {
    private String idFood;
    private int quantity;

    public FoodOrder(String idFood, int quantity) {
        this.idFood = idFood;
        this.quantity = quantity;
    }

    public FoodOrder() {
    }

    public String getIdFood() {
        return idFood;
    }
    public int getQuantity() {
        return quantity;
    }
}
