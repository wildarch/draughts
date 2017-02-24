
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.group19.MyDraughtsPlayer;
import nl.tue.s2id90.group19.MyExtraDraughtPlayer;


/**
 *
 * @author daan
 */
public class Profile {
        public static void main(String[] args) {
            MyDraughtsPlayer p1 = new MyDraughtsPlayer(4);
            MyExtraDraughtPlayer p2 = new MyExtraDraughtPlayer(4);
            
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
    }
    
}
