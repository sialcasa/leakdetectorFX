package de.buildpath.leakscanner;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//This could be a view
public class LeakingView extends VBox {

    public final Car car;
    private ChangeListener<String> carChangedListener;
    final static int HEIGHT =3;
    final static int WIDTH =1;
    private ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/relaxo.jpg")));

    private final Label carLabel = new Label();

    public LeakingView(Car car) {
        this.car = car;
        imageView.setFitHeight(400);
        imageView.setFitWidth(400);
        carLabel.textProperty().bind(car.name);


        // Leaking - (If you use lambdas instead of anonymous classes you have
        // to reference a class member from the
        // lambda, otherwise the compile will optimize the lambda to a static
        // method and a leak is avoided)
        this.carChangedListener = (observable, oldValue, newValue) -> {
            if (newValue.equals("kill")) {
                removeListener();
            }

            if (newValue.equals("remove")) {
                ((VBox) getParent()).getChildren().remove(this);
            }

            if (newValue.equals("showhbox")) {
                if (getChildren().size() > 1) {
                    HBox hBoxInstanz = (HBox) getChildren().get(1);
                    System.out.println("Deine HBox gibt es noch: " + hBoxInstanz);
                    System.out.println("--> Hat Kinder: " + hBoxInstanz.getChildren());
                } else {
                    System.out.println("Keine HBox mehr da: " + getChildren());
                }
            }


            if (newValue.equals("hbr")) {
                getChildren().remove(1);
            }

        };

        car.name.addListener(carChangedListener);

        getChildren().addAll(carLabel,imageView);
        installHBox();
    }

    private void installHBox() {
        AnchorPane rootPane= new AnchorPane();
        AnchorPane paneTmp=rootPane;

        //create node tree
        for (int i=0;i<=HEIGHT;i++){
            AnchorPane childPane = new AnchorPane();
            for(int k=0; k <= WIDTH; k++) {
                childPane= new AnchorPane();
                paneTmp.getChildren().add(childPane);
            }
            paneTmp=childPane;
        }
        getChildren().add(rootPane);
    }

    public void removeListener() {
        car.name.removeListener(this.carChangedListener);
        this.carChangedListener = null;
    }

}
