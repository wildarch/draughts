package nl.tue.s2id90.group19;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org.junit.Test;
import static org.junit.Assert.*;
import org10x10.dam.game.BoardState;
import org10x10.dam.game.Move;

/**
 *
 * @author daan
 */
public class MyDraughtsPlayerTest {
    
    /** Time limit per move */
    int timelimit = 2;
    
    public MyDraughtsPlayerTest() {
    }
    
    
    private int simulateGame(DraughtsPlayer white, DraughtsPlayer black) {
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
                    break;
                }
                state.doMove(bestMove);    
                
            }
            
            return evaluate(state);
    }
    

    @Test
    public void testBeatsExtra() {
        DraughtsPlayer white = new MyDraughtsPlayer(5);
        DraughtsPlayer black = new MyDraughtsPlayer(5);
        int resultGame = simulateGame(white, black);
        assertTrue(resultGame > 0);
    }
    
    
    //for generating patterns more easily
    /* 
     * Simple method to convert a state into a string: useful for more 
     * efficiently copying a state and searching for patterns
     * Format: String with length 50. Every 5 characters is 1 row.
     * Meaning of characters:
     * 0 = empty
     * 1 = white piece
     * 2 = black piece
     * 3 = white king
     * 4 = black king
     */
    
    
    
    
    /** Simple evaluate function that counts pieces and counts king as 3 */
    public static int evaluate(DraughtsState state) {
        int whites = 0;
        int blacks = 0;
        int whiteKings = 0;
        int blackKings = 0;
        int[] pieces = state.getPieces();
 
        for(int i = 0; i < pieces.length; i++) {
            int piece = pieces[i];
            switch(piece) {
                case DraughtsState.WHITEPIECE:
                    whites++;
                    break;
                case DraughtsState.BLACKPIECE:
                    blacks++;
                    break;
                case DraughtsState.WHITEKING:
                    whiteKings++;
                    break;
                case DraughtsState.BLACKKING:
                    blackKings++;
                    break;
            }
        }
        int score;
        if (whites+whiteKings == 0) {
            return MIN_VALUE;
        }
        else if (blacks + blackKings == 0) {
            return MAX_VALUE;
        }
        score = whites - blacks + 3*(whiteKings - blackKings);
        return score;
    }
}
