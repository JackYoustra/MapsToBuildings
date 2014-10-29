import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RenderedImage;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;


public class MapSection {
	
	// https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000
	private BufferedImage imageSection;
	
	public BufferedImage getImageSection() {
		return imageSection;
	}

	public MapSection() throws IOException{
		URL nonLabelurl = new URL("https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000&style=feature:all|element:labels|visibility:off&key=AIzaSyAeiTCYdp5wB-m9fJskfzKQBW4SWefHyEs");
		imageSection = ImageIO.read(nonLabelurl);
		imageSection = processImagePixally(imageSection);
		writeImage(imageSection);
	}
	
	private static void writeImage(RenderedImage ri) throws IOException{
		ImageIO.write(ri, "png", new File("DebugImageWithPixel.png"));
	}
	
	private static BufferedImage processImagePixally(final BufferedImage image){
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
				
				// standard is to do nothing
				/*
				if(red == 242 && green == 240 && blue == 233){
					// is the standard color
					//red = blue = green = 255;
					
				}
				*/
				// the trick here is to limit it to a particular band of grey
				int diff = 0;
				// could optimize
				int[] diffCandidates = {red, green, blue};
				Arrays.sort(diffCandidates); 
				diff = diffCandidates[2] - diffCandidates[0];
				
				if(11 >= diff && diff >= 3){
					// should be fair game
					
				}
				else{
					red = blue = green = 0;
				}
				
				// end do stuff
				int pixel = (alpha<<24) + (red<<16) + (green<<8) + blue;
		        img.setRGB(x, y, pixel);
		    }
		}
		
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
