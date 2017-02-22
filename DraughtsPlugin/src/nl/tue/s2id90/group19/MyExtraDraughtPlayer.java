package nl.tue.s2id90.group19;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import nl.tue.s2id90.draughts.DraughtsState;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class MyExtraDraughtPlayer  extends MyDraughtsPlayer {
    
    public MyExtraDraughtPlayer(int maxSearchDepth) {
        super(maxSearchDepth);
    }

    MyExtraDraughtPlayer(int baseSearchDepth, boolean enableDeepening) {
        super(baseSearchDepth, enableDeepening);
    }
    
    @Override
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth, boolean maximize, boolean didCapture)
            throws AIStoppedException
    {
        if(stopped) throw new AIStoppedException();
        int bestValue = maximize? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if(depth == 0) return evaluate(node.getState());
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            int value = alphaBeta(node, alpha, beta, depth - 1, !maximize, false);
            if (maximize && value >= bestValue) {
                bestValue = value;
                if(depth == searchDepth) {
                    node.setBestMove(m);
                }
                // Cutoff
                if (bestValue >= beta) {
                    node.getState().undoMove(m);
                    return bestValue;
                }
            }
            else if (!maximize && value <= bestValue) {
                bestValue = value;
                if(depth == searchDepth) {
                    node.setBestMove(m);
                }
                // Cutoff
                if (bestValue <= alpha) {
                    node.getState().undoMove(m);
                    return bestValue;
                }
            }
            node.getState().undoMove(m);
        }
        return bestValue;
    }
    
}
