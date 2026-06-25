package com.ecosim.strategy;

import com.ecosim.model.*;
import java.util.List;

public class DominantStrategy implements SurvivalStrategy {
    @Override
    public void execute(Entity self, List<Entity> allEntities) {
        if (self.isDead())
            return;

        avoidObstacles(self, allEntities);
        // Logic sinh tồn cơ bản: Ưu tiên tìm nước, sau đó tìm thức ăn
        if (self.getThirst() < 30) {
             self.moveTowards(850, 1800, self.getBaseSpeed() * 1.5);
             return;
        } else if (self.getThirst() > 90 && self.distanceToPoint(850, 1800) < 350) {
        // Khát đầy và ở trong hồ thì ra khỏi hồ
             self.moveWander(); 
             return;
        } else if (self.getHunger() < 80) {
            Entity targetGrass = findNearestPlant(self, allEntities);
            if (targetGrass != null) {
                self.moveTowards(targetGrass.getX(), targetGrass.getY(), self.getBaseSpeed());
                // Tầm ăn của Voi (25) rộng hơn vì kích thước Voi lớn
                if (self.distanceTo(targetGrass) < 25) { 
                    targetGrass.setDead(true);
                    self.setHunger(self.getHunger() + 100);
                }
            } else {
                self.moveWander();
            }
        } else {
            self.moveWander();
        }
    }

    private Entity findNearestPlant(Entity self, List<Entity> allEntities) {
        Entity nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Entity e : allEntities) {
            if (e instanceof Plant) {
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
                if (dist < 80) { 
                    double diffX = self.getX() - e.getX();
                    double diffY = self.getY() - e.getY();
                    double sideX = -diffY;
                    double sideY = diffX;
                    self.setX(self.getX() + (sideX / dist) * self.getBaseSpeed());
                    self.setY(self.getY() + (sideY / dist) * self.getBaseSpeed());
                }
            }
        }
    }
}