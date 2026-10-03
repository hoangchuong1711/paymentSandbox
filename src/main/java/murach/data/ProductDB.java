package murach.data;

import murach.business.Product;
import java.util.ArrayList;

public class ProductDB {
    private static ArrayList<Product> products = new ArrayList<>();

    static {
        // Giá tính bằng VND
        products.add(new Product("86", "86 (the band) - True Life Songs and Pictures", 349000));
        products.add(new Product("PF1", "Paddlefoot - The first CD", 299000));
        products.add(new Product("PF2", "Paddlefoot - The second CD", 349000));
        products.add(new Product("JR1", "Joe Rut - Genuine Wood Grained Finish", 349000));
    }

    public static ArrayList<Product> getProducts() {
        return products;
    }

    public static Product getProduct(String code) {
        for (Product product : products) {
            if (product.getCode().equals(code)) {
                return product;
            }
        }
        return null;
    }
}