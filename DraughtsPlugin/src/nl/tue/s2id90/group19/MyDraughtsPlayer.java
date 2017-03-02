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
    private int bestValue = 0;
    int baseSearchDepth;
    int searchDepth;
    boolean useIterativeDeepening;
    
    //the characteristics of this specific draughtsplayer
    public int scoreValue = 20; //default value
    public int winValue = 1000; //default value
    public int tempiValue = 1;
    public int[][] patterns;
    public int[] patternValues;
    
    //machine learning values
    public int fitness = 0;
    public int generation = 0;
    
    int moveStackCounter = 0;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    boolean stopped;
    
    public MyDraughtsPlayer(int baseSearchDepth, boolean enableDeepening) {
        super("smiley");
        this.baseSearchDepth = baseSearchDepth;
        this.useIterativeDeepening = enableDeepening;
        
        
    }

    public MyDraughtsPlayer(int searchDepth) {
        super("smiley");
        this.baseSearchDepth = searchDepth;
        this.useIterativeDeepening = false;
        
    }
    
    public MyDraughtsPlayer(int searchDepth, int scoreValue, int winValue, int tempiValue, int[][] patterns, int[] patternValues) {
        super("smiley");
        this.baseSearchDepth = searchDepth;
        this.useIterativeDeepening = false;
        this.scoreValue = scoreValue;
        this.winValue = winValue;
        this.tempiValue = tempiValue;
        this.patterns = patterns;
        this.patternValues = patternValues;
        
    }
    
    @Override public Move getMove(DraughtsState s) {
        stopped = false;
        Move bestMove = null;
        try {
            DraughtsNode node = new DraughtsNode(s.clone());

            searchDepth = baseSearchDepth;
            while (true) {
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, searchDepth, s.isWhiteToMove(), false);
                if (moveStackCounter != 0) {
                    throw new RuntimeException("moveStackCounter: "+moveStackCounter);
                }
                bestMove = node.getBestMove();

                if ((bestValue == MAX_VALUE && s.isWhiteToMove()) || 
                        (bestValue == MIN_VALUE && !s.isWhiteToMove())
                        || !useIterativeDeepening) {
                    // We have a winning strategy
                    break;
                }
                
                searchDepth++;
                System.out.println("Increased search depth to "+searchDepth);
            }
            
        } catch (AIStoppedException ex) {  moveStackCounter = 0; }    
        
        if (bestMove==null) {
            System.out.println("Playing a random move!");
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
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth, boolean maximize, boolean didCapture)
            throws AIStoppedException
    {
        if(stopped) throw new AIStoppedException();
        int bestValue = maximize? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if(depth <= 0 && (isQuiet(node.getState()) || depth <= -2)) return evaluate(node.getState());
        //if(depth == 0) return evaluate(node.getState());
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            moveStackCounter++;
            //int newDepth = depth - 1;
            int newDepth = depth + (m.isCapture()? 0 : -1);
            int msc = moveStackCounter;
            int value = alphaBeta(node, alpha, beta, newDepth, !maximize, m.isCapture());
            if (msc != moveStackCounter) {
                throw new RuntimeException("Unbalanced state move manipulation at depth "+newDepth);
            }
            if (maximize && value >= bestValue) {
                bestValue = value;
                if(depth == searchDepth && !didCapture) {
                    node.setBestMove(m);
                }
                // Cutoff
                if (bestValue >= beta) {
                    node.getState().undoMove(m);
                    moveStackCounter--;
                    return bestValue;
                }
            }
            else if (!maximize && value <= bestValue) {
                bestValue = value;
                if(depth == searchDepth && !didCapture) {
                    node.setBestMove(m);
                }
                // Cutoff
                if (bestValue <= alpha) {
                    node.getState().undoMove(m);
                    moveStackCounter--;
                    return bestValue;
                }
            }
            node.getState().undoMove(m);
            moveStackCounter--;
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
                if(depth == baseSearchDepth) {
                    node.setBestMove(m);
                }
            }
            else if (!maximize && value <= bestValue) {
                bestValue = value;
                if(depth == baseSearchDepth) {
                    node.setBestMove(m);
                }
            }
            node.getState().undoMove(m);
        }
        return bestValue;
    }
    
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
                    totalTempi -= tempi;
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
            score = -winValue;
        }
        else if (blacks + blackKings == 0) {
            score = winValue;
        }
        
        //Check for patterns
        for (int i = 0; i < patterns.length; i++) {
            //Scan through current draughststate and see if a pattern occurs:
            //if so, give points for this
            boolean samePattern = true;
            
            for (int j = 1; j < pieces.length; j++) {
                if (patterns[i][j-1] != 9) { //if 9, DO NOT CARE: don't compare
                    if (patterns[i][j-1] != pieces[j]) {
                        //part of pattern is not the same as current state: stop
                        samePattern = false;
                        break;
                    }
                }
            }
            
            //If pattern is in current state: Give points for this
            if (samePattern) {
                score += patternValues[i];
            }
        }
        
        score += whites - blacks + 3*(whiteKings - blackKings);
        return scoreValue*score + tempiValue*totalTempi;
    }

    private boolean isQuiet(DraughtsState state) {
        for (Move m : state.getMoves()) {
            if (m.isCapture()) {
                return false;
            }
        }
        return true;
    }
}
