package com.ecosim;

import com.ecosim.view.SimulationPanel;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Chạy ứng dụng trên Event Dispatch Thread (EDT) để đảm bảo an toàn luồng cho Swing
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        // 1. Khởi tạo khung hình (Window)
        JFrame frame = new JFrame("Wild-Life Eco Simulation");
        
        // 2. Thiết lập hành động khi đóng cửa sổ
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 3. Thêm Panel mô phỏng vào Frame
        SimulationPanel ecoSystem = new SimulationPanel();
        frame.add(ecoSystem);
        
        // 4. Thiết lập kích thước và vị trí
        frame.setSize(1000, 700);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        frame.setVisible(true);

        System.out.println("===============================================");
        System.out.println("Running Wild-Life Eco Simulation v1.0...");
        System.out.println("Status: Initialized and GUI is visible.");
        System.out.println("===============================================");
    }
}
