package tank_bullet;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * ̹�˷�����ڵ���ĸ���
 * @author Asichurter
 *
 */

public class Bullet {
	private int X;
	private int Y;
	public static final int V = 20;
	public static final int WIDTH = 20;
	public static final int HEIGHT = 10;
	private Dir direction;
	private double Damage = 25;
	private Image image;
	
	public Bullet(int x, int y, Dir dir) {
		this.X = x;
		this.Y = y;
		this.direction = dir;
	}
	
	public Bullet() {}
	
	public Bullet(int x, int y, Dir dir, double damage, boolean isEnemy) {
		this.X = x;
		this.Y = y;
		this.direction = dir;
		this.Damage = damage;
		if (!isEnemy)
			this.image = Toolkit.getDefaultToolkit().getImage("Icon/�ӵ�/�ӵ�" + this.direction.getDes() + ".png");
		else this.image = Toolkit.getDefaultToolkit().getImage("Icon/�ӵ�/�����ӵ�" + this.direction.getDes() + ".png");
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return this.image;
	}
	
	public void setDamage(double damage) {
		this.Damage = damage;
	}
	
	public double getDamage() {
		return this.Damage;
	}
	
	/**
	 * ����ͬʱ����x��y����ֵ
	 * @param x ���õ�x����ֵ
	 * @param y ���õ�y����ֵ
	 */
	public void setXY(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
	public void setDir(Dir dir) {
		this.direction = dir;
	}
	
	public void setDir(Dir dir, boolean isEnemy) {
		this.direction = dir;
		if (isEnemy)
			this.image = Toolkit.getDefaultToolkit().getImage("Icon/�ӵ�/�����ӵ�" + this.direction.getDes() + ".png");
		else this.image = Toolkit.getDefaultToolkit().getImage("Icon/�ӵ�/�ӵ�" + this.direction.getDes() + ".png");
	}
	
	public Dir getDir() {
		return this.direction;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
}
