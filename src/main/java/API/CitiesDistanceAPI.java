package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONArray;
import org.json.JSONObject;

public class CitiesDistanceAPI {

    private String BaseUrl;
    private String CoordinatesCityOne;
    private String CoordinatesCityTwo;

    public CitiesDistanceAPI(String coor1, String coor2) {
        this.BaseUrl = "http://router.project-osrm.org/route/v1/car/";
        this.CoordinatesCityOne = coor1;
        this.CoordinatesCityTwo = coor2;
    }

    public double getDistanceBetweenTwoCities() throws IOException {
        // Create HTTP connection
        HttpURLConnection httpcon = (HttpURLConnection) ((new URL(this.BaseUrl + this.CoordinatesCityOne + ";" + this.CoordinatesCityTwo).openConnection()));
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpcon.setRequestProperty("Accept", "application/json");
        httpcon.setRequestMethod("GET");
        httpcon.connect();

        OutputStream os = httpcon.getOutputStream();
        os.close();
        Double distance = null;
        Double temps = null;
        if (httpcon.getResponseCode() == 200) {
            // Read/Output response from server
            BufferedReader inreader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String decodedString;
            String json = null;
            while ((decodedString = inreader.readLine()) != null) {
                json = decodedString;
            }
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("routes");
            System.out.println(obj);
            temps = (arr.getJSONObject(0).getDouble("duration"));
            distance = (arr.getJSONObject(0).getDouble("distance")) / 1000;
            // System.out.println(distance + " km");

            // Close reader and DB connection
            inreader.close();
            httpcon.disconnect();
        } else {
            System.out.println(httpcon.getResponseCode() + " : " + httpcon.getResponseMessage());
        }

        return distance;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Cabannes->Saint-Andiol");
        CitiesDistanceAPI api = new CitiesDistanceAPI("43.8667,4.95", "43.8333,4.95");
        System.out.println(api.getDistanceBetweenTwoCities());
        System.out.println("Cabannes->Caumont");
        api = new CitiesDistanceAPI("43.8667,4.95", "43.9,4.95");
        System.out.println(api.getDistanceBetweenTwoCities());
        System.out.println("Cabannes->Avignon");
        api = new CitiesDistanceAPI("43.8667,4.95", "43.95,4.81667");
        System.out.println(api.getDistanceBetweenTwoCities());
        System.out.println("Saint-Andiol->Caumont");
        api = new CitiesDistanceAPI("43.8333,4.95", "43.9,4.95");
        System.out.println(api.getDistanceBetweenTwoCities());
    }
}