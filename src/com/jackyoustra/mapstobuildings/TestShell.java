package com.jackyoustra.mapstobuildings;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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
	 * @throws IOException 
	 */
	// profiled at (relative) 4.7 seconds
	public static void main(String[] args) throws IOException {
		
		//try {
			/*
			MapSection ms = new MapSection(false);
			BufferedImage bi = ms.getImageSection();
			
			long time = System.currentTimeMillis();
			drawPolygon(bi, ms);
			time -= System.currentTimeMillis();
			time *=-1;
			System.out.println("Seconds Elapsed:" + time/1000.0);
			*/
			JFrame window = new JFrame("Image Section");			
			
			//final BufferedImage rawCombined = MapAccumulator.createMacroSection();
			final BufferedImage rawCombined = ImageIO.read(new File("RawAggregateMap.png")); // evade google api cost
			
			window.add(new JLabel(new ImageIcon(rawCombined)));
			ImageIO.write(rawCombined, "png", new File("RawAggregateMap.png"));
			
			ImageIO.write(BuildingUtilities.processImagePixally(rawCombined), "png", new File("AggregateMap.png"));
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.pack();
			window.setVisible(true);
		/*} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	
	
	/**
	 * Draw building polygons over image.
	 *
	 * @param bi the buffered image to write on
	 * @param ms the underlying map section
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	// profiled at (relative) 4.1 seconds
	public static void drawPolygon(BufferedImage bi) throws IOException{
		Polygon[] buildingsBounds = BuildingUtilities.buildingCoordinatesInImage(bi);
		StringBuilder sb = new StringBuilder();
		for(Polygon cp : buildingsBounds){
			for(int i = 0; i < cp.npoints; i++){
				sb.append("{" + cp.xpoints[i] + "," + cp.ypoints[i] + "}, ");
				bi.setRGB(cp.xpoints[i], cp.ypoints[i], new Color(0, 255, 0).getRGB());
			}
			sb.append("\n");
		}
		PrintWriter writer = new PrintWriter("polygonDebugInfo.txt", "UTF-8");
		writer.print(sb.toString());
		writer.close();
		ImageIO.write(bi, "png", new File("cleanedOutlinedImage" + ".png"));

	}

}
