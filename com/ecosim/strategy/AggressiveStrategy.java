package com.ecosim.strategy;

import com.ecosim.model.*;
import java.util.List;

public class AggressiveStrategy implements SurvivalStrategy {
    @Override
    public void execute(Entity self, List<Entity> allEntities) {
        if (self.isDead()) return;

        // 1. Né đá 
        avoidObstacles(self, allEntities);

        // 2. Hunger > 50: Quay về PassiveStrategy
        if (self.getHunger() > 50) {
            self.setStrategy(new PassiveStrategy());
            return;
        }

        // Quá đói
        Entity targetGrass = findNearestPlant(self, allEntities);
        if (targetGrass != null) {
            // Tăng tốc chạy đến thức ăn
            self.moveTowards(targetGrass.getX(), targetGrass.getY(), self.getBaseSpeed() * 1.5);
            
            if (self.distanceTo(targetGrass) < 20) {
                targetGrass.setDead(true);
                self.setHunger(self.getHunger() + 50); 
            }
        } else {
            self.moveWander();
        }
    }

    private Entity findNearestPlant(Entity self, List<Entity> allEntities) {
        Entity nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Entity e : allEntities) {
            if (e instanceof Plant && !e.isDead()) {
                double dist = self.distanceTo(e);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = e;
                }
            }
        }
        return nearest;
    }

    private void avoidObstacles(Entity self, List<Entity> allEntities) {
        for (Entity e : allEntities) {
            if (e instanceof Rock) {
                double dist = self.distanceTo(e);
                if (dist < 60) {
                    double diffX = self.getX() - e.getX();
                    double diffY = self.getY() - e.getY();
                    self.setX(self.getX() + (-diffY / dist) * self.getBaseSpeed());
                    self.setY(self.getY() + (diffX / dist) * self.getBaseSpeed());
                }
            }
        }
    }
}