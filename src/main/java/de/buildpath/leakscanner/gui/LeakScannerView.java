package de.buildpath.leakscanner.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.buildpath.leakscanner.WeakRef;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class LeakScannerView extends BorderPane implements Initializable {

    @FXML
    private TextField filterTextField;

    @FXML
    private TreeTableView<WeakRef<Node>> leakTreeTableView;

    @FXML
    private TreeTableColumn<WeakRef<Node>, String> nodeCol;

    @FXML
    private TreeTableColumn<WeakRef<Node>, Number> hashCodeCol;

    public LeakScannerView(TreeItem<WeakRef<Node>> root) {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("LeakScannerView.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        leakTreeTableView.setRoot(root);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nodeCol.setCellValueFactory(new Callback<CellDataFeatures<WeakRef<Node>, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<WeakRef<Node>, String> w) {
                WeakRef<Node> ref = w.getValue().getValue();

                if (ref != null && ref.get() != null) {
                    return new SimpleStringProperty(ref.get().toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        hashCodeCol
                .setCellValueFactory(new Callback<CellDataFeatures<WeakRef<Node>, Number>, ObservableValue<Number>>() {
                    @Override
                    public ObservableValue<Number> call(CellDataFeatures<WeakRef<Node>, Number> w) {
                        WeakRef<Node> ref = w.getValue().getValue();

                        if (ref != null && ref.get() != null) {
                            return new SimpleIntegerProperty(ref.get().hashCode());
                        } else {
                            return new SimpleIntegerProperty();
                        }
                    }

                });
    }

    @FXML
    void onGcButtonPressed(ActionEvent event) {
        System.gc();
    }
}
