package com.ecosim.strategy;
import com.ecosim.model.Entity;
import java.util.List;

public interface SurvivalStrategy {
    void execute(Entity self, List<Entity> allEntities);
}