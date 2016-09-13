package de.saxsys.leakscanner.leakdetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.leakscanner.WeakRef;
import de.saxsys.leakscanner.leakdetector.LeakDetector;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


@RunWith(JfxRunner.class)
public class LeakDetectorTest {
    private LeakDetector leakDetector;
    private Scene scene;
    private VBox root;
    private HBox hBox;
    
    @Before
    public void init(){
        root = new VBox();
        scene = new Scene(root);
        hBox = new HBox(new Label("VW"), new Label("Porsche"), new Label("Nissan"));
        root.getChildren().add(hBox);

        leakDetector = new LeakDetector(scene);
    }
    
    
    /**
     * Tests if it works to add a node to with map.put()
     * 
     */
    @Test
    public void insertWeakRefIntoMapTest() {
        Label label = new Label("Label");
        WeakRef<Node> labelRef = new WeakRef<Node>(label);
        
        TreeItem<WeakRef<Node>> returned = leakDetector.insertWeakRefIntoMap(labelRef);
        assertEquals(labelRef, returned.getValue());
        
    }
    
    
    /**
     * Tests the getTreeItemFromMap()
     * 
     */
    @Test
    public void getTreeItemFromMapTest() {
        Label label = new Label("Label");
        WeakRef<Node> labelRef = new WeakRef<Node>(label);
        
        TreeItem<WeakRef<Node>> child = new TreeItem<WeakRef<Node>>(labelRef);
        leakDetector.map.put(labelRef, child);
        
        TreeItem<WeakRef<Node>> treeItemhBox = leakDetector.getTreeItemFromMap(new WeakRef<Node>(label));
        assertEquals(child, treeItemhBox);
    }
    

    /**
     * Tests the insertIntoMap(Node)
     * 
     */
    @Test
    public void insertIntoMapTest() {
        Label label = new Label("New Node");
        TreeItem<WeakRef<Node>> labelItem = leakDetector.insertIntoMap(label);
        
        if(labelItem == null) {
            fail("Returned node is null");
        }
                
        assertEquals(label, labelItem.getValue().get());
    }
    
    
    /**
     * Tests if it works to add a new node which doesn't exists in the map to it.
     * 
     */
    @Test
    public void addNodeTwiceToMapTest() {
        Label label = new Label("New Node");
        int mapSizeBefore = leakDetector.getLeakedObjects().size();
        leakDetector.insertIntoMap(label);
        int mapSizeAfter = leakDetector.getLeakedObjects().size();
        
        assertEquals(mapSizeBefore+1, mapSizeAfter);
        
        leakDetector.insertIntoMap(label);
        int mapSizeAfterTwice = leakDetector.getLeakedObjects().size();
        assertEquals(mapSizeAfter, mapSizeAfterTwice);
    }
    
    
    /**
     * Tests if it gets the right parent of a node
     * 
     */
    @Test
    public void getParentOfNodeTest(){
        Label label = new Label();
        HBox hbox = new HBox(label);

        Parent hboxRef = leakDetector.getParent(new WeakRef<Node>(label));
        assertEquals(hbox, hboxRef);
    }
    
    
    /**
     * Tests if it works to add parent to leakingMap
     * 
     */
    @Test
    public void addParentOfNodeTest(){
        Label label = new Label();
        HBox hbox = new HBox(label);
        WeakRef<Node> labelRef = new WeakRef<Node>(label);

        leakDetector.addParentOfNode(labelRef, new TreeItem<WeakRef<Node>>(labelRef));
        TreeItem<WeakRef<Node>> hboxTI = leakDetector.getLeakedObjects().get(new WeakRef<Node>(hbox)); 
        assertEquals(hbox, hboxTI.getValue().get());
    }
    

    /**
     * Tests the whole leakDetector
     * 
     */
    @Test
    public void leakDetectorTest(){
        // in our init method we created a scene with some elements
        // now we delete them from the scene graph and we'll get a leak
        root.getChildren().clear();
        leakDetector.getLeakedObjects().keySet().stream().forEach(e -> { System.out.println(e.get()); });
    }
}
