package restaurant_business_system.db.bill;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public class BillDAO {
    private final Jdbi jdbi;
    
    public BillDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @SqlUpdate("INSERT INTO bill (idBill, idTable, status) VALUES (:idBill, :idTable, :status)")
    public Bill create(Bill b) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("INSERT INTO bill (idBill, idTable, status) VALUES (:idBill, :idTable, :status)")
                    .bind("idBill", b.getIdBill())
                    .bind("idTable", b.getIdTable())
                    .bind("status", b.getStatus())
                    .execute();
        });
        return b;
    }

    @SqlUpdate("UPDATE bill SET status = :status WHERE idBill = :idBill")
    public void updateStatus(String idBill, String status) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("UPDATE bill SET status = :status WHERE idBill = :idBill")
                    .bind("idBill", idBill)
                    .bind("status", status)
                    .execute();
        });
    }

    @SqlUpdate("DELETE FROM bill WHERE idBill = :idBill")
    public void delete(String idBill) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("DELETE FROM bill WHERE idBill = :idBill")
                    .bind("idBill", idBill)
                    .execute();
        });
    }

    @SqlUpdate("SELECT * FROM bill WHERE id_table = :idTable")
    public void getBillByTable(String idTable) {
        jdbi.useHandle(handle -> {
            handle.createQuery("SELECT * FROM bill WHERE id_table = :idTable")
                    .bind("idTable", idTable)
                    .mapToBean(Bill.class)
                    .list();
        });
    }
}
