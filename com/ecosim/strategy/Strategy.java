package com.ecosim.strategy;

import com.ecosim.model.Entity;
import java.util.List;

public interface Strategy {
    void update(Entity self, List<Entity> others, int width, int height);
}