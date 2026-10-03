package murach.business;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<LineItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public ArrayList<LineItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<LineItem> items) {
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public void addItem(LineItem item) {
        String code = item.getProduct().getCode();
        int quantity = item.getQuantity();

        for (LineItem lineItem : items) {
            if (lineItem.getProduct().getCode().equals(code)) {
                lineItem.setQuantity(lineItem.getQuantity() + quantity);
                return;
            }
        }
        items.add(item);
    }

    public void removeItem(LineItem item) {
        items.remove(item);
    }

    public double getTotal() {
        double total = 0;
        for (LineItem item : items) {
            total += item.getTotal();
        }
        return total;
    }

    public String getTotalCurrencyFormat() {
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currency.format(getTotal());
    }

    public void clear() {
        items.clear();
    }
}