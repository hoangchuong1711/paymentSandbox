package murach.payment;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import murach.business.Cart;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {

    // ===== THAY THÔNG TIN VNPAY CỦA BẠN =====
    private static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("create_payment".equals(action)) {
            String tmnCode = System.getenv("VNP_TMN_CODE");
            String hashSecret = System.getenv("VNP_HASH_SECRET");
            if (tmnCode == null || tmnCode.isBlank() || hashSecret == null || hashSecret.isBlank()) {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "VNPAY sandbox credentials are not configured.");
                return;
            }

            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");

            if (cart == null || cart.getItems().isEmpty()) {
                response.sendRedirect("cart.jsp");
                return;
            }

            // Số tiền nhân 100 (VNPAY yêu cầu)
            long amount = (long) (cart.getTotal() * 100);
            String txnRef = String.valueOf(System.currentTimeMillis());

            // Sắp xếp tham số theo alphabet (TreeMap tự sắp xếp)
            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", tmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", txnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + txnRef);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            // Use this deployment's address so the sandbox returns to the local app.
            String returnUrl = System.getenv("VNP_RETURN_URL");
            if (returnUrl == null || returnUrl.isBlank()) {
                returnUrl = request.getRequestURL().toString() + "?action=return";
            }
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", getClientIp(request));
            vnp_Params.put("vnp_CreateDate", new java.text.SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new java.util.Date()));

            // Tạo query string
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    if (hashData.length() > 0) {
                        hashData.append('&');
                        query.append('&');
                    }
                    hashData.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
                         .append('=')
                         .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                }
            }

            // Tạo checksum
            String vnp_SecureHash = hmacSHA512(hashSecret, hashData.toString());
            String paymentUrl = VNP_PAY_URL + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

            response.sendRedirect(paymentUrl);

        } else if ("return".equals(action)) {
            // Xử lý kết quả trả về
            String responseCode = request.getParameter("vnp_ResponseCode");
            String txnRef = request.getParameter("vnp_TxnRef");
            String transactionStatus = request.getParameter("vnp_TransactionStatus");
            String hashSecret = System.getenv("VNP_HASH_SECRET");
            String tmnCode = System.getenv("VNP_TMN_CODE");
            boolean validSignature = hashSecret != null && !hashSecret.isBlank()
                    && verifyReturnSignature(request, hashSecret)
                    && tmnCode != null && tmnCode.equals(request.getParameter("vnp_TmnCode"));

            if (validSignature && "00".equals(responseCode) && "00".equals(transactionStatus)) {
                HttpSession session = request.getSession();
                Cart cart = (Cart) session.getAttribute("cart");
                if (cart != null) {
                    cart.clear();
                    session.setAttribute("cart", cart);
                }
                request.setAttribute("message", "Thanh toán thành công! Mã đơn: " + txnRef);
            } else {
                request.setAttribute("message", "Thanh toán thất bại. Mã lỗi: " + responseCode);
            }
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);
        }
    }

    // Lấy IP client
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // HMAC SHA512
    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo checksum", e);
        }
    }

    private boolean verifyReturnSignature(HttpServletRequest request, String hashSecret) {
        String receivedHash = request.getParameter("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }

        Map<String, String> fields = new TreeMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (key.startsWith("vnp_")
                    && !"vnp_SecureHash".equals(key)
                    && !"vnp_SecureHashType".equals(key)
                    && values != null && values.length > 0) {
                fields.put(key, values[0]);
            }
        });

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> field : fields.entrySet()) {
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            hashData.append(URLEncoder.encode(field.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(field.getValue(), StandardCharsets.UTF_8));
        }

        String expectedHash = hmacSHA512(hashSecret, hashData.toString());
        return MessageDigest.isEqual(
                expectedHash.getBytes(StandardCharsets.US_ASCII),
                receivedHash.getBytes(StandardCharsets.US_ASCII));
    }
}
