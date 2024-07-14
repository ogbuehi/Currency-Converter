package com.example.currencyconverter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;
import static java.net.HttpURLConnection.HTTP_OK;

public class HelloController {
    private String currencyOne, currencyTwo ;
    private static final String API = "5147b9865f1bdc07c61bf316b60a8c0c";

    private   ArrayList<String> currencyList;
    @FXML
    private ImageView logo;

    @FXML
    private TextField enterAmountField;

    @FXML
    private ComboBox<String> currencyOneBox, currencyTwoBox;

    @FXML
    private Label resultLabel;
    @FXML
    private Button convert;

    public void initialize(){
        // retrieve api key


        // load logo
//        loadLogo();

        try{
            // retrieve and store list of currencies
             currencyList = loadCurrencyList();

            // store the list in the combo boxes
            ObservableList<String> options = FXCollections.observableArrayList(currencyList);
            currencyOneBox.setItems(options);
            currencyTwoBox.setItems(options);

        }catch(IOException e){
            System.out.println("Error: Failed to load currency list " + e);
        }

    }

    public void setCurrencyOne(){
        currencyOne = currencyOneBox.getValue();
    }
    public void setCurrencyTwo(){
        currencyTwo = currencyTwoBox.getValue();
    }

//    private void getApiKey(){
//        BufferedReader reader = null;
//        try{
//            String filePath =(getClass().getResource
//                            ("/resources/apikey.txt")).getPath()
//                    .replaceAll("%20", " ");
//            reader = new BufferedReader(new FileReader(filePath));
//
//            // store api key
//            apiKey = reader.readLine();
//
//        }catch(IOException e){
//            System.out.println("Error: " + e);
//        }finally{
//            // close the buffered reader object to free up resources
//            try{
//                if(reader != null) reader.close();
//            }catch(IOException e){
//                System.out.println("Error: " + e);
//            }
//        }
//    }

//    private void loadLogo(){
//        String logoPath = getClass().getResource("/resources/Image/coin.jpg").getPath()
//                .replaceAll("%20", " ");
//        logo.setImage(new Image(new File(logoPath).getAbsolutePath()));
//    }

    private ArrayList<String> loadCurrencyList() throws IOException {
        // retrieve currency list through api call
        try {
            URL url = new URL("https://api.apilayer.com/currency_data/list");
            HttpClient client = HttpClient.newHttpClient();

            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(url.toURI())
                    .header("apiKey", API)
                    .timeout(Duration.ofMinutes(30))
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HTTP_OK) {
                System.out.println("Error reading web page :" + url);
            }
            String body = response.body();
            JsonElement jsonElement = new Gson().fromJson(body, JsonElement.class);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Set<String> object = jsonObject.getAsJsonObject("currencies").keySet();
            ArrayList<String> currencyList = new ArrayList<>();
//            JsonArray jsonArray = jsonObject.getAsJsonArray("currencies");
            if(!object.isEmpty()){
                for (String objects: object){
                    currencyList.add(objects);
                }
            }
            return currencyList;
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
//        Response response = client.newCall(request).execute();

    }

    @FXML
    public void convertCurrency(ActionEvent event) throws IOException {
        if(enterAmountField.getText().isEmpty()) return;
        if(currencyOne == null || currencyTwo == null) return;

        float conversionRate = getConversionRate();

        // calculate conversion
        float conversionResult = Float.parseFloat(enterAmountField.getText()) * conversionRate;

        // display result
        if(event.getSource() == convert){
            resultLabel.setText(conversionResult + " " + currencyTwo);
        }

    }

    private float getConversionRate() throws IOException{
        try {
        URL url = new URL("https://api.apilayer.com/currency_data/live?source=" +
                 currencyOne + "&currencies=" +  currencyTwo);
        HttpClient newHttpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(url.toURI())
                    .header("apiKey",API)
                    .timeout(Duration.ofSeconds(30))
                    .build();
            HttpResponse<String> response = newHttpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HTTP_OK){
                System.out.println("Error reading web page :"+url);
            }
            String body = response.body();
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(body,
                    JsonElement.class);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // return the conversion rate
            String key = currencyOne + currencyTwo;
            return Float.parseFloat(jsonObject.getAsJsonObject("quotes").get(key).getAsString());
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}