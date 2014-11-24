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
			
			Polygon[] buildingsBounds = ms.buildingCoordinatesInImage();
			for(Polygon cp : buildingsBounds){
				for(int i = 0; i < cp.npoints; i++){
					for(int x = 0, y = 0; x < 4; x++, y++){
						//bi.setRGB(cp.xpoints[i]-2+x, cp.ypoints[i]-2+y, new Color(0, 255, 0).getRGB());
					}
					bi.setRGB(cp.xpoints[i], cp.ypoints[i], new Color(0, 255, 0).getRGB());
				}
			}
			ImageIO.write(bi, "png", new File("outlinedImage" + ".png"));
			window.add(new JLabel(new ImageIcon(bi)));
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.pack();
			window.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
