package com.dmytroporoshyn;

import java.util.*;
import java.text.*;

/**
 * Containing items and calculating price.
 */
public class ShoppingCart {

    private static final NumberFormat MONEY;

    /**
     * Container for added items
     */
    private final List<Item> items = new ArrayList<>();

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        MONEY = new DecimalFormat("$#.00", symbols);
    }

    /**
     * Tests all class methods.
     */
    public static void main(String[] args) {
        // TODO: add tests here
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, ItemType.NEW);
        cart.addItem("Banana", 20.00, 4, ItemType.SECOND_FREE);
        cart.addItem("A long piece of toilet paper", 17.20, 1, ItemType.SALE);
        cart.addItem("Nails", 2.00, 500, ItemType.REGULAR);
        System.out.println(cart.formatTicket());
    }

    /**
     * Adds new item.
     *
     * @param title    item title 1 to 32 symbols
     * @param price    item ptice in USD, > 0
     * @param quantity item quantity, from 1
     * @param type     item type
     * @throws IllegalArgumentException if some value is wrong
     */
    public void addItem(String title, double price, int quantity, ItemType type) {
        if (title == null || title.length() == 0 || title.length() > 32)
            throw new IllegalArgumentException("Illegal title");
        if (price < 0.01)
            throw new IllegalArgumentException("Illegal price");
        if (quantity <= 0)
            throw new IllegalArgumentException("Illegal quantity");
        Item item = new Item(title, price, quantity, type);
        items.add(item);
    }

    /**
     * Formats shopping price.
     *
     * @return string as lines, separated with \n,
     * first line: # Item Price Quan. Discount Total
     * second line: ---------------------------------------------------------
     * next lines: NN Title $PP.PP Q DD% $TT.TT
     * 1 Some title $.30 2 - $.60
     * 2 Some very long $100.00 1 50% $50.00
     * ...
     * 31 Item 42 $999.00 1000 - $999000.00
     * end line: ---------------------------------------------------------
     * last line: 31 $999050.60
     * <p>
     * if no items in cart returns "No items." string.
     */
    public String formatTicket() {
        if (items.size() == 0) {
            return "No items.";
        }
        List<String[]> lines = new ArrayList<>();
        String[] header = {"#", "Item", "Price", "Quan.", "Discount", "Total"};
        int[] align = new int[]{1, -1, 1, 1, 1, 1};
        int[] width = new int[]{0, 0, 0, 0, 0, 0};
        // formatting each line
        double total = 0.00;
        int index = 0;
        for (Item item : items) {
            int discount = calculateDiscount(item.getType(), item.getQuantity());
            double itemTotal = item.getPrice() * item.getQuantity() * (100.00 - discount) / 100.00;
            String[] line = new String[]{
                    String.valueOf(++index),
                    item.getTitle(),
                    MONEY.format(item.getPrice()),
                    String.valueOf(item.getQuantity()),
                    (discount == 0) ? "-" : (discount + "%"),
                    MONEY.format(itemTotal)
            };
            lines.add(line);
            buildColumnMaxLength(width, line);
            total += itemTotal;
        }
        String[] footer = {String.valueOf(index), "", "", "", "", MONEY.format(total)};
        // column max length
        buildColumnMaxLength(width, header);
        buildColumnMaxLength(width, footer);
        // line length
        StringBuilder sb = new StringBuilder();
        int lineLength = width.length - 1;
        // header
        for (int i = 0; i < header.length; i++) {
            appendFormatted(sb, header[i], align[i], width[i]);
            lineLength += width[i];
        }
        sb.append("\n");
        // separator
        buildSeparator(sb, lineLength);
        // lines
        for (String[] line : lines) {
            for (int i = 0; i < line.length; i++) {
                appendFormatted(sb, line[i], align[i], width[i]);
            }
            sb.append("\n");
        }
        // separator
        buildSeparator(sb, lineLength);
        // footer
        for (int i = 0; i < footer.length; i++)
            appendFormatted(sb, footer[i], align[i], width[i]);
        return sb.toString();
    }

    /**
     * Calculate max width array.
     *
     * @param width is width array.
     * @param value is an array of columns contents.
     */
    public static void buildColumnMaxLength(int[] width, String[] value) {
        for (int i = 0; i < value.length; i++)
            width[i] = Math.max(width[i], value[i].length());
    }

    /**
     * Appends separator to sb.
     *
     * @param lineLength is length of separator.
     */
    public static void buildSeparator(StringBuilder sb, int lineLength) {
        sb.append("-".repeat(Math.max(0, lineLength)));
        sb.append("\n");
    }

    /**
     * Appends to sb formatted value.
     * Trims string if its length > width.
     *
     * @param align -1 for align left, 0 for center and +1 for align right.
     */
    public static void appendFormatted(StringBuilder sb, String value, int align, int width) {
        if (value.length() > width)
            value = value.substring(0, width);
        int before = switch (align) {
            case 0 -> (width - value.length()) / 2;
            case 1 -> width - value.length();
            default -> 0;
        };
        int after = width - value.length() - before;
        while (before-- > 0)
            sb.append(" ");
        sb.append(value);
        while (after-- > 0)
            sb.append(" ");
        sb.append(" ");
    }

    /**
     * Calculates item's discount.
     * For NEW item discount is 0%;
     * For SECOND_FREE item discount is 50% if quantity > 1
     * For SALE item discount is 70%
     * For each full 10 not NEW items item gets additional 1% discount,
     * but not more than 80% total
     */
    public static int calculateDiscount(ItemType type, int quantity) {
        int discount = 0;
        switch (type) {
            case NEW:
                return 0;
            case SECOND_FREE:
                if (quantity > 1)
                    discount = 50;
                break;
            case SALE:
                discount = 70;
                break;
        }
        discount += quantity / 10;
        if (discount > 80)
            discount = 80;
        return discount;
    }
}
