package nl.tue.s2id90.group19.samples;


import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.group19.DraughtsNode;
import nl.tue.s2id90.group19.MyDraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * A simple draughts player that always returns an invalid move.
 * @author huub
 */
public class BuggyPlayer extends DraughtsPlayer {
    
    int bestValue;

    public BuggyPlayer() {
        super(BuggyPlayer.class.getResource("best.png"));
    }
    
    private static int searchDepth = 5;
    private static boolean isWhite = false;
    
    @Override
    /** @return a random move **/
    public Move getMove(DraughtsState s) {
        isWhite = s.isWhiteToMove();
        DraughtsNode node = new DraughtsNode(s);
        bestValue = depthFirstSearch(node, searchDepth, isWhite);
        return node.getBestMove();
    }
    
    
    private int depthFirstSearch(DraughtsNode node, int depth, boolean maximize) {
        int bestValue = maximize? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if(depth == 0) return evaluate(node);
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            int value = depthFirstSearch(node, depth-1, !maximize);
            if (maximize && value >= bestValue) {
                bestValue = value;
                if(depth == searchDepth) {
                    node.setBestMove(m);
                }
            }
            else if (!maximize && value <= bestValue) {
                bestValue = value;
                if(depth == searchDepth) {
                    node.setBestMove(m);
                }
            }
            node.getState().undoMove(m);
        }
        return bestValue;
    }
    
    
    private int evaluate(DraughtsNode n) {
        return MyDraughtsPlayer.evaluate(n.getState());
    }

    @Override
    public Integer getValue() {
        return bestValue;
    }
}
