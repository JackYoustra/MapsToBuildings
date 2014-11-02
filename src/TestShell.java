import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class TestShell {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedImage bi = new MapSection(11, 1).getImageSection();
			JFrame window = new JFrame("Image Section");
			
			window.add(new JLabel(new ImageIcon(bi)));
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.pack();
			window.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
