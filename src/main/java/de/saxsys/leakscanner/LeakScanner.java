package de.saxsys.leakscanner;

import de.saxsys.leakscanner.gui.LeakScannerStageFactory;
import de.saxsys.leakscanner.leakdetector.LeakDetector;
import javafx.scene.Scene;

public class LeakScanner {
    LeakDetector leakDetector;
    
    public LeakScanner(Scene observableScene, long garbageCollectionIntervalMillis) {
        leakDetector = new LeakDetector(observableScene);
        
        
        LeakScannerStageFactory.showLeakedObjects(leakDetector);
        GCActivity.startGCActivity(leakDetector::checkLeaksAndContinueGC, garbageCollectionIntervalMillis);
    }

    public LeakDetector getLeakDetector() {
        return leakDetector;
    }
}
