package nl.tue.s2id90.group19;

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
    
    public MyDraughtsPlayerTest() {
    }
    
    private boolean winsOf(DraughtsPlayer p1, DraughtsPlayer p2) {
        DraughtsState ds = new DraughtsState();
        while(!ds.isEndState()) {
            DraughtsPlayer p = ds.isWhiteToMove()? p1 : p2;
            ds.doMove(p.getMove(ds.clone()));
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
