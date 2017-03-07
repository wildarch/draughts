package nl.tue.s2id90.group19;

import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import static nl.tue.s2id90.group19.Match.MatchResult.BLACK;
import static nl.tue.s2id90.group19.Match.MatchResult.DRAW;
import static nl.tue.s2id90.group19.Match.MatchResult.WHITE;
import org10x10.dam.game.Move;

/**
 *
 * @author daan
 */
public class Match implements Runnable {
    Sloeber white;
    Sloeber black;
    private MatchResult result;

    @Override
    public void run() {
        //System.out.println("Running Match "+this);
        getResult();
        //System.out.println("Finished Match "+this);
    }
    
    public enum MatchResult {
        WHITE,
        DRAW,
        BLACK
    }
    
    public Match(EvaluationWeights p1, EvaluationWeights p2, int searchDepth) {
        white = new Sloeber(searchDepth, p1);
        black = new Sloeber(searchDepth, p2);
    }
    
    public MatchResult getResult() {
        if (result == null) {
            result = playMatch();
        }
        
        return result;
    }
    
    private MatchResult playMatch() {
        DraughtsState state = new DraughtsState();
        int captureTimer = 0;
        while(!state.isEndState()) {
            DraughtsPlayer player = state.isWhiteToMove()? white : black;
            Move bestMove = player.getMove(state);
            if (bestMove.isCapture() || bestMove.isPieceMove()) {
                captureTimer = 0;
            } else {
                captureTimer++;
            }
            if (captureTimer>=20) {
                //only kings moved around, no captures for 20 moves: draw
                return DRAW;
            }
            state.doMove(bestMove);    
        }
        
        return state.isWhiteToMove()? BLACK : WHITE;
    }
    
    public EvaluationWeights getWinner() {
        return getResult() == WHITE ? white.evalWeights : black.evalWeights;
    }
    
    public EvaluationWeights getLoser() {
        return getResult() == WHITE ? black.evalWeights : white.evalWeights;
    }
}
