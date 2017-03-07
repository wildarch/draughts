package nl.tue.s2id90.group19;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Daan de Graaf
 */
public class EvaluationLearner {
    EvaluationWeights[] population;
    private int playerSearchDepth;
    
    // The likelyhood that a feature will mutate under normal circumstances
    public final static double MUTATION_COEFFICIENT = 0.05;
    
    // The likelyhood that a feature will mutate 
    // of a child where father.equals(mother)
    public final static double INCEST_MUTATION_COEFFICIENT = 0.5;
    
    public EvaluationLearner(int populationSize, int playerSearchDepth) {
        population = new EvaluationWeights[populationSize];
        this.playerSearchDepth = playerSearchDepth;
        
        // Randomly generate weights
        for (int i = 0; i < population.length; i++) {
            population[i] = EvaluationWeights.randomWeights();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Starting learning process");
        EvaluationLearner learner = new EvaluationLearner(50, 4);
        System.out.println("Generation 0");
        try {
            for (int i = 0; i < 10; i++) {
                long time = System.currentTimeMillis();
                learner.evolve();
                long seconds = (System.currentTimeMillis() - time) / 1000;
                System.out.println("Generation "+(i+1)+" ("+seconds+"s)");
            }
        }
        catch (InterruptedException e) {
            System.out.println("The threadpool was interrupted!");
        }
        System.out.println("Weights from last generation:");
        for (EvaluationWeights w : learner.population) {
            System.out.println(w);
            System.out.println();
        }
    }
    
    public void evolve() throws InterruptedException {
        Match[] matches = randomPair(population);
        getResults(matches);
        
        int[] indices = shuffle(range(0, matches.length));
        EvaluationWeights[] survivors = new EvaluationWeights[matches.length];
        for (int i : indices) {
            if(i < 2)
                survivors[i] = matches[i].getLoser();
            else
                survivors[i] = matches[i].getWinner();
        }
        
        EvaluationWeights[] newPopulation = mate(survivors, population.length);
        population = newPopulation;
    }
    
    static int[] range(int start, int end) {
        int[] values = new int[end-start];
        for (int i = start; i < end; i++) {
            values[i-start] = i;
        }
        return values;
    }
    
    /**
     * Shuffles an array (in place!).
     * @param array Array to shuffle
     * @return Reference to array (for chaining)
     */
    static int[] shuffle(int[] array) {
        int n = array.length;
        for (int i = 0; i < array.length; i++) {
            // Get a random index of the array past i.
            int random = i + (int) (Math.random() * (n - i));
            // Swap the random element with the present element.
            int randomElement = array[random];
            array[random] = array[i];
            array[i] = randomElement;
        }
        return array;
    }
    
    /**
     * Gets the results from the given matches using a thread pool.
     * Results can be accessed using the getResult method of the corresponding match
     * @param matches
     */
    private void getResults(Match[] matches) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for(Match m : matches) {
            pool.execute(m);
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.MINUTES);
    }
    
    /**
     * Randomly pairs an even number of EvaluationWeightss.
     * @param population
     * @return The matches resulting from the pairing
     */
    private Match[] randomPair(EvaluationWeights[] population) {
        int nMatches = population.length/2;
        Match[] matches = new Match[nMatches];
        int[] opponents = shuffle(range(nMatches, population.length));
        for(int i = 0; i < nMatches; i++) {
            matches[i] = new Match(population[i], population[opponents[i]], playerSearchDepth);
        }
        return matches;
    }

    private EvaluationWeights[] mate(
            EvaluationWeights[] survivors, int offspringSize) {
        EvaluationWeights[] offSpring = new EvaluationWeights[offspringSize];
        Random random = new Random();
        for(int i = 0; i < offspringSize; i++) {
            EvaluationWeights father = randElem(survivors, random);
            EvaluationWeights mother = randElem(survivors, random);
            EvaluationWeights child = crossover(father, mother);
            if(father == mother) {
                mutate(child, random, INCEST_MUTATION_COEFFICIENT);
            }
            else {
                child = mutate(child, random, MUTATION_COEFFICIENT);
            }
            offSpring[i] = child;
        }
        return offSpring;
    }
    
    private<T> T randElem(T[] array, Random r) {
        return array[r.nextInt(array.length)];
    }

    private EvaluationWeights crossover(
            EvaluationWeights father, EvaluationWeights mother) {
        int[] fWeights = father.getWeights();
        int[] mWeights = mother.getWeights();
        int[] weights = new int[fWeights.length];
        for(int i = 0; i < weights.length; i++) {
            weights[i] = Math.random() < 0.5 ? fWeights[i] : mWeights[i];
        }
        return new EvaluationWeights(weights);
    }

    private EvaluationWeights mutate(EvaluationWeights child, Random r, double coefficient) {
        int[] weights = child.getWeights();
        for(int i = 0; i < weights.length; i++) {
            if (Math.random() < coefficient) {
                weights[i] = r.nextInt();
            }
        }
        EvaluationWeights.normalize(weights);
        return new EvaluationWeights(weights);
    }
    
    
    
}
