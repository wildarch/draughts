package nl.tue.s2id90.group19.samples;


import java.util.Arrays;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * A simple draughts player that always returns an invalid move.
 * @author huub
 */
public class BuggyPlayer extends DraughtsPlayer {

    public BuggyPlayer() {
        super(BuggyPlayer.class.getResource("best.png"));
    }
    
    @Override
    public Move getMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Move bestMove = moves.get(0);
        int bestValue = evaluate(s, bestMove);
        for (Move m : moves) {
            int value = evaluate(s, m);
            if(value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
        }
        return bestMove;
    }
    
    @Override
    public Integer getValue() {
        return 0;
    }
    
    final static int[] mapper = {0, 1, -1, 10, -10, 0};
    
    int evaluate(DraughtsState state, Move m) {
        state.doMove(m);
        int val = (int) Arrays.stream(state.getPieces())
                .skip(1)    // We don't use a[0]
                .map(
                        (p) -> mapper[p]
                ).sum();
        state.undoMove(m);
        if (state.isWhiteToMove()) {
            return val;
        }
        else {
            return -val;
        }
    }
}
