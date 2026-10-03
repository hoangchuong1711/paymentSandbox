<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Your Cart</title>
    <link rel="stylesheet" href="styles/main.css">
</head>
<body>
    <div class="container">
        <h1>Your cart</h1>

        <c:choose>
            <c:when test="${empty cart.items}">
                <div class="empty-cart">Your cart is empty.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>Quantity</th>
                        <th>Description</th>
                        <th>Price</th>
                        <th>Amount</th>
                        <th>&nbsp;</th>
                    </tr>

                    <c:forEach var="item" items="${cart.items}">
                        <tr>
                            <td>
                                <form action="cart" method="post" style="display: flex; align-items: center; gap: 5px;">
                                    <input type="hidden" name="productCode" value="${item.product.code}">
                                    <input type="text" name="quantity" value="${item.quantity}" class="quantity-input">
                                    <input type="submit" value="Update" class="btn-update">
                                </form>
                            </td>
                            <td>${item.product.description}</td>
                            <td>${item.product.price} ₫</td>
                            <td>${item.totalCurrencyFormat}</td>
                            <td>
                                <form action="cart" method="post">
                                    <input type="hidden" name="productCode" value="${item.product.code}">
                                    <input type="hidden" name="quantity" value="0">
                                    <input type="submit" value="Remove Item" class="btn-remove">
                                </form>
                            </td>
                        </tr>
                    </c:forEach>

                    <tr class="total-row">
                        <td colspan="4" style="text-align: right;">Total:</td>
                        <td>${cart.totalCurrencyFormat}</td>
                    </tr>
                </table>

                <p class="instruction">
                    To change the quantity, enter the new quantity and click on the Update button.
                </p>
            </c:otherwise>
        </c:choose>

        <div class="btn-group">
            <a href="index.jsp" class="continue-btn">Continue Shopping</a>
            <c:if test="${not empty cart.items}">
                <form action="payment" method="post" style="display: inline;">
                    <input type="hidden" name="action" value="create_payment">
                    <input type="submit" value="Thanh toán VNPAY" class="checkout-btn">
                </form>
            </c:if>
        </div>
    </div>
</body>
</html>
