package com.example.proiect_dianaciodolan;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public class FxImageUtils {

    public static Image toFxImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(x, y, bufferedImage.getRGB(x, y));
            }
        }

        return image;
    }
}