package nl.tue.s2id90.group19;


import java.util.Random;


/**
 *
 * @author daan
 */
public class EvaluationWeights {
    //the characteristics of this specific draughtsplayer
    public int piece = 20;
    public int tempi = 1;
    public int balance = 0;
    public int coherence = 0;
    public int mobility = 0;
    
    
    public EvaluationWeights(
            final int piece, 
            final int tempi, 
            final int balance, 
            final int coherence,
            final int mobility) {
        this.piece = piece;
        this.tempi = tempi;
        this.balance = balance;
        this.coherence = coherence;
        this.mobility = mobility;
    }
    
    /**
     * Creates random (normalised) evaluation weights
     * @return Random evaluation weights
     */
    public static EvaluationWeights randomWeights() {
        Random r = new Random();
        int[] values = r.ints(5).toArray();
        normalize(values);
        return new EvaluationWeights(
                values[0], 
                values[1], 
                values[2], 
                values[3], 
                values[4]
        );
    }
    
    public static void normalize(int[] values) {
        int min = Integer.MAX_VALUE;
        for (int v : values) {
            min = Math.min(v, min);
        }
        for (int i = 0; i < values.length; i++) {
            values[i] /= min;
        }
    }
}
