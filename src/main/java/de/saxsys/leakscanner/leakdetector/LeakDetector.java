package de.saxsys.leakscanner.leakdetector;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.saxsys.leakscanner.WeakRef;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;


public class LeakDetector extends LeakDetectorBase {
    protected final ObservableMap<WeakRef<Node>, TreeItem<WeakRef<Node>>> map = FXCollections.observableHashMap();

    public LeakDetector(Scene... scenes) {
        registerListenerOnSceneRoot(scenes[0]);

        map.addListener(((MapChangeListener<WeakRef<Node>, TreeItem<WeakRef<Node>>>) c -> {
          if (c.wasAdded()) {
              // add TreeItem to root if the node has no parent (else append to TreeItem of parent) 
              if(getParent(c.getKey()) == null) {
                  appendTreeItemToRoot(c.getValueAdded());
              } else {
                  addParentOfNode(c.getKey(), c.getValueAdded());
              }
          }
          
          if(c.wasRemoved()) {
              // remove also TreeItem if node was removed from map
              Platform.runLater(() -> {
                  rootItem.getChildren().remove(c.getValueRemoved());
              }); 
          }
        }));
    }
    
    
    /**
     * The given treeItemChild will be added the the children of the root treeItem.
     *
     * @param treeItemChild
     */
    protected void appendTreeItemToRoot(TreeItem<WeakRef<Node>> treeItemChild) {
        rootItem.getChildren().add(treeItemChild);
    }

    
    /**
     * If node has a parent the parent will be added to the map and the parent treeItem gets 
     * treeItemChild as a child.
     *
     * @param node
     * @param treeItemChild
     */
    protected void addParentOfNode(WeakRef<Node> node, TreeItem<WeakRef<Node>> treeItemChild) {
        Parent p = getParent(node);
        if(p != null) {
            // has Parent
            TreeItem<WeakRef<Node>> treeItemParent = getTreeItemFromMap(new WeakRef<Node>(p));
            if(treeItemParent == null) {
                // parent not in map, create TreeItem
                treeItemParent = insertIntoMap(p);
            }
            // add child
            treeItemParent.getChildren().add(treeItemChild);
        }
    }
    
    
    /**
     * Adds the node (given as a WeakRef) to the map.
     *
     * @param weakRef
     * @return TreeItem from this node
     */
    protected TreeItem<WeakRef<Node>> insertWeakRefIntoMap(WeakRef<Node> weakRef) {
        TreeItem<WeakRef<Node>> child = new TreeItem<WeakRef<Node>>(weakRef);
        map.put(weakRef, child);
        
        return child;
    }
    
    
    /**
     * Covers the node into a WeakRef and adds it to the map.
     *
     * @param node
     * @return TreeItem from this node
     */
    protected TreeItem<WeakRef<Node>> insertIntoMap(Node node) {
        return insertWeakRefIntoMap(new WeakRef<Node>(node));
    }

    
    /**
     * Returns the parent of node.
     *
     * @param node
     * @return Parent of node
     */
    protected Parent getParent(WeakRef<Node> node) {
        return node.get().getParent();
    }


    /**
     * Returns the treeItem of the given node.
     *
     * @param node
     * @return Parent of node
     */
    protected TreeItem<WeakRef<Node>> getTreeItemFromMap(WeakRef<Node> node) {
        TreeItem<WeakRef<Node>> treeItem = map.get(node);
        return treeItem;
    }

    
    /**
     * Check the scene property of a parent for getting null. If it gets null
     * and the Object retains in Memory (leakedObject List), it is likely a
     * leak.
     *
     * @param parent
     */
    protected void registerLeakDetection(Node parent) {
        Optional<WeakRef<Node>> parentReference = map.keySet().stream()
                .filter(element -> element.get() == parent)
                .findFirst();
        
        WeakRef<Node> weakRef = new WeakRef<Node>(parent);
        ChangeListener<Scene> sceneListener = (observable, oldValue, newValue) -> {
            if (newValue == null) {
                if (!parentReference.isPresent()) {
                    if(map.get(weakRef) == null) {
                        insertWeakRefIntoMap(weakRef);
                    }
                }
            } else {
                if(parentReference.isPresent()) {
                    map.remove(parentReference.get());
                }
            }
        };

        listeners.add(sceneListener);
        parent.sceneProperty().addListener(new WeakChangeListener<>(sceneListener));
    }

    
    /**
     * We check whether a WeakRef got cleared and remove it if yes. In
     * addition we continue the GC.
     *
     * @return whether the GC penetration should get continued
     */
    public boolean checkLeaksAndContinueGC() {
        synchronized (this) {
            List<WeakRef<Node>> collect = map.keySet().stream()
                    .filter(ref -> (ref.get() == null))
                    .collect(Collectors.toList());
            
            map.keySet().removeAll(collect);
        }
        // Continue GC
        return true;
    }

    
    /*
     * GETTER SETTER
     */

    public final ObservableMap<WeakRef<Node>, TreeItem<WeakRef<Node>>> getLeakedObjects() {
        return map;
    }
}
