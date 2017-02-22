package nl.tue.s2id90.group19;

import nl.tue.s2id90.group19.samples.UninformedPlayer;
import nl.tue.s2id90.group19.samples.OptimisticPlayer;
import nl.tue.s2id90.group19.samples.BuggyPlayer;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;



/**
 *
 * @author huub
 */
@PluginImplementation
public class MyDraughtsPlugin extends DraughtsPlayerProvider implements DraughtsPlugin {
    public MyDraughtsPlugin() {
        // make one or more players available to the AICompetition tool
        // During the final competition you should make only your 
        // best player available. For testing it might be handy
        // to make more than one player available.
        super(new MyDraughtsPlayer(4, true),
                new MyExtraDraughtPlayer(4, true),
                new UninformedPlayer(5),
                new OptimisticPlayer(),
                new BuggyPlayer(5)
        );
    }
}
