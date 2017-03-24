import java.text.DecimalFormat;

public class EnvironmentRunner
{

    private static DecimalFormat df = new DecimalFormat();
    static { df.setMaximumFractionDigits(3); df.setMinimumFractionDigits(3); }
    
    public static double trainAndTestAgent(LearningAgent agent, Environment environment,
        int trainingEpisodes, int testingEpisodes) {
        double totalScore = 0;
        environment.setAgent(agent);
        for(int i = 0; i < trainingEpisodes; i++) {
            environment.runSingleEpisode(true);
        }
        for(int i = 0; i < testingEpisodes; i++) {
            totalScore += environment.runSingleEpisode(false);
        }
        return totalScore;
    }
    
    private static final int TRAINING_EPISODES = 50_000_000;
    private static final int TESTING_EPISODES = 100_000;
    
    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Error: must provide agent and environment classes as arguments");
        }
        try {
            Class agentClass = Class.forName(args[0]);
            Class environmentClass = Class.forName(args[1]);
            LearningAgent agent = (LearningAgent) agentClass.newInstance();
            Environment environment = (Environment) environmentClass.newInstance();
            double totalScore = trainAndTestAgent(agent, environment, TRAINING_EPISODES, TESTING_EPISODES);
            System.out.println(agent.getAuthor() + " : " + df.format(totalScore / TESTING_EPISODES));
        }
        catch(Exception e) {
            System.err.println("Failure: " + e);
        }
    }
    
    public static void runBlackJack() {
        TDLearningAgent agent = new TDLearningAgent();
        System.out.println(trainAndTestAgent(agent, new BlackJack(), TRAINING_EPISODES, TESTING_EPISODES));
        agent.dumpValues();
    }
}