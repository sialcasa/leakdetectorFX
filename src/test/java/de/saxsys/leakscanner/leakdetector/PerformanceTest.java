package de.saxsys.leakscanner.leakdetector;
//package de.buildpath.leakscanner.leakdetector;
//
//import de.buildpath.leakscanner.WeakRef;
//import de.saxsys.javafx.test.JfxRunner;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.control.TreeItem;
//import javafx.scene.layout.AnchorPane;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by maximilian.grosser on 24.08.2016.
// */
//@RunWith(JfxRunner.class)
//public class PerformanceTest {
//    final static int HEIGHT =300;
//    final static int WIDTH = 100;
//
//    static Scene scene;
//    static AnchorPane paneTmp;
//    static AnchorPane paneTmp2;
//    
//    @BeforeClass
//    public static void setUp() {
//        final AnchorPane rootPane= new AnchorPane();
//        paneTmp=rootPane;
//
//        //create node tree
//        for (int i=0;i<=HEIGHT;i++){
//            AnchorPane childPane = new AnchorPane();
//            for(int k=0; k <= WIDTH; k++) {
//                childPane= new AnchorPane();
//                paneTmp.getChildren().add(childPane);
//            }
//            paneTmp=childPane;
//        }
//        
//        scene = new Scene(rootPane);
//    }
//    
//    @Test
//    public void performanceTest2(){
//        //add to leakedObjects
//        LeakDetectorMax leakDetector=new LeakDetectorMax(scene);
//        WeakRef<Node> weakRef =new WeakRef<Node>(paneTmp);
//        leakDetector.getLeakedObjects().add(weakRef);
//
//        TreeItem<WeakRef<Node>> treeItem= leakDetector.getRootItem().getChildren().get(0);
//        WeakRef<Node> weakRef1= (WeakRef<Node>)treeItem.getValue();
//        assertEquals(scene.getRoot(),weakRef1.get());
//    }
//
//    @Test
//    public void performanceTest(){
//        //add to leakedObjects
//        LeakDetector leakDetector=new LeakDetector(scene);
//        WeakRef<Node> weakRef =new WeakRef<Node>(paneTmp);
//        leakDetector.getLeakedObjects().put(weakRef,new TreeItem<WeakRef<Node>>(weakRef));
//        
//        TreeItem<WeakRef<Node>> treeItem= leakDetector.getRootItem().getChildren().get(0);
//        WeakRef<Node> weakRef1= (WeakRef<Node>)treeItem.getValue();
//        assertEquals(scene.getRoot(),weakRef1.get());
//    }
//}
