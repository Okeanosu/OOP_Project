package com.ecosim.model;

import com.ecosim.strategy.SurvivalStrategy;
import java.awt.Color;
import java.util.List;

public abstract class Entity {
    protected double x, y;
    protected double speed, baseSpeed;
    protected int hunger = 100;
    protected int thirst = 100; // Thêm thanh khát
    protected boolean dead = false;
    protected Color color;
    protected SurvivalStrategy strategy;
    protected double lastAngle = Math.random() * Math.PI * 2;

    public Entity(double x, double y, Color color, double speed) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.baseSpeed = speed;
        this.speed = speed;
    }

    // Hàm update chính được gọi mỗi frame
    public void update(List<Entity> allEntities) {
        if (dead)
            return;

        // 1. Giảm sinh lực theo thời gian
        if (Math.random() < 0.05) { // 5% mỗi frame
            hunger = Math.max(0, hunger - 1);
            thirst = Math.max(0, thirst - 1);
        }

        if (hunger <= 0 || thirst <= 0)
            dead = true;

        // 2. Chạy bộ não AI
        if (strategy != null) {
            strategy.execute(this, allEntities);
        } else {
            moveWander(); // Nếu không có chiến lược, di chuyển ngẫu nhiên
        }
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

        // Chặn biên 3000x3000px
        x = Math.max(0, Math.min(3000, x));
        y = Math.max(0, Math.min(3000, y));
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
}