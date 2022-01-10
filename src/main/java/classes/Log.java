package classes;

public class Log {
    private User player1, player2;
    String winner;
    int rounds;
    
    public Log(User player1, User player2, String winner, int rounds){
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.rounds = rounds;
    }
}
