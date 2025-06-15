import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Parse {
    private static final String API_KEY = "d3c9a2737c5c4383813cb9ca3c1dcad0";
    private ProgramLogic logic;
    private final HttpClient httpClient;

    public Parse(ProgramLogic logic) {
        this.httpClient = HttpClient.newHttpClient();
        this.logic = logic;
    }

    public void fetchStockData(String symbol) {
        if (logic.getMap().containsKey(symbol)) {
            System.out.println("Already exists");
            return;
        }
        String url = "https://api.twelvedata.com/time_series" +
                "?symbol=" + symbol +
                "&interval=1day" +
                "&outputsize=90" +
                "&apikey=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                parseResponse(response.body(), symbol);
            } else {
                System.err.println("Error: HTTP " + response.statusCode());
                System.err.println("Body: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseResponse(String json, String symbol) {
        JSONObject root = new JSONObject(json);

        if (!root.has("values")) {
            System.err.println("API Error: " + root.toString());
            return;
        }

        JSONArray values = root.getJSONArray("values");

        ArrayList<Double> list = new ArrayList<>();

        for (int i = 89; i >= 0; i--) {
            JSONObject day = values.getJSONObject(i);
            String close = day.getString("close");
            list.add(Double.parseDouble(close));
        }
        System.out.println("added successfully, " + symbol + " is currently worth " + list.get(89));
        logic.addToMap(symbol, list);
        logic.addToNames(symbol);
        logic.addToColorMap(symbol);
    }


}