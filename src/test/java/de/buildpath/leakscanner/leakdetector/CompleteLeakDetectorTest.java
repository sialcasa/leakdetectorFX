package de.buildpath.leakscanner.leakdetector;

import java.lang.ref.WeakReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.buildpath.leakscanner.Car;
import de.buildpath.leakscanner.GCActivity;
import de.buildpath.leakscanner.LeakingView;
import de.buildpath.leakscanner.WeakRef;
import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


@RunWith(JfxRunner.class)
public class CompleteLeakDetectorTest {
    // Long living model element
    private final Car dataModel = new Car();
    
    // While the WeakReference contains the reference to the view, it's the
    // proof that the view retains in memory
    private WeakReference<LeakingView> weakReference;
    
    private LeakDetector leakDetector;
    
    @Before
    public void init(){
        // Create the leaking view and the WeakReference
        LeakingView view = new LeakingView(dataModel);
        
        // While the WeakReference contains the reference to the view, it's the
        // proof that the view retains in memory
        weakReference = new WeakReference<LeakingView>(view);

        TextField commandTextField = new TextField();
        commandTextField.textProperty().bindBidirectional(dataModel.name);

        // UI
        VBox root = new VBox(view, commandTextField);

        Scene scene = new Scene(root);
//        new LeakScanner(scene, 100L);
        leakDetector = new LeakDetector(scene);
        GCActivity.startGCActivity(leakDetector::checkLeaksAndContinueGC, 10L);
    }
    
    /**
     * Tests the whole leakDetector
     * 
     */
    @Test
    @TestInJfxThread
    public void leakDetectorTest(){
        LeakingView view = weakReference.get();
        System.out.println("=> REMOVE");
        view.car.name.set("remove");
        System.out.println("SIZE "+leakDetector.getLeakedObjects().size());
        
        System.out.println("=> KILL");
        view.car.name.set("kill");
        System.out.println("SIZE "+leakDetector.getLeakedObjects().size());
        
        for(WeakRef<Node> w : leakDetector.getLeakedObjects().keySet()) {
            System.out.println("--> "+w.get());
        }
    }
}
