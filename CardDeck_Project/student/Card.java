package student;

import ias.GameException;

import java.util.HashMap;

public class Card {

    String name;
    HashMap<Property, Object> propertyValues = new HashMap<>(); // why object and not String? oh bc Object -> String or int

    public Card (String name){
        this.name=name;
    }

    public void setCardProperty(Property property, String propertyValue) throws GameException {
        if(!property.propertyType.equals("string"))
            throw new GameException("property type of "+property.propertyName+" is not string");
        propertyValues.put(property,propertyValue);
    }


    public void setCardProperty(Property property, int propertyValue) throws GameException {      // different parameters than method w/ same name above so ok22
        if(!property.propertyType.equals("int"))
            throw new GameException("property type of "+property.propertyName+" is not int");
        // values in die hashmap packen
        propertyValues.put(property,propertyValue);
    }

    /**
     * looks at all properties of the card and counts how many times the card looses against the other card as well as how many times it wins
     * and then compares the counters
     */
    public boolean beats(Card card1){

        int counterWin=0;
        int counterLose=0;

        for (Property p:propertyValues.keySet()){
            if (p.beats(card1,this))
                counterLose++;
            if (p.beats(this,card1))
                counterWin++;
        }
        return counterWin>counterLose;
    }

}
