package de.buildpath.leakscanner;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

//This could be a view
public class LeakingView extends VBox {

    private final Car car;
    private ChangeListener<String> carChangedListener;
    private ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/relaxo.jpg")));
    
    public LeakingView(Car car) {
        this.car = car;
        imageView.setFitHeight(400);
        imageView.setFitWidth(400);
        
        // Leaking - (If you use lambdas instead of anonymous classes you have
        // to reference a class member from the
        // lambda, otherwise the compile will optimize the lambda to a static
        // method and a leak is avoided)
        this.carChangedListener = (change, o, newValue) -> {
            if (newValue.equals("kill")) {
                removeListener();
            }

            if (newValue.equals("remove")) {
                if(getParent() != null) {
                    ((VBox) getParent()).getChildren().remove(this);
                }
            }
        };
        
        car.name.addListener(carChangedListener);
        getChildren().addAll(imageView);
    }

    public void removeListener() {
        car.name.removeListener(this.carChangedListener);
        this.carChangedListener = null;
    }

}
