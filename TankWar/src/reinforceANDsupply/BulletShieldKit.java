package reinforceANDsupply;

import java.awt.Toolkit;
import java.util.Random;

import buff.Buff;
import buff.BulletShield;
import gameFrame.TankFrame;

public class BulletShieldKit extends Supply{
	private int LastTime = 600;
	
	public BulletShieldKit(int x, int y) {
		super(x, y, Toolkit.getDefaultToolkit().getImage("Icon/������/���ܰ�.png"));
	}
	
	public BulletShieldKit() {
		super(new Random().nextInt(TankFrame.WIDTH), new Random().nextInt(TankFrame.HEIGHT), Toolkit.getDefaultToolkit().getImage("Icon/������/���ܰ�.png"));
	}
	
	public Buff getBuff() {
		return new BulletShield(LastTime);
	}

}
