package de.saxsys.leakscanner;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

//This could be a view
public class LeakingView extends VBox {

    private final SomeDataModel dataModel;
    private ChangeListener<String> nameChangedListener;
    private ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/pikatchu.jpg")));
    
    public LeakingView(SomeDataModel data) {
        this.dataModel = data;
        imageView.setFitHeight(400);
        imageView.setFitWidth(400);
        
        // Leaking - (If you use lambdas instead of anonymous classes you have
        // to reference a class member from the
        // lambda, otherwise the compile will optimize the lambda to a static
        // method and a leak is avoided)
        this.nameChangedListener = (change, o, newValue) -> {
            if (newValue.equals("kill")) {
                removeListener();
            }

            if (newValue.equals("remove")) {
                if(getParent() != null) {
                    ((VBox) getParent()).getChildren().remove(this);
                }
            }
        };
        
        data.name.addListener(nameChangedListener);
        getChildren().addAll(imageView);
    }

    public void removeListener() {
        dataModel.name.removeListener(this.nameChangedListener);
        this.nameChangedListener = null;
    }

}
