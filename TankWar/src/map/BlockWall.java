package map;

import java.awt.Toolkit;

import tank_bullet.Bullet;

public class BlockWall extends MapType{
	/**
	 * 
	 */
	private static final long serialVersionUID = 953641468130686724L;
	private final boolean CAN_CROSS = false;
	private final boolean CAN_DESTROY = true;
	private double HEALTH = 200;
	private final double FULL_HEALTH = 200;
	private double ARMOR = 5;
	private final Toolkit tool = Toolkit.getDefaultToolkit();
	private final int BLOCKWALL_IDENTY = 6;
	
	public BlockWall(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/地形/砖墙.png"), false, true, false, x, y);
	}
	
	/**
	 * 用于给子类使用的构造器
	 * @param x X坐标
	 * @param y Y坐标
	 */
	public BlockWall(int x, int y) {

	}
	
	@Override
	public double getHealth() {
		return this.HEALTH;
	}
	
	public void setArmor(double a) {
		this.ARMOR = a;
	}
	
	private double caculateDamage(Bullet bullet) {
		if (bullet.getDamage() > this.ARMOR)	
			return bullet.getDamage() - this.ARMOR;
		else return 0;
	}
	
	private double caculateDamage(double v) {
		if (Math.pow(v, 2)/7 > this.ARMOR)	
			return Math.pow(v, 2)/7 - this.ARMOR;
		else return 0;
	}
	
	
	//被子弹击中
	public void beHit(Bullet bullet) {
		if (caculateDamage(bullet) < this.HEALTH )
			HEALTH -= caculateDamage(bullet);
		else this.HEALTH = 0;
	}
	
	//被坦克撞击
	public void beHit(double v) {
		//System.out.println("这次撞击速度为"+ v +"\n对墙壁造成伤害为" + caculateDamage(v));											//调试语句
		if (caculateDamage(v) < this.HEALTH)
			HEALTH -= caculateDamage(v);
		else this.HEALTH = 0;
	}
	
	@Override
	//描述与子弹或者坦克发生撞击的动作
	public void Crash(Bullet bullet) {
		this.beHit(bullet);
		this.checkIcon();
	}
	
	public void Crash(double v) {
		this.beHit(v);
		this.checkIcon();
	}
	
	//用于检查图标是否有变化，只在被撞击behit的时候调用
	public void checkIcon() {
		if (this.HEALTH > 3*this.FULL_HEALTH/4)
			this.setIcon(tool.getImage("Icon/地形/砖墙.png"));
		else if (this.HEALTH > this.FULL_HEALTH/2)
			this.setIcon(tool.getImage("Icon/地形/砖墙1.png"));
		else if (this.HEALTH > this.FULL_HEALTH/4)
			this.setIcon(tool.getImage("Icon/地形/砖墙2.png"));
		else this.setIcon(tool.getImage("Icon/地形/砖墙3.png"));
	}
	
	public int getMapIdentity() {
		return this.BLOCKWALL_IDENTY;
	}
}
