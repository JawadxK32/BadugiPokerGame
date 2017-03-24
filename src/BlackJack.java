import java.util.*;

/**
 * An environment that operates as the simplified blackjack casino game. The possible
 * actions are hit and surrender (no surrender, split or double down).
 * 
 * @author Ilkka Kokkarinen
 * @version (a version number or a date)
 */
public class BlackJack implements Environment
{
    
    private LearningAgent agent;
    private Random rng = new Random();
    private List<String> possibleActions = 
        Collections.unmodifiableList(Arrays.asList("hit", "stand"));  
        
    public void setAgent(LearningAgent agent) { this.agent = agent; }

    private int getCard() {
        int card = 1 + rng.nextInt(13);
        if(card > 10) { card = 10; } // faces are counted as 10
        if(card == 1) { card = 11; } // and aces are 11, although see below for softness
        return card;
    }
    
    // These need to be fields since the runSingleEpisode method also needs them for
    // the last update to the agent at the end of the hand.
    private String currentPercept, nextPercept, action;
    
    // Returns 22 for natural blackjack, 23 for any bust.
    private int play(boolean training, int dealerCard, int firstCard, int secondCard, boolean dealer) {
        int sum = firstCard + secondCard;
        int softAces = (firstCard == 11 ? 1 : 0) + (secondCard == 11 ? 1 : 0);
        if(!dealer) { currentPercept = "BJ"; nextPercept = "BJ"; action = ""; }
        if(sum == 21) { return 22; } // natural blackjack
        while(true) {
            while(sum > 21 && softAces > 0) { // saved by the soft ace
                softAces--; sum -= 10;
            }
            if(sum > 21) { // busted!
                if(!dealer) { nextPercept = "busted"; }
                return 23;
            } 
            
            if(dealer) { // dealer's action is determined by his current total
                if(sum < 17 /* || (sum == 17 && softAces > 0) */) { // dealer hits
                    int card = getCard();
                    sum += card;
                    softAces += (card == 11 ? 1 : 0);
                }
                else { return sum; } // dealer must stand on any 18+ or soft 17
            }
            else { // ask the agent for the player's action
                if(!currentPercept.equals("BJ")) { // update the value of previous action
                    nextPercept = sum + (softAces > 0 ? "S" : "H") + dealerCard;
                    agent.observeResults(currentPercept, action, nextPercept, 0);
                }
                currentPercept = sum + (softAces > 0 ? "S" : "H") + dealerCard;
                action = agent.chooseAction(training, currentPercept, possibleActions);
                if(action.equals("hit")) { // player hits
                    int card = getCard();
                    sum += card;
                    softAces += (card == 11 ? 1 : 0);
                }
                else { nextPercept = "stand"; return sum; } // player stands
            }
        }
    }
    
    private double playBlackJackHand(boolean training) {
        int dealerFirst = getCard();
        int dealerSecond = getCard();
        int playerFirst = getCard();
        int playerSecond = getCard();
        int playerResult = play(training, dealerFirst, playerFirst, playerSecond, false);
        if(playerResult == 23) { return -1; } // player busts
        int dealerResult = play(false, dealerFirst, dealerFirst, dealerSecond, true);
        if(dealerResult == 22) { // dealer got a natural blackjack
            if(playerResult == 22) { return 0; }
            return -1;
        }
        if(playerResult == 22) { // player won with a natural blackjack
            return +1.5;
        }
        if(dealerResult == 23) { return +1; } // dealer busts
        // Otherwise, compare hand values
        if(dealerResult > playerResult) { return -1; } // dealer won
        if(dealerResult < playerResult) { return +1; } // player won
        return 0; // push
    }
    
    public double runSingleEpisode(boolean training) {
       double result = playBlackJackHand(training);
       agent.observeResults(currentPercept, action, nextPercept, result);
       return result;
    }
}