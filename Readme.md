Wild-Life Eco Simulation
1. Thiết kế
MVC Logic sinh tồn (BioLogic) và Giao diện hiển thị (ViewLogic):
- BioLogic: Xử lý hành vi, sức khỏe, và di chuyển của thực thể.
- ViewLogic: Render hình ảnh, camera, và tương tác người dùng.
2. Các gói (Package) & Các lớp
- com.ecosim.model: Chứa các thực thể (Entity, Rabbit, Wolf, Tiger, Plant, Rock, Bush).
- com.ecosim.strategy: Chứa các chiến lược sinh tồn (SurvivalStrategy, HunterStrategy, PassiveStrategy, AggressiveStrategy).
- com.ecosim.view: Chứa giao diện chính (SimulationPanel.java).
- com.ecosim.Main: Điểm khởi chạy ứng dụng.
3. Kỹ thuật lập trình Hướng đối tượng (OOP) áp dụng
- Kế thừa (Inheritance): Các class thực thể kế thừa từ class cha Entity (Tái sử dụng code, thống nhất các thực thể)
- Đa hình (Polymorphism): Hàm update() của mỗi thực thể (Cho phép mỗi loài có hành vi riêng biệt nhưng cùng chung interface)
- Strategy Pattern: Class SurvivalStrategy giúp tách não AI khỏi thực thể để dễ thay đổi hành vi thực thể
- Abstract: Class Entity
4. Công nghệ & Thuật toán nổi bật
- Công nghệ: Java Swing(Graphics2D)
- Thuật toán:
+) AI Decision Making: tự động chuyển Strategy theo ngưỡng Hunger (Đói)
+) Camera Viewport: Xử lý Zoom và pan
5. Hướng dẫn sử dụng:
+) Di chuyển Camera: Phím W,A,S,D
+) Zoom: Lăn chuột giữa
+) Chuyển chế độ hiển thị: Phím V để chuyển giữa Sprite và Mô hình
+) Phím tắt: Phím H (Zoom hồ nước), Phím M (xem toàn bộ bản đồ)
+) Tương tác: Chuột phải để chuyển thực thể đặt (Cỏ,Đá,Bụi), Chuột trái để đặt thực thể 
