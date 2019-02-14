package reinforceANDsupply;

import java.awt.Toolkit;
import java.util.Random;

import buff.Buff;
import buff.LoadTimeDecresing;
import gameFrame.TankFrame;

public class FireLoadkit extends Supply{
	private int Decrease = 2000;
	private int LastTime = 400;

	public FireLoadkit(int x, int y) {
		super(x, y, Toolkit.getDefaultToolkit().getImage("Icon/补给包/开火包.png"));
	}
	
	public FireLoadkit() {
		super(new Random().nextInt(TankFrame.WIDTH), new Random().nextInt(TankFrame.HEIGHT), Toolkit.getDefaultToolkit().getImage("Icon/补给包/开火包.png"));
	}
	
	public Buff getBuff() {
		return new LoadTimeDecresing(this.Decrease, this.LastTime);
	}
}
