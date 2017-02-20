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
    
    /** Timelimit per move */
    int timelimit = 2;
    
    public MyDraughtsPlayerTest() {
    }
    
    private boolean winsOf(DraughtsPlayer p1, DraughtsPlayer p2) {
        boolean stuckInLoop = false;
        DraughtsState oldState = new DraughtsState();
        boolean copyThisState = false;
        Timer timer = new Timer();  
        DraughtsState ds = new DraughtsState();
        while(!ds.isEndState() && !stuckInLoop) {
            DraughtsPlayer p = ds.isWhiteToMove()? p1 : p2;
            
            /** Make sure the move does not exceed the time limit */
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                        p.stop();
                }            
           };
           timer.schedule(task, timelimit * 1000);      
           
           /** Get best move and do it */
           Move currentMove = p.getMove(ds.clone());
           ds.doMove(currentMove);
           
           /** Check if the game is stuck in a loop 
            * It compares the current state to the state of 2 rounds ago
            * If they are equal, the game is stuck in a loop: will never end
            */
           if (copyThisState) {
               stuckInLoop = ds.equals(oldState);
               oldState = ds.clone();
           } else {
               copyThisState = true;
           }
        }
        //return !ds.isWhiteToMove(); does not work if stuck in a loop?
        
        System.out.print("End score: "+ evaluate(ds) + ". Reason for end: ");
        if (stuckInLoop) {
            System.out.println("stuck in loop.");
        } else {
            System.out.println("in end state");
        }
        return evaluate(ds) > 0; 
    }
    

    @Test
    public void testBeatsExtra() {
        DraughtsPlayer white = new MyDraughtsPlayer(5);
        DraughtsPlayer black = new MyDraughtsPlayer(5);
        //DraughtsPlayer black = new MyExtraDraughtPlayer(5);
        assertTrue(winsOf(white, black));
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
