package de.saxsys.leakscanner.leakdetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    private Label labelPorsche = new Label("Porsche");
    private Label labelVW = new Label("VW");

    @Before
    public void init() {
        root = new VBox();
        scene = new Scene(root);
        hBox = new HBox(labelVW, labelPorsche, new Label("Nissan"));
        root.getChildren().add(hBox);

        leakDetector = new LeakDetector(scene);
        // now we delete them from the scene graph and we'll get leaked suspicious nodes
        root.getChildren().clear();
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

        if (labelItem == null) {
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

        assertEquals(mapSizeBefore + 1, mapSizeAfter);

        leakDetector.insertIntoMap(label);
        int mapSizeAfterTwice = leakDetector.getLeakedObjects().size();
        assertEquals(mapSizeAfter, mapSizeAfterTwice);
    }

    /**
     * Tests if it gets the right parent of a node
     * 
     */
    @Test
    public void getParentOfNodeTest() {
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
    public void addParentOfNodeTest() {
        Label label = new Label();
        HBox hbox = new HBox(label);
        WeakRef<Node> labelRef = new WeakRef<Node>(label);

        leakDetector.addParentOfNode(labelRef, new LeakedItem(labelRef));
        LeakedItem hboxTI = leakDetector.getLeakedObjects().get(new WeakRef<Node>(hbox));
        assertEquals(hbox, hboxTI.getNode().get());
    }

    /**
     * Tests if it works to remove a LeakedItem from the LeakedItem concatenation.
     * 
     */
    @Test
    public void removeFromLeakedItemHierarchyTest() {
        LeakedItem leakedItemHbox = (LeakedItem) leakDetector.getRootItem().getChildren().get(0);
        assertEquals(3, leakedItemHbox.getChildren().size());

        LeakedItem leakedItemLabelPorsche = leakedItemHbox.getChildren().get(1);
        leakDetector.removeFromLeakedItemHierarchy(leakedItemLabelPorsche);

        assertEquals(2, leakedItemHbox.getChildren().size());
    }

    /**
     * Tests if it works to add node to whitelist.
     * 
     */
    @Test
    public void addToWhiteListTest() {
        LeakedItem leakedItemHbox = (LeakedItem) leakDetector.getRootItem().getChildren().get(0);
        leakDetector.addToWhiteList(leakedItemHbox);

        // first check: sucessfully added to whitelist?
        assertTrue(leakDetector.getWhiteList().contains(leakedItemHbox.getNode()));

        // second check: sucessfully removed from map?
        assertFalse(leakDetector.getLeakedObjects().containsKey(weakRef(hBox)));
    }

    /**
     * Tests if it works to ignore node which is on our whitelist.
     * 
     */
    @Test
    public void processNodeOnWhitelistTest() {
        root.getChildren().add(hBox);
        root.getChildren().clear();
        assertEquals(4, leakDetector.getLeakedObjects().size());

        leakDetector.getLeakedObjects().clear();
        leakDetector.addToWhiteList(new LeakedItem(weakRef(hBox)));
        root.getChildren().add(hBox);
        root.getChildren().clear();
        assertEquals(0, leakDetector.getLeakedObjects().size());
    }

    /**
     * Tests if it works to check if the node and its parents are on whitelist.
     * 
     */
    @Test
    public void nodeOnWhitelistTest() {
        leakDetector.addToWhiteList(new LeakedItem(weakRef(hBox)));
        assertTrue(leakDetector.nodeOnWhitelist(weakRef(hBox)));
        assertTrue(leakDetector.nodeOnWhitelist(weakRef(labelPorsche)));
    }

    /**
     * Helper method to get a new WeakRef instance.
     * 
     */
    public WeakRef<Node> weakRef(Node node) {
        return new WeakRef<Node>(node);
    }

    /**
     * Tests the whole leakDetector
     * 
     */
    @Test
    public void leakDetectorTest() {
        // leakDetector.getLeakedObjects().keySet().stream().forEach(e -> {
        // System.out.println(e.get());
        // });
    }
}
