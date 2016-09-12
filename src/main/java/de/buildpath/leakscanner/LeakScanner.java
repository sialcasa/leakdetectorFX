package de.buildpath.leakscanner;

import de.buildpath.leakscanner.gui.Algorithm;
import de.buildpath.leakscanner.gui.LeakScannerStageFactory;
import de.buildpath.leakscanner.leakdetector.LeakDetector;
import de.buildpath.leakscanner.leakdetector.LeakDetectorBase;
import de.buildpath.leakscanner.leakdetector.LeakDetectorMax;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;

public class LeakScanner {
    LeakDetectorBase leakDetector;
    
    public LeakScanner(Scene observableScene, long garbageCollectionIntervalMillis, Algorithm algorithm) {
        if(algorithm == Algorithm.MAX) {
            leakDetector = new LeakDetectorMax(observableScene);
        } else {
            leakDetector = new LeakDetector(observableScene);
        }
//        leakDetector = new LeakDetector(observableScene);
        
        TreeItem<WeakRef<Node>> root = leakDetector.getRootItem();
        LeakScannerStageFactory.showLeakedObjects(root);
        GCActivity.startGCActivity(leakDetector::checkLeaksAndContinueGC, garbageCollectionIntervalMillis);
    }

    /*public LeakDetector getLeakDetector() {
        return leakDetector;
    }*/
}
