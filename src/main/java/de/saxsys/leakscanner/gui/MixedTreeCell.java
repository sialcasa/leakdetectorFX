package de.saxsys.leakscanner.gui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.saxsys.leakscanner.LeakedItem;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Created by maximilian.grosser on 12.10.2016.
 */
public class MixedTreeCell extends TreeTableCell<LeakedItem,String> {
    HBox vBox;
    Label lastParentLabel;
    ImageView imageView;

    public MixedTreeCell(){
        vBox=new HBox();
        vBox.setAlignment(Pos.CENTER);
        lastParentLabel= new Label("label");
        imageView= new ImageView();
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        vBox.getChildren().addAll(lastParentLabel, GlyphsDude.createIcon(FontAwesomeIcon.SEARCH));
        vBox.setSpacing(5);
//        setGraphic(vBox);

    }

    @Override
    public void updateItem(String item, boolean showImageView){
        System.out.println(item);

        if(item!=null){
            lastParentLabel.setText(item);
            setGraphic(vBox);
        }else{
            lastParentLabel.setText("");
            setGraphic(null);
        }



    }
}
