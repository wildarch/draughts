package nl.tue.s2id90.group19;

import java.util.ArrayList;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;

/**
 *
 * @author daan
 */
public class ClosingBook {
    
    public static void main(String[] args) {
        ArrayList<DraughtsState> boards = new ArrayList<>();
        for(int w = 1; w < 51; w++) {
            for(int b = 1; b < 51; b++) {
                if (w != b) {
                    DraughtsState state = new DraughtsState();
                    int[] pieces = state.getPieces();
                    for(int i = 0; i < 51; i++) {
                        if(i == w) {
                            pieces[i] = DraughtsState.WHITEPIECE;
                        }
                        else if(i == b) {
                            pieces[i] = DraughtsState.BLACKPIECE;
                        }
                        else {
                            pieces[i] = DraughtsState.EMPTY;
                        }
                    }
                    boards.add(state);
                }
            }
        }
        System.out.println("Found "+boards.size()+" boards");
        DraughtsPlayer player = new MyDraughtsPlayer(5);
        for(DraughtsState s : boards) {
            System.out.println(s);
        }
    }
    
}
