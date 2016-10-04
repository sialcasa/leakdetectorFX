package de.saxsys.leakscanner.leakdetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.leakscanner.LeakedItem;
import de.saxsys.leakscanner.WeakRef;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
        
        LeakedItem returned = leakDetector.insertWeakRefIntoMap(labelRef);
        assertEquals(labelRef, returned.getNode());
        
    }
    
    
    /**
     * Tests the getLeakedItemFromMap()
     * 
     */
    @Test
    public void getLeakedItemFromMapTest() {
        Label label = new Label("Label");
        WeakRef<Node> labelRef = new WeakRef<Node>(label);
        
        LeakedItem child = new LeakedItem(labelRef);
        leakDetector.getLeakedObjects().put(labelRef, child);
        
        LeakedItem treeItemhBox = leakDetector.getLeakedItemFromMap(new WeakRef<Node>(label));
        assertEquals(child, treeItemhBox);
    }
    

    /**
     * Tests the insertIntoMap(Node)
     * 
     */
    @Test
    public void insertIntoMapTest() {
        Label label = new Label("New Node");
        LeakedItem labelItem = leakDetector.insertIntoMap(label);
        
        if(labelItem == null) {
            fail("Returned node is null");
        }
                
        assertEquals(label, labelItem.getNode().get());
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

        leakDetector.addParentOfNode(labelRef, new LeakedItem(labelRef));
        LeakedItem hboxTI = leakDetector.getLeakedObjects().get(new WeakRef<Node>(hbox)); 
        assertEquals(hbox, hboxTI.getNode().get());
    }
    

    /**
     * Tests the whole leakDetector
     * 
     */
    @Test
    public void leakDetectorTest(){
        // in our init method we created a scene with some elements
        // now we delete them from the scene graph and we'll get leaked suspicious nodes
        root.getChildren().clear();
        leakDetector.getLeakedObjects().keySet().stream().forEach(e -> { System.out.println(e.get()); });
    }
}
