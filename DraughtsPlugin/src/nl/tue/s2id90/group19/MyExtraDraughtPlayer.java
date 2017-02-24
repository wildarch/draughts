package nl.tue.s2id90.group19;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import nl.tue.s2id90.draughts.DraughtsState;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class MyExtraDraughtPlayer  extends MyDraughtsPlayer {
    
    public MyExtraDraughtPlayer(int maxSearchDepth) {
        super(maxSearchDepth);
    }

    MyExtraDraughtPlayer(int baseSearchDepth, boolean enableDeepening) {
        super(baseSearchDepth, enableDeepening);
    }
    
    @Override
    public int evaluate(DraughtsState state) {
        int whites = 0;
        int blacks = 0;
        int whiteKings = 0;
        int blackKings = 0;
        
        int[] pieces = state.getPieces();
        
        int totalTempi = 0;
        
        for(int i = 0; i < pieces.length; i++) {
            int piece = pieces[i];
            int tempi = (i-1) / 5;
            switch(piece) {
                case DraughtsState.WHITEPIECE:
                    whites++;
                    totalTempi += 10-tempi;
                    break;
                case DraughtsState.BLACKPIECE:
                    blacks++;
                    totalTempi += tempi;
                    break;
                case DraughtsState.WHITEKING:
                    whiteKings++;
                    break;
                case DraughtsState.BLACKKING:
                    blackKings++;
                    break;
            }
        }
        int score = 0;
        
        if (whites+whiteKings == 0) {
            return MIN_VALUE;
        }
        else if (blacks + blackKings == 0) {
            return MAX_VALUE;
        }
        score += whites - blacks + 3*(whiteKings - blackKings);
        return 20*score + totalTempi;
    }
    
}
