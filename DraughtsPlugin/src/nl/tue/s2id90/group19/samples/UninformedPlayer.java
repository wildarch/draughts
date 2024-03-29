package nl.tue.s2id90.group19.samples;

import nl.tue.s2id90.group19.EvaluationWeights;
import nl.tue.s2id90.group19.Sloeber;

/**
 * A simple draughts player that plays random moves
 * and values all moves with value 0.
 * @author huub
 */
public class UninformedPlayer extends Sloeber {

    public UninformedPlayer(int maxSearchDepth) {
        super(maxSearchDepth, EvaluationWeights.randomWeights());
    }
}
