package com.ecosim.strategy;
import com.ecosim.model.*;
import java.util.List;

public class HunterStrategy implements SurvivalStrategy {
    @Override
public void execute(Entity self, List<Entity> allEntities) {
    if (self.isDead()) return;

    // LUÔN NÉ ĐÁ TRƯỚC (Bẻ lái sang bên)
    avoidObstacles(self, allEntities);

    Wolf thisWolf = (Wolf) self;

    // ƯU TIÊN 1: UỐNG NƯỚC (Giữ nguyên logic của bạn nhưng thêm việc xóa mục tiêu cũ)
    if (self.getThirst() < 30) {
        thisWolf.targetPrey = null; // Bỏ săn để đi uống nước
        self.moveTowards(850, 1800, self.getBaseSpeed()*3);
        if (self.distanceToPoint(850, 1800) < 300) self.setThirst(100);
        return;
    }

    // ƯU TIÊN 2: SĂN MỒI (Mỗi con 1 mục tiêu)
    if (self.getHunger() < 80) {
        // Nếu chưa có mục tiêu hoặc mục tiêu đã chết/quá xa/đang trốn/gần bụi cỏ -> Tìm con thỏ chưa bị ai đuổi
        if (thisWolf.targetPrey == null || thisWolf.targetPrey.isDead() || self.distanceTo(thisWolf.targetPrey) > 600 || isPreyHiding(thisWolf.targetPrey, allEntities) || isPreyNearBush(thisWolf.targetPrey, allEntities)) {
            thisWolf.targetPrey = findUniqueRabbit(thisWolf, allEntities);
        }

        if (thisWolf.targetPrey != null) {
            self.moveTowards(thisWolf.targetPrey.getX(), thisWolf.targetPrey.getY(), self.getBaseSpeed() * 2.8);
            if (self.distanceTo(thisWolf.targetPrey) < 20 && !isPreyHiding(thisWolf.targetPrey, allEntities) && !isPreyNearBush(thisWolf.targetPrey, allEntities)){
                thisWolf.targetPrey.setDead(true);
                self.setHunger(100);
                thisWolf.targetPrey = null;
            }
            return;
        }
    }

    // ƯU TIÊN CUỐI: LANG THANG
    self.moveWander();
}

private Entity findUniqueRabbit(Wolf self, List<Entity> allEntities) {
    Entity bestChoice = null;
    double minDist = 500;
        // SÓI ĐÓI QUÁ
        if (self.getHunger() < 30) minDist = 1000;
    for (Entity e : allEntities) {
        if (e instanceof Rabbit && !e.isDead() && !isPreyHiding(e, allEntities) && !isPreyNearBush(e, allEntities)) {
            // Kiểm tra xem có sói nào khác đang nhắm con thỏ này không
            boolean beingChased = false;
            for (Entity other : allEntities) {
                if (other instanceof Wolf && other != self) {
                    if (((Wolf) other).targetPrey == e) {
                        beingChased = true;
                        break;
                    }
                }
            }

            if (!beingChased) {
                double d = self.distanceTo(e);
                if (d < minDist) {
                    minDist = d;
                    bestChoice = e;
                }
            }
        }
    }
    // Nếu tất cả thỏ đều đang bị đuổi, con sói này sẽ chọn con gần nhất (không để nó đứng im)
    return (bestChoice != null) ? bestChoice : findNearestRabbit(self, allEntities);
}

    private Entity findNearestRabbit(Entity self, List<Entity> allEntities) {
        Entity nearest = null;
        double minDist = 500; // Tầm nhìn của sói
        // SÓI ĐÓI QUÁ
        if (self.getHunger() < 30) minDist = 1000;
        for (Entity e : allEntities) {
            if (e instanceof Rabbit && !e.isDead() && !isPreyHiding(e, allEntities) && !isPreyNearBush(e, allEntities)) {
                double d = self.distanceTo(e);
                if (d < minDist) {
                    minDist = d;
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

    private boolean isPreyHiding(Entity prey, List<Entity> allEntities) {
        for (Entity e : allEntities) {
            if (e instanceof Bush && ((Bush) e).getHidingPrey().contains(prey)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPreyNearBush(Entity prey, List<Entity> allEntities) {
        for (Entity e : allEntities) {
            if (e instanceof Bush && prey.distanceTo(e) < 80) {
                return true;
            }
        }
        return false;
    }
}