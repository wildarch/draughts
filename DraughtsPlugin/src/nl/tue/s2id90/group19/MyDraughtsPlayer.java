package nl.tue.s2id90.group19;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
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
    
    /** boolean that indicates the color of this player */
    private static boolean isWhite;

    public MyDraughtsPlayer(int maxSearchDepth) {
        super("smiley.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
        System.out.println("Search depth: "+maxSearchDepth);
    }
    
    @Override public Move getMove(DraughtsState s) {
        isWhite = s.isWhiteToMove(); //current player must be white
        
        Move bestMove = null;
        bestValue = -7777777;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);
            System.out.println("Value: "+bestValue);
            
            // store the bestMove found uptill now
            // NB this is not done in case of an AIStoppedException in alphaBeat()
            bestMove  = node.getBestMove();
            
            /*
            DraughtsState state = s.clone();
            evaluate(s, true);
            for(Move m : state.getMoves()) {
                if (bestMove == null) {
                    bestMove = m;
                }
                state.doMove(m);
                int score = alphaBeta(node, -8888888, 9999999, maxSearchDepth);
                System.out.println("Score: "+score);
                state.undoMove(m);
                if(score > bestValue) {
                    bestValue = score;
                    bestMove = m;
                }
            }
            */
            
            // print the results for debugging reasons
            System.err.format(
                "%s: depth= %2d, best move = %5s, value=%d\n", 
                this.getClass().getSimpleName(),maxSearchDepth, bestMove, bestValue
            );
        } catch (AIStoppedException ex) {  /* nothing to do */  }
        
        if (bestMove==null) {
            System.err.println("no valid move found!");
            Move m = getRandomValidMove(s);
            if (m == null) {
                System.out.println("No move possible!");
                System.out.println(s);
            }
            return m;
        } else {
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
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
            return alphaBetaMin(node, alpha, beta, depth);
        } else  {
            return alphaBetaMax(node, alpha, beta, depth);
        }
    }
    
    /** Does an alphabeta computation with the given alpha and beta
     * where the player that is to move in node is the minimizing player.
     * 
     * <p>Typical pieces of code used in this method are:
     *     <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     *          <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     *          <li><code>node.setBestMove(bestMove);</code></li>
     *          <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     *     </ul>
     * </p>
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth  maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been set to true.
     */
     int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        //if (stopped) { stopped = false; throw new AIStoppedException(); }
        if (depth <= 0) {
            return evaluate(node.getState());
        }
        
        for (Move move : node.getState().getMoves()) {
            node.getState().doMove(move);
            int val = alphaBetaMax(node, alpha, beta, depth-1);
            //if (val > -9000) {
                if (val <= beta) {
                    beta = val;
                    if(depth == maxSearchDepth)
                        node.setBestMove(move);
                }
                if (beta <= alpha) {
                    return beta;
                }
            //}
            node.getState().undoMove(move);
        }
        return beta;
     }
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        //if (stopped) { stopped = false; throw new AIStoppedException(); }
        if (depth <= 0) {
            return evaluate(node.getState());
        }
        
        for (Move move : node.getState().getMoves()) {
            node.getState().doMove(move);
            int val = alphaBetaMin(node, alpha, beta, depth-1);
            //if(val < 9000){
                if (val >= alpha) {
                    alpha = val;
                    if(depth == maxSearchDepth)
                        node.setBestMove(move);
                }
                if (beta <= alpha) {
                    return beta;
                }
            //}
            node.getState().undoMove(move);
        }
        return alpha;
        /*
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        Move bestMove = state.getMoves().get(0);
        int value = 0;
        node.setBestMove(bestMove);
        return value;
        */
    }
    
    /** A method that evaluates the given state. */
    int evaluate(DraughtsState s) {
        return evaluate(s, true);
    }
    
    public static int evaluate(DraughtsState state, boolean verbose) {
        int whites = 0;
        int blacks = 0;
        int whiteKings = 0;
        int blackKings = 0;
        
        for(int piece : state.getPieces()) {
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
        score = whites - blacks + 10*(whiteKings - blackKings);
        
        if (!isWhite) {
            score *= -1;
        }
        
        if (verbose) {
            System.out.println("w: "+whites);
            System.out.println("b: "+blacks);
            System.out.println("wk:"+whiteKings);
            System.out.println("bk:"+blackKings);
            System.out.println("score: "+score);
        }
        return score;
    }
}
