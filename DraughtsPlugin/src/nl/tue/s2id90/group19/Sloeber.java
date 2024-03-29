package nl.tue.s2id90.group19;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface, inspired by Kabouter Wesley.
 *
 * @author Daan de Graaf & Yoeri Poels
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class Sloeber extends DraughtsPlayer {

    private int bestValue = 0;
    int baseSearchDepth;
    int searchDepth;
    boolean useIterativeDeepening;

    int winValue = 1000000;

    public EvaluationWeights evalWeights;
    
    int moveStackCounter = 0;

    /**
     * boolean that indicates that the GUI asked the player to stop thinking.
     */
    boolean stopped;

    public Sloeber(int baseSearchDepth, EvaluationWeights weights,
            boolean enableDeepening) {
        super("sloeber.png");
        initialize(baseSearchDepth, weights, enableDeepening);
    }

    public Sloeber(int searchDepth, EvaluationWeights weights) {
        super("sloeber.png");
        initialize(searchDepth, weights, false);

    }

    private void initialize(int depth, EvaluationWeights weights, boolean itDeep) {
        this.baseSearchDepth = depth;
        this.evalWeights = weights;
        this.useIterativeDeepening = itDeep;
    }

    @Override
    public Move getMove(DraughtsState s) {
        stopped = false;
        Move bestMove = null;
        try {
            DraughtsNode node = new DraughtsNode(s.clone());

            searchDepth = baseSearchDepth;
            while (true) {
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, searchDepth, s.isWhiteToMove(), false);
                if (moveStackCounter != 0) {
                    throw new RuntimeException("moveStackCounter: " + moveStackCounter);
                }
                bestMove = node.getBestMove();

                if ((bestValue == MAX_VALUE && s.isWhiteToMove())
                        || (bestValue == MIN_VALUE && !s.isWhiteToMove())
                        || !useIterativeDeepening) {
                    // We have a winning strategy
                    break;
                }

                searchDepth++;
                System.out.println("Increased search depth to " + searchDepth);
            }

        } catch (AIStoppedException ex) {
            moveStackCounter = 0;
        }

        if (bestMove == null) {
            System.out.println("Playing a random move!");
            Move m = getRandomValidMove(s);
            if (m == null) {
                System.out.println("No move possible!");
                System.out.println(s);
            }
            return m;
        } else {
            //System.out.println(bestMove.getFullNotation());
            return bestMove;
        }
    }

    /**
     * This method's return value is displayed in the AICompetition GUI.
     *
     * @return the value for the draughts state s as it is computed in a call to
     * getMove(s).
     */
    @Override
    public Integer getValue() {
        return bestValue;
    }

    /**
     * Tries to make alphabeta search stop. Search should be implemented such
     * that it throws an AIStoppedException when boolean stopped is set to true;
    *
     */
    @Override
    public void stop() {
        stopped = true;
    }

    /**
     * returns random valid move in state s, or null if no moves exist.
     */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty() ? null : moves.get(0);
    }

    /**
     * Implementation of alphabeta that automatically chooses the white player
     * as maximizing player and the black player as minimizing player.
     *
     * @param node contains DraughtsState and has field to which the best move
     * can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     *
     */
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth, boolean maximize, boolean didCapture)
            throws AIStoppedException {
        if (stopped) {
            throw new AIStoppedException();
        }
        int bestValue = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if (depth <= 0 && (isQuiet(node.getState()) || depth <= -2)) {
            return evaluate(node.getState());
        }
        //if(depth == 0) return evaluate(node.getState());
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            moveStackCounter++;
            //int newDepth = depth - 1;
            int newDepth = depth + (m.isCapture() ? 0 : -1);
            int msc = moveStackCounter;
            int value = alphaBeta(node, alpha, beta, newDepth, !maximize, m.isCapture());
            if (msc != moveStackCounter) {
                throw new RuntimeException("Unbalanced state move manipulation at depth " + newDepth);
            }
            if (maximize && value >= bestValue) {
                bestValue = value;
                if (depth == searchDepth && !didCapture) {
                    node.setBestMove(m);
                }
                // Cutoff
                if (bestValue >= beta) {
                    node.getState().undoMove(m);
                    moveStackCounter--;
                    return bestValue;
                }
            } else if (!maximize && value <= bestValue) {
                bestValue = value;
                if (depth == searchDepth && !didCapture) {
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
        if (stopped) {
            throw new AIStoppedException();
        }
        int bestValue = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        if (depth == 0) {
            return evaluate(node.getState());
        }
        for (Move m : node.getState().getMoves()) {
            node.getState().doMove(m);
            int value = depthFirstSearch(node, depth - 1, !maximize);
            if (maximize && value >= bestValue) {
                bestValue = value;
                if (depth == baseSearchDepth) {
                    node.setBestMove(m);
                }
            } else if (!maximize && value <= bestValue) {
                bestValue = value;
                if (depth == baseSearchDepth) {
                    node.setBestMove(m);
                }
            }
            node.getState().undoMove(m);
        }
        return bestValue;
    }

    public int evaluate(DraughtsState state) {
        int returnScore = 0;
        int whites = 0;
        int blacks = 0;
        int whiteKings = 0;
        int blackKings = 0;

        int whiteLeftPieces = 0;
        int whiteRightPieces = 0;
        int blackLeftPieces = 0;
        int blackRightPieces = 0;

        int whiteCoherence = 0;
        int blackCoherence = 0;

        int[] pieces = state.getPieces();

        int totalTempi = 0;

        for (int i = 0; i < pieces.length; i++) {
            int piece = pieces[i];
            int tempi = (i - 1) / 5;
            int column = (i - 1) % 5;
            switch (piece) {
                case DraughtsState.WHITEPIECE:
                    whites++;
                    totalTempi += 9 - tempi;

                    /* Coherence */
                    whiteCoherence += pieceCoherence(DraughtsState.WHITEPIECE, pieces, i, column, tempi);

                    /* Balance */
                    if (column < 2) {
                        whiteLeftPieces++;
                    } else if (column > 2) {
                        whiteRightPieces++;
                    }
                    break;
                case DraughtsState.BLACKPIECE:
                    blacks++;
                    totalTempi -= tempi;

                    /* Coherence */
                    blackCoherence += pieceCoherence(DraughtsState.BLACKPIECE, pieces, i, column, tempi);

                    /* Balance */
                    if (column < 2) {
                        blackLeftPieces++;
                    } else if (column > 2) {
                        blackRightPieces++;
                    }
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

        if (whites + whiteKings == 0) {
            score = -winValue;
        } else if (blacks + blackKings == 0) {
            score = winValue;
        }
        /*
        //Value columns (for building formations) and check for split piece position
        for (int i = 0; i < columnsWhite.length; i++) {
            if (columnsWhite[i]>=3) {
                score += columnValue;
            }
            if (columnsBlack[i]>=3) {
                score -= columnValue;
            }                
            // only check if it is not on the side
            if (i > 0 && i < columnsWhite.length - 1) { 
                 if (columnsWhite[i] == 1 && columnsWhite[i-1] == 0 && columnsWhite[i+1] == 0) {
                    //unbalanced piece
                    score += splitValue;
                } 
                if (columnsBlack[i] == 1 && columnsBlack[i-1] == 0  && columnsBlack[i+1] == 0) {
                    //unbalanced piece
                    score -= splitValue;
                }                  
            }
        }
         */

        int balanceScore = Math.max(blackLeftPieces, blackRightPieces) - Math.min(blackLeftPieces, blackRightPieces)
                - (Math.max(whiteLeftPieces, whiteRightPieces) - Math.min(whiteLeftPieces, whiteRightPieces));

        
        int coherenceScore = whiteCoherence - blackCoherence;
        score += whites - blacks + 3 * (whiteKings - blackKings);
        returnScore += evalWeights.piece * score + evalWeights.tempi * totalTempi + evalWeights.balance * balanceScore + evalWeights.coherence * coherenceScore + evalWeights.mobility * state.getMoves().size();
        return returnScore;
    }

    private int pieceCoherence(int colour, int[] pieces, int i, int column, int tempi) {

        int coherence = 0;
        boolean topbottom = (tempi == 0 || tempi == 9);
        boolean side = (column == 0 && (tempi % 2 == 1)) || (column == 4 && (tempi % 2 == 0));

        if (!topbottom && !side) {
            if (pieces[i - 6] == colour) { coherence++; }
            if (pieces[i - 5] == colour) { coherence++; }
            if (pieces[i + 4] == colour) { coherence++; }
            if (pieces[i + 5] == colour) { coherence++; }
        } else if (!topbottom && side) {
            coherence += 2;
            if (pieces[i - 5] == colour) { coherence++; }
            if (pieces[i + 5] == colour) { coherence++; }
        } else if (topbottom && !side) {
            coherence += 2;
            if (tempi == 0) { //top
                if (pieces[i + 5] == colour) { coherence++; }
                if (pieces[i + 6] == colour) { coherence++; }
            } else { //bottom
                if (pieces[i - 5] == colour) { coherence++; }
                if (pieces[i - 6] == colour) { coherence++; }
            }
        } else if (topbottom && side) {
            coherence += 3;
            if (tempi == 0) { //top right
                if (pieces[i + 5] == colour) { coherence++; }
            } else { // bottom left
                if (pieces[i - 5] == colour) { coherence++; }
            }
        }
        return coherence;
        
    }
    
    private boolean isQuiet(DraughtsState state) {
        List<Move> moves = state.getMoves();

        if (!moves.isEmpty()) {
            return !moves.get(0).isCapture();
        }
        return true;
    }
}
