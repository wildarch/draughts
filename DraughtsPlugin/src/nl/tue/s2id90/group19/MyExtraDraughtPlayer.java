package nl.tue.s2id90.group19;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class MyExtraDraughtPlayer  extends MyDraughtsPlayer {
    
    public MyExtraDraughtPlayer(int maxSearchDepth) {
        super(maxSearchDepth);
        setup();
    }

    MyExtraDraughtPlayer(int baseSearchDepth, boolean enableDeepening) {
        super(baseSearchDepth, enableDeepening);
        setup();
    }

    private void setup() {
        scoreValue = 43;
        winValue = 6472;
        tempiValue = 3;
        columnValue = 0;
        splitValue = 0;
    }
    
}
