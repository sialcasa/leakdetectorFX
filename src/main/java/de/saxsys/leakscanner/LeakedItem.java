package de.saxsys.leakscanner;

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
   
    public ObservableList<LeakedItem> getChildren() { 
        return children;
    }
        
    public WeakRef<Node> getNode() {
        return node;
    }

    public StringProperty nodeProperty() { 
        if(node.get() == null) {
            return null; 
        } else {
            return new SimpleStringProperty(node.get().toString()); 
        }
    }
    
    public IntegerProperty hashCodeProperty() { 
        if(node.get() == null) {
            return null; 
        } else {
            return new SimpleIntegerProperty(node.get().hashCode()); 
        }
    }
}
