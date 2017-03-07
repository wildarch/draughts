package nl.tue.s2id90.group19;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class MyExtraDraughtPlayer  extends Sloeber {
    
    public MyExtraDraughtPlayer(int baseSearchDepth, EvaluationWeights weights, boolean enableDeepening) {
        super(baseSearchDepth, weights, enableDeepening);
    }
    
}
