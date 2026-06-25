package com.ecosim.model;
import com.ecosim.strategy.HunterStrategy;
import java.awt.Color;
import java.util.List;

public class Wolf extends Entity {
    public Entity targetPrey = null; // Biến để lưu con thỏ đang săn
    public Wolf(double x, double y) {
        super(x, y, Color.GRAY, 2.5);
        this.strategy = new HunterStrategy();
    }
@Override
public void update(List<Entity> allEntities) {
    super.update(allEntities); // Gọi hàm update của lớp cha để xử lý đói, khát và di chuyển
}
}