package com.jackyoustra.mapstobuildings;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.jhlabs.image.DilateFilter;
import com.jhlabs.image.ErodeFilter;


/**
 * The Class MapSection describes a section of map gotten from the googleAPIs and isolated for buildings.
 */
public class MapSection {
	
	// https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000
	/** The image section. */
	private BufferedImage imageSection;
	
	/**
	 * Gets the underlying image section.
	 *
	 * @return the image section
	 */
	public BufferedImage getImageSection() {
		return imageSection;
	}

	/**
	 * Instantiates a new building-processed map section.
	 *
	 * @param toleranceMax the tolerance max
	 * @param toleranceMin the tolerance min
	 * @param local choose to access a local sample instead of retrieving from the internet
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public MapSection(int toleranceMax, int toleranceMin, boolean local) throws IOException{
		if(local){
			File pictureHandle = new File("C:\\Users\\Jack\\Pictures\\staticmapnolabel.png");
			imageSection = ImageIO.read(pictureHandle);
		}
		else{
			URL nonLabelurl = new URL("https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000&style=feature:all|element:labels|visibility:off&key=AIzaSyAeiTCYdp5wB-m9fJskfzKQBW4SWefHyEs");
			imageSection = ImageIO.read(nonLabelurl);
		}
		
		imageSection = processImagePixally(imageSection, toleranceMax, toleranceMin);
		writeImage(imageSection, "cleanedImage");
		//writeImage(imageSection, "Max_" + toleranceMax + " Min_" + toleranceMin);
	}
	
	/**
	 * Write image.
	 *
	 * @param ri the rendered image
	 * @param name the name of the image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void writeImage(RenderedImage ri, String name) throws IOException{
		new File("DebugImage").mkdirs();
		ImageIO.write(ri, "png", new File("DebugImage" + File.separator + name + ".png"));
	}
	
	/**
	 * Building coordinates in image.
	 *
	 * @return the polygon edge of the buildings
	 */
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
		Point oldPoint = new Point(-1, -1);
		while(bluePointList.size() > 0){
			System.out.println(bluePointList.size());
			Point currentPoint = bluePointList.get(0);
			if(oldPoint.equals(currentPoint)){
				bluePointList.remove(0);
				continue;
				// make sure no repetition happens
			}
			oldPoint = currentPoint;
			Point[] coordinatePoints = starRunner(pixels, currentPoint.x, currentPoint.y, new ArrayList<Point>());			
			
			int[] xes = new int[coordinatePoints.length];
			int[] ys = new int[coordinatePoints.length];
			for(int coordCounter = 0; coordCounter < coordinatePoints.length; coordCounter++){
				final Point temp = coordinatePoints[coordCounter];
				xes[coordCounter] = temp.x;
				ys[coordCounter] = temp.y;
				for(int i = 0; i < bluePointList.size(); i++){
					if(bluePointList.get(i).equals(temp)){
						bluePointList.remove(i);
						i--;
					}
				}
			}
			Polygon border = new Polygon(xes, ys, coordinatePoints.length);
			buildings.add(border);				// clean list
			cleanList(bluePointList, border); // make sure to go through all
		}
		
		// clean buildings
		for(int i = 0; i < buildings.size(); i++){
			Polygon building = buildings.get(i);
			int area = polyArea(building);
			if(area < 9){
				buildings.remove(i);
				i--;
			}
		}
		
		return buildings.toArray(new Polygon[0]);
	}
	
	/**
	 * Poly area.
	 *
	 * @param target the polygon that one can retrieve the area from
	 * @return the area of target
	 */
	public static int polyArea(Polygon target){
		int sum = 0;
        for (int i = 0; i < target.npoints ; i++){
            sum = sum + target.xpoints[i]*target.ypoints[(i+1)%target.npoints] - target.ypoints[i]*target.xpoints[(i+1)%target.npoints];
        }

        return Math.abs(sum / 2);
	}
	
	/**
	 * Star runner style building edge finder.
	 *
	 * @param screen the rgba pixel array 
	 * @param x the current x coordinate
	 * @param y the current y coordinate
	 * @param blueList the blue list (blacklist) of points already covered
	 * @return a point[] compromised of points of the border of the building outside it
	 */
	private Point[] starRunner(int[][] screen, int x, int y, List<Point> blueList){
		if(x < 0 || y < 0 || x >= screen.length || y >= screen[0].length) return new Point[0];
		
		Color pixelColor = new Color(screen[x][y], true);
		if(!(pixelColor.getBlue() == 255 && pixelColor.getGreen() == 0 && pixelColor.getRed() == 0)){
			// not blue
			Point[] containerArr = {new Point(x, y)};
			return containerArr;
		}
		boolean didEndSoon = false;
		for(Point p : blueList){
			if(p.x == x && p.y == y){
				didEndSoon = true;
				break;
			}
		}
		if(didEndSoon){
			// is on list
			return new Point[0];
		}
		
		blueList.add(new Point(x, y));
		
		// not on list, blue, so valid, untouched pixel
		Point[] up = starRunner(screen, x, y+1, blueList);
		Point[] down = starRunner(screen, x, y-1, blueList);
		Point[] right = starRunner(screen, x+1, y, blueList);
		Point[] left = starRunner(screen, x-1, y, blueList);
		
		Point[] totalArr = new Point[up.length + down.length + right.length + left.length];
		System.arraycopy(up, 0, totalArr, 0, up.length);
		System.arraycopy(down, 0, totalArr, up.length, down.length);
		System.arraycopy(right, 0, totalArr, up.length + down.length, right.length);
		System.arraycopy(left, 0, totalArr, up.length+down.length+right.length, left.length);
		return totalArr;
	}
	
	// cleans list of all blue contained in borders, return number of stuff removed
	/**
	 * Clean blue point list of blue points contained in a polygon.
	 *
	 * @param blueList the existing eligible blue points
	 * @param borders the borders to be cleared
	 * @return the number of points removed
	 */
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
	
		// TODO: Make optimal (http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image)
	/**
		 * Pixels from buffered image.
		 *
		 * @param image a buffered image
		 * @return the rgba pixels in int[][] form
		 */
		public static int[][] pixelsFromBufferedImage(final BufferedImage image){
		int[][] pixArr = new int[image.getWidth()][image.getHeight()];
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){
				pixArr[x][y] = image.getRGB(x, y);
			}
		}
		return pixArr;
	}
	
	/**
	 * Process image, clearing it of everything but buildings by going through pixel-by-pixel.
	 * This is done by creating a band of tolerable greys and eradicating them, as well as some other special values.
	 *
	 * @param image the map square to be processed (as an image)
	 * @param toleranceMax the tolerance max for the greyscale bound.
	 * @param toleranceMin the tolerance min for the greyscale bound.
	 * @return the cleaned image
	 */
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
	
}
