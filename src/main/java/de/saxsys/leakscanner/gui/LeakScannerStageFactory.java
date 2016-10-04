package de.saxsys.leakscanner.gui;

import de.saxsys.leakscanner.leakdetector.LeakDetector;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LeakScannerStageFactory {   
    static Stage stage = new Stage();
    public static void showLeakedObjects(LeakDetector root) {
        stage.setTitle("Potentially Leaked Nodes");

        LeakScannerView leakScannerView = new LeakScannerView(root);     

        Scene scene = new Scene(leakScannerView);
        stage.setScene(scene);
        stage.show();
    }
}
