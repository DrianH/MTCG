package classes;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;

@JsonPOJOBuilder
public class User {
    private String Username, Password;
    private ArrayList<Card> deck;
    private int coins = 20;

    public User(String username, String password) {
        this.Username = username;
        this.Password = password;
        this.deck = new ArrayList<>();
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
