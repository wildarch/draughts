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
    
    private void winsOf(DraughtsPlayer p1, DraughtsPlayer p2) {
            DraughtsState stateWB = new DraughtsState();
            DraughtsState stateBW = new DraughtsState();
            while(!stateWB.isEndState()) {
                DraughtsPlayer player = stateWB.isWhiteToMove()? p1 : p2;
                stateWB.doMove(player.getMove(stateWB));    
            }
            while(!stateBW.isEndState()) {
                DraughtsPlayer player = stateBW.isWhiteToMove()? p2 : p1;
                stateBW.doMove(player.getMove(stateBW));    
            }
            
            assertFalse(stateWB.isWhiteToMove());
            assertTrue(stateBW.isWhiteToMove());
    }
    

    @Test
    public void testBeatsExtra() {
        DraughtsPlayer white = new MyDraughtsPlayer(4);
        DraughtsPlayer black = new MyExtraDraughtPlayer(4);
        winsOf(white, black);
    }
    
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
