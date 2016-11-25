package de.saxsys.leakscanner.gui;

import de.saxsys.leakscanner.LeakedItem;
import de.saxsys.leakscanner.WeakRef;
import de.saxsys.leakscanner.leakdetector.LeakDetector;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeakScannerView extends BorderPane implements Initializable {

    private boolean changes=false;

    @FXML
    private TextField filterTextField;

    @FXML
    private TreeTableView<LeakedItem> leakTreeTableView;

    @FXML
    private TreeTableColumn<LeakedItem, String> nodeCol;

    @FXML
    private TreeTableColumn<LeakedItem, Number> hashCodeCol;

    @FXML
    private TreeTableColumn<LeakedItem, String> oldParentCol;


    @FXML
    private ListView<WeakRef<Node>> whiteListView;

    final LeakDetector leakDetector;

    private WeakRef<Parent> lastParent;

    private Timeline buildTreeTimeLine;


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
        buildTreeTimeLine=  new Timeline();
        buildTreeTimeLine.getKeyFrames().addAll(new KeyFrame(Duration.seconds(3), event -> {
            if(changes){
                Platform.runLater(()->buildTree());
                changes=false;
            }

        }));
        buildTreeTimeLine.setCycleCount(Timeline.INDEFINITE);
        buildTreeTimeLine.play();

        leakDetector.getLeakedObjects().addListener((MapChangeListener<WeakRef<Node>, LeakedItem>) change -> {

            //blue border changes Region
//            if(change!=null &&change.getKey()!=null && change.getKey().get()!=null && !change.getKey().get().toString().contains("Region")){
//                System.out.println("item changed: "+change.getKey().get());
////                Platform.runLater(()->buildTree());
//            }
            changes=true;

        });

        TreeItem<LeakedItem> rootItem = new TreeItem<LeakedItem>(new LeakedItem(new WeakRef<Node>(new Label("Scene"))));
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

//        change the border-color of old parent to blue
        leakTreeTableView.setOnMouseReleased(event -> {
            final TreeItem<LeakedItem> target = leakTreeTableView.getSelectionModel().getSelectedItem();
            if (target != null) {
                if (lastParent != null) {
                    lastParent.get().setStyle("");
                }
                System.out.println("target " + target);
                lastParent = target.getValue().getOldSceneParent();
                if (lastParent != null) {
                    lastParent.get().setStyle("-fx-border-color: blue ;\n" +
                            "    -fx-border-width: 8 ; ");
                }
            }

        });

        nodeCol.setCellValueFactory(w -> {
            if (leakTreeTableView.getRoot() == w.getValue()) {
                return new SimpleStringProperty("Scene");
            } else {
                if(w!=null && w.getValue()!=null) {
                    LeakedItem item = w.getValue().getValue();

                    return item.nodeProperty();
                }else {
                    return null;
                }
            }
        });

        hashCodeCol.setCellValueFactory(w -> {
            if(w!=null && w.getValue()!=null) {
                LeakedItem item = w.getValue().getValue();

                return item.hashCodeProperty();
            }else {
                return null;
            }

        });

        oldParentCol.setCellValueFactory(w -> {
            if(w!=null && w.getValue()!=null) {
                LeakedItem item = w.getValue().getValue();

                return item.oldParentProperty();
            }else {
                return null;
            }


        });


        oldParentCol.setCellFactory(param -> {
            TreeTableCell<LeakedItem, String> summaryCell = new MixedTreeCell();
            return summaryCell;
        });

//        oldParentCol.setCellFactory(new Callback<TreeTableColumn<LeakedItem, LeakedItem>, TreeTableCell<LeakedItem, LeakedItem>>() {
//            @Override
//            public TreeTableCell<LeakedItem, LeakedItem> call(TreeTableColumn<LeakedItem, LeakedItem> param) {
//                TreeTableCell<LeakedItem, LeakedItem> summaryCell = new MixedTreeCell();
//
//                return summaryCell;
//            }
//        });

        whiteListView.setItems(leakDetector.getWhiteList());
        addContextMenuWhitelist();
    }

    @FXML
    void onGcButtonPressed(ActionEvent event) {
        System.gc();
    }

    private void buildTree() {
        synchronized (this){
            System.out.println("buildTree");
            leakTreeTableView.getRoot().getChildren().clear();

            for (LeakedItem child : leakDetector.getRootItem().getChildren()) {
                createTreeItemForLeakedItem(child, leakTreeTableView.getRoot());
            }
        }

    }

    private void createTreeItemForLeakedItem(LeakedItem leak, TreeItem<LeakedItem> parent) {
        TreeItem<LeakedItem> treeItemParent = new TreeItem<LeakedItem>(leak);
        parent.getChildren().add(treeItemParent);

        for (LeakedItem childLeak : leak.getChildren()) {
            createTreeItemForLeakedItem(childLeak, treeItemParent);
        }
    }

    private void addContextMenuWhitelist() {
        MenuItem whiteListMenuItem = new MenuItem();
        whiteListMenuItem.setText("remove from whitelist");
        whiteListMenuItem.setOnAction(event -> {
            WeakRef<Node> selectedItem = whiteListView.getSelectionModel().getSelectedItem();
            // remove from whiteList
            leakDetector.removeFromWhiteList(selectedItem);
        });

        ContextMenu whiteListContextMenu = new ContextMenu();
        whiteListContextMenu.getItems().add(whiteListMenuItem);

        whiteListView.setContextMenu(whiteListContextMenu);
    }
}
