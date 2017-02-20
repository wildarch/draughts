package nl.tue.s2id90.group19;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class MyDraughtsPlayer  extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public MyDraughtsPlayer(int maxSearchDepth) {
        super("smiley.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
        System.out.println("Search depth: "+maxSearchDepth);
    }
    
    @Override public Move getMove(DraughtsState s) {
        stopped = false;
        Move bestMove = null;
        try {
            DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
            // compute bestMove and bestValue in a call to alphabeta
            System.out.println("Search depth: "+maxSearchDepth);
            while (true) {
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth, s.isWhiteToMove());
                if ((bestValue == MAX_VALUE && s.isWhiteToMove()) || 
                        (bestValue == MIN_VALUE && !s.isWhiteToMove())) {
                    // We have a winning strategy
                    break;
                }
                //bestValue = depthFirstSearch(node, maxSearchDepth, s.isWhiteToMove());
                bestMove = node.getBestMove();
                maxSearchDepth++;
                System.out.println("Increased search depth to "+maxSearchDepth);
            }
        } catch (AIStoppedException ex) {  /* nothing to do */  }    
        
        maxSearchDepth = 5;
        
        if (bestMove==null) {
            Move m = getRandomValidMove(s);
            if (m == null) {
                System.out.println("No move possible!");
                System.out.println(s);
            }
            return m;
        } else {
            System.out.println(bestMove.getFullNotation());
            return bestMove;
        }
    } 

    /** This method's return value is displayed in the AICompetition GUI.
     * 
     * @return the value for the draughts state s as it is computed in a call to getMove(s). 
     */
    @Override public Integer getValue() { 
       return bestValue;
    }

    /** Tries to make alphabeta search stop. Search should be implemented such that it
     * throws an AIStoppedException when boolean stopped is set to true;
    **/
    @Override public void stop() {
       stopped = true; 
    }
    
    /** returns random valid move in state s, or null if no moves exist. */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty()? null : moves.get(0);
    }
    
    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth, boolean maximize)
            throws AIStoppedException
    {
        if(stopped) throw new AIStoppedException();
        int bestValue = maximize? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if(depth == 0) return evaluate(node.getState());
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            int value = alphaBeta(node, alpha, beta, depth-1, !maximize);
            if (maximize && value >= bestValue) {
                bestValue = value;
                if(depth == maxSearchDepth) {
                    node.setBestMove(m);
                }
                // Cutoff
                if (bestValue >= beta) {
                    node.getState().undoMove(m);
                    return bestValue;
                }
            }
            else if (!maximize && value <= bestValue) {
                bestValue = value;
                if(depth == maxSearchDepth) {
                    node.setBestMove(m);
                }
                // Cutoff
                if (bestValue <= alpha) {
                    node.getState().undoMove(m);
                    return bestValue;
                }
            }
            node.getState().undoMove(m);
        }
        return bestValue;
    }
    
    private int depthFirstSearch(DraughtsNode node, int depth, boolean maximize)
            throws AIStoppedException {
        if (stopped) throw new AIStoppedException();
        int bestValue = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if(depth == 0) return evaluate(node.getState());
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            int value = depthFirstSearch(node, depth-1, !maximize);
            if (maximize && value >= bestValue) {
                bestValue = value;
                if(depth == maxSearchDepth) {
                    node.setBestMove(m);
                }
            }
            else if (!maximize && value <= bestValue) {
                bestValue = value;
                if(depth == maxSearchDepth) {
                    node.setBestMove(m);
                }
            }
            node.getState().undoMove(m);
        }
        return bestValue;
    }
    
    public static int evaluate(DraughtsState state) {
        int whites = 0;
        int blacks = 0;
        int whiteKings = 0;
        int blackKings = 0;
        
        int[] pieces = state.getPieces();
        
        int totalTempi = 0;
        
        for(int i = 0; i < pieces.length; i++) {
            int piece = pieces[i];
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
