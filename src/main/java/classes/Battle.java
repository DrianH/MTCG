package classes;

import java.util.ArrayList;

public class Battle {
    private int round;
    private User c1;
    private User c2;
    private User winner;

    public Battle(User c1, User c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public void begin(){
        this.round = 1;
        System.out.println("Battle between " + c1.getUsername() + " and " + c2.getUsername() + "\n");

        boolean over = false;
        ArrayList<Card> deck1 = c1.getDeck();
        ArrayList<Card> deck2 = c2.getDeck();

        while(!over){
            System.out.println("Round " + round);
            Card card1 = deck1.get((int)(Math.random() * deck1.size()));
            Card card2 = deck2.get((int)(Math.random() * deck2.size()));

            System.out.println(card1.getName() + " with " + card1.getDamage() + " Power battles " + card2.getName() + " with " + card2.getDamage() + " Power");

            comparePower(deck1, deck2, card1, card2);

            if(deck1.isEmpty()){
                this.winner = c1;
                System.out.println("Winner is " + winner.getUsername());
                over = true;
                new Log(c1, c2, winner.getUsername(), round);
            }
            else if(deck2.isEmpty()){
                this.winner = c2;
                System.out.println("Winner is " + winner.getUsername());
                over = true;
                new Log(c1, c2, winner.getUsername(), round);
            }
            else if(round == 100){
                System.out.println("It's a tie");
                over = true;
                new Log(c1, c2, "Tie", round);
            }

            round++;
        }
    }

    public void comparePower(ArrayList<Card> deck1, ArrayList<Card> deck2, Card card1, Card card2){
        if(card1.getDamage() > card2.getDamage()){
            deck1.add(card2);
            deck2.remove(card2);
            System.out.println(card1.getName() + " wins\n");
        }
        else if(card1.getDamage() < card2.getDamage()){
            deck2.add(card1);
            deck1.remove(card1);
            System.out.println(card2.getName() + " wins\n");
        }
        else{
            System.out.println("Tie\n");
        }
    }
}
