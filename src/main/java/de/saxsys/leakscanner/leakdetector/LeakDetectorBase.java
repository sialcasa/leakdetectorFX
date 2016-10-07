package de.saxsys.leakscanner.leakdetector;

import java.util.ArrayList;
import java.util.List;

import de.saxsys.leakscanner.LeakedItem;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public abstract class LeakDetectorBase {
    
    protected final List<Object> listeners = new ArrayList<>();
    protected LeakedItem rootItem;
        
    protected abstract void registerLeakDetection(Node parent);
    public abstract boolean checkLeaksAndContinueGC();
    

    protected void registerListenerOnSceneRoot(Scene scene) {
        scene.rootProperty().addListener(new WeakChangeListener<>((observable, oldValue, newValue) -> {
            safeRegisterTracking(newValue);
        }));

        Parent root = scene.getRoot();
        safeRegisterTracking(root);
    }

    
    
    /**
     * The current node and his children (recursive) gets registered for Children Tracking and Memory Leak
     * tracking. In addition a lister for new children is registered, to apply
     * the checks to new children also.
     *
     * @param node
     */
    protected void processNodeAndObserve(Node node) {
        if(isParent(node)) {
            Parent parent = (Parent)node;
            // recursive call every existing child
            final ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                processNodeAndObserve(child);
            }

            // for new child
            final ListChangeListener<Node> childrenListChangeListener = (ListChangeListener<Node>) c -> {
                while (c.next()) {
                    if (c.wasAdded()) {
                        List<? extends Node> added = c.getAddedSubList();
                        for (int i = 0; i < added.size(); i++) {
                            Node child = added.get(i);
                            processNodeAndObserve(child);
                        }
                    }
                }
            };

            listeners.add(childrenListChangeListener);
            children.addListener(new WeakListChangeListener<Node>(childrenListChangeListener));
        }

        registerLeakDetection(node);
    }
    
    
    /*
     * internal Helpers
     */

    protected boolean isParent(Node node) {
        return node instanceof Parent;
    }

    protected void safeRegisterTracking(Parent root) {
        if (root != null) {
            processNodeAndObserve(root);
        }
    }
    
    
    /*
     * GETTER SETTER
     */
    public LeakedItem getRootItem() {
        return rootItem;
    }
}
