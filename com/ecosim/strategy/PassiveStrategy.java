package com.ecosim.strategy;

import com.ecosim.model.*;
import java.util.List;

public class PassiveStrategy implements SurvivalStrategy {
    @Override
    public void execute(Entity self, List<Entity> allEntities) {
        if (self.isDead())
            return;
        
        if (self.getHunger() < 50) {
            self.setStrategy(new AggressiveStrategy());
            return; // Dừng chạy các logic nhút nhát phía dưới
        }

        // Xóa thỏ khỏi danh sách ẩn nấp của tất cả các bụi cỏ
        for (Entity e : allEntities) {
            if (e instanceof Bush) {
                ((Bush) e).getHidingPrey().remove(self);
            }
        }

        // 1. LUÔN NÉ VẬT CẢN ĐẦU TIÊN
        avoidObstacles(self, allEntities);

        // 2. TÌM SÓI GẦN NHẤT ĐỂ CHẠY TRỐN (ƯU TIÊN CAO NHẤT)
        Entity wolf = null;
        double panicDistance = 150; // Tầm mà thỏ bắt đầu hoảng loạn

        for (Entity e : allEntities) {
            if (e instanceof Wolf && self.distanceTo(e) < panicDistance) {
                wolf = e;
                break;
            }
        }

        if (wolf != null) {
            // TÌM BỤI CỎ GẦN NHẤT
            Entity nearestBush = findNearestBush(self, allEntities);
    
        // Nếu có bụi cỏ gần đó (< 300px), thỏ sẽ chạy vào nấp thay vì chạy loạn
        if (nearestBush != null && self.distanceTo(nearestBush) < 300) {
        self.moveTowards(nearestBush.getX() + 40, nearestBush.getY() + 40, self.getBaseSpeed() * 2.2);
        // Add to hiding when within safe zone (80px - bush size)
        if (self.distanceTo(nearestBush) < 80) {
            if (!((Bush) nearestBush).getHidingPrey().contains(self)) {
                ((Bush) nearestBush).getHidingPrey().add(self);
            }
        }
        return;
    }
            // TÍNH TOÁN HƯỚNG CHẠY TRỐN DỨT KHOÁT
            // Điểm an toàn = Tọa độ thỏ + (Vectơ từ Sói đến Thỏ) kéo dài ra
            double escapeX = self.getX() + (self.getX() - wolf.getX()) * 2;
            double escapeY = self.getY() + (self.getY() - wolf.getY()) * 2;

            // Thỏ chạy "bán sống bán chết" với tốc độ cao hơn bình thường
            self.moveTowards(escapeX, escapeY, self.getBaseSpeed() * 2.2);
            return;
        }

        // 3. NẾU AN TOÀN THÌ MỚI XÉT ĐẾN ĐÓI / KHÁT
        if (self.getThirst() < 50) {
            self.moveTowards(850, 1800, self.getBaseSpeed());
            if (self.distanceToPoint(850, 1800) < 300)
                self.setThirst(100);
        } else if (self.getHunger() < 80) {
            Entity targetGrass = findNearestPlant(self, allEntities);
            if (targetGrass != null) {
                self.moveTowards(targetGrass.getX(), targetGrass.getY(), self.getBaseSpeed());
                if (self.distanceTo(targetGrass) < 15) {
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

    // Hàm tìm bụi cỏ gần nhất để thỏ có thể nấp khi bị sói đuổi
    private Entity findNearestBush(Entity self, List<Entity> allEntities) {
        Entity nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Entity e : allEntities) {
            if (e instanceof Bush && ((Bush) e).getHidingPrey().size() < 2) {
                double dist = self.distanceTo(e);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = e;
                }
            }
        }
        return nearest;
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
            if (dist < 60) { // Tầm nhận biết vật cản (nên lớn hơn kích thước đá)
                // 1. Tính vector từ đá đến Sói
                double diffX = self.getX() - e.getX();
                double diffY = self.getY() - e.getY();

                // 2. Tạo lực bẻ lái vuông góc (Side Force)
                // Hoán đổi diffX, diffY và đổi dấu một bên để tạo vector vuông góc
                // Điều này giúp sói lách sang trái hoặc phải thay vì lùi lại
                double sideX = -diffY;
                double sideY = diffX;

                // 3. Áp dụng lực lách
                self.setX(self.getX() + (sideX / dist) * self.getBaseSpeed() * 1.5);
                self.setY(self.getY() + (sideY / dist) * self.getBaseSpeed() * 1.5);
            }
        }
    }
}
}