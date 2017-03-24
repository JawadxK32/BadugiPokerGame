import java.util.List;

/**
 * An interface that defines the methods that all Agents in this course must implement.
 * 
 * @author Ilkka Kokkarinen
 * @version (a version number or a date)
 */
public interface LearningAgent
{
      
    /**
     * Ask the agent which action to perform, based on the current perception.
     * 
     * @param training Whether the action counts towards the agent's actual score,
     * which can affect the agent's choice of exploration and exploitation.
     * @param currentPercept The current percept of the agent.
     * @param possibleActions The list of possible actions available to the agent.
     * @return the action chosen by the agent.
     */
    public String chooseAction(boolean training, String currentPercept, List<String> possibleActions);

    /**
     * Observe the results of the Action performed in currentPercept. The environment is
     * guaranteed to call this method exactly once after each chooseAction call.
     * @param currentPercept The percept in which the action was performed.
     * @param action The action chosen by the agent.
     * @param nextPercept The percept resulting to the agent from performing that action.
     * @param reward The immediate reward given to the agent as result of the chosen action.
     */
    public void observeResults(String currentPercept, String action, String nextPercept, double reward);
    
    /**
     * Return the author of this agent. Each subclass of Agent should override this method
     * to return the author's name as a String.
     * @return The author of this agent.
     */
    public String getAuthor();
}