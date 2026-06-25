package com.ecosim.model;
import java.awt.Color;
import java.util.List;

public class Plant extends Entity {
    public Plant(double x, double y) { super(x, y, new Color(34, 139, 34), 0); }
    @Override public void update(List<Entity> allEntities) { /* Cỏ không di chuyển */ }
}