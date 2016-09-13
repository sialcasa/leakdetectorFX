package de.buildpath.leakscanner;

import de.buildpath.leakscanner.gui.LeakScannerStageFactory;
import de.buildpath.leakscanner.leakdetector.LeakDetector;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;

public class LeakScanner {
    LeakDetector leakDetector;
    
    public LeakScanner(Scene observableScene, long garbageCollectionIntervalMillis) {
        leakDetector = new LeakDetector(observableScene);
        
        TreeItem<WeakRef<Node>> root = leakDetector.getRootItem();
        LeakScannerStageFactory.showLeakedObjects(root);
        GCActivity.startGCActivity(leakDetector::checkLeaksAndContinueGC, garbageCollectionIntervalMillis);
    }

    public LeakDetector getLeakDetector() {
        return leakDetector;
    }
}
