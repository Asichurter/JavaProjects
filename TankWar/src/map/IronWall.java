package map;

import java.awt.Toolkit;

import tank_bullet.Bullet;

/**
 * ���ɴݻٵĵ�����
 * @author Asichurter
 *
 */

public class IronWall extends BlockWall{
	
	private static final long serialVersionUID = -6605801527141072364L;
	
	private final int ARMOR = 1000;
	private final int IRONWALL_IDENTITY = 9;

	public IronWall(int x, int y, String des) {
		super(x, y, "");
		this.setIcon(Toolkit.getDefaultToolkit().getImage("Icon/����/��ǽ.png"));
		this.setArmor(ARMOR);
	}
	
	@Override
	/**
	 * ���ǵ�����Ŀɱ��ݻٵ����м�ⷽ��
	 */
	public void checkIcon() {}
	
	@Override
	/**
	 * ���ǵ�����Ŀɱ��ݻٵ����м�ⷽ��
	 */
	public void Crash(Bullet bullet) {}
	
	@Override
	/**
	 * ���ǵ�����Ŀɱ��ݻٵ����м�ⷽ��
	 */
	public void Crash(double v) {}
	
	@Override
	public int getMapIdentity() {
		return this.IRONWALL_IDENTITY;
	}
}
