package com.ecosim.strategy;

import com.ecosim.model.*;
import java.util.List;

public class HunterStrategy implements SurvivalStrategy {
    @Override
    public void execute(Entity self, List<Entity> allEntities) {
        if (self.isDead()) return;

        // LUÔN NÉ ĐÁ TRƯỚC
        avoidObstacles(self, allEntities);

        // ƯU TIÊN 1: UỐNG NƯỚC KHI KHÁT (Chạy về Hồ)
        if (self.getThirst() < 30) {
             self.moveTowards(850, 1800, self.getBaseSpeed() * 1.5);
             return;
        } else if (self.getThirst() > 90 && self.distanceToPoint(850, 1800) < 350) {
        // Khát đầy và ở trong hồ thì ra khỏi hồ
             self.moveWander(); 
             return;
        }

        // ƯU TIÊN 2: SĂN MỒI (Hổ và Sói săn Thỏ, Hươu)
        if (self.getHunger() < 80) {
            // Kiểm tra: Nếu chưa có mục tiêu, mục tiêu đã chết, đi quá xa, hoặc con mồi đang trốn bụi rậm
            if (self.targetPrey == null || self.targetPrey.isDead() || 
                self.distanceTo(self.targetPrey) > 600 || 
                isPreyHiding(self.targetPrey, allEntities)) {
                
                self.targetPrey = findUniquePrey(self, allEntities);
            }

            if (self.targetPrey != null) {
                self.moveTowards(self.targetPrey.getX(), self.targetPrey.getY(), self.getBaseSpeed() * 2.5);
                
                // Cắn con mồi nếu khoảng cách < 25
                if (self.distanceTo(self.targetPrey) < 25 && !isPreyHiding(self.targetPrey, allEntities)) {
                    self.targetPrey.setDead(true);
                    self.setHunger(self.getHunger() + 100);
                    self.targetPrey = null;
                }
                return;
            }
        }

        // ƯU TIÊN CUỐI: LANG THANG
        self.moveWander();
    }

    private Entity findUniquePrey(Entity self, List<Entity> allEntities) {
        Entity bestChoice = null;
        double minDist = (self.getHunger() < 30) ? 1000 : 500; // Đói quá thì quét mục tiêu xa hơn

        for (Entity e : allEntities) {
            // Săn Thỏ hoặc Hươu
            if ((e instanceof Rabbit || e instanceof Deer) && !e.isDead() && !isPreyHiding(e, allEntities)) {
                
                // Tránh tranh giành mồi của con thú dữ khác (Sói/Hổ)
                boolean beingChased = false;
                for (Entity other : allEntities) {
                    if ((other instanceof Wolf || other instanceof Tiger) && other != self) {
                        if (other.targetPrey == e) {
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
        return (bestChoice != null) ? bestChoice : findNearestPrey(self, allEntities);
    }

    private Entity findNearestPrey(Entity self, List<Entity> allEntities) {
        Entity nearest = null;
        double minDist = (self.getHunger() < 30) ? 1000 : 500;
        for (Entity e : allEntities) {
            if ((e instanceof Rabbit || e instanceof Deer) && !e.isDead() && !isPreyHiding(e, allEntities)) {
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
                if (dist < 60) {
                    double diffX = self.getX() - e.getX();
                    double diffY = self.getY() - e.getY();
                    self.setX(self.getX() + (-diffY / dist) * self.getBaseSpeed() * 1.5);
                    self.setY(self.getY() + (diffX / dist) * self.getBaseSpeed() * 1.5);
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
}