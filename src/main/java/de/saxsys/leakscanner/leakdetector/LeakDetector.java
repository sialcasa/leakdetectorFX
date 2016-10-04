package de.saxsys.leakscanner.leakdetector;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.saxsys.leakscanner.LeakedItem;
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

public class LeakDetector extends LeakDetectorBase {
    protected final ObservableMap<WeakRef<Node>, LeakedItem> map = FXCollections.observableHashMap();

    public LeakDetector(Scene scene) {
        registerListenerOnSceneRoot(scene);
        rootItem = new LeakedItem(new WeakRef<Node>(scene.getRoot()));

        map.addListener(((MapChangeListener<WeakRef<Node>, LeakedItem>) c -> {
            if (c.wasAdded()) {
                // add to root if the node has no parent (else append to parent)
                if (getParent(c.getKey()) == null) {
                    appendToRoot(c.getValueAdded());
                } else {
                    addParentOfNode(c.getKey(), c.getValueAdded());
                }
            }

            if (c.wasRemoved()) {
                // remove also from LeakedItem hierarchy if node was removed from map
                Platform.runLater(() -> {
                    rootItem.getChildren().remove(c.getValueRemoved());
                });
            }
        }));
    }

    /**
     * The given LeakedItem will be added as a children of the root LeakedItem.
     *
     * @param leakedItemChild
     */
    protected void appendToRoot(LeakedItem leakedItemChild) {
        rootItem.getChildren().add(leakedItemChild);
    }

    /**
     * If node has a parent it will be added to the map and the parent LeakedItem gets the node as a child.
     *
     * @param node
     * @param leakedItemChild
     */
    protected void addParentOfNode(WeakRef<Node> node, LeakedItem leakedItemChild) {
        Parent p = getParent(node);
        if (p != null) {
            // has Parent
            LeakedItem leakedItemParent = getLeakedItemFromMap(new WeakRef<Node>(p));
            if (leakedItemParent == null) {
                // parent not in map, create LeakedItem
                leakedItemParent = insertIntoMap(p);
            }
            // add child
            leakedItemParent.getChildren().add(leakedItemChild);
        }
    }

    /**
     * Adds the node (given as a WeakRef) to the map.
     *
     * @param weakRef
     * @return LeakedItem from this node
     */
    protected LeakedItem insertWeakRefIntoMap(WeakRef<Node> weakRef) {
        LeakedItem child = new LeakedItem(weakRef);
        map.put(weakRef, child);

        return child;
    }

    /**
     * Covers the node into a WeakRef and adds it to the map.
     *
     * @param node
     * @return LeakedItem from this node
     */
    protected LeakedItem insertIntoMap(Node node) {
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
     * Returns the LeakedItem of the given node.
     *
     * @param node
     * @return Parent of node
     */
    protected LeakedItem getLeakedItemFromMap(WeakRef<Node> node) {
        LeakedItem treeItem = map.get(node);
        return treeItem;
    }

    /**
     * Check the scene property of a parent for getting null. If it gets null and the Object retains in Memory
     * (leakedObject List), it is likely a leak.
     *
     * @param parent
     */
    protected void registerLeakDetection(Node parent) {
        Optional<WeakRef<Node>> parentReference =
                map.keySet().stream().filter(element -> element.get() == parent).findFirst();

        WeakRef<Node> weakRef = new WeakRef<Node>(parent);
        ChangeListener<Scene> sceneListener = (observable, oldValue, newValue) -> {
            if (newValue == null) {
                if (!parentReference.isPresent()) {
                    if (map.get(weakRef) == null) {
                        insertWeakRefIntoMap(weakRef);
                    }
                }
            } else {
                if (parentReference.isPresent()) {
                    map.remove(parentReference.get());
                }
            }
        };

        listeners.add(sceneListener);
        parent.sceneProperty().addListener(new WeakChangeListener<>(sceneListener));
    }

    /**
     * We check whether a WeakRef got cleared and remove it if yes. In addition we continue the GC.
     *
     * @return whether the GC penetration should get continued
     */
    public boolean checkLeaksAndContinueGC() {
        synchronized (this) {
            List<WeakRef<Node>> collect =
                    map.keySet().stream().filter(ref -> (ref.get() == null)).collect(Collectors.toList());

            map.keySet().removeAll(collect);
        }
        // Continue GC
        return true;
    }

    /*
     * GETTER SETTER
     */

    public final ObservableMap<WeakRef<Node>, LeakedItem> getLeakedObjects() {
        return map;
    }
}
