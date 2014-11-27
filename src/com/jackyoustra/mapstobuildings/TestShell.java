package com.jackyoustra.mapstobuildings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class TestShell {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MapSection ms = new MapSection(11, 1, true);
			
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
