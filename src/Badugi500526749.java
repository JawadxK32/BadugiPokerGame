import java.util.*;


public class Badugi500526749 implements BadugiPlayer {

    private static int count = 0;
    private int id;
    private String nick;
    private int position;


    public Badugi500526749() {
        this.id = ++count;
    }

    public Badugi500526749(String nick) {
        this.nick = nick;
    }


    public void startNewHand(int position, int handsToGo, int currentScore) {


    }


    public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {

        int activeCardAmount = hand.getActiveRanks().length;
        int topCard = hand.getActiveRanks()[1];

        if(activeCardAmount==4){
            if(drawsRemaining==3) {
                if(position==0) {
                    if (toCall <= 0 && topCard <= 8){
                    	return +1;
                    }
                    else{
                    	return 0;
                    }
                }
                if(position==0){
                 if(toCall >=0 && topCard <=6){
                	 return 0;
                 }
                 else{
                	 return +1;
                 }
                }
                if (position == 1) {
                    if(toCall >= 0 && topCard <=6){
                    	return +1;
                    }
                    else{
                    	return 0;
                    }
                }
            }
            if(drawsRemaining==2) {
                if (position == 0) {
                    if (opponentDrew>0 && toCall <= 0 && topCard <= 8){
                    	return 0;
                    }
                    else{
                    	return +1;
                    }
                }
                if (position == 1) {
                    if (opponentDrew<=0 && toCall >= 0 && topCard <= 6){
                    	return 0;
                    }
                    else{
                    	return +1;
                    }
                }
                        }
            if(drawsRemaining==1) {
                if (position == 0) {
                    if (toCall <= 0 && topCard <= 8){
                    	return 0;
                    }
                    else{
                    	return +1;
                    }
                }
                if (position == 1) {
                    if (toCall >= 0 && topCard <= 6){
                    	return +1;
                    }
                    else{
                    	return 0;
                    }
                }
            }
            if(drawsRemaining==0) {
                if (opponentDrew <= 0) {
                    //No draw but betted, they must have a good hand
                    if (toCall > 0) {
                        if (topCard > 10){
                        	return +1;
                        }
                        if (topCard <= 10){
                        	return +1;
                        }
                        if (topCard < 6){
                        	return 0;
                        }
                    }

                    //No draw no Bet, lets just call?
                    if(toCall <= 0){
                        return +1;
                    }
                }
                if (opponentDrew > 0) {
                    if (toCall <= 0) {
                        if (topCard >= 9){
                        	return 0;
                        }
                        if (topCard < 9){
                        	return +1;
                        }
                    }

                    if (toCall > 0) {
                        if (topCard >= 6){
                        	return 0;
                        }
                        if (topCard < 6){
                        	return +1;
                        }
                    }
                }
            }
        }
        if (activeCardAmount == 3) {
            if (drawsRemaining == 3) {
                if ((position == 0 && topCard <= 6)){
                	return 0;
                }
                if (toCall > 0 && topCard <=6){
                	return +1;
                }
                if (toCall > 0 && topCard > 6){
                	return -1;
                }
            }
            if (drawsRemaining == 2) {

                if (toCall > 0 && topCard > 7){
                	return -1;
                }
                if (toCall > 0 && opponentDrew <= 0){
                	return -1;
                }
                return 0;
            }
            if (drawsRemaining == 1) {
                if(opponentDrew >= 1 && toCall <=0){
                	return +1;
                }
                if (toCall > 0 || topCard > 7){
                	return -1;
                }
                return 0;
            }
            if (drawsRemaining == 0) {
                if (toCall > 0) return -1;
                return 0;
            }
        }
        while(activeCardAmount <= 2) {
            if (drawsRemaining == 3) {
                if(position == 0 && toCall <=0){
                	return 0;
                }
                else{
                	return +1;
                }
            }
            if (drawsRemaining == 2) {
                if(opponentDrew >=2 && toCall<=0){
                	return 0;
                }
                else{
                	return -1;
                }
            }
            if (drawsRemaining == 1) {
               if(opponentDrew >= 1 && toCall <=0){
            	   return +1;
               }
                else return -1;
            }
            if (drawsRemaining == 0) {
                if(opponentDrew > 0 || toCall <=0){
                	return +1;
                }
                else{
                	return -1;
                }
            }
        }
        return 0;
    }
    public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {

        return hand.getInactiveCards();
    }

    public void showdown(BadugiHand yourHand, BadugiHand opponentHand) { 
    	/*
    	System.out.println("Showdown seen by human player " + id + ".");
        System.out.println("Your hand at showdown: " + yourHand);
        System.out.println("Opponent hand at showdown: " + opponentHand);
        */
    }


    public String getAgentName() {
        if(nick != null) { return nick; } else { return "Jawad"; }
    }

    public String getAuthor() { return "Kokkarinen, Ilkka"; }
}
