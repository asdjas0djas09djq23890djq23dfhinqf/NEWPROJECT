import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ProgramLogic {
    private HashMap<String, ArrayList<Double>> mapOfAllStocks;
    private ArrayList<String> listOfStockNames;
    private Parse parser;
    private SampleFrame frame;
    private int currentDay;
    private HashMap<String, Color> mapOfColors;

    public ProgramLogic() {
        frame = new SampleFrame(this);
        parser = new Parse(this);
        Scanner scanner = new Scanner(System.in);

        mapOfAllStocks = new HashMap<>();
        listOfStockNames = new ArrayList<>();
        mapOfColors = new HashMap<>();

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

    public void addToMap(String inputString, ArrayList<Double> inputList) {
        mapOfAllStocks.put(inputString, inputList);
    }

    public void addToNames(String inputString) {
        listOfStockNames.add(inputString);
    }

    public HashMap<String, ArrayList<Double>> getMap() {
        return mapOfAllStocks;
    }

    public ArrayList<String> getListOfStockNames() {
        return listOfStockNames;
    }

    public int getDay() {
        return currentDay;
    }

    public Color getColorFromSymbol(String symbol) {
        return mapOfColors.getOrDefault(symbol, Color.black);
    }

    public void addToColorMap(String symbol) {
        Color color;
        int decider = (int) (Math.random() * 11);
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
        } else if (decider == 9) {
            color = Color.red;
        } else {
            color = Color.yellow;
        }
        mapOfColors.put(symbol, color);
    }

    public double getLowestStockValue() {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < listOfStockNames.size(); i++) {
            String name = listOfStockNames.get(i);
            for (int j = 89; j < mapOfAllStocks.get(name).size(); j++) {
                min = Math.min(min, mapOfAllStocks.get(name).get(j));
            }
        }
        return min;
    }

    public double getHighestStockValue() {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < listOfStockNames.size(); i++) {
            String name = listOfStockNames.get(i);
            for (int j = 89; j < mapOfAllStocks.get(name).size(); j++) {
                max = Math.max(max, mapOfAllStocks.get(name).get(j));
            }
        }
        return max;
    }

    public void nextDay() {
        for (int i = 0; i < listOfStockNames.size(); i++) {
            String stock = listOfStockNames.get(i);
            double[] array = mapOfAllStocks.get(stock).stream().mapToDouble(d -> d).toArray();
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
            System.out.println(stock + " has a predicted value of " + forecastData[0] + " today");
            double estimate = forecastData[0];
            estimate += estimate * (Math.random() * 0.02 - 0.01);
            mapOfAllStocks.get(stock).add(estimate);
            currentDay++;
        }
    }
}
