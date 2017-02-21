package nl.tue.s2id90.group19;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import nl.tue.s2id90.draughts.DraughtsState;

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
            // This makes no sense and is probably total bullshit
            int tempi = i % 5;
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
        int score;
        if (whites+whiteKings == 0) {
            return MIN_VALUE;
        }
        else if (blacks + blackKings == 0) {
            return MAX_VALUE;
        }
        score = whites - blacks + 3*(whiteKings - blackKings);
        return 10*score + totalTempi;
    }
    
}
