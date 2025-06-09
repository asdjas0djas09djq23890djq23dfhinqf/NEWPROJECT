import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Parse {
    private static final String API_KEY = "d0ob859r01qu2361j1j0d0ob859r01qu2361j1jg";
    private static final String BASE_URL = "https://finnhub.io/api/v1";
    private final HttpClient httpClient;

    public Parse() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void fetchStockData(String symbol) {
        String endpoint = "/quote";
        String url = BASE_URL + endpoint + "?symbol=" + symbol + "&token=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                parseStockData(response.body(), symbol);
            } else {
                System.err.println("Error: HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseStockData(String jsonResponse, String symbol) {
        JSONObject json = new JSONObject(jsonResponse);
        System.out.println("=== Stock Data for " + symbol + " ===");
        System.out.println("Current Price: $" + json.getDouble("c"));
        System.out.println("High Price: $" + json.getDouble("h"));
        System.out.println("Low Price: $" + json.getDouble("l"));
        System.out.println("Open Price: $" + json.getDouble("o"));
        System.out.println("Previous Close: $" + json.getDouble("pc"));
    }

    }