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
public class BuggyPlayer extends MyDraughtsPlayer {

    public BuggyPlayer(int maxSearchDepth) {
        super(maxSearchDepth);
    }
}
