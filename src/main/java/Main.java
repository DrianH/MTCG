import java.io.*;
import java.net.InetSocketAddress;

import auxilliary.DBManager;
import classes.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main{
    private static Map<String,String> token = new HashMap<>();

    public static void main(String[] args){
        int port = 10001;
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            handleRequests(server);
            server.setExecutor(null);
            server.start();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void handleRequests(HttpServer server){
        ObjectMapper objectMapper = new ObjectMapper();
        DBManager dbManager = new DBManager();


        HttpContext users = server.createContext("/users", exchange -> {
            InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String jsonString = br.readLine();

            try {
                JSONObject json = new JSONObject(jsonString);
                dbManager.insertUser(json.get("Username").toString(), json.get("Password").toString());
                exchange.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        HttpContext sessions = server.createContext("/sessions", exchange -> {
            InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String jsonString = br.readLine();

            try {
                JSONObject json = new JSONObject(jsonString);
                if(dbManager.login(json.get("Username").toString(), json.get("Password").toString())){
                    System.out.println("User " + json.get("Username") + " logged in");
                    token.put("Basic " + json.get("Username").toString() + "-mtcgToken", json.get("Username").toString());
                    System.out.println(token.get("Basic " + json.get("Username").toString() + "-mtcgToken"));
                    exchange.close();
                }
                else{
                    System.out.println("Credentials not found");
                    exchange.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        HttpContext packages = server.createContext("/packages", exchange -> {
            InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String jsonString = br.readLine();

            if(token.containsKey(exchange.getRequestHeaders().get("Authorization").get(0))
                    &&
            exchange.getRequestHeaders().get("Authorization").get(0).equals("Basic admin-mtcgToken")){
                try{
                    JSONArray json = new JSONArray(jsonString);
                    Card[] cards = new Card[5];

                    for(int i = 0; i < json.length(); i++){
                        System.out.println("Inserting cards");
                        cards[i] = new Card(json.getJSONObject(i).get("Id").toString(), json.getJSONObject(i).get("Name").toString(), Double.parseDouble(json.getJSONObject(i).get("Damage").toString()));
                    }

                    dbManager.insertPackage(cards);
                    exchange.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Token not found.");
            }
        });

        HttpContext buyPackage = server.createContext("/transactions/packages", exchange -> {
            if(token.containsKey(exchange.getRequestHeaders().get("Authorization").get(0))){
                dbManager.buyPackage(token.get(exchange.getRequestHeaders().get("Authorization").get(0)));
            }
        });

        HttpContext cards = server.createContext("/cards", exchange -> {
            if(token.containsKey(exchange.getRequestHeaders().get("Authorization").get(0))){
                dbManager.showCards(token.get(exchange.getRequestHeaders().get("Authorization").get(0)));
                exchange.close();
            }
            else{
                System.out.println("No token.");
            }
        });

        HttpContext deck = server.createContext("/deck", exchange -> {
            if("GET".equals(exchange.getRequestMethod())){
                dbManager.showDeck(token.get(exchange.getRequestHeaders().get("Authorization").get(0)));
                exchange.close();
            }

            if("PUT".equals(exchange.getRequestMethod())){
                InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
                BufferedReader br = new BufferedReader(isr);
                String jsonString = br.readLine();

                try{
                    JSONArray array = new JSONArray(jsonString);
                    String card1 = array.getString(0);
                    String card2 = array.getString(1);
                    String card3 = array.getString(2);
                    String card4 = array.getString(3);
                    String user = token.get(exchange.getRequestHeaders().get("Authorization").get(0));

                    dbManager.createDeck(card1, card2, card3, card4, user);
                    exchange.close();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        HttpContext kienboec = server.createContext("/users/kienboec", exchange -> {

        });

        HttpContext altenhof = server.createContext("/users/altenhof", exchange -> {

        });

        HttpContext stats = server.createContext("/stats", exchange -> {

        });

        HttpContext score = server.createContext("/score", exchange -> {

        });

        HttpContext tradings = server.createContext("/tradings", exchange -> {

        });

        HttpContext battles = server.createContext("/battles", exchange -> {

        });
    }
}
