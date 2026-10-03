package murach.business;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String description;
    private double price;

    public Product() {
        code = "";
        description = "";
        price = 0;
    }

    public Product(String code, String description, double price) {
        this.code = code;
        this.description = description;
        this.price = price;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}