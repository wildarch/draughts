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
public class UninformedPlayer extends MyDraughtsPlayer {

    public UninformedPlayer(int maxSearchDepth) {
        super(maxSearchDepth);
    }
}
