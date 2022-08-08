package student;

import ias.Game;
import ias.GameException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

public class Property {
    String propertyName;
    String propertyType;
    String integerRule;
    HashSet<NameRule> stringRules=new HashSet<>();

    public Property (String propertyName, String propertyType){
        this.propertyName=propertyName;
        this.propertyType=propertyType;
    }

    /*public void defineRule(String winningName, String loosingName){
        NameRule newRule= new NameRule(winningName, loosingName, false);
        NameRule opposite= new NameRule(loosingName,winningName,false);

        if (stringRules.contains(opposite))

    }*/

    public void giveRules (Consumer <String> out){
        if (integerRule!=null)
            out.accept(propertyName +  integerRule);
        for (NameRule n: stringRules){
            out.accept(propertyName + ":" + n.winningName + ">" + n.loosingName ); // type (type of the property named type is String)
        }
    }

    public void defineRule(String winningName, String loosingName) throws GameException {
        NameRule newRule = new NameRule(winningName, loosingName);
        if (stringRules.contains(newRule)){
            throw new GameException("rule already defined");
        }

        if (winningName.equals(loosingName)){
            throw new GameException("winning name and loosing name must be different!");
        }

        stringRules.add(newRule);
    }


    public void defineRule(String operation) throws GameException {

        if (integerRule!=null){
            throw new GameException("rule already defined");
        }
        if ((operation.equals(">") || operation.equals("<"))){     // < means less one wins. > means greater one wins
            //gameDataArray[nPosition].operation=operation;
            integerRule=operation; // check if properties.get returns
        } else {
            throw new GameException("only > or < allowed");
        }
    }

    /**
     * @param card1
     * @param card2
     * @return whether card1 beats card2 for 'this' property
     */

    public boolean beats(Card card1,Card card2){
        Object value1= card1.propertyValues.get(this); // this is the property before the dot (in card class, when method was called)
        Object value2= card2.propertyValues.get(this);

        if (value1==null)
            return false;
        if (value2==null)
            return false;
        if (propertyType.equals("int")) {
            int value1Int = (Integer) value1;
            int value2Int = (Integer) value2;  // know it's same type as value1 bc it has same propertyType (and Property) as value1, and PropertyNames are unique
            if (integerRule.equals(">"))
                return (value1Int>value2Int);
            else if (integerRule.equals(">"))
                return (value2Int>value1Int);
            else
                return false;
        }

        if (propertyType.equals("string")) {
            String value1String = (String) value1;
            String value2String = (String) value2;

            return stringRules.contains(new NameRule(value1String, value2String)); // if stringRules doesn't contain rule where value1String beats value2String then it's false
        }

        throw new IllegalStateException();

    }
}
