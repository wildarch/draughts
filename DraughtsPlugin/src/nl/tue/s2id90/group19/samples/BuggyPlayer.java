package nl.tue.s2id90.group19.samples;


import nl.tue.s2id90.group19.EvaluationWeights;
import nl.tue.s2id90.group19.Sloeber;

/**
 * A simple draughts player that always returns an invalid move.
 * @author huub
 */
public class BuggyPlayer extends Sloeber {

    public BuggyPlayer(int maxSearchDepth) {
        super(maxSearchDepth, EvaluationWeights.randomWeights());
    }
}
