package com.jackyoustra.mapstobuildings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
			MapSection ms = new MapSection(11, 1);
			BufferedImage bi = ms.getImageSection();
			JFrame window = new JFrame("Image Section");
			
			//window.add(new JLabel(new ImageIcon(bi)));
			Polygon[] buildingsBounds = ms.buildingCoordinatesInImage();
				
			for(Polygon cp : buildingsBounds){
				System.out.println(cp.toString());
			}
			
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.pack();
			window.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
