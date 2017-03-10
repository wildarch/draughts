package nl.tue.s2id90.group19;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;



/**
 *
 * @author Daan de Graaf & Yoeri Poels
 */
@PluginImplementation
public class MyDraughtsPlugin extends DraughtsPlayerProvider implements DraughtsPlugin {
    public MyDraughtsPlugin() {
        // make one or more players available to the AICompetition tool
        // During the final competition you should make only your 
        // best player available. For testing it might be handy
        // to make more than one player available.
        super(  new Sloeber(4, new EvaluationWeights(60, 6, 11, 1, 3), true)
                , new MyExtraDraughtPlayer(4, new EvaluationWeights(60, 6, 11, 1, 3), true)
        );
    }
}
