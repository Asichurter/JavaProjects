package map;

import java.awt.Toolkit;

import tank_bullet.Bullet;

/**
 * 不可摧毁的地形类
 * @author Asichurter
 *
 */

public class IronWall extends BlockWall{
	
	private static final long serialVersionUID = -6605801527141072364L;
	
	private final int ARMOR = 1000;
	private final int IRONWALL_IDENTITY = 9;

	public IronWall(int x, int y, String des) {
		super(x, y, "");
		this.setIcon(Toolkit.getDefaultToolkit().getImage("Icon/地形/铁墙.png"));
		this.setArmor(ARMOR);
	}
	
	@Override
	/**
	 * 覆盖掉父类的可被摧毁的所有检测方法
	 */
	public void checkIcon() {}
	
	@Override
	/**
	 * 覆盖掉父类的可被摧毁的所有检测方法
	 */
	public void Crash(Bullet bullet) {}
	
	@Override
	/**
	 * 覆盖掉父类的可被摧毁的所有检测方法
	 */
	public void Crash(double v) {}
	
	@Override
	public int getMapIdentity() {
		return this.IRONWALL_IDENTITY;
	}
}
