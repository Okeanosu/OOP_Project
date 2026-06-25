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
    private int placementMode = 0;
    private final Set<Integer> pressedKeys = new HashSet<>();

    private boolean useSpriteMode = false; // Chế độ vẽ sprite
    private Image wolfSprite, rabbitSprite, tigerSprite, elephantSprite, DeerSprite;

    public SimulationPanel() {
        // Tải ảnh động từ thư mục assets
        try {
            wolfSprite = new ImageIcon(getClass().getResource("/assets/wolf.gif")).getImage();
            rabbitSprite = new ImageIcon(getClass().getResource("/assets/rabbit.gif")).getImage();
            tigerSprite = new ImageIcon(getClass().getResource("/assets/tiger.gif")).getImage();
            elephantSprite = new ImageIcon(getClass().getResource("/assets/elephant.gif")).getImage();
            DeerSprite = new ImageIcon(getClass().getResource("/assets/deer.gif")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading sprites: " + e.getMessage());
        }

        initEntities();
        setFocusable(true);
        requestFocusInWindow();

        // Xử lý phím WASD, Zoom và chuyển đổi giao diện
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());

                if (e.getKeyCode() == KeyEvent.VK_M) {
                    zoomFactor = Math.min(getWidth(), getHeight()) / 3200.0;
                    offsetX = getWidth() / 2.0 - 1500 * zoomFactor; 
                    offsetY = getHeight() / 2.0 - 1500 * zoomFactor;
                    repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_H) {
                    zoomFactor = 1.2;
                    offsetX = getWidth() / 2.0 - 850 * zoomFactor;
                    offsetY = getHeight() / 2.0 - 1800 * zoomFactor;
                    repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_V) {
                    useSpriteMode = !useSpriteMode;
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        // Zoom tại vị trí chuột
        addMouseWheelListener(e -> {
            double oldZoom = zoomFactor;
            if (e.getWheelRotation() < 0) zoomFactor *= 1.1;
            else zoomFactor /= 1.1;
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
                    placementMode = (placementMode + 1) % 3;
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

        // VÒNG LẶP MÔ PHỎNG (30ms/frame)
        new Timer(30, e -> {
            updateCamera();

            List<Entity> toRemove = new ArrayList<>();
            for (Entity en : entities) {
                try {
                    en.update(entities);
                    if (en.isDead()) toRemove.add(en);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            entities.removeAll(toRemove);

            // Cỏ thỉnh thoảng tự mọc thêm
            if (Math.random() < 0.02)
                entities.add(new Plant(Math.random() * 3000, Math.random() * 3000));
            repaint();
        }).start();
    }

    private void updateCamera() {
        double moveStep = 15 / zoomFactor;
        if (pressedKeys.contains(KeyEvent.VK_W)) offsetY += moveStep;
        if (pressedKeys.contains(KeyEvent.VK_S)) offsetY -= moveStep;
        if (pressedKeys.contains(KeyEvent.VK_A)) offsetX += moveStep;
        if (pressedKeys.contains(KeyEvent.VK_D)) offsetX -= moveStep;
    }

    // CÂN BẰNG LẠI HỆ SINH THÁI
    private void initEntities() {
        // Môi trường tự nhiên (Đá, Bụi rậm)
        for (int i = 0; i < 20; i++) entities.add(new Rock(Math.random() * 2800, Math.random() * 2800));
        for (int i = 0; i < 20; i++) entities.add(new Bush(Math.random() * 2800, Math.random() * 2800));
        
        // Thức ăn (Cỏ)
        for (int i = 0; i < 80; i++) entities.add(new Plant(Math.random() * 3000, Math.random() * 3000));
        
        // Động vật ăn cỏ (Nhỏ và Lớn)
        for (int i = 0; i < 15; i++) entities.add(new Rabbit(Math.random() * 2500, Math.random() * 2500));
        for (int i = 0; i < 10; i++) entities.add(new Deer(Math.random() * 2500, Math.random() * 2500));
        
        // Động vật săn mồi
        for (int i = 0; i < 3; i++) entities.add(new Wolf(Math.random() * 2500, Math.random() * 2500));
        for (int i = 0; i < 2; i++) entities.add(new Tiger(Math.random() * 2500, Math.random() * 2500));
        
        // Động vật đầu bảng
        for (int i = 0; i < 2; i++) entities.add(new Elephant(Math.random() * 2500, Math.random() * 2500));
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

        // VẼ MÔI TRƯỜNG

        // 1. Nền Đồng Cỏ
        g2.setColor(new Color(130, 200, 80)); 
        g2.fillRect(0, 0, 3000, 3000);
        
        // Vẽ viền bản đồ
        g2.setColor(new Color(75, 125, 45));
        g2.setStroke(new BasicStroke(15));
        g2.drawRect(0, 0, 3000, 3000);
        
        // Vẽ các khóm cỏ nhỏ rải rác
        g2.setColor(new Color(110, 180, 60));
        g2.setStroke(new BasicStroke(2));
        for (int x = 0; x < 3000; x += 150) {
            for (int y = 0; y < 3000; y += 150) {
                int px = x + (y * 31) % 120; 
                int py = y + (x * 17) % 120;
                g2.drawLine(px, py, px - 4, py - 8);   
                g2.drawLine(px, py, px + 4, py - 7);   
                g2.drawLine(px, py, px, py - 10);      
            }
        }

        // 2. Bãi Bùn Lầy
        RadialGradientPaint mudGradient = new RadialGradientPaint(
            new Point(850, 1800), 550f, 
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{new Color(139, 115, 85, 220), new Color(139, 115, 85, 120), new Color(139, 115, 85, 0)}
        );
        g2.setPaint(mudGradient);
        g2.fillOval(300, 1250, 1100, 1100);

        // 3. Hồ Nước
        RadialGradientPaint lakeGradient = new RadialGradientPaint(
            new Point(850, 1800), 350f,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(20, 90, 180, 255), new Color(60, 180, 255, 200), new Color(60, 180, 255, 0)}
        );
        g2.setPaint(lakeGradient);
        g2.fillOval(500, 1450, 700, 700);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawArc(650, 1600, 300, 150, 20, 140);
        g2.drawArc(600, 1800, 400, 100, 10, 160);

        // 4. Khu Rừng Rậm
        g2.setColor(new Color(20, 60, 20, 100));
        g2.fillRoundRect(1800, 400, 1400, 2000, 200, 200);
        
        for(int i = 0; i < 150; i++) {
            int tx = 1850 + (i * 137) % 1250;
            int ty = 450 + (i * 93) % 1850;
            
            // Đổ bóng cây xuống mặt đất
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillOval(tx - 10, ty + 25, 40, 15);
            
            // Thân cây gỗ nâu
            g2.setColor(new Color(101, 67, 33));
            g2.fillRect(tx + 5, ty + 10, 10, 20);
            
            // Tán cây
            g2.setColor(new Color(34, 139, 34));
            g2.fillOval(tx - 15, ty - 15, 50, 40);
            g2.setColor(new Color(50, 205, 50));
            g2.fillOval(tx - 5, ty - 20, 30, 25);
        }

        // VẼ THỰC THỂ
        for (Entity en : entities) {
            int ex = (int) en.getX();
            int ey = (int) en.getY();
            int size = (int) en.getSize();

            if (en instanceof Bush) {
                g2.setColor(new Color(15, 80, 15)); 
                g2.fillOval(ex + 5, ey + 5, size*3-10, size*3-10); 
                g2.setColor(en.getColor());
                g2.fillOval(ex, ey, size*3, size*3);
                g2.setColor(new Color(100, 200, 100, 80)); 
                g2.fillOval(ex + 10, ey + 10, size, size);
            } else if (en instanceof Rock) {
                g2.setColor(new Color(60, 60, 60, 180));
                g2.fillRoundRect(ex + 4, ey + 4, size*2, size*2, 15, 15);
                g2.setColor(new Color(110, 110, 120));
                g2.fillRoundRect(ex, ey, size*2, size*2, 15, 15);
                g2.setColor(new Color(160, 160, 170, 100));
                g2.fillRoundRect(ex + 8, ey + 8, size, size, 5, 5);
            } else if (en instanceof Plant) {
                g2.setColor(new Color(80, 180, 80));
                g2.fillOval(ex - 2, ey - 2, 18, 18);
                g2.setColor(new Color(140, 240, 90));
                g2.fillOval(ex, ey, 14, 14);
            } else {
                if (useSpriteMode) {
                    Image imgToDraw = null;
                    int imgSize = size * 2; 

                    if (en instanceof Wolf) imgToDraw = wolfSprite;
                    else if (en instanceof Rabbit) imgToDraw = rabbitSprite;
                    else if (en instanceof Deer) imgToDraw = DeerSprite;
                    else if (en instanceof Tiger) imgToDraw = tigerSprite;
                    else if (en instanceof Elephant) imgToDraw = elephantSprite;

                    if (imgToDraw != null) {
                        g2.drawImage(imgToDraw, ex - imgSize/2, ey - imgSize/2, imgSize, imgSize, this);
                    } else {
                        g2.setColor(en.getColor());
                        g2.fillOval(ex - size/2, ey - size/2, size, size);
                    }
                } else {
                    // CHẾ ĐỘ BASIC (Vẽ hình học)
                    g2.setColor(new Color(0, 0, 0, 70));
                    g2.fillOval(ex + 2, ey + size - 5, size, size/3);
                    
                    Color bodyColor = en.getColor();
                    g2.setColor(new Color(bodyColor.getRed() / 2, bodyColor.getGreen() / 2, bodyColor.getBlue() / 2));
                    g2.fillRoundRect(ex + 1, ey + 1, size, size, size/3, size/3);
                    
                    g2.setColor(bodyColor);
                    g2.fillRoundRect(ex, ey, size, size, size/3, size/3);
                    
                    if (en instanceof Wolf || en instanceof Tiger) {
                        g2.setColor(Color.WHITE); 
                        g2.fillOval(ex + size/5, ey + size/4, 4, 4);
                        g2.fillOval(ex + size/2, ey + size/4, 4, 4);
                    } else if (en instanceof Rabbit || en instanceof Deer) {
                        g2.setColor(Color.BLACK); 
                        g2.fillOval(ex + size/4, ey + size/3, 3, 3);
                    }
                    
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(ex, ey, size, size, size/3, size/3);
                }

                drawBars(g2, en);
            }
        }

        // 4. kết thúc cắt clip
        g2.setTransform(new AffineTransform());

        RadialGradientPaint vignette = new RadialGradientPaint(
            new Point(getWidth()/2, getHeight()/2), getWidth()/2,
            new float[]{0.7f, 1.0f},
            new Color[]{new Color(0,0,0,0), new Color(0,0,0,100)}
        );
        g2.setPaint(vignette);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // 5. vẽ UI
        drawUI(g2);
    }

    private void drawBars(Graphics2D g2, Entity en) {
        int x = (int) en.getX();
        int y = (int) en.getY();
        int barW = 24, barH = 8;
        
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x - 2, y - 18, barW + 4, barH*2 + 4, 2, 2);

        int hW = (int) (en.getHunger() * (barW/100.0));
        if (hW > 0) {
            g2.setColor(en.getHunger() < 30 ? new Color(255, 50, 50) : new Color(100, 220, 100));
            g2.fillRoundRect(x, y - 16, Math.min(hW, barW), barH-2, 1, 1);
        }
        int tW = (int) (en.getThirst() * (barW/100.0));
        if (tW > 0) {
            g2.setColor(new Color(80, 180, 255));
            g2.fillRoundRect(x, y - 9, Math.min(tW, barW), barH-2, 1, 1);
        }
    }

    private void drawUI(Graphics2D g2) {
        GradientPaint gradient = new GradientPaint(20, 20, new Color(20, 20, 35, 230), 320, 200, new Color(45, 45, 65, 230));
        g2.setPaint(gradient);
        g2.fillRoundRect(30, 30, 310, 290, 20, 20); 
        
        g2.setColor(new Color(100, 180, 255, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(20, 20, 310, 290, 20, 20);
        
        g2.setColor(new Color(120, 210, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 17));
        g2.drawString("Wildlife Ecosystem", 40, 50);
        
        g2.setColor(new Color(100, 150, 255, 100));
        g2.drawLine(40, 60, 310, 60);
        
        g2.setColor(new Color(170, 220, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("CONTROLS:", 40, 85);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("• WASD: Move Camera", 40, 105);
        g2.drawString("• Scroll: Zoom In/Out", 40, 125);
        g2.drawString("• H: View Lake  |  M: View Map", 40, 145);
        g2.setColor(new Color(255, 215, 0)); 
        g2.drawString("• V: Toggle Sprite Mode", 40, 165);
        
        g2.setColor(new Color(170, 220, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("PLACEMENT MODE:", 40, 195);
        
        String modeText = switch(placementMode) {
            case 0 -> "Grass"; case 1 -> "Rock"; case 2 -> "Bush"; default -> "";
        };
        int modeColor = switch(placementMode) {
            case 0 -> 0xFF90EE90; case 1 -> 0xFF808080; case 2 -> 0xFF228B22; default -> 0xFFFFFFFF;
        };
        
        g2.setColor(new Color(modeColor, true));
        g2.fillRoundRect(40, 205, 270, 25, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString("Place: " + modeText, 50, 223);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString("(Right Click to Change)", 165, 222);
        
        g2.setColor(new Color(170, 220, 255));
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("ACTIONS:", 40, 255);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("• Left Click: Place Selected Entity", 40, 275);
    }
}