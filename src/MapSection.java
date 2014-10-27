import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RenderedImage;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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
		imageSection = processImage(imageSection);
		writeImage(imageSection);
	}
	
	private static void writeImage(RenderedImage ri) throws IOException{
		ImageIO.write(ri, "png", new File("DebugImage.png"));
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
