package com.jackyoustra.mapstobuildings;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.geometry.Point2D;

import javax.imageio.ImageIO;


/**
 * The Class MapSection. Should hold all things Google inside it.
 */
public class MapSection {
	
	// https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000
	private BufferedImage rawMapImage;	
	
	public static final int zoom = 18;
	
	/**
	 * Gets the raw map image.
	 *
	 * @return the raw google map image
	 */
	public BufferedImage getRawMapImage() {
		return rawMapImage;
	}
	/**
	 * Instantiates a new building-processed map section.
	 *
	 * @param local choose to access a local sample instead of retrieving from the internet
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public MapSection(boolean local) throws IOException{
		if(local){
			File pictureHandle = new File("C:\\Users\\Jack\\Pictures\\staticmapnolabel.png");
			rawMapImage = ImageIO.read(pictureHandle);
		}
		else{
			URL nonLabelurl = new URL("https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000&style=feature:all|element:labels|visibility:off&key=AIzaSyAeiTCYdp5wB-m9fJskfzKQBW4SWefHyEs");
			rawMapImage = ImageIO.read(nonLabelurl);
		}
	}
	
	/**
	 * Instantiates a new map section.
	 *
	 * @param latitude the latitude of the center of the square in world coordinates
	 * @param longitude the longitude of the center of the square in world coordinates
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public MapSection(double latitude, double longitude) throws IOException{
		URL nonLabelurl = new URL("https://maps.googleapis.com/maps/api/staticmap?center=" + latitude +"," + longitude + "&zoom="+ zoom +"&size=6000x6000&style=feature:all|element:labels|visibility:off&key=AIzaSyAeiTCYdp5wB-m9fJskfzKQBW4SWefHyEs");
		rawMapImage = ImageIO.read(nonLabelurl);
	}
	
	
	public static Point2D normPixelToWorldCoordinates(Point normalizedPixel){
		final double divPoint = Math.pow(2, zoom);
		return new Point2D(normalizedPixel.x/divPoint, normalizedPixel.y/divPoint);
	}
	
	public static Point worldCoordinatesToNormPixel(Point2D worldCoordinate){
		final double mulPoint = Math.pow(2, zoom);
		return new Point((int)Math.round(worldCoordinate.getX()*mulPoint), (int)Math.round(worldCoordinate.getY()*mulPoint));
	}
}
