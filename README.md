# Chạy dự án bằng IntelliJ IDEA + Smart Tomcat

Dự án là Maven WAR (`pom.xml`), dùng Java 17 và Jakarta Servlet 6, phù hợp với Apache Tomcat 10.1.

1. Trong IntelliJ IDEA, mở **thư mục này** (hoặc `pom.xml`) dưới dạng Maven project. Đợi Maven import xong và chọn JDK 17 trở lên.
2. Cài plugin **Smart Tomcat** nếu IntelliJ chưa có.
3. Mở **Run > Edit Configurations**, chọn **Ch07Cart (Smart Tomcat)**. Cấu hình đã chọn module `ch07cart`, Deployment Directory `src/main/webapp`, Context Path `/ch07cart`, Server Port `8081` và Admin Port `8006`. Kiểm tra Tomcat Server trỏ tới thư mục cài **Tomcat 10.1** trên máy; cấu hình hiện dùng `C:\Program Files\Apache Software Foundation\Tomcat 10.1`. Nếu máy cài ở chỗ khác, chọn lại trong IntelliJ.
4. Nhấn **Run**. Mở <http://localhost:8081/ch07cart/>.

Có thể kiểm tra build bằng `mvn package`; WAR được tạo tại `target/ch07cart.war`. Smart Tomcat chạy trực tiếp từ `src/main/webapp`, không cần chọn thư mục `target` làm Deployment Directory.

Khi thanh toán qua VNPAY sandbox, URL trả về được tạo từ địa chỉ ứng dụng đang chạy. Nếu dùng cổng hoặc context path khác, URL trả về sẽ theo địa chỉ đó.

## Deploy lên Render và kết nối VNPAY Sandbox

1. Push project lên GitHub. Trong Render, chọn **New > Blueprint**, kết nối repository và để Render đọc `render.yaml` cùng `Dockerfile`. App được deploy ở đường dẫn gốc `/`.
2. Sau deploy, copy public URL dạng `https://<ten-service>.onrender.com`.
3. Mở trang đăng ký VNPAY Sandbox. Nhập tên website và URL dạng raw `https://<ten-service>.onrender.com` vào **Địa chỉ URL**. Không nhập localhost, URL sandbox của VNPAY, dấu ngoặc nhọn, dấu ngoặc vuông, dấu cách hoặc đường dẫn `/payment`. Dùng đúng domain HTTPS Render vừa cấp.
4. Lấy `vnp_TmnCode` và `vnp_HashSecret` VNPAY cấp, vào **Render > service > Environment** và thêm `VNP_TMN_CODE`, `VNP_HASH_SECRET`, `VNP_RETURN_URL`. Đặt `VNP_RETURN_URL` thành `https://<ten-service>.onrender.com/payment?action=return`, rồi lưu để Render deploy lại.
5. Mở URL website, thêm sản phẩm vào giỏ và thử thanh toán sandbox. Không dùng thông tin thẻ thật.

Ứng dụng lấy credentials từ environment, không lưu merchant secret trong source. Kết quả Return URL được xác minh chữ ký trước khi báo thành công. Luồng hiện tại chưa có IPN endpoint hay lưu order bền vững; chỉ phù hợp chạy demo sandbox, cần bổ sung các phần đó trước khi nhận thanh toán thật.
