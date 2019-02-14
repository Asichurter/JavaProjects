package reinforceANDsupply;

import java.awt.Toolkit;
import java.util.Random;

import buff.Buff;
import buff.Recovering;
import gameFrame.TankFrame;

public class RepairToolkit extends Supply{
	private double RECOVERY_D = 0.1;

	public RepairToolkit(int x, int y) {
		super(x, y, Toolkit.getDefaultToolkit().getImage("Icon/补给包/维修包.png"));
	}
	
	public RepairToolkit() {
		super(new Random().nextInt(TankFrame.WIDTH), new Random().nextInt(TankFrame.HEIGHT), Toolkit.getDefaultToolkit().getImage("Icon/补给包/维修包.png"));
	}
	
	public Buff getBuff() {
		return new Recovering(RECOVERY_D);
	}
}
