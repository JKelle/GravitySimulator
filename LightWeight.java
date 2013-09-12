import java.awt.Color;

public class LightWeight extends Box {
	private final double vi = .05;
	
	public LightWeight(long curTime) {
		super(5);
		mass = .25;
		force = 0;
		color = Color.magenta;
	}
	
	public LightWeight(Box other) {
		super(other);
	}
	public void setInitialVelocities() {
		double[] u = getRandUnitVector();
		vx = u[0]*vi;
		vy = u[1]*vi;
	}	
}
