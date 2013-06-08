import java.awt.Canvas;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.*;
import javax.swing.JFrame;
import java.io.File;
import javax.imageio.ImageIO;

import java.util.HashSet;
import java.util.Scanner;
import java.util.ArrayList;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Scanner;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScreenSaver implements Runnable
{
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	MouseControl mouse;
	KeyControl key;

	private static final int WIDTH = 1400;
	private static final int HEIGHT = 700;
	private static final int PANALWIDTH = 160;
	private static final int SPACEWIDTH = WIDTH-PANALWIDTH;
	
	private HashSet<PanalElement> panalElems = new HashSet<PanalElement>();
	private Button gravityUp;
	private Button gravityDown;
	private Button pause;
	private PanalElement selectedBoxElem;

	private ArrayList<Box> boxes = new ArrayList<Box>();
	private Box selectedBox;
	private Rectangle selectedRect;
	private static ArrayList<int[]> centersOfGravity = new ArrayList<int[]>();
	
	private int nWH = 0;
	private int nLW = 200;
	private int nHW = 0;
	private static double gravity = 1.0;
	private static boolean isPaused = true;

	public ScreenSaver()
	{
		frame = new JFrame("ScreenSaver");

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		mouse = new MouseControl();
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);

		key = new KeyControl();
		canvas.addKeyListener(key);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();

	}
	
	private class KeyControl extends KeyAdapter	{
		public void keyPressed(KeyEvent e) {
			if( e.getKeyCode() == KeyEvent.VK_SPACE )
				playPause();
			if( e.getKeyCode() == KeyEvent.VK_UP )
				gravity += .1;
			if( e.getKeyCode() == KeyEvent.VK_DOWN )
				gravity -= .1;
		}

		public void keyReleased(KeyEvent e)	{
		}

	}

	private class MouseControl extends MouseAdapter	{
		public void mouseClicked(MouseEvent e) {
			if( e.isAltDown() ) 
				centersOfGravity.add( new int[]{e.getX(), e.getY()});
			
			//panalElements
			for(PanalElement p : panalElems)
				if( p.contains(e.getPoint()) ) {
					if( p == gravityUp  )
						gravity += .1;
					if( p == gravityDown )
						gravity -= .1;
					if( p == pause )
						playPause();
				}
			
			//select a box
			for(Box b : boxes)
				if( b.contains(e.getPoint()) ) {
					selectedBox = b;
					int selectedPadding = selectedBoxElem.width/2-b.width/2;
					selectedRect = new Rectangle( selectedBoxElem.x+selectedPadding,
												  selectedBoxElem.y + selectedPadding,
												  b.length, b.length);
					break;
				}
		}
		public void mouseDragged(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {
			for(PanalElement p : panalElems)
				if( p.contains(e.getPoint()) )
					p.setImage("hover"+ p.name);
				else
					p.setImage(p.name);
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseWheelMoved(MouseEvent e) {}
	}
	
	public void playPause() {
		if(!isPaused) {
			pause.setImage("play");
			pause.name = "play";
		}
		else {
			pause.setImage("pause");
			pause.name = "pause";
		}
		isPaused = !isPaused;
	}

	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;
	boolean running = true;

	public void run()
	{

		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;
		int deltaTime;

		init();

		while(running)
		{
			beginLoopTime = System.nanoTime();

			render();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			deltaTime = (int) ((currentUpdateTime - lastUpdateTime)/(1000*1000));
			update(deltaTime);

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;

			if(deltaLoop <= desiredDeltaLoop)
			{
				try
				{
					Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
				} catch(InterruptedException e) { /* Do nothing */ }
			}
		}
	}

	private void render()
	{
		Graphics2D g = (Graphics2D)bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}

	public static void main(String[] args)
	{
		ScreenSaver ex = new ScreenSaver();
		new Thread(ex).start();
	}

	//edit init------------------------------------------------------------------
	private void init()	{ 
		addBoxes();
		panalInit();	
	}
	private void addBoxes() {
		while(boxes.size() < nHW) {
			Box newBox = new HeavyWeight(System.currentTimeMillis());
			if( !overlaps(newBox) )
				boxes.add(newBox);
		}
		while(boxes.size() < nHW+nWH) {
			Box newBox = new WhiteHole(System.currentTimeMillis());
			if( !overlaps(newBox) )
				boxes.add(newBox);
		}
		while(boxes.size() < nHW+nWH+nLW) {
			Box newBox = new LightWeight(System.currentTimeMillis());
			if( !overlaps(newBox) )
				boxes.add(newBox);
		}
	}
	private boolean overlaps(Box b1) {
		for(Box b : boxes)
			if( b.intersects(b1) )
				return true;
		return false;
	}
	private void panalInit() {
		gravityUp = new Button("up", SPACEWIDTH+50+65, HEIGHT/2-9, 15, 15);
		gravityDown = new Button("down", gravityUp.x, gravityUp.y+gravityUp.height+5, gravityUp.width, gravityUp.height);
		panalElems.add(gravityUp);
		panalElems.add(gravityDown);
		
		pause = new Button("play", SPACEWIDTH+PANALWIDTH/2-20, 20, 40, 40);
		panalElems.add(pause);
		
		int selectedWidth = 100;
		selectedBoxElem = new PanalElement("selected", SPACEWIDTH+PANALWIDTH/2-selectedWidth/2, 100, selectedWidth, selectedWidth*3/2);
		panalElems.add(selectedBoxElem);
	}
	//edit update----------------------------------------------------------------
	protected void update(int deltaTime) {
		if(isPaused)
			return;
		for(Box b : boxes)
			b.update(deltaTime);
		handleCollisions();
	}
	public void handleCollisions() {
		for(int i = 0; i < boxes.size(); i++) {
			Box b1 = boxes.get(i);
			for(int j = i+1; j < boxes.size(); j++) {
				Box b2 = boxes.get(j);
				if( b1.intersects(b2) ) {
					Box btemp = new WhiteHole(b1);
					b1.collideWith(b2);
					b2.collideWith(btemp);
				}
			}
		}
	}
	//edit render----------------------------------------------------------------
	protected void render(Graphics2D g)	{
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		drawPanal(g);
		
		g.setColor(Color.GREEN);
		for(int[] c : centersOfGravity)
			g.drawRect(c[0]-2, c[1]-2, 4, 4);
		for(Box b : boxes) {
			if(b == selectedBox) {
				g.setColor(Color.red);
				g.fillRect(b.x-2, b.y-2, b.width+4, b.height+4);
			}
			g.setColor(b.getColor());
			g.fill(b);
			drawPaths(g, b);
		}
	}
	public void drawPanal(Graphics2D g) {
		g.setColor( new Color(72,83,235) );
		g.fillRect(WIDTH-PANALWIDTH, 0, PANALWIDTH, HEIGHT);
		g.setColor(Color.black);
		int panalBoarder = 10;
		g.fillRect(WIDTH-PANALWIDTH+panalBoarder, panalBoarder, PANALWIDTH-2*panalBoarder, HEIGHT-2*panalBoarder);
		
		g.setColor(Color.white);
		g.setFont( new Font("MONOSPACE", Font.BOLD, 13) );
		g.drawString( String.format("Gravity = %4.2f", gravity), SPACEWIDTH+15, HEIGHT/2+15);
		
		for(PanalElement e : panalElems) {
			if( e.image != null )
				g.drawImage(e.image, e.x, e.y, e.width, e.height, canvas);
		}
		
		if(selectedBox != null) {
			if( selectedBox.getColor().equals(Color.white) ) {
				g.setColor(Color.black);
				g.fillRect(selectedRect.x-2, selectedRect.y-2, selectedRect.width+4, selectedRect.height+4);
			}
			g.setColor( selectedBox.getColor() );
			g.fill( selectedRect );
			g.setColor(Color.black);
			g.drawString(String.format("vx = %6.4f", selectedBox.vx), selectedBoxElem.x+13, selectedBoxElem.y+selectedBoxElem.width+10);
			g.drawString(String.format("vy = %6.4f", selectedBox.vy), selectedBoxElem.x+13, selectedBoxElem.y+selectedBoxElem.width+22);
			g.drawString(String.format("v  = %6.4f", Math.sqrt(selectedBox.vy*selectedBox.vy + selectedBox.vx*selectedBox.vx)),
										selectedBoxElem.x+13, selectedBoxElem.y+selectedBoxElem.width+34);
		}
	}
	public void drawPaths(Graphics2D g, Box b) {
		Color c = Color.cyan;
		int N = 100;
		for(int i = b.pastLocs.size()-1; i > 0 && b.pastLocs.size() - i < N; i--) {
			c = new Color( c.getRed(), c.getBlue(), c.getGreen(), c.getAlpha()-255/N);
			g.setColor( c );
			int[] prevLoc = b.pastLocs.get(i-1);
			int[] curLoc = b.pastLocs.get(i);
			g.drawLine(prevLoc[0], prevLoc[1], curLoc[0], curLoc[1]);
		}
	}
	
	public static ArrayList<int[]> getCentersOfGravity() {
		return centersOfGravity;
	}	
	public static double getGravity() {
		return gravity;
	}
	public static int getWidth() {
		return WIDTH;
	}
	public static int getPanalWidth() {
		return PANALWIDTH;
	}
	public static int getSpaceWidth() {
		return SPACEWIDTH;
	}
	public static int getHeight() {
		return HEIGHT;
	}
}