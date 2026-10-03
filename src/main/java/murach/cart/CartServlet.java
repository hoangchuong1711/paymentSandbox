package murach.cart;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import murach.business.Cart;
import murach.business.LineItem;
import murach.business.Product;
import murach.data.ProductDB;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        String productCode = request.getParameter("productCode");
        String quantityString = request.getParameter("quantity");
        String action = request.getParameter("action");

        if (productCode != null && !productCode.isEmpty()) {
            Product product = ProductDB.getProduct(productCode);
            if (product != null) {
                LineItem existingItem = null;
                for (LineItem item : cart.getItems()) {
                    if (item.getProduct().getCode().equals(productCode)) {
                        existingItem = item;
                        break;
                    }
                }

                if ("increase".equals(action)) {
                    if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + 1);
                    }
                } else if ("decrease".equals(action)) {
                    if (existingItem != null) {
                        if (existingItem.getQuantity() > 1) {
                            existingItem.setQuantity(existingItem.getQuantity() - 1);
                        } else {
                            cart.removeItem(existingItem);
                        }
                    }
                } else if (quantityString == null || quantityString.isEmpty()) {
                    cart.addItem(new LineItem(product, 1));
                } else {
                    int quantity;
                    try {
                        quantity = Integer.parseInt(quantityString);
                    } catch (NumberFormatException e) {
                        quantity = 0;
                    }
                    if (quantity > 0) {
                        if (existingItem != null) {
                            existingItem.setQuantity(quantity);
                        } else {
                            cart.addItem(new LineItem(product, quantity));
                        }
                    } else if (existingItem != null) {
                        cart.removeItem(existingItem);
                    }
                }
                session.setAttribute("cart", cart);
            }
        }

        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("cart.jsp")) {
            response.sendRedirect("cart.jsp");
        } else {
            response.sendRedirect("cart.jsp");
        }
    }
}
