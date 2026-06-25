package com.ecosim.model;
import java.awt.Color;
import java.util.List;

public class Rock extends Entity {
    public Rock(double x, double y) {
        super(x, y, Color.DARK_GRAY, 0); // Đá không di chuyển (speed = 0)
    }
    @Override
    public void update(List<Entity> allEntities) { /* Đá không làm gì cả */ }
}