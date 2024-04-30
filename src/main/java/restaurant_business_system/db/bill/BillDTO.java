package restaurant_business_system.db.bill;

import java.util.List;

class FoodDetail{
    private String idFood;
    private String name;
    private int price;
    private int quantity;

    public FoodDetail(String idFood, String name, int price, int quantity) {
        this.idFood = idFood;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getIdFood() {
        return idFood;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}

public class BillDTO {
    private String idTable;
    private String idBill;
    private List<FoodDetail> foods;
    private double total;
    private String status;

    public BillDTO(String idTable, String idBill, List<FoodDetail> foods, String status) {
        this.idTable = idTable;
        this.idBill = idBill;
        this.foods = foods;
        double total = 0;
        for (FoodDetail food : foods) {
            total += food.getPrice() * food.getQuantity();
        }
        this.total = total;
        this.status = status;
    }

    public String getIdTable() {
        return idTable;
    }

    public String getIdBill() {
        return idBill;
    }

    public List<FoodDetail> getFoods() {
        return foods;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}
