package reinforceANDsupply;

import java.awt.Toolkit;
import java.util.Random;

import gameFrame.TankFrame;

public class PrecisionAttackKit extends Supply{

	public PrecisionAttackKit(int x, int y) {
		super(x, y, Toolkit.getDefaultToolkit().getImage("Icon/������/��׼�����.png"));
	}
	
	public PrecisionAttackKit() {
		super(new Random().nextInt(TankFrame.WIDTH), new Random().nextInt(TankFrame.HEIGHT), Toolkit.getDefaultToolkit().getImage("Icon/������/��׼�����.png"));
	}

}
