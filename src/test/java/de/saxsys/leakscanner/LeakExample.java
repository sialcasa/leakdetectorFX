package de.saxsys.leakscanner;

import java.lang.ref.WeakReference;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LeakExample extends Application {

    // Long living model element
    private final SomeDataModel dataModel = new SomeDataModel();

    // While the WeakReference contains the reference to the view, it's the
    // proof that the view retains in memory
    @SuppressWarnings("unused")
    private WeakReference<LeakingView> weakReference;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create the leaking view and the WeakReference
        LeakingView view = new LeakingView(dataModel);

        // While the WeakReference contains the reference to the view, it's the
        // proof that the view retains in memory
        weakReference = new WeakReference<LeakingView>(view);

        TextField commandTextField = new TextField();
        commandTextField.textProperty().bindBidirectional(dataModel.name);

        final VBox vbox = new VBox(commandTextField, view);
        final ScrollPane root = new ScrollPane(vbox);
        commandTextField.textProperty().addListener((c, o, n) -> {
            if (n.equals("add")) {
                LeakingView newView = new LeakingView(dataModel);
                vbox.getChildren().add(newView);
            }
            if (n.equals("init")) {
                vbox.getChildren().add(view);
            }
        });

        // UI
        Scene scene = new Scene(root);
        new LeakScanner(scene, 100L);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
