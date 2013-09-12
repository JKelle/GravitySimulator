import java.awt.Color;

public class WhiteHole extends Box{
	private final double vi = .1;
	
	public WhiteHole(long curTime) {
		super(50);
		mass = 1;
		force = .00;
		color = Color.white;
	}
	public WhiteHole(Box other) {
		super(other);
	}
	public void setInitialVelocities() {
		double[] u = getRandUnitVector();
		vx = u[0]*vi;
		vy = u[1]*vi;
	}
}
