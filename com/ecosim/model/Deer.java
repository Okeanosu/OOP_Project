package com.ecosim.model;
import com.ecosim.strategy.PassiveStrategy;
import java.awt.Color;
import java.util.List;

public class Deer extends Entity {
    public Deer(double x, double y) {
        super(x, y, new Color(139, 69, 19), 2.8); 
        this.size = 30;
        this.strategy = new PassiveStrategy();
    }

    @Override
    public void update(List<Entity> allEntities) {
        super.update(allEntities); 
    }

}
