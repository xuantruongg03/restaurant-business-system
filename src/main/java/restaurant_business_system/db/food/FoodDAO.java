package restaurant_business_system.db.food;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public class FoodDAO {
    private final Jdbi jdbi;

    public FoodDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @SqlUpdate("INSERT INTO foods (id_food, id_menu, name, price, image) VALUES (:idFood, :idMenu, :name, :price, :image)")
    public Food create(Food food) {
        jdbi.useHandle(handle -> handle.createUpdate("INSERT INTO foods (id_food, id_menu, name, price, image) VALUES (:idFood, :idMenu, :name, :price, :image)")
                .bind("idFood", food.getIdFood())
                .bind("idMenu", food.getIdMenu())
                .bind("name", food.getName())
                .bind("price", food.getPrice())
                .bind("image", food.getImage())
                .execute());
        return food;
    }
}
