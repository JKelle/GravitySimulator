import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import javax.imageio.ImageIO;

public class PanalElement extends Rectangle{
	public Image image;
	private boolean isHovered;
	public String name;
	
	public PanalElement(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;		
	}
	public PanalElement(String fileName, int x, int y, int w, int h) {
		this(x,y,w,h);
		name = fileName;
		setImage(fileName);
		isHovered = false;
	}
	
	public void setImage(String fileName) {
		boolean old = true;
		
		if( image != null && ((isHovered && fileName.equals("hover" + name)) || (!isHovered && fileName.equals(name))) )
			return;		
		try {
			if(old)
				image = ImageIO.read( new File("Images\\"+ fileName +".png") );
			else
				image = getImageResource(fileName +".png");
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		isHovered = !isHovered;
	}
	
	private Image getImageResource(String name) {

		String url = ""+getClass().getResource(name);
		if (url.equals("null")||url==null) {
			System.out.println("DEBUG: image resource name: " + name);
			System.out.println("DEBUG: image resource url: " + url);
		}

		Image tbr = null;

		try {

			tbr = Toolkit.getDefaultToolkit().getImage(getClass().getResource(name));

//			long startTime = System.currentTimeMillis();
//			while (tbr.getWidth(canvas)<1 && System.currentTimeMillis() < startTime + 5000) {}

		} catch (Exception e) {
			System.out.println("Exception thrown for image " + name + ": ");
			e.printStackTrace();
		}

		return tbr;
	}
}
