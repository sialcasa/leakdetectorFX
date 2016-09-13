package de.saxsys.leakscanner;

import java.util.Random;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Car {
	// Normally you have setters / getters
	public StringProperty name = new SimpleStringProperty("Audi");
	public ImageView image;
	
	public void startRandomNameGeneration() {
		Thread thread = new Thread(() -> {
			while (true) {
				Platform.runLater(() -> {
					name.set(String.valueOf(new Random().nextInt(Short.MAX_VALUE + 1)));
				});
				try {
					Thread.sleep(5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
}
