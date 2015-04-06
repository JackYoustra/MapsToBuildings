package com.jackyoustra.mapstobuildings;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

public class MapAccumulator {
	// NY coordinates:  40.705345 | Longitude: -74.018812 rfwj park ,Latitude: 40.719603 | Longitude: -74.010035 trib park
	
	public static BufferedImage createMacroSection() throws IOException {
		List<List<BufferedImage>> rawRowImages = new ArrayList<>();
		
		Point p = MapSection.worldCoordinatesToNormPixel(new Point2D(40.705345,-74.018812));
		Point2D wc = MapSection.normPixelToWorldCoordinates(p);
		do{
			ArrayList<BufferedImage> rawVertImages = new ArrayList<>();
			p = MapSection.worldCoordinatesToNormPixel(new Point2D(40.705345, wc.getY()));
			wc = MapSection.normPixelToWorldCoordinates(p);
			do{
				MapSection currentSection = new MapSection(wc.getX(), wc.getY());
				rawVertImages.add(currentSection.getRawMapImage());
				p.x+=682;
				wc = MapSection.normPixelToWorldCoordinates(p);
			}while(wc.getX() < 40.719603);
			rawRowImages.add(rawVertImages);
			p.y+=898;
			wc = MapSection.normPixelToWorldCoordinates(p);
		}while(wc.getY() < -74.010035);

		final BufferedImage rawCombined = mergeImages(rawRowImages);
		return rawCombined;
	}
	
	private static BufferedImage mergeImages(List<List<BufferedImage>> images){
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
}
