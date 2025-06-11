import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ProgramLogic {
    private HashMap<String, ArrayList<Double>> mapOfAllStocks;
    private ArrayList<String> listOfStockNames;
    private Parse parser;
    private SampleFrame frame;

    public ProgramLogic() {
        frame = new SampleFrame();
        parser = new Parse(this);
        Scanner scanner = new Scanner(System.in);

        mapOfAllStocks = new HashMap<>();
        listOfStockNames = new ArrayList<>();

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

    public HashMap getMap() {
        return mapOfAllStocks;
    }

    public void nextDay() {
        for (int i = 0; i < listOfStockNames.size(); i++) {
            String stock = listOfStockNames.get(i);
            double[] array = mapOfAllStocks.get(stock).stream().mapToDouble(d -> d).toArray();
            int p = 1;
            int d = 1;
            int q = 1;
            int P = 0;
            int D = 0;
            int Q = 0;
            int m = 0;
            int forecastSize = 1;
            ArimaParams params = new ArimaParams(p, d, q, P, D, Q, m);
            ForecastResult forecastResult = Arima.forecast_arima(array, forecastSize, params);
            double[] forecastData = forecastResult.getForecast();
            System.out.println(stock + " has a predicted value of " + forecastData[0] + " today");
            mapOfAllStocks.get(stock).add(forecastData[0]);
        }
    }
}
