package de.saxsys.leakscanner.gui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.saxsys.leakscanner.LeakedItem;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Created by maximilian.grosser on 12.10.2016.
 */
public class MixedTreeCell extends TreeTableCell<LeakedItem,String> {
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
        vBox.getChildren().addAll(imageView,lastParentLabel, GlyphsDude.createIcon(FontAwesomeIcon.SEARCH));
        setGraphic(vBox);

    }

    @Override
    public void updateItem(String item, boolean showImageView){
        super.updateItem(item,showImageView);
        lastParentLabel
setText("vhuiijk");
//        if(showImageView){
//            vBox.getChildren().addAll(GlyphsDude.createIcon(FontAwesomeIcon.SEARCH));

//        }



    }
}
