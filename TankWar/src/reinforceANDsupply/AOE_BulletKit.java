package reinforceANDsupply;

import java.awt.Toolkit;
import java.util.Random;

import gameFrame.TankFrame;

public class AOE_BulletKit extends Supply{

	public AOE_BulletKit(int x, int y) {
		super(x, y, Toolkit.getDefaultToolkit().getImage("Icon/补给包/AOE导弹包.png"));
	}
	
	public AOE_BulletKit() {
		super(new Random().nextInt(TankFrame.WIDTH), new Random().nextInt(TankFrame.HEIGHT), Toolkit.getDefaultToolkit().getImage("Icon/补给包/AOE导弹包.png"));
	}
}
