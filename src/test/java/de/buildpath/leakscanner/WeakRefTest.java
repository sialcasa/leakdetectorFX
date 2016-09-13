package de.buildpath.leakscanner;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.saxsys.javafx.test.JfxRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;

@RunWith(JfxRunner.class)
public class WeakRefTest {
    private final ObservableMap<WeakRef<Node>, TreeItem<WeakRef<Node>>> leakedMap = FXCollections.observableHashMap();
    
    @Test
    public void addTest() {
        Label label = new Label("Label 1");
        Label label2 = new Label("Label 2");
        
        /* add label with 2 different WeakRefs */
        WeakRef<Node> labelRef = new WeakRef<Node>(label);
        leakedMap.put(labelRef, new TreeItem<WeakRef<Node>>(labelRef));
        
        WeakRef<Node> anotherLabelRef = new WeakRef<Node>(label);
        leakedMap.put(anotherLabelRef, new TreeItem<WeakRef<Node>>(anotherLabelRef));
        assertEquals(1, leakedMap.size());
        
        /* add label2 */
        WeakRef<Node> label2Ref = new WeakRef<Node>(label2);
        leakedMap.put(label2Ref, new TreeItem<WeakRef<Node>>(label2Ref));
        assertEquals(2, leakedMap.size());
    }
    
    @Test
    public void getTest() {
        TextField textField = new TextField();
        Label label = new Label("Label");
        
        WeakRef<Node> textFieldRef = new WeakRef<Node>(textField);
        leakedMap.put(textFieldRef, new TreeItem<WeakRef<Node>>(textFieldRef));
        
        WeakRef<Node> labelRef = new WeakRef<Node>(label);
        leakedMap.put(labelRef, new TreeItem<WeakRef<Node>>(labelRef));
        
        TreeItem<WeakRef<Node>> treeItem = leakedMap.get(new WeakRef<Node>(label));
        assertEquals(label, treeItem.getValue().get());
        // tests the same thing - doppelt hält besser! :D
        assertSame(treeItem.getValue(), labelRef);
    }
}
