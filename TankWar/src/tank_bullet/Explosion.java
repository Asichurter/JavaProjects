package tank_bullet;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * 爆炸的父类，用于描述坦克子弹的爆炸效果
 * @author Asichurter
 *
 */

public class Explosion{
	
	private int X;
	private int Y;
	public static final int TOTAL_TIME = 300;
	public static final int W = 45;
	private int NOW_TIME = Explosion.TOTAL_TIME;
	private int REDUCE_EACHTIME;
	private Image image = Toolkit.getDefaultToolkit().getImage("Icon/爆炸.png");

	public Explosion(int x, int y, int reduce) {
		this.X = x;
		this.Y = y;
		this.REDUCE_EACHTIME = reduce;
	}
	
	public Explosion(int x, int y, int reduce, int nowtime) {
		this.X = x;
		this.Y = y;
		this.REDUCE_EACHTIME = reduce;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
	
	public int getNowTime() {
		return this.NOW_TIME;
	}
	
	public Image getImage() {
		return this.image;
	}
	
	/**
	 * 刷新爆炸效果的持续时间
	 */
	public void refreshShowTime() {
		this.NOW_TIME -= this.REDUCE_EACHTIME;
		if (NOW_TIME < 0)
			NOW_TIME = 0;
	}
}
