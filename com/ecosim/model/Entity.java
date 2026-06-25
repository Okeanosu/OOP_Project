package com.ecosim.model;

import com.ecosim.strategy.SurvivalStrategy;
import java.awt.Color;
import java.util.List;

public abstract class Entity {
    public Entity targetPrey = null;
    protected double x, y;
    protected double speed, baseSpeed;
    protected int hunger = 100;
    protected int thirst = 100; // Thêm thanh khát
    protected boolean dead = false;
    protected Color color;
    protected SurvivalStrategy strategy;
    protected double lastAngle = Math.random() * Math.PI * 2;
    protected double size = 20; // Kích thước mặc định của sinh vật

    public Entity(double x, double y, Color color, double speed) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.baseSpeed = speed;
        this.speed = speed;
    }

        protected void applyTerrainEffects() {
        // Tọa độ tâm Hồ Nước hiện tại của bạn là (850, 1800)
        double distToLakeCenter = distanceToPoint(850, 1800);

        if (distToLakeCenter < 350) {
            // 1. Đang bơi trong Nước -> Đi rất chậm (40%), Hồi khát nước
            this.speed = this.baseSpeed * 0.4;
            this.thirst = Math.min(100, this.thirst + 2);
        } else if (distToLakeCenter < 550) {
            // 2. Đang dẫm lên Bãi bùn lầy -> Đi chậm (60%)
            this.speed = this.baseSpeed * 0.6;
        } else if (this.x > 1800 && this.x < 3300 && this.y > 400 && this.y < 2400) {
            // 3. Đang trong Khu Rừng rậm -> Đi hơi chậm (80%)
            this.speed = this.baseSpeed * 0.8;
        } else {
            // 4. Ở Đồng Cỏ -> Tốc độ bình thường (100%)
            this.speed = this.baseSpeed;
        }
    }

    public void update(List<Entity> allEntities) {
        if (dead) return;

        // 1. Giảm sinh lực theo thời gian
        if (Math.random() < 0.05) { 
            hunger = Math.max(0, hunger - 1);
            if (distanceToPoint(850, 1800) >= 300) {
                // Chỉ giảm khát nếu không ở dưới hồ
                thirst = Math.max(0, thirst - 1);
            }
        }
        if (hunger <= 0 || thirst <= 0) dead = true;

        applyTerrainEffects(); // 2. Áp dụng hiệu ứng địa hình


        // 2. Chạy bộ não AI
        if (strategy != null) {
            strategy.execute(this, allEntities);
        } moveWander();
        }

    public void moveTowards(double tx, double ty, double s) {
        double dx = tx - x;
        double dy = ty - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            // Thay vì gán trực tiếp, hãy cộng thêm vào tọa độ
            x += (dx / dist) * s;
            y += (dy / dist) * s;
            lastAngle = Math.atan2(dy, dx);
        }
    }

    public void moveWander() {
        lastAngle += (Math.random() - 0.5) * 0.2;
        x += Math.cos(lastAngle) * baseSpeed;
        y += Math.sin(lastAngle) * baseSpeed;

        if (x <= 50) {
            x = 50;
            lastAngle = Math.PI - lastAngle;
        } else if (x >= 2950) {
            x = 2950;
            lastAngle = Math.PI - lastAngle;
        }

        if (y <= 50) {
            y = 50;
            lastAngle = -lastAngle;
        } else if (y >= 2950) {
            y = 2950;
            lastAngle = -lastAngle;
        }
    }

    public double distanceTo(Entity other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public double distanceToPoint(double px, double py) {
        return Math.sqrt(Math.pow(x - px, 2) + Math.pow(y - py, 2));
    }

    // Getters & Setters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setStrategy(SurvivalStrategy strategy) {
        this.strategy = strategy;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int h) {
        this.hunger = Math.min(h, 100);
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int t) {
        this.thirst = Math.min(t, 100);
    }

    public Color getColor() {
        return color;
    }

    public double getSpeed() {
        return speed;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean d) {
        this.dead = d;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}