package de.buildpath.leakscanner;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class LeakedItem {
    private WeakRef<Node> node;
    private ObservableList<LeakedItem> children = FXCollections.observableArrayList();
    
    public LeakedItem(WeakRef<Node> node) {
        this.node = node;
    }
    
//    @Override
//    public int hashCode() {
//        return this.getNode().hashCode();
//    }
//    
//    @Override
//    public boolean equals(Object e) {
//        if (e != null && e instanceof LeakedItem) {
//            return (this.getNode().get() == ((LeakedItem) e).getNode().get());
//        } else {
//            return false;
//        }
//    }
   
    public ObservableList<LeakedItem> getChildren() { 
        return children;
    }
    
    public WeakRef<Node> getNode() {
        return node;
    }

    public StringProperty nodeProperty() { 
        if(node.get() == null) {
            return new SimpleStringProperty("null"); 
        } else {
            return new SimpleStringProperty(node.get().toString()); 
        }
        
    }
    
    public IntegerProperty hcProperty() {
        if (node == null) return new SimpleIntegerProperty(0);
        return new SimpleIntegerProperty(node.hashCode()); 
    }
}
