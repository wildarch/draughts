package nl.tue.s2id90.group19.samples;

import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.group19.DraughtsNode;
import nl.tue.s2id90.group19.MyDraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * A simple draughts player that plays random moves
 * and values all moves with value 0.
 * @author huub
 */
public class UninformedPlayer extends DraughtsPlayer {
    
    private static int searchDepth = 5;
    private static boolean isWhite = false;

    public UninformedPlayer() {
        super(UninformedPlayer.class.getResource("smiley.png"));
    }
    
    @Override
    /** @return a random move **/
    public Move getMove(DraughtsState s) {
        isWhite = s.isWhiteToMove();
        DraughtsNode node = new DraughtsNode(s);
        int val = depthFirstSearch(node, searchDepth);
        return node.getBestMove();
    }
    
    
    private int depthFirstSearch(DraughtsNode node, int depth) {
        int bestValue = Integer.MIN_VALUE;
        if(depth == 0) return evaluate(node);
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            int value = depthFirstSearch(node, depth-1);
            if (value >= bestValue) {
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
        int scale = isWhite? 1:-1;
        return MyDraughtsPlayer.evaluate(n.getState()) * scale;
    }

    @Override
    public Integer getValue() {
        return 0;
    }
}
