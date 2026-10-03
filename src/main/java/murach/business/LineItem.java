package murach.business;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class LineItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;

    public LineItem() {
        product = new Product();
        quantity = 0;
    }

    public LineItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotal() {
        return product.getPrice() * quantity;
    }

    public String getTotalCurrencyFormat() {
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currency.format(getTotal());
    }
}