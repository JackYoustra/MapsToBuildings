package com.jackyoustra.mapstobuildings;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Point2D;

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
		// NY coordinates:  40.705345 | Longitude: -74.018812 rfwj park ,Latitude: 40.719603 | Longitude: -74.010035 trib park
			JFrame window = new JFrame("Image Section");			
			
			List<List<BufferedImage>> rowImages = new ArrayList<>();
			List<List<BufferedImage>> rawRowImages = new ArrayList<>();
			
			Point p = MapSection.worldCoordinatesToNormPixel(new Point2D(40.705345,-74.018812));
			Point2D wc = MapSection.normPixelToWorldCoordinates(p);
			do{
				ArrayList<BufferedImage> vertImages = new ArrayList<>();
				ArrayList<BufferedImage> rawVertImages = new ArrayList<>();
				p = MapSection.worldCoordinatesToNormPixel(new Point2D(40.705345, wc.getY()));
				wc = MapSection.normPixelToWorldCoordinates(p);
				do{
					MapSection currentSection = new MapSection(wc.getX(), wc.getY());
					rawVertImages.add(currentSection.getRawMapImage());
					vertImages.add(currentSection.getImageSection());
					p.x+=682;
					wc = MapSection.normPixelToWorldCoordinates(p);
					
					
				}while(wc.getX() < 40.719603);
				rowImages.add(vertImages);
				rawRowImages.add(rawVertImages);
				p.y+=898;
				wc = MapSection.normPixelToWorldCoordinates(p);
			}while(wc.getY() < -74.010035);

			BufferedImage combined = mergeImages(rowImages);
			final BufferedImage rawCombined = mergeImages(rawRowImages);
			
			window.add(new JLabel(new ImageIcon(rawCombined)));
			ImageIO.write(combined, "png", new File("AggregateMap.png"));
			ImageIO.write(rawCombined, "png", new File("RawAggregateMap.png"));
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.pack();
			window.setVisible(true);
		/*} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public static BufferedImage mergeImages(List<List<BufferedImage>> images){
		final int width = images.size()*640;
		final int height = images.get(0).size()*640;
		BufferedImage combined = new BufferedImage(width, height, images.get(0).get(0).getType());
		for(int i = 0; i < images.size(); i++){
			List<BufferedImage> column = images.get(i);
			for(int j = 0; j < column.size(); j++){
				BufferedImage bi = column.get(j);
				Graphics g = combined.getGraphics();
				g.drawImage(bi, i*640, (column.size()-j-1)*640, null);
			}
		}
		return combined;
	}
	
	/**
	 * Draw building polygons over image.
	 *
	 * @param bi the buffered image to write on
	 * @param ms the underlying map section
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	// profiled at (relative) 4.1 seconds
	public static void drawPolygon(BufferedImage bi, MapSection ms) throws IOException{
		Polygon[] buildingsBounds = ms.buildingCoordinatesInImage();
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
