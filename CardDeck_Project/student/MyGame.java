package student;

import ias.Deck;
import ias.Game;
import ias.GameException;

import javax.print.DocFlavor;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;


public class MyGame implements Game {

    String name;

    HashMap <String, String> gameNameMap=new HashMap<String, String>(); // in each position of map there is different card name (z.B. Bicycle, UNO, etc)
    public MyGame(String gameName){
        this.name=gameName;
    }

    HashMap <String, Card> cards=new HashMap<String, Card>();
    HashMap <String, Property> properties=new HashMap<String, Property>();


    public void defineCard(String name){ // defineCard("Emrakul")
        cards.put(name, new Card(name));
    }

    public void defineProperty(String name, String type){ // defineProperty("power", "integer")
        properties.put(name, new Property(name,type));

    }

    public void setProperty(String cardName, String propertyName, String value) throws GameException { // if propertyName = type (String)
        Card card = cards.get(cardName);
        if (card==null)
            throw new GameException("card not defined!");
        // why getExistingProperty ?
        card.setCardProperty(getExistingProperty(propertyName), value); // change setProperty to setCardProperty to avoid same name w/ this method?
    }

    public void setProperty(String cardName, String propertyName, int value) throws GameException{ // if propertyName = power (Int)
        Card card = cards.get(cardName);
        if (card==null)
            throw new GameException("card not defined!");
        card.setCardProperty(getExistingProperty(propertyName), value);
    }

    public void defineRule(String propertyName, String operation) throws GameException {
        getExistingProperty(propertyName).defineRule(operation);
        // else return error? or just return?

    }
    public void defineRule(String propertyName, String winningName, String loosingName) throws GameException {
            getExistingProperty(propertyName).defineRule(winningName,loosingName);
    }

    public Property getExistingProperty(String propertyName) throws GameException {
        Property property = properties.get(propertyName);
        if (property==null){
            throw new GameException("no property named " + propertyName);
        }
        return property;
    }

    public Card getExistingCard(String cardName) throws GameException {
        Card card = cards.get(cardName);
        if (card==null){
            throw new GameException("no property named " + cardName);
        }
        return card;
    }

    static final private String [] emptyArray = new String[0];
    public String[] get(String type, String name) {
        switch (type){
            case "game":
                return emptyArray;
            case "card":
                if (name.equals("*"))
                    return cards.keySet().toArray(new String [cards.size()]); // turns into object array instead of string array. so need to convert again to string array
                if (cards.containsKey(name))
                    return new String [] {name};
                else
                    return emptyArray;
            case "property":
                if (name.equals("*"))
                    return properties.keySet().toArray(new String [properties.size()]);
                if (properties.containsKey(name))
                    return new String [] {name};
                else return emptyArray;
            case "rule":
                ArrayList <String> ruleOutputs= new ArrayList<>();
                if (name.equals("*"))
                    for (Property p: properties.values())
                        p.giveRules(ruleOutputs::add);     // creates consumer that consumes things by adding to ruleOutput  (e -> ruleOutputs.add(e))
                else {
                    Property valueOfName = properties.get(name);
                    if (valueOfName==null){
                       return emptyArray;
                    } else {
                        valueOfName.giveRules(ruleOutputs::add);
                    }
                }
                return ruleOutputs.toArray(new String [ruleOutputs.size()]);
            default: throw new IllegalArgumentException(); // or GameException
        }

    }

    public void saveToFile(String path) {
        try(
                FileOutputStream fos = new FileOutputStream(path);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter output = new BufferedWriter(osw)
                ) // try w/ resouces statement
        {

                output.write("Game: " + "| ");
                output.write(name);
                output.newLine();

            for(Card card: cards.values()){
                output.write("Card: ");
                output.write(card.name);
                output.newLine();
            }

            for(Property property: properties.values()){
                output.write("Property: ");
                output.write(property.propertyName);
                output.write(" | ");
                output.write(property.propertyType);
                output.newLine();
            }

            for(Card card: cards.values()){
                for (Property property: card.propertyValues.keySet()) { // gives all schlussel from hashmap property values
                    output.write("CardProperty: ");
                    output.write(card.name);
                    output.write(" | ");
                    output.write(property.propertyName);
                    output.write(" | ");
                    output.write(card.propertyValues.get(property).toString());
                    output.newLine();
                }
            }

            for (Property property: properties.values()){
                if (property.integerRule!=null) {
                    output.write("GameRuleInteger: ");
                    output.write(property.propertyName);
                    output.write(" | ");
                    output.write(property.integerRule);
                    output.newLine();
                }
            }

            for (Property property: properties.values()){
                for (NameRule n: property.stringRules){
                    output.write("GameRuleString: ");
                    output.write(property.propertyName);
                    output.write(" | ");
                    output.write(n.winningName);
                    output.write(" | ");
                    output.write(n.loosingName);
                    output.newLine();
                }
            }
            //System.out.println("File has been written");

        }catch(Exception e){
            System.out.println("Could not create file");
        }
    }


    public Deck createDeck(){
        return new DeckImplementation(this); // this is the thing before the .createDeck()
    }

    public static Game createGame(String name) throws GameException {
        return new MyGame(name);
    }

    public static Game loadGame(String path) throws GameException{
        try(
                FileInputStream fos = new FileInputStream(path);
                InputStreamReader osw = new InputStreamReader(fos, StandardCharsets.UTF_8);
                BufferedReader input = new BufferedReader(osw)
        ) // try-w/-resouces statement
        {
            MyGame game = null;
            while (true){
                String line = input.readLine();
                if (line==null){
                    if (game==null)                // if at end of file game == null, i.e. no game is loaded
                        throw new GameException("no Game loaded!"); // then throw error
                    return game;
                }

                if (line.isEmpty())
                    continue;
                int colonPos = line.indexOf(": ");
                if (colonPos==-1)
                    throw new GameException("no colon found");
                String head = line.substring(0,colonPos);
                String rest = line.substring(colonPos+1);
                String[] result = rest.split(" \\| ");

                switch (head){
                    case "Game":
                        if (result.length!=1 || game!=null)
                            throw new GameException("malformed file");
                        game = new MyGame(result[0]);

                        break;
                    case "Card":
                        if (result.length!=1 || game==null)
                            throw new GameException("malformed file");
                        game.defineCard(result[0]);
                        break;

                    case "Property":
                        if (result.length!=2 || game==null)
                            throw new GameException("malformed file");
                        game.defineProperty(result[0], result[1]);
                        break;
                    case "CardProperty":
                        if (result.length!=3 || game==null)
                            throw new GameException("malformed file");
                        if (game.getExistingProperty(result[1]).propertyType.equals("string"))
                        game.setProperty(result[0], result[1], result[2]);
                        else if (game.getExistingProperty(result[1]).propertyType.equals("int"))
                            game.setProperty(result[0], result[1], Integer.parseInt(result[2]));
                        else
                            assert false; // this else should never happen
                        break;
                    case "GameRuleInteger":
                        if (result.length!=2 || game==null)
                            throw new GameException("malformed file");
                        game.defineRule(result[0],result[1]);
                        break;
                    case "GameRuleString":
                        if (result.length!=3 || game==null)
                            throw new GameException("malformed file");
                        game.defineRule(result[0],result[1], result[2]);
                        break;
                    default: throw new GameException("invalid line");

                }


            }

        } catch (IOException | NumberFormatException e) {
            throw new GameException("could not load game "+e.getMessage());
        }

    }
}
