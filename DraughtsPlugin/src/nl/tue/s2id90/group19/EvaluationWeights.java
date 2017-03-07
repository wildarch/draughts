package nl.tue.s2id90.group19;


import java.util.Random;


/**
 *
 * @author daan
 */
public class EvaluationWeights {
    
    public final static int MAX_WEIGHT = 100;
    
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
    
    public EvaluationWeights(int[] weights) {
        this.piece = weights[0];
        this.tempi = weights[1];
        this.balance = weights[2];
        this.coherence = weights[3];
        this.mobility = weights[4];
    }
    
    /**
     * Creates random (normalised) evaluation weights
     * @return Random evaluation weights
     */
    public static EvaluationWeights randomWeights() {
        Random r = new Random();
        int[] values = r.ints(5).toArray();
        normalize(values);
        return new EvaluationWeights(values);
    }
    
    public static void normalize(int[] values) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            int v = values[i];
            v %= MAX_WEIGHT;
            if(v < 0)
                v = -v;
            
            if(v != 0)
                min = Math.min(min, v);
            values[i] = v;
        }
        
        for (int i = 0; i < values.length; i++) {
            values[i] = (values[i]) / min;
        }
    }
    
    public static EvaluationWeights average(EvaluationWeights[] population) {
        int[] weights = new int[population[0].getWeights().length];
        for(EvaluationWeights w : population) {
            int[] values = w.getWeights();
            for(int i = 0; i < weights.length; i++) {
                weights[i] += values[i];
            }
        }
        for(int i = 0; i < weights.length; i++) {
            weights[i] /= population.length;
        }
        return new EvaluationWeights(weights);
    }
    
    public int[] getWeights() {
        return new int[]{piece, tempi, balance, coherence, mobility};
    }
    
    @Override
    public String toString() {
        return "Piece:     "+piece + 
        "\nTempi:     " + tempi +
        "\nBalance:   " + balance +
        "\nCoherence: " + coherence +
        "\nMobility:  " + mobility;
    }
}
