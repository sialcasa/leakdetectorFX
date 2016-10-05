package de.saxsys.leakscanner.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.leakscanner.LeakedItem;
import de.saxsys.leakscanner.WeakRef;
import de.saxsys.leakscanner.leakdetector.LeakDetector;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

public class LeakScannerView extends BorderPane implements Initializable {

    @FXML
    private TextField filterTextField;

    @FXML
    private TreeTableView<LeakedItem> leakTreeTableView;

    @FXML
    private TreeTableColumn<LeakedItem, String> nodeCol;

    @FXML
    private TreeTableColumn<LeakedItem, Number> hashCodeCol;

    final LeakDetector leakDetector;

    public LeakScannerView(LeakDetector leakDetector) {
        this.leakDetector = leakDetector;
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("LeakScannerView.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        leakDetector.getLeakedObjects().addListener((MapChangeListener<WeakRef<Node>, LeakedItem>) change -> {
            buildTree();
        });

        TreeItem<LeakedItem> rootItem = new TreeItem<LeakedItem>(leakDetector.getRootItem());
        rootItem.setExpanded(true);
        leakTreeTableView.setRoot(rootItem);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MenuItem m = new MenuItem();
        m.setText("Add to whitelist");
        m.setOnAction(event -> {
            TreeItem<LeakedItem> selectedItem = leakTreeTableView.getSelectionModel().getSelectedItem();
            // add to whitelist for persistence
            leakDetector.addToWhiteList(selectedItem.getValue());
        });
        
        ContextMenu rootContextMenu = new ContextMenu();
        rootContextMenu.getItems().add(m);
        
        leakTreeTableView.setContextMenu(rootContextMenu);
        
        nodeCol.setCellValueFactory(w -> {
            LeakedItem item = w.getValue().getValue();

            return item.nodeProperty();
        });

        hashCodeCol.setCellValueFactory(w -> {
            LeakedItem item = w.getValue().getValue();

            return item.hashCodeProperty();
        });
    }

    @FXML
    void onGcButtonPressed(ActionEvent event) {
        System.gc();
    }

    private void buildTree() {
        leakTreeTableView.getRoot().getChildren().clear();

        for (LeakedItem child : leakDetector.getRootItem().getChildren()) {
            createTreeItemForLeakedItem(child, leakTreeTableView.getRoot());
        }
    }

    private void createTreeItemForLeakedItem(LeakedItem leak, TreeItem<LeakedItem> parent) {
        TreeItem<LeakedItem> treeItemParent = new TreeItem<LeakedItem>(leak);
        parent.getChildren().add(treeItemParent);

        for (LeakedItem childLeak : leak.getChildren()) {
            createTreeItemForLeakedItem(childLeak, treeItemParent);
        }
    }
}