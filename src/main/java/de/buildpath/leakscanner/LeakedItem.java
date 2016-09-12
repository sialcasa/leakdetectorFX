package de.buildpath.leakscanner;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class LeakedItem {
    private WeakRef<Node> node;
    private ObservableList<LeakedItem> children = FXCollections.observableArrayList();
    
    public LeakedItem(WeakRef<Node> node) {
        this.node = node;
        
//        children.addListener((ListChangeListener<LeakedItem>) c -> {
//            System.out.println("JO");            
//                    });
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
    
    public void setChildrenList(ObservableList<LeakedItem> l) {
        children = l;
    }
    
    public WeakRef<Node> getNode() {
        return node;
    }

    public StringProperty nodeProperty() { 
        if(node.get() == null) {
            return new SimpleStringProperty("THERE IS NO NODE ..."); 
        } else {
            return new SimpleStringProperty(node.get().toString()); 
        }
        
    }
    
    public IntegerProperty hcProperty() {
        if (node == null) return new SimpleIntegerProperty(0);
        return new SimpleIntegerProperty(node.hashCode()); 
    }
}
