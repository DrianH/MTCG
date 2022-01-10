package classes;

import java.util.UUID;

public class Card {
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    private String id;
    private String name;
    private double damage;

    public Card(String id, String name, double damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public String toString(){
        return
            "Id : " + this.id +
            "Name : " + this.name +
            "Damage : " + this.damage;
    }
}
