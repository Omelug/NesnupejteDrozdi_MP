package drozdi.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

public class FileManager {
    public static BufferedImage loadResource(String imagePath){
        try {
            InputStream imageStream = FileManager.class.getClassLoader().getResourceAsStream(imagePath);
            return ImageIO.read(imageStream);
        } catch (Exception e) {
            System.out.println("ERROR -- load resource " + imagePath + " - " + e);
            return null;
        }
    }
    public static ImageIcon loadImageIcon(String imagePath) {
        try {
            URL imageStream = FileManager.class.getClassLoader().getResource(imagePath);
            return new ImageIcon(imageStream);
        } catch (Exception e) {
            System.out.println("ERROR -- load icon " + imagePath + " - " + e);
            return null;
        }
    }
    public static URL getResource(String path) {
        return FileManager.class.getClassLoader().getResource(path);
    }

}
