package de.buildpath.leakscanner.gui;

import de.buildpath.leakscanner.WeakRef;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class LeakScannerStageFactory {   
    static Stage stage = new Stage();
    public static void showLeakedObjects(TreeItem<WeakRef<Node>> root) {
        stage.setTitle("Potentially Leaked Nodes");

        LeakScannerView leakScannerView = new LeakScannerView(root);     

        Scene scene = new Scene(leakScannerView);
        stage.setScene(scene);
        stage.show();
    }
}
