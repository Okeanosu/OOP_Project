package com.ecosim.model;
import com.ecosim.strategy.PassiveStrategy;
import java.awt.Color;
import java.util.List;

public class Rabbit extends Entity {
    public Rabbit(double x, double y) {
        super(x, y, Color.WHITE, 2.5); // Thỏ chạy nhanh
        this.strategy = new PassiveStrategy(); // Gán trí tuệ cho Thỏ
    }

    @Override
    public void update(List<Entity> allEntities) {
        // Gọi hàm update của lớp cha (Entity) để xử lý đói, khát và di chuyển
        super.update(allEntities); 
        
        // Bạn có thể thêm logic riêng của Thỏ ở đây nếu muốn
    }
}