package de.saxsys.leakscanner.gui;

import de.saxsys.leakscanner.LeakedItem;
import de.saxsys.leakscanner.WeakRef;
import de.saxsys.leakscanner.leakdetector.LeakDetector;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeakScannerView extends BorderPane implements Initializable {

    private boolean changes = false;
    private DoubleProperty delay = new SimpleDoubleProperty(20);

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
    @FXML
    private CheckBox delayCheckBox;
    @FXML
    private TextField delayTextfield;

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
        buildTreeTimeLine = new Timeline();
        double delay = Double.parseDouble(delayTextfield.getText());
        buildTreeTimeLine.getKeyFrames().addAll(new KeyFrame(Duration.seconds(delay), event -> {
            if (changes) {
                System.out.println(delayCheckBox.isSelected());
                System.out.println("i am the original keyframe");
                Platform.runLater(() -> buildTree());
                changes = false;
            }

        }));
        buildTreeTimeLine.setCycleCount(Timeline.INDEFINITE);


        leakDetector.getLeakedObjects().addListener((MapChangeListener<WeakRef<Node>, LeakedItem>) change -> {
            setUpTreeViewRefresh(change);
        });

        TreeItem<LeakedItem> rootItem = new TreeItem<LeakedItem>(new LeakedItem(new WeakRef<Node>(new Label("Scene"))));
        rootItem.setExpanded(true);
        leakTreeTableView.setRoot(rootItem);

    }

    private void setUpTreeViewRefresh(MapChangeListener.Change<? extends WeakRef<Node>, ? extends LeakedItem> change){
        //no delay
        double delay = Double.parseDouble(delayTextfield.getText());
        if (!delayCheckBox.isSelected()) {
            System.out.println("no delay "+delayCheckBox.isSelected());
            //blue border changes Region
            if (change != null && change.getKey() != null && change.getKey().get() != null && !change.getKey().get().toString().contains("Region")) {
//                System.out.println("item changed: "+change.getKey().get());
                Platform.runLater(() -> buildTree());
            }
        } else {
            changes = true;
            System.out.println("delay2 "+delayCheckBox.isSelected()+delay);
            buildTreeTimeLine.play();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        delayTextfield.setText(String.valueOf(delay.get()));

        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(delayTextfield.textProperty(),delay,converter);

        delay.addListener((observable, oldValue, newValue) -> {
            System.out.println("delay "+delayCheckBox.isSelected()+delay);
            buildTreeTimeLine.stop();
            buildTreeTimeLine.getKeyFrames().clear();
            buildTreeTimeLine.getKeyFrames().addAll(new KeyFrame(Duration.seconds(delay.get()), event -> {
                if (changes) {
                    System.out.println("i am the new keyframe");
                    Platform.runLater(() -> buildTree());
                    changes = false;
                }
            }));
        });

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
                if (w != null && w.getValue() != null) {
                    LeakedItem item = w.getValue().getValue();

                    return item.nodeProperty();
                } else {
                    return null;
                }
            }
        });

        hashCodeCol.setCellValueFactory(w -> {
            if (w != null && w.getValue() != null) {
                LeakedItem item = w.getValue().getValue();

                return item.hashCodeProperty();
            } else {
                return null;
            }

        });

        oldParentCol.setCellValueFactory(w -> {
            if (w != null && w.getValue() != null) {
                LeakedItem item = w.getValue().getValue();

                return item.oldParentProperty();
            } else {
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
        synchronized (this) {
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
