import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherService {
    private static final String API_KEY = "02983c9f-1623-4d3e-a034-97463d20af1a";
    private static final String BASE_URL = "https://api.weather.yandex.ru/v2/forecast";

    public static void main(String[] args) {
        double lat = 55.75; // Широта
        double lon = 37.62; // Долгота
        int limit = 7; // Количество дней для среднего значения температуры

        try {
            // Формируем URL для запроса
            String urlString = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&limit=" + limit;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Yandex-Weather-Key", API_KEY);

            // Чтение ответа
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Выводим весь ответ
            System.out.println("Response: " + response.toString());

            // Парсим JSON и извлекаем данные
            JSONObject jsonResponse = new JSONObject(response.toString());
            double currentTemp = jsonResponse.getJSONObject("fact").getDouble("temp");
            System.out.println("Current Temperature: " + currentTemp);

            // Вычисление средней температуры за период
            double avgTemp = calculateAverageTemperature(jsonResponse);
            System.out.println("Average Temperature over " + limit + " days: " + avgTemp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double calculateAverageTemperature(JSONObject jsonResponse) {
        double totalTemp = 0;
        int count = 0;

        for (int i = 0; i < jsonResponse.getJSONArray("forecasts").length(); i++) {
            totalTemp += jsonResponse.getJSONArray("forecasts").getJSONObject(i).getJSONObject("parts").getDouble("day").getDouble("temp_avg");
            count++;
        }

        return totalTemp / count;
    }
}