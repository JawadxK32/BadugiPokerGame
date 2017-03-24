/**
 * An environment where the agent operates in.
 * 
 * @author Ilkka Kokkarinen
 * @version (a version number or a date)
 */
public interface Environment
{
    /**
     * Sets the agent that operates in this environment.
     * @param agent The agent that operates in this environment.
     */
    public void setAgent(LearningAgent agent);
    
    /**
     * Tells the environment to run a single episode.
     * @param training Whether the episode counts towards the agent's actual score,
     * which can affect the agent's choice of exploration and exploitation.
     */
    public double runSingleEpisode(boolean training);
      
}