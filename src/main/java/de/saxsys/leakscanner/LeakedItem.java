package de.saxsys.leakscanner;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;

public class LeakedItem {
    private WeakRef<Node> node;
    private ObservableList<LeakedItem> children = FXCollections.observableArrayList();
    private LeakedItem parent = null;
    private WeakRef<Parent> oldSceneParent; 

    public LeakedItem(WeakRef<Node> node) {
        this.node = node;

        children.addListener((ListChangeListener<LeakedItem>) change -> {
            while (change.next()) {
                for (LeakedItem newLeakedItem : change.getAddedSubList()) {
                    newLeakedItem.setParent(LeakedItem.this);
                }
            }
        });
    }
    
    public ObservableList<LeakedItem> getChildren() {
        return children;
    }

    public WeakRef<Node> getNode() {
        return node;
    }
    
    public LeakedItem getParent() {
        return parent;
    }

    public void setParent(LeakedItem parent) {
        this.parent = parent;
    }
    
    public WeakRef<Parent> getOldSceneParent() {
        return oldSceneParent;
    }

    public void setOldSceneParent(WeakRef<Parent> weakRef) {
        this.oldSceneParent = weakRef;
    }

    public StringProperty nodeProperty() {
        if (node.get() == null) {
            return null;
        } else {
            return new SimpleStringProperty(node.get().toString());
        }
    }

    public IntegerProperty hashCodeProperty() {
        if (node.get() == null) {
            return null;
        } else {
            return new SimpleIntegerProperty(node.get().hashCode());
        }
    }
    
    public StringProperty oldParentProperty() {
        if (oldSceneParent == null || oldSceneParent.get()==null) {
            return null;
        } else {
            return new SimpleStringProperty(getOldSceneParent().get().toString());
        }
    }
}
