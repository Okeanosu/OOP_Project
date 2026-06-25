package com.ecosim.model;
import com.ecosim.strategy.HunterStrategy;
import java.awt.Color;
import java.util.List;

public class Tiger extends Entity {
    public Tiger(double x, double y) {
        super(x, y, Color.ORANGE, 3.2); 
        this.size = 35; 
        this.strategy = new HunterStrategy();
    }

    @Override
    public void update(List<Entity> allEntities) {
        super.update(allEntities);
    }
}