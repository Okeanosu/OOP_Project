package com.ecosim.model;
import com.ecosim.strategy.DominantStrategy;
import java.awt.Color;
import java.util.List;

public class Elephant extends Entity {
    public Elephant(double x, double y) {
        super(x, y, Color.DARK_GRAY, 1.2); 
        this.size = 60;
        this.strategy = new DominantStrategy();
    }

    @Override
    public void update(List<Entity> allEntities) {
        super.update(allEntities);
    }
}