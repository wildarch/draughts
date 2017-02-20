package nl.tue.s2id90.group19;

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
        Timer timer = new Timer();  
        DraughtsState ds = new DraughtsState();
        while(!ds.isEndState()) {
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
           System.out.println(currentMove.getFullNotation());
           ds.doMove(currentMove);
        }
        return !ds.isWhiteToMove();
    }

    @Test
    public void testBeatsExtra() {
        DraughtsPlayer white = new MyDraughtsPlayer(5);
        DraughtsPlayer black = new MyExtraDraughtPlayer(5);
        assertTrue(winsOf(white, black));
        
    }
    
}
