package API;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import com.google.gson.Gson;

public class DistanceGoogleAPI {

    private final String url;

    public DistanceGoogleAPI(ArrayList<String> adressList) throws IOException {
        String filePath = "src/main/java/API/googleKey";

        // recuperation de l'API_KEY
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String apiKey = reader.readLine();

        StringBuilder adresses = new StringBuilder();
        for(String adresse : adressList){
            adresses.append(adresse).append("|");
        }

        this.url = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins="+ adresses +
                "&destinations=" + adresses +
                "&avoid=highways" +
                "&key="+ apiKey;
    }

    public int[][] getDistances() throws IOException {

        // creation d'un client okhttp et preparation de la requete
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder().url(this.url).build();

        // envoie de la requete et recuperation de la reponse
        Response response = client.newCall(request).execute();
        String responseData = Objects.requireNonNull(response.body()).string();


        return extractDistancesFromJsonResponse(responseData);
    }

    private int[][] extractDistancesFromJsonResponse(String responseData){

        System.out.println(responseData);
        // recuperation du corps de la requete dans un objet json
        JsonObject jsonObject = new Gson().fromJson(responseData, JsonObject.class);

        JsonArray rows = jsonObject.getAsJsonArray("rows");

        // initialisation matrice distance
        int nbAdresses = rows.size();
        int[][] matriceDistances = new int[nbAdresses][nbAdresses];

        for(int i = 0; i < nbAdresses; i++){
            JsonObject row = rows.get(i).getAsJsonObject();
            JsonArray elements = row.getAsJsonArray("elements");

            for(int j = 0; j < nbAdresses; j++){
                JsonObject element = elements.get(j).getAsJsonObject();
                JsonObject distance = element.getAsJsonObject("distance");
                double distanceValue = distance.get("value").getAsDouble();
                matriceDistances[i][j] = (int) Math.round(distanceValue/1000);
            }
        }
        return matriceDistances;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> cyties = new ArrayList<>();
        cyties.add("Paris");
        cyties.add("Madrid");
        cyties.add("Londres");

        DistanceGoogleAPI api = new DistanceGoogleAPI(cyties);
        int[][] results = api.getDistances();

        int size = 3;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                System.out.print(results[i][j]+" ");
            }
            System.out.println();
        }
    }
}
