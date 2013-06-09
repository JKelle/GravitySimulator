import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Box extends Rectangle{
	ArrayList<int[]> pastLocs;
	
	double centerx, centery;
	double rx, ry;	//position
	double vx, vy;	//velocity
	double ax, ay;	//acceleration
	double mass;
	double force;
	int length;
	Color color;
	
	public Box(int length) {
		super(	(int)(Math.random()*(ScreenSaver.getSpaceWidth()-length)),
				(int)(Math.random()*(ScreenSaver.getHeight()    -length)),
				length,
				length);
		pastLocs = new ArrayList<int[]>();
		setInitialVelocities();
		rx = x;
		ry = y;
		this.length = length;
		
		centerx = rx+length/2;
		centery = ry+length/2;
	}
	
	public Box(Box other) {
		super(other);
		rx = other.rx; ry = other.ry;
		vx = other.vx; vy = other.vy;
		ax = other.ax; ay = other.ay;
		mass = other.mass;
		
		pastLocs = new ArrayList<int[]>();
	}
	public void collideWith(Box b2) {
		vx = (mass - b2.mass)/(mass + b2.mass)*vx + 2*b2.mass/(mass + b2.mass)*b2.vx;
		vy = (mass - b2.mass)/(mass + b2.mass)*vy + 2*b2.mass/(mass + b2.mass)*b2.vy;
	}
	public Color getColor() {
		return color;
	}
	public abstract void setInitialVelocities();

	public double[] getRandUnitVector() {
		double a = Math.random() - 0.5;
		double b = Math.random() - 0.5;
		double mag = Math.sqrt(a*a+b*b);
		return new double[]{a/mag, b/mag};
	}
	
	public void update(int dt) {		
		double[] jerk = getRandUnitVector();
		ax = jerk[0]*force;
		ay = jerk[1]*force;
		
		ArrayList<int[]> gs = ScreenSaver.getCentersOfGravity();
		double grav = ScreenSaver.getGravity();
		
		for(int[] g : gs)
			if(g[0] >= 0) {			
				double[] r = new double[]{g[0]-centerx, g[1]-centery};
				double rmag = Math.sqrt(r[0]*r[0] + r[1]*r[1]);
				
				double[] rhat = new double[]{r[0]/rmag, r[1]/rmag};
				
				if(rmag != 0) {
					ax += grav*rhat[0]*.0001;
					ay += grav*rhat[1]*.0001;
				}
			}
			else
				ay -= g[2]*.001;
			
		
		vx += ax*dt;
		vy += ay*dt;

		double dx = vx*dt;
		double dy = vy*dt;
		
		int xLimit = ScreenSaver.getSpaceWidth()-(int)getWidth();
		int yLimit = ScreenSaver.getHeight()-(int)getHeight();
		
		if( rx + dx < 0 || rx + dx > xLimit ) {
			dx = 0;
			vx = -vx;
		}
		if( ry + dy < 0 || ry + dy > yLimit) {
			dy = 0;
			vy = -vy;
		}
		
		rx += dx;
		ry += dy;


		centerx = rx+length/2;
		centery = ry+length/2;
		
		x = (int)rx;
		y = (int)ry;
		pastLocs.add( new int[]{x+length/2,y+length/2} );
	}
}