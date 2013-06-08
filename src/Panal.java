import java.awt.Rectangle;
import java.util.ArrayList;

public class Panal extends Rectangle{
	ArrayList<PanalElement> elems;
	
	public Panal() {
		super(ScreenSaver.getSpaceWidth(), 0, ScreenSaver.getPanalWidth(), ScreenSaver.getHeight());
		elems = new ArrayList<PanalElement>();
	}
}
