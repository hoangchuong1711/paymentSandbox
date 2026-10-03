<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="murach.data.ProductDB, murach.business.Product, java.util.ArrayList" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CD List</title>
    <link rel="stylesheet" href="styles/main.css">
</head>
<body>
    <div class="container">
        <h1>CD list</h1>

        <table>
            <tr>
                <th>Description</th>
                <th>Price</th>
                <th>&nbsp;</th>
            </tr>

            <%
                ArrayList<Product> products = ProductDB.getProducts();
                request.setAttribute("products", products);
            %>

            <c:forEach var="product" items="${products}">
                <tr>
                    <td>${product.description}</td>
                    <td>${product.price} ₫</td>
                    <td>
                        <form action="cart" method="post">
                            <input type="hidden" name="productCode" value="${product.code}">
                            <input type="submit" value="Add To Cart" class="btn-add">
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</body>
</html>
