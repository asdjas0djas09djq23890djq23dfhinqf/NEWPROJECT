
import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ProgramLogic {
    private Parse parser;
    private SampleFrame frame;
    private int currentDay;
    private ArrayList<Stock> stocks;
    private double balance;

    public ProgramLogic() {
        frame = new SampleFrame(this);
        parser = new Parse(this);
        Scanner scanner = new Scanner(System.in);
        balance = 10000;

        stocks = new ArrayList<>();

        currentDay = 1;

        while (true) {
            System.out.print("input a stock symbol to purchase, or type \"next day\" to go on to the next day: ");
            String symbol = scanner.nextLine();
            if (symbol.equals("next day")) {
                nextDay();
            } else {
                initializeStock(symbol);
            }
        }
    }

    public void initializeStock(String symbol) {
        parser.fetchStockData(symbol);
    }

    public double getBalance() {
        return balance;
    }

    public void changeBalance(double amt) {
        balance += amt;
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }

    public void addToStocks(Stock stock) {
        stocks.add(stock);
    }

    public int getDay() {
        return currentDay;
    }

    public Color randomColor() {
        Color color;
        int decider = (int) (Math.random() * 10);
        if (decider == 0) {
            color = Color.blue;
        } else if (decider == 1) {
            color = Color.cyan;
        } else if (decider == 2) {
            color = Color.darkGray;
        }  else if (decider == 3) {
            color = Color.gray;
        } else if (decider == 4) {
            color = Color.green;
        } else if (decider == 5) {
            color = Color.lightGray;
        } else if (decider == 6) {
            color = Color.magenta;
        } else if (decider == 7) {
            color = Color.orange;
        } else if (decider == 8) {
            color = Color.pink;
        } else {
            color = Color.red;
        }
        return color;
    }

    public double getLowestStockValue() {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < stocks.size(); i++) {
            Stock current = stocks.get(i);
            String name = current.getName();
            for (int j = 89; j < current.getPrices().size(); j++) {
                min = Math.min(min, current.getPrices().get(j));
            }
        }
        return min;
    }

    public double getHighestStockValue() {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < stocks.size(); i++) {
            Stock current = stocks.get(i);
            String name = current.getName();
            for (int j = 89; j < current.getPrices().size(); j++) {
                max = Math.max(max, current.getPrices().get(j));
            }
        }
        return max;
    }

    public void nextDay() {
        for (int i = 0; i < stocks.size(); i++) {
            Stock current = stocks.get(i);
            double currentPrice = current.getPrices().get(current.getPrices().getSize() - 1)
            double[] array = current.getPrices().stream().mapToDouble(d -> d).toArray();
            int p = 0;
            int d = 1;
            int q = 5;
            int P = 0;
            int D = 0;
            int Q = 0;
            int m = 0;
            int forecastSize = 1;
            ArimaParams params = new ArimaParams(p, d, q, P, D, Q, m);
            ForecastResult forecastResult = Arima.forecast_arima(array, forecastSize, params);
            double[] forecastData = forecastResult.getForecast();
            System.out.println(current.getName() + " has a predicted value of " + forecastData[0] + " today");
            double estimate = forecastData[0];
            estimate += estimate * (Math.random() * 0.02 - 0.01);
            current.addToPrices(estimate);
            balance -= (currentPrice - estimate)
            currentDay++;
        }
    }

    public void initializeOrBuy(String symbol, int quantity) {
        boolean exists = false;
        int location = stocks.size();
        for (int i = 0; i < stocks.size(); i++) {
            if (stocks.get(i).getName().equals(symbol)) {
                location = i;
                exists = true;
                break;
            }
        }
        if (!exists) {
            initializeStock(symbol);
        }
        stocks.get(location).addQuantity(quantity);
    }
}
