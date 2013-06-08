import java.awt.Color;

public class HeavyWeight extends Box {
	private final double vi = 0.05;
	
	public HeavyWeight(long curTime) {
		super(70);
		mass = 4;
		force = 0;
		color = Color.green;
	}
	
	public HeavyWeight(Box other) {
		super(other);
	}
	public void setInitialVelocities() {
		double[] u = getRandUnitVector();
		vx = u[0]*vi;
		vy = u[1]*vi;
	}		
}