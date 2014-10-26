import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;


public class MapSection {
	
	// https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000
	private Image imageSection;
	
	public Image getImageSection() {
		return imageSection;
	}

	public MapSection() throws IOException{
		URL url = new URL("https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=18&size=6000x6000");
		imageSection = ImageIO.read(url);
		writeImage();
	}
	
	private void writeImage() throws IOException{
		ImageIO.write((RenderedImage) imageSection, "png", new File("DebugImage.png"));
	}
	
}
