package com.ecosim.view;

import com.ecosim.model.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;

public class SimulationPanel extends JPanel {
    private final CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<>();
    private double zoomFactor = 0.5;
    private double offsetX = 100, offsetY = 100;
    private boolean isPlacingRock = false;
    private int placementMode = 0;
    private final Set<Integer> pressedKeys = new HashSet<>();

    public SimulationPanel() {
        initEntities();
        setFocusable(true);
        requestFocusInWindow();

        // Xử lý phím WASD
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        // Zoom tại vị trí chuột
        addMouseWheelListener(e -> {
            double oldZoom = zoomFactor;
            if (e.getWheelRotation() < 0)
                zoomFactor *= 1.1;
            else
                zoomFactor /= 1.1;
            zoomFactor = Math.max(0.05, Math.min(zoomFactor, 5.0));
            double scaleChange = zoomFactor / oldZoom;
            offsetX = e.getX() - (e.getX() - offsetX) * scaleChange;
            offsetY = e.getY() - (e.getY() - offsetY) * scaleChange;
            repaint();
        });

        // Click chuột để đặt vật thể
        addMouseListener(new MouseAdapter() {
            @Override
public void mousePressed(MouseEvent e) {
    double worldX = (e.getX() - offsetX) / zoomFactor;
    double worldY = (e.getY() - offsetY) / zoomFactor;

    if (SwingUtilities.isRightMouseButton(e)) {
        placementMode = (placementMode + 1) % 3; // Chuyển vòng giữa 3 chế độ
    } else {
        switch (placementMode) {
            case 0 -> entities.add(new Plant(worldX, worldY));
            case 1 -> entities.add(new Rock(worldX - 30, worldY - 30));
            case 2 -> entities.add(new Bush(worldX - 40, worldY - 40));
        }
    }
    repaint();
}
        });

        // VÒNG LẶP MÔ PHỎNG AN TOÀN
        new Timer(30, e -> {
            updateCamera();

            // Sử dụng danh sách tạm để tránh lỗi ConcurrentModification
            List<Entity> toRemove = new ArrayList<>();
            for (Entity en : entities) {
                try {
                    en.update(entities);
                    if (en.isDead())
                        toRemove.add(en);
                } catch (Exception ex) {
                    ex.printStackTrace(); // Hiện lỗi nếu logic strategy bị hỏng
                }
            }
            entities.removeAll(toRemove);

            if (Math.random() < 0.02)
                entities.add(new Plant(Math.random() * 3000, Math.random() * 3000));
            repaint();
        }).start();
    }

    private void updateCamera() {
        double moveStep = 15 / zoomFactor;
        if (pressedKeys.contains(KeyEvent.VK_W))
            offsetY += moveStep;
        if (pressedKeys.contains(KeyEvent.VK_S))
            offsetY -= moveStep;
        if (pressedKeys.contains(KeyEvent.VK_A))
            offsetX += moveStep;
        if (pressedKeys.contains(KeyEvent.VK_D))
            offsetX -= moveStep;
    }

    private void initEntities() {
        for (int i = 0; i < 60; i++)
            entities.add(new Plant(Math.random() * 3000, Math.random() * 3000));
        for (int i = 0; i < 25; i++)
            entities.add(new Rabbit(Math.random() * 2500, Math.random() * 2500));
        for (int i = 0; i < 5; i++)
            entities.add(new Wolf(Math.random() * 2500, Math.random() * 2500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform at = new AffineTransform();
        at.translate(offsetX, offsetY);
        at.scale(zoomFactor, zoomFactor);
        g2.setTransform(at);

        // VẼ MÔI TRƯỜNG ĐẸP - Enhanced landscape
        g2.setColor(new Color(120, 180, 70)); // Base grass
        g2.fillRect(-5000, -5000, 15000, 15000);
        
        // Grass patches for detail
        g2.setColor(new Color(140, 200, 80));
        for (int i = 0; i < 20; i++) {
            g2.fillOval(i * 750 - 5000, (i * 500) % 6000 - 5000, 400, 300);
        }

        // Hồ nước lung linh - Enhanced water
        g2.setColor(new Color(50, 150, 255, 220));
        g2.fillOval(400, 1500, 900, 600);
        
        // Water ripple effect
        g2.setColor(new Color(100, 200, 255, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(450, 1550, 750, 450);
        g2.drawOval(500, 1600, 650, 350);
        
        // Water shine
        g2.setColor(new Color(200, 255, 255, 150));
        g2.fillOval(500, 1550, 150, 100);

        // Rừng rậm tối - Enhanced forest
        g2.setColor(new Color(25, 80, 25, 180));
        g2.fillRoundRect(1800, 400, 1500, 2000, 100, 100);
        
        // Forest detail
        g2.setColor(new Color(40, 120, 40, 150));
        g2.fillOval(1900, 500, 300, 300);
        g2.fillOval(2500, 800, 250, 250);
        g2.fillOval(2200, 1200, 280, 280);

        for (Entity en : entities) {
            int ex = (int) en.getX();
            int ey = (int) en.getY();
            if (en instanceof Bush) {
                // BUSH - 3D effect with gradient
                g2.setColor(new Color(20, 100, 20)); // Dark inner shadow
                g2.fillOval(ex + 5, ey + 5, 70, 70);
                
                g2.setColor(en.getColor()); // Main bush color
                g2.fillOval(ex, ey, 80, 80);
                
                // Highlight for depth
                g2.setColor(new Color(100, 200, 100, 100));
                g2.fillOval(ex + 10, ey + 10, 30, 30);
                
                g2.setColor(new Color(50, 120, 50)); // Border
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(ex, ey, 80, 80);
            } else if (en instanceof Rock) {
                int size = 60;
                
                // Shadow for depth
                g2.setColor(new Color(80, 80, 80, 180));
                g2.fillRoundRect(ex + 3, ey + 3, size, size, 10, 10);
                
                // Main rock body with gradient
                g2.setColor(new Color(120, 120, 130));
                g2.fillRoundRect(ex, ey, size, size, 10, 10);
                
                // Light reflection
                g2.setColor(new Color(180, 180, 190, 120));
                g2.fillRoundRect(ex + 8, ey + 8, 15, 15, 5, 5);
                
                // Border
                g2.setColor(new Color(80, 80, 90));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(ex, ey, size, size, 10, 10);
            } else if (en instanceof Plant) {
                // GRASS/PLANT - Better appearance
                g2.setColor(new Color(100, 200, 100)); // Darker green base
                g2.fillOval(ex - 2, ey - 2, 16, 16);
                
                g2.setColor(new Color(150, 255, 100)); // Light green top
                g2.fillOval(ex, ey, 12, 12);
                
                // Highlight
                g2.setColor(new Color(200, 255, 150, 200));
                g2.fillOval(ex + 2, ey + 1, 4, 4);
            } else {
                // ANIMALS - Better rendering
                // Shadow
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillOval(ex + 2, ey + 18, 20, 8);
                
                // Main body with shadow
                Color bodyColor = en.getColor();
                g2.setColor(new Color(bodyColor.getRed() / 2, bodyColor.getGreen() / 2, bodyColor.getBlue() / 2));
                g2.fillRoundRect(ex + 1, ey + 1, 22, 22, 8, 8);
                
                // Main body color
                g2.setColor(bodyColor);
                g2.fillRoundRect(ex, ey, 22, 22, 8, 8);
                
                // Eye/head detail
                if (en instanceof Wolf) {
                    g2.setColor(new Color(255, 100, 100)); // Red eyes for wolf
                    g2.fillOval(ex + 4, ey + 5, 2, 2);
                    g2.fillOval(ex + 11, ey + 5, 2, 2);
                } else if (en instanceof Rabbit) {
                    g2.setColor(new Color(255, 150, 200)); // Pink nose for rabbit
                    g2.fillOval(ex + 7, ey + 9, 2, 2);
                    // Ears
                    g2.setColor(new Color(255, 200, 220));
                    g2.fillOval(ex + 5, ey - 3, 3, 5);
                    g2.fillOval(ex + 14, ey - 3, 3, 5);
                }
                
                // Border/outline
                g2.setColor(new Color(0, 0, 0, 100));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(ex, ey, 22, 22, 8, 8);

                // THANH MÁU & KHÁT - Luôn vẽ nếu chưa chết
                drawBars(g2, en);
            }
        }

        g2.setTransform(new AffineTransform());
        drawUI(g2);
    }

    private void drawBars(Graphics2D g2, Entity en) {
        int x = (int) en.getX();
        int y = (int) en.getY();
        
        int barWidth = 24;
        int barHeight = 10;

        // Background frame - Dark border
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(x - 1, y - 16, barWidth + 2, barHeight + 2, 2, 2);
        
        // Background - Dark interior
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRoundRect(x, y - 15, barWidth, barHeight, 2, 2);

        // Thanh đói (Hunger) - Gradient from Red to Green
        int hW = (int) (en.getHunger() * 0.22);
        if (hW > 0) {
            Color hungerColor = en.getHunger() < 30 ? 
                new Color(255, 50, 50) : 
                new Color(100, 220, 100);
            g2.setColor(hungerColor);
            g2.fillRoundRect(x + 1, y - 13, Math.min(hW, 22), 3, 1, 1);
        }

        // Thanh khát (Thirst) - Blue
        int tW = (int) (en.getThirst() * 0.22);
        if (tW > 0) {
            g2.setColor(new Color(100, 200, 255));
            g2.fillRoundRect(x + 1, y - 8, Math.min(tW, 22), 3, 1, 1);
        }
    }

    private void drawUI(Graphics2D g2) {
        // MAIN PANEL BACKGROUND - Gradient effect
        GradientPaint gradient = new GradientPaint(20, 20, new Color(20, 20, 30, 220), 320, 200, new Color(40, 40, 60, 220));
        g2.setPaint(gradient);
        g2.fillRoundRect(20, 20, 300, 250, 20, 20);
        
        // Border
        g2.setColor(new Color(100, 150, 255, 150));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(20, 20, 300, 250, 20, 20);
        
        // Title
        g2.setColor(new Color(100, 200, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("🌍 Ecosystem Simulator", 40, 50);
        
        // Separator line
        g2.setColor(new Color(100, 150, 255, 100));
        g2.drawLine(40, 60, 300, 60);
        
        // CONTROLS SECTION
        g2.setColor(new Color(150, 200, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString("CONTROLS:", 40, 85);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString("• WASD: Move Camera", 40, 105);
        g2.drawString("• Scroll: Zoom In/Out", 40, 122);
        
        // PLACEMENT MODE SECTION
        g2.setColor(new Color(150, 200, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString("PLACEMENT MODE:", 40, 150);
        
        String modeText = switch(placementMode) {
            case 0 -> "🌱 Place Grass";
            case 1 -> "🪨 Place Rock";
            case 2 -> "🌿 Place Bush";
            default -> "";
        };
        
        // Mode indicator with color
        int modeColor = switch(placementMode) {
            case 0 -> 0xFF90EE90; // Light green
            case 1 -> 0xFF808080; // Gray
            case 2 -> 0xFF228B22; // Forest green
            default -> 0xFFFFFFFF;
        };
        g2.setColor(new Color(modeColor, true));
        g2.fillRoundRect(40, 158, 260, 20, 5, 5);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(modeText, 50, 173);
        g2.drawString("(Right Click to Change)", 180, 173);
        
        // ACTIONS
        g2.setColor(new Color(150, 200, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString("ACTIONS:", 40, 205);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString("• Left Click: Place Selected Entity", 40, 225);
        g2.drawString("• Right Click: Change Mode", 40, 242);
    }
}