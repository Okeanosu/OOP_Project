package com.ecosim.model;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Bush extends Entity {
    private List<Entity> hidingPrey = new ArrayList<>();

    public Bush(double x, double y) {
        super(x, y, new Color(34, 139, 34, 200), 0); // Màu xanh lá đậm, hơi trong suốt
    }

    public List<Entity> getHidingPrey() {
        return hidingPrey;
    }

    @Override
    public void update(List<Entity> allEntities) {
        super.update(allEntities);
        // Remove dead prey from hiding list
        hidingPrey.removeIf(Entity::isDead);
    }
}