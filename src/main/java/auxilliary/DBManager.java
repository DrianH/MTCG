package auxilliary;

import classes.Card;

import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    private Connection conn = null;

    public DBManager(){
        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            String username = "root";
            String password = "";
            String url = "jdbc:MySQL://localhost/mtcg";
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established");
        }
        catch(Exception ex){
            System.err.println("Cannot connect to database.");
            ex.printStackTrace();
        }
    }

    public boolean login(String username, String password){
        boolean res = false;

        try{
            String sql = "select * from user where username = ? and password = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) res = true;
            else res = false;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return res;
    }

    public void insertUser(String username, String password){
        try{
            String sql1 = "select * from user where username = ?";

            PreparedStatement ps1 = conn.prepareStatement(sql1);

            ps1.setString(1, username);

            ResultSet rs = ps1.executeQuery();

            if(!rs.next()){
                String sql2 = "insert into user(username, password, isAdmin, coins) values(?, ?, ?, ?)";

                PreparedStatement ps2 = conn.prepareStatement(sql2);

                ps2.setString(1, username);
                ps2.setString(2, password);

                if(username == "admin") ps2.setInt(3, 1);
                else ps2.setInt(3, 0);

                ps2.setInt(4, 20);

                ps2.executeUpdate();
            }
            else{
                System.out.println("Username already exists.");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void insertPackage(Card[] cards){
        try {
            for(int i = 0; i < cards.length; i++){
                String sql = "insert into card(id, name, damage) values(?, ?, ?)";

                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, cards[i].getId());
                ps.setString(2, cards[i].getName());
                ps.setDouble(3, cards[i].getDamage());

                ps.executeUpdate();
            }

            String sql = "insert into package(card1, card2, card3, card4, card5) values(?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, cards[0].getId());
            ps.setString(2, cards[1].getId());
            ps.setString(3, cards[2].getId());
            ps.setString(4, cards[3].getId());
            ps.setString(5, cards[4].getId());

            ps.executeUpdate();

            System.out.println("Successfully created packages.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void buyPackage(String username){
        try{
            //Remove coins
            String removeCoins = "update user set coins = coins - 5 where username = ? ";
            PreparedStatement ps1 = conn.prepareStatement(removeCoins);

            ps1.setString(1, username);

            //Get user to receive package
            String getUser = "select * from user where username = ?";
            PreparedStatement ps2 = conn.prepareStatement(getUser);

            ps2.setString(1, username);

            ResultSet user = ps2.executeQuery();

            //Get Coins
            String getCoins = "select coins from user where username = ?";
            PreparedStatement ps4 = conn.prepareStatement(getCoins);

            ps4.setString(1, username);

            ResultSet rs2 = ps4.executeQuery();

            //Get packages without owner
            String getEmptyPackages = "select * from package where user is null";
            PreparedStatement emptyPackage = conn.prepareStatement(getEmptyPackages);

            ResultSet emptyPack = emptyPackage.executeQuery();

            if(emptyPack.next()){
                rs2.next();
                if(rs2.getDouble("coins") > 0){
                    String givePackage = "update package set user = ? where user is null limit 1";
                    PreparedStatement ps3 = conn.prepareStatement(givePackage);

                    user.next();
                    ps3.setInt(1, user.getInt("id"));
                    try{
                        ps3.executeUpdate();
                        ps1.executeUpdate();
                        System.out.println("User " + username + " bought a package");
                    }
                    catch(SQLException ex){
                        ex.printStackTrace();
                    }
                }
                else{
                    System.out.println("User " + username + " has no coins left");
                }
            }
            else{
                System.out.println("There are no packages left.");
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void showCards(String username){
        try{
            String sql =
                    "select p.card1, p.card2, p.card3, p.card4, p.card5 " +
                    "from package p " +
                    "join user u " +
                    "on p.user = u.id " +
                    "where u.username = ?";

            PreparedStatement getCards = conn.prepareStatement(sql);
            getCards.setString(1, username);

            ResultSet rs = getCards.executeQuery();

            while(rs.next()){
                System.out.println("Cards for " + username);
                System.out.println("Card 1 ID: " + rs.getString("p.card1"));
                System.out.println("Card 2 ID: " + rs.getString("p.card2"));
                System.out.println("Card 3 ID: " + rs.getString("p.card3"));
                System.out.println("Card 4 ID: " + rs.getString("p.card4"));
                System.out.println("Card 5 ID: " + rs.getString("p.card5"));
                System.out.println();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void showDeck(String username){
        try {
            String getUser = "select * from user where username = ?";
            PreparedStatement ps = conn.prepareStatement(getUser);

            ps.setString(1, username);

            ResultSet user = ps.executeQuery();

            ps.setString(1, username);


            String getDeck = "select * from deck where userid = ?";
            PreparedStatement ps2 = conn.prepareStatement(getDeck);

            user.next();

            ps2.setInt(1, user.getInt("id"));

            ResultSet deck = ps2.executeQuery();

            if(deck.next()){
                System.out.println("Deck for " + username);
                System.out.println("Card 1: " + deck.getString("card1"));
                System.out.println("Card 2: " + deck.getString("card2"));
                System.out.println("Card 3: " + deck.getString("card3"));
                System.out.println("Card 4: " + deck.getString("card4"));
            }
            else{
                System.out.println("No deck found for " + username);
            }
        } catch (SQLException throwables) {
            System.out.println("That user already has a deck.");
        }
    }

    public void createDeck(String card1, String card2, String card3, String card4, String username){
        try{
            String getUser = "select * from user where username = ?";
            PreparedStatement ps = conn.prepareStatement(getUser);

            ps.setString(1, username);

            ResultSet user = ps.executeQuery();
            user.next();


            String insertDeck = "insert into deck(card1, card2, card3, card4, userid) values(?, ?, ?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(insertDeck);

            ps2.setString(1, card1);
            ps2.setString(2, card2);
            ps2.setString(3, card3);
            ps2.setString(4, card4);
            ps2.setInt(5, user.getInt("id"));

            ps2.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void selectUser(String username){
        try{
            String sql = "select * from user where username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            rs.next();

            System.out.println("Data for " + username);
            System.out.println("Username: " + rs.getString("username"));
            System.out.println("Bio: " + rs.getString("bio"));
            System.out.println("Image: " + rs.getString("image"));
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateUser(String newUsername, String newBio, String newImage, String username){
        try{
            String sql = "update user set username = ?, bio = ?, image = ? where username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, newUsername);
            ps.setString(2, newBio);
            ps.setString(3, newImage);
            ps.setString(4, username);

            ps.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
