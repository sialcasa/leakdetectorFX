package de.saxsys.leakscanner.gui;

import de.saxsys.leakscanner.WeakRef;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;

class WeakRefCell extends TreeCell<WeakRef<Node>> {
    String newText;

    @Override
    public void updateItem(WeakRef<Node> item, boolean empty) {
        super.updateItem(item, empty);
        newText = null;
        
        if (item != null) {
            if (item.get() != null) {
            newText = item.get().toString();
            }
        }
        
        if(Platform.isFxApplicationThread()) {
            setText(newText);
        } else {
            Platform.runLater(new Runnable() {
                
                @Override
                public void run() {
                    setText(newText);
                }
            });
        }
    }
}