package de.buildpath.leakscanner.leakdetector;

import de.buildpath.leakscanner.WeakRef;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by maximilian.grosser on 20.05.2016.
 */
public class LeakDetectorMax extends LeakDetectorBase {

    private final ReadOnlyListWrapper<WeakRef<Node>> leakedObjects =
            new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    private final List<Object> listeners = new ArrayList<>();
    private boolean buildTreeViewRunning=false;

    public ObservableSet<WeakRef<Node>> getRootParents() {
        return rootParents;
    }

    private TreeItem<WeakRef<Node>> rootItem = new TreeItem<>();

    public void setRootParents(ObservableSet<WeakRef<Node>> rootParents) {
        this.rootParents = rootParents;
    }

    ObservableSet<WeakRef<Node>> rootParents = FXCollections.observableSet();

    public LeakDetectorMax(Scene... scenes) {
        rootParents.addListener((SetChangeListener<WeakRef<Node>>) change -> {
            buildTreeView();
        });
        registerListenerOnSceneRoot(scenes[0]);

        leakedObjects.addListener((observable, oldValue, newValue) -> {
            getRootNodes();
//            System.out.println("leakedObjets: " + leakedObjects.size());
//            System.out.println("rootparents size " + rootParents.size());

            List<WeakRef<Node>> weakRefs = FXCollections.observableArrayList();

            rootParents.removeAll(weakRefs);

            if (leakedObjects.size() == 0) {
                rootParents.clear();
                System.out.println("cleared rootparents size " + rootParents.size());
            }
        });

    }

    // element is added to leakedobjects
    // get root Node of this element

    protected Set<WeakRef<Node>> getRootNodes() {
        rootParents.clear();
        List<WeakRef<Node>> weakRefsList = new ArrayList<>();
        for (WeakRef<Node> WeakRef : leakedObjects) {
            WeakRef<Node> WeakRefTmp = WeakRef;
            WeakRef<Node> topParentWeakRef = WeakRefTmp;
            if (WeakRefTmp != null && WeakRefTmp.get() != null) {
                // get top Parent of current weakRef
                while (null != WeakRefTmp.get().getParent()) {

                    topParentWeakRef = new WeakRef<>(WeakRefTmp.get().getParent());
                    WeakRefTmp = topParentWeakRef;
                }
            }
            weakRefsList.add(topParentWeakRef);
//            checkAndAddToRootNodes(topParentWeakRef);

        }
        for (WeakRef weakRef : weakRefsList) {
            checkAndAddToRootNodes(weakRef);
        }

        return rootParents;

    }

    protected void checkAndAddToRootNodes(WeakRef<Node> WeakRef) {

        final WeakRef<Node> finalTopParentWeakRef = WeakRef;
        if (finalTopParentWeakRef != null) {
            final Optional<WeakRef<Node>> first = rootParents.stream().filter(nodeWeakRef -> {
                if (nodeWeakRef.get() != null) {
                    return nodeWeakRef.get().equals(finalTopParentWeakRef.get());
                } else {
                    return false;
                }

            }).findFirst();
            if (!first.isPresent()) {
                rootParents.add(WeakRef);
//                System.out.println("rootParents.size" + rootParents.size());
            }
        }
    }

    protected void buildTreeView() {

        // clear root treeItem and add current rootparents again
//        if(!buildTreeViewRunning){
//            rootItem.getChildren().clear();
//        }

        List<TreeItem<WeakRef<Node>>> parentRootItems = new ArrayList<>();
//        ObservableSet<WeakRef<Node>> rootParentsa = FXCollections.observableSet(rootParents);
        buildTreeViewRunning=true;
        for (WeakRef<Node> rootParent : rootParents) {
            if (rootParent.get() != null) {
                WeakRef<Node> weakRefParent = rootParent;
                TreeItem<WeakRef<Node>> parentRootItem = new TreeItem<>(weakRefParent);
                //add all child nodes to parentRootItem
                addChildren(parentRootItem);

                // check if parentRootItem is already in the treeview
                Optional<TreeItem<WeakRef<Node>>> firstItem = rootItem.getChildren().stream()
                        .filter(WeakRefTreeItem -> WeakRefTreeItem.getValue().get().equals(rootParent.get()))
                        .findFirst();

                if (!firstItem.isPresent()) {
//                    rootItem.getChildren().add(parentRootItem);
                    parentRootItems.add(parentRootItem);

//                    System.out.println("root item child 0 " + rootItem.getChildren().get(0));
                }
            }
        }
        rootItem.getChildren().addAll(parentRootItems);
//        buildTreeViewRunning=false;

    }

    protected void buildTreeViewNew() {
        // clear root treeItem and add current rootparents again
        rootItem.getChildren().clear();
        List<TreeItem<WeakRef<Node>>> parentRootItems = new ArrayList<>();
        for (WeakRef<Node> rootParent : rootParents) {
            if (rootParent.get() != null) {
                WeakRef<Node> weakRefParent = rootParent;
                TreeItem<WeakRef<Node>> parentRootItem = new TreeItem<>(weakRefParent);
                //add all child nodes to parentRootItem
                addChildren(parentRootItem);

//              rootItem.getChildren().add(parentRootItem);
                parentRootItems.add(parentRootItem);

            }
        }
        rootItem.getChildren().addAll(parentRootItems);

    }

    protected void addChildren(TreeItem<WeakRef<Node>> parent) {

        Parent castedParent = null;
        try {
            castedParent = (Parent) parent.getValue().get();
        } catch (Exception e) {
            System.out.println("cannot cast " + parent.getValue().get());
        }

        if (castedParent != null) {
            try {
                for (Node child : castedParent.getChildrenUnmodifiable()) {

                    WeakRef<Node> childReference = new WeakRef<Node>((Parent) child);
                    TreeItem<WeakRef<Node>> childItem = new TreeItem<>(childReference);
                    parent.getChildren().add(childItem);

                    addChildren(childItem);
                }
            } catch (Exception e) {
                // System.out.println("unable to cast");
            }

        }
    }

    /**
     * Check the scene property of a parent for getting null. If it gets null and the Object retains in Memory
     * (leakedObject List), it is likely a leak.
     *
     * @param parent
     */
    protected void registerLeakDetection(Node parent) {
        Optional<WeakRef<Node>> parentReference =
                leakedObjects.stream().filter(element -> element.get() == parent).findFirst();

        // WeakRef is deklared outside the listener because otherwise it isn't weak
        WeakRef<Node> WeakRef = new WeakRef<Node>(parent);
        ChangeListener<Scene> sceneListener = (observable, oldValue, newValue) -> {
            if (newValue == null) {
                if (!parentReference.isPresent()) {
                    leakedObjects.add(WeakRef);
                }
            } else {
                leakedObjects.remove(parentReference);
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
                    leakedObjects.stream().filter(ref -> (ref.get() == null)).collect(Collectors.toList());
            leakedObjects.removeAll(collect);
        }
        // Continue GC
        return true;
    }

    /*
     * GETTER SETTER
     */

    public final ReadOnlyListProperty<WeakRef<Node>> leakedObjectsProperty() {
        return this.leakedObjects;
    }

    public final ReadOnlyListWrapper<WeakRef<Node>> getLeakedObjects() {
        return leakedObjects;
    }

    public TreeItem<WeakRef<Node>> getRootItem() {
        return rootItem;
    }

}
