package com.jackyoustra.mapstobuildings;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RenderedImage;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import com.jhlabs.image.DilateFilter;
import com.jhlabs.image.ErodeFilter;
import com.jhlabs.image.ReduceNoiseFilter;


public class MapSection {
	
	// https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000
	private BufferedImage imageSection;
	
	public BufferedImage getImageSection() {
		return imageSection;
	}

	public MapSection(int toleranceMax, int toleranceMin) throws IOException{
		URL nonLabelurl = new URL("https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000&style=feature:all|element:labels|visibility:off&key=AIzaSyAeiTCYdp5wB-m9fJskfzKQBW4SWefHyEs");
		imageSection = ImageIO.read(nonLabelurl);
		imageSection = processImagePixally(imageSection, toleranceMax, toleranceMin);
		writeImage(imageSection, "cleanedImage");
		//writeImage(imageSection, "Max_" + toleranceMax + " Min_" + toleranceMin);
	}
	
	private static void writeImage(RenderedImage ri, String name) throws IOException{
		new File("DebugImage").mkdirs();
		ImageIO.write(ri, "png", new File("DebugImage" + File.separator + name + ".png"));
	}
	
	// @description: performs partition on imageSection
	// @return 2D building polygon
	// TODO: 3D building coordinates
	public Polygon[] buildingCoordinatesInImage(){
		int[][] pixels = pixelsFromBufferedImage(imageSection);
		List<Point> bluePointList = new ArrayList<>();
		List<Polygon> buildings = new ArrayList<>();
		for(int x = 0; x < pixels.length; x++){
			for(int y = 0; y < pixels.length; y++){
				int px = pixels[x][y];
				int blue = (int) (px & 0xFF);
				if(blue==255){ // is standard blue hue
					bluePointList.add(new Point(x, y));
				}
			}
		}
		// bluePointList now has all blue points
		
		// get individual building coordinate
		while(bluePointList.size() > 0){ // no iterator needed, clear array of current one anyway
			Point currentPoint = bluePointList.get(0);
			// Diagonals
			Point[] upLeft = diagonalRunner(pixels, currentPoint.x, currentPoint.y, DiagonalDirections.UPPER_LEFT);
			Point[] upRight = diagonalRunner(pixels, currentPoint.x, currentPoint.y, DiagonalDirections.UPPER_RIGHT);
			Point[] lowLeft = diagonalRunner(pixels, currentPoint.x, currentPoint.y, DiagonalDirections.LOWER_LEFT);
			Point[] lowRight = diagonalRunner(pixels, currentPoint.x, currentPoint.y, DiagonalDirections.LOWER_RIGHT);
			// Axes streaks
			Point upperEdge = linearRunner(pixels, currentPoint.x, currentPoint.y, LinearDirections.UP);
			Point lowerEdge = linearRunner(pixels, currentPoint.x, currentPoint.y, LinearDirections.DOWN);
			Point leftEdge = linearRunner(pixels, currentPoint.x, currentPoint.y, LinearDirections.LEFT);
			Point rightEdge = linearRunner(pixels, currentPoint.x, currentPoint.y, LinearDirections.RIGHT);
			
			// copy into one array
			Point[] coordinatePoints = new Point[upLeft.length + upRight.length + lowLeft.length + lowRight.length + 4];
			int totalLength = 0;
			System.arraycopy(upLeft, 0, coordinatePoints, totalLength, upLeft.length);
			totalLength+=upLeft.length;
			System.arraycopy(upRight, 0, coordinatePoints, totalLength, upRight.length);
			totalLength+=upRight.length;
			System.arraycopy(lowLeft, 0, coordinatePoints, totalLength, lowLeft.length);
			totalLength+=lowLeft.length;
			System.arraycopy(lowRight, 0, coordinatePoints, totalLength, lowRight.length);
			
			coordinatePoints[coordinatePoints.length-4] = upperEdge;
			coordinatePoints[coordinatePoints.length-3] = lowerEdge;
			coordinatePoints[coordinatePoints.length-2] = leftEdge;
			coordinatePoints[coordinatePoints.length-1] = rightEdge;
			// coordinatePoints now has all of that blue
			
			// generate polygon
			int[] xes = new int[coordinatePoints.length];
			int[] ys = new int[coordinatePoints.length];
			for(int coordCounter = 0; coordCounter < coordinatePoints.length; coordCounter++){
				final Point temp = coordinatePoints[coordCounter];
				xes[coordCounter] = temp.x;
				ys[coordCounter] = temp.y;
			}
			Polygon border = new Polygon(xes, ys, coordinatePoints.length);
			// add polygon
			buildings.add(border);
			// clean list
			cleanList(bluePointList, border); // make sure to go through all
			System.out.println("" + bluePointList.size());
		}
		// convert array
		return buildings.toArray(new Polygon[0]);
	}
	
	// cleans list of all blue contained in borders, return number of stuff removed
	private int cleanList(List<Point> blueList, Polygon borders){
		int numRemoved = 0;
		for(int i = 0; i < blueList.size(); i++){
			Point currentPoint = blueList.get(i);
			if(borders.contains(currentPoint)){
				blueList.remove(i);
				i--;
				numRemoved++;
			}
		}
		return numRemoved;
	}
	
	// @return list of points at 45 degree angles that are blue-edged to it
	private Point[] diagonalRunner(int[][] pixels, int x, int y, DiagonalDirections dir){
		switch(dir){
		case LOWER_LEFT:
			x--; y++;
			break;
		case LOWER_RIGHT:
			x++; y++;
			break;
		case UPPER_LEFT:
			x--; y--;
			break;
		case UPPER_RIGHT:
			x++; y--;
			break;
		default:
			break;
		}
		if(x < 0 || y < 0 || x >= pixels.length || y >= pixels[0].length){ // not inside bounds
			return new Point[0];
		}
		int blue = (int) (pixels[x][y] & 0xFF);
		if(blue == 255){ // is blue
			LinearDirections horDir = null, vertDir = null;
			
			switch(dir){
			case LOWER_LEFT:
				horDir = LinearDirections.LEFT;
				vertDir = LinearDirections.DOWN;
				break;
			case LOWER_RIGHT:
				horDir = LinearDirections.RIGHT;
				vertDir = LinearDirections.DOWN;
				break;
			case UPPER_LEFT:
				horDir = LinearDirections.LEFT;
				vertDir = LinearDirections.UP;
				break;
			case UPPER_RIGHT:
				horDir = LinearDirections.RIGHT;
				vertDir = LinearDirections.UP;
				break;
			default:
				break;
			
			}
			Point horPt = linearRunner(pixels, x, y, horDir);
			Point vertPt = linearRunner(pixels, x, y, vertDir);
			Point[] diagonalPts = diagonalRunner(pixels, x, y, dir);
			Point[] finalPts = new Point[diagonalPts.length+2];
			System.arraycopy(diagonalPts, 0, finalPts, 0, diagonalPts.length);
			finalPts[finalPts.length-2] = horPt;
			finalPts[finalPts.length-1] = vertPt;
			return finalPts;
		}
		return new Point[0]; // else, no points
	}
	
	// @return last blue pixel location on path. If edge, returns the edge
	private Point linearRunner(int[][] pixels, int x, int y, LinearDirections dir){
		int newx = x; int newy = y;
		switch(dir){
		case DOWN:
			newy++; // origin upper left
			break;
		case LEFT:
			newx--;
			break;
		case RIGHT:
			newx++;
			break;
		case UP:
			newy--;
			break;
		default:
			break;
		}
		if(newx < 0 || newy < 0 || newx >= pixels.length || newy >= pixels[0].length){// hit edge
			return new Point(x, y);
		}
		int blue = (int) (pixels[newx][newy] & 0xFF);
		if(blue == 0){ // hit black
			return new Point(x, y);
		}
		else{
			return linearRunner(pixels, newx, newy, dir);
		}
	}
	
	// TODO: Make optimal (http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image)
	public static int[][] pixelsFromBufferedImage(final BufferedImage image){
		int[][] pixArr = new int[image.getWidth()][image.getHeight()];
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){
				pixArr[x][y] = image.getRGB(x, y);
			}
		}
		return pixArr;
	}
	
	private static BufferedImage processImagePixally(final BufferedImage image, int toleranceMax, int toleranceMin){
		BufferedImage img = new BufferedImage(
			    image.getWidth(), 
			    image.getHeight(), 
			    BufferedImage.TYPE_INT_ARGB);
		ColorConvertOp cco = new ColorConvertOp(null);
		cco.filter(image, img);
		
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int px = img.getRGB(x, y);
				int alpha = (px >> 24) & 0xFF;
				int red = (px >> 16) & 0xFF;
				int green = (px >> 8) & 0xFF;
				int blue = px & 0xFF;
			    // do stuff here
				// different color for tops of buildings
				
				if(red == 242 && green == 240 && blue == 233){
					//red = blue = green = 255;
					blue = 255;
					green = red = 0;
				}
				else if(red == 196 && green == 196 && blue == 212){
					// fair game, magic unformulaic value for dark zones
				}
				else{
					// the trick here is to limit it to a particular band of grey
					int diff = 0;
					// could optimize
					int[] diffCandidates = {red, green, blue};
					Arrays.sort(diffCandidates); 
					diff = diffCandidates[2] - diffCandidates[0];
					
					if(toleranceMax >= diff && diff >= toleranceMin){
						// should be fair game
						
					}
					else{
						red = blue = green = 0;
					}
				}
				
				
				// end do stuff
				int pixel = (alpha<<24) + (red<<16) + (green<<8) + blue;
		        img.setRGB(x, y, pixel);
		    }
		}
		
		DilateFilter df = new DilateFilter();
		img = df.filter(img, null);
		
		ErodeFilter ef = new ErodeFilter();
		img = ef.filter(img, null);
		
		return img;
	}
	
	private static BufferedImage processImage(final BufferedImage image){
		// init lookup table to zero
		BufferedImage finalImage = new BufferedImage(
			    image.getWidth(), 
			    image.getHeight(), 
			    BufferedImage.TYPE_INT_RGB);
		ColorConvertOp cco = new ColorConvertOp(null);
		cco.filter(image, finalImage);
		
		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];
		
		// set to default values
		red[242] = 255;
		green[240] = 255;
		blue[233] = 255;
		
		// hold all data for table
		short[][] data = new short[][]{
				red, green, blue
		};
		LookupTable lookupTable = new ShortLookupTable(0, data);
		LookupOp op = new LookupOp(lookupTable, null);
		op.filter(finalImage, finalImage);
		return finalImage;
	}
}
