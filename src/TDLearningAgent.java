import java.util.*;
import java.text.*;

/**
 * A temporal difference learning agent, using one-step Q-learning. This agent does not need
 * the transition or the reward model of the Markovian environment that it operates in, nor
 * this environment to be fully observable.
 * 
 * @author Ilkka Kokkarinen
 * @version (a version number or a date)
 */
public class TDLearningAgent implements LearningAgent
{
    
    private static DecimalFormat df = new DecimalFormat();
    static { df.setMinimumFractionDigits(5); df.setMaximumFractionDigits(5); }
    
    private static final char SEP = '#';
    
    private Map<String, Double> actionValues = new HashMap<>();
    private Map<String, List<String>> possibleActionsMap = new HashMap<>();
    private int actionCount = 0;
    private int actionGoal = 50_000;
    private double explorationProb = 0.2;
    private double learningRate = 0.02;
    private Random rng = new Random();
    
    private String encode(String percept, String action) {
        return percept + SEP + action;
    }
    
    private double getActionValue(String percept, String action) {
        String perceptAction = encode(percept, action);
        if(actionValues.containsKey(perceptAction)) {
            return actionValues.get(perceptAction);
        }
        else { return 0.0; } // "In the beginning, everything was even money." -- Mike Caro
    }
    
    private void updateActionValue(String percept, String action, double newValue) {
        actionValues.put(encode(percept, action), newValue);
    }
    
    public String chooseAction(boolean training, String currentPercept, List<String> possibleActions) {
        if(!possibleActionsMap.containsKey(currentPercept)) {
            possibleActionsMap.put(currentPercept, possibleActions);
        }
        String bestAction = "NONE";
        double bestActionValue = Double.NEGATIVE_INFINITY;
        // Examine the possible actions starting from random index.
        int idx = rng.nextInt(possibleActions.size());
        for(int i = 0; i < possibleActions.size(); i++) {
            String action = possibleActions.get( (idx + i) % possibleActions.size() );
            double currentActionValue = getActionValue(currentPercept, action);
            if(currentActionValue > bestActionValue) { // The best action seen so far
                if(!training || bestAction.equals("NONE") || rng.nextDouble() > explorationProb) {
                    bestAction = action;
                    bestActionValue = currentActionValue;
                }
            }
            else { // When training, sometimes explore currently non-optimal actions
                if(training && rng.nextDouble() < explorationProb) {
                    bestAction = action;
                    bestActionValue = currentActionValue;
                }
            }
        }
        return bestAction;
    }
    
    public void observeResults(String currentPercept, String action, String nextPercept, double reward) {
        double oldValue = getActionValue(currentPercept, action);
        double newValue = reward;
        double bestActionValue = Double.NEGATIVE_INFINITY;
        if(possibleActionsMap.containsKey(nextPercept)) {
            // Find the currently best action value for those in the observed target state
            for(String nextAction : possibleActionsMap.get(nextPercept)) {
                double currentActionValue = getActionValue(nextPercept, nextAction);
                if(currentActionValue > bestActionValue) { bestActionValue = currentActionValue; }
            }
        }
        else { bestActionValue = 0; }
        newValue += bestActionValue;
        // One step Q-learning update rule
        updateActionValue(currentPercept, action, (1 - learningRate) * oldValue + learningRate * newValue);
        if(++actionCount == actionGoal) { 
            actionGoal = 2*actionGoal; 
            learningRate = 0.75 * learningRate;
            explorationProb = 0.75 * explorationProb;
        }
    }
    
    public String getAuthor() { return "Ilkka Kokkarinen"; }
    
    public void dumpValues() {
        System.out.println("Soft");
        dumpBJValues("S");
        System.out.println("\nHard");
        dumpBJValues("H");
        ArrayList<String> keys = new ArrayList<>(actionValues.keySet());
        Collections.sort(keys);
        for(String key: keys) {
            System.out.println(key + " == " + df.format(actionValues.get(key)));
        }
    }
    
    private void dumpBJValues(String soft) {
        System.out.println("   A23456789T");
        for(int v = 21; v >= (soft.equals("S") ? 13 : 4); v--) {
            System.out.printf("%2d ", v);
            for(int d = 1; d <= 10; d++) {
                int dd = (d == 1 ? 11 : d);
                double hit = actionValues.get(v + soft + dd + "#hit");
                double stand = actionValues.get(v + soft + dd + "#stand");
                System.out.print(hit >= stand ? "H" : "S");
            }
            System.out.println("");
        }
    }
}