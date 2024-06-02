package restaurant_business_system.db.food;

import java.util.List;

import restaurant_business_system.db.bill.Bill;

public class FoodOrderDTO {
    private String idTable;
    private List<Bill> bills;

    public FoodOrderDTO(String idTable, List<Bill> bills) {
        this.idTable = idTable;
        this.bills = bills;
    }

    public String getIdTable() {
        return idTable;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void addBill(Bill bill) {
        bills.add(bill);
    }
}
