package com.jackyoustra.mapstobuildings;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * The Class TestShell. This is used to test the functionality of the main map processing unit
 */
public class TestShell {

	/**
	 * The main method.
	 *
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {
		try {
			MapSection ms = new MapSection(true);
			
			//window.add(new JLabel(new ImageIcon(bi)));
			BufferedImage bi = ms.getImageSection();
			JFrame window = new JFrame("Image Section");
			//drawPolygon(bi, ms);
			
			window.add(new JLabel(new ImageIcon(bi)));
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.pack();
			window.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Draw building polygons over image.
	 *
	 * @param bi the buffered image to write on
	 * @param ms the underlying map section
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void drawPolygon(BufferedImage bi, MapSection ms) throws IOException{
		Polygon[] buildingsBounds = ms.buildingCoordinatesInImage();
		for(Polygon cp : buildingsBounds){
			for(int i = 0; i < cp.npoints; i++){
				bi.setRGB(cp.xpoints[i], cp.ypoints[i], new Color(0, 255, 0).getRGB());
			}
		}
		ImageIO.write(bi, "png", new File("cleanedOutlinedImage" + ".png"));

	}

}
