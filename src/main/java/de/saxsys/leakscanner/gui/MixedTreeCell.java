package de.saxsys.leakscanner.gui;

import de.saxsys.leakscanner.WeakRef;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Created by maximilian.grosser on 12.10.2016.
 */
public class MixedTreeCell extends TreeCell<WeakRef<Node>>{
    VBox vBox;
    Label lastParentLabel;
    ImageView imageView;

    public MixedTreeCell(){
        vBox=new VBox();
        vBox.setAlignment(Pos.CENTER);
        lastParentLabel= new Label();
        imageView= new ImageView();
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        vBox.getChildren().addAll(imageView,lastParentLabel);

    }

    @Override
    public void updateItem(WeakRef<Node>  item, boolean showImageView){
        lastParentLabel.setText(oldParent);
        super.updateItem(item, showImageView);



    }
}
