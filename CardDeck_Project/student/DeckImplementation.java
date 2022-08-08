package student;

/*Adds a card previously defined in the game to the deck. A deck can contain any number of
different cards and the same card can appear in a deck any number of times.


Beispiel: deck1.addCard(“Emrakul”); deck1.addCard(“Emrakul”)
Beispiel: deck2.addCard(“Emrakul”)*/


import ias.Deck;
import ias.GameException;

import java.util.ArrayList;
import java.util.HashMap;

public class DeckImplementation implements Deck {

    ArrayList<Card> deck = new ArrayList<>();  // difference between this and filling in the <> with String?
    String [] emptyArray= new String[0];
    MyGame myGame;

    public DeckImplementation(MyGame myGame) {
        this.myGame=myGame;
    }

    public void addCard(String cardName) throws GameException{
            deck.add(myGame.getExistingCard(cardName));         // if card doesn't exist it's handled already in the getExistingCard method
    }

    public String[] getAllCards() {
        if (deck.isEmpty())
            return  emptyArray;
        String [] deckString= new String[deck.size()];
        for (int i=0; i< deck.size(); i++){
            deckString[i]=deck.get(i).name;
        }
        return deckString;
    }

    public String[] getMatchingCards(String propertyName, int value) throws GameException {
        Property matchingProperty = myGame.getExistingProperty(propertyName);
        if(!matchingProperty.propertyType.equals("int"))
            throw new GameException("only integer type property allowed");

        ArrayList<String> matchingCardsInDeck = new ArrayList<>();
        Integer valueInt = value;                                                            // autoboxing int to Integer. More efficient than repeatedly casting in line 49
        for (Card c: deck){
            if (valueInt.equals(c.propertyValues.get(matchingProperty)))                    // valueInt safe against null pointers. need to put valueInt first
              matchingCardsInDeck.add(c.name);                                                                         // bc can't call method on null value
        }
            return matchingCardsInDeck.toArray(new String [matchingCardsInDeck.size()]);
    }
    public String[] getMatchingCards(String propertyName, String value) throws GameException{
        Property matchingProperty = myGame.getExistingProperty(propertyName);
        if(!matchingProperty.propertyType.equals("string"))
            throw new GameException("only string type property allowed");

        ArrayList<String> matchingCardsInDeck = new ArrayList<>();

        for (Card c: deck){
            if (value.equals(c.propertyValues.get(matchingProperty)))
                matchingCardsInDeck.add(c.name);
        }

        return matchingCardsInDeck.toArray(new String [matchingCardsInDeck.size()]);
    }

    public String[] selectBeatingCards(String opponentCard) throws GameException{
        Card oppCard= myGame.cards.get(opponentCard);
        ArrayList<String> beatingCards = new ArrayList<>();
        for (int i=0; i<deck.size(); i++){
            if (deck.get(i).beats(oppCard))          // compares card against all other cards using the beats method of card
                beatingCards.add(deck.get(i).name);
        }
        return beatingCards.toArray(new String [beatingCards.size()]);

    }
}
