import java.awt.*;
import java.util.ArrayList;

public class Stock {
    private String name;
    private Color color;
    private ProgramLogic logic;
    private ArrayList<Double> prices;
    private int quantity;
    private double mostRecentPrice;

    public Stock(String name, ProgramLogic logic, ArrayList<Double> list) {
        this.name = name;
        this.logic = logic;
        prices = new ArrayList<>();
        color = logic.randomColor();
        this.quantity = 0;
        prices = list;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Double> getPrices() {
        return prices;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addToPrices(double price) {
        prices.add(price);
    }

    public void addQuantity(int amt) {
        quantity += amt;
    }
}
