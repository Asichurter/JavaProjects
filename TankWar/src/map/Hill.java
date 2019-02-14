package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class Hill extends MapType{
	private double SPEED_D = -2;
	private double ACCELERATE_D = -0.2;
	private double VM_TOTAL = 4;
	private double DAMAGE_D = -10;
	private double ARMOR_D = 6;	
	private final int HILL_IDENTY = 5;
	
	//用于在地图中创建地形单元的构造器
	public Hill(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/地形/山地.png"), true, false, true, x, y);
	}
	
	//用于在坦克的地形发生改变的时候，为坦克的地型实例域提供对象的构造器
	public Hill() {
		super();
	}
	
	@Override
	//用于返回一个指定类型的实例（可能存在BUG）
	public Hill getInstance() {
		return new Hill();
	}
	
	@Override
	//地形增强坦克的方法
	public void Buff(AbstractTank tank) {
		tank.setArmor(tank.getArmor() + this.ARMOR_D);
	}
	
	@Override
	//地形削弱坦克的方法
	public void DeBuff(AbstractTank tank) {
		tank.setVm(VM_TOTAL);
		if (tank.getV() > tank.getVm() - this.SPEED_D)
			tank.resetV(tank.getVm());
		else if (tank.getV() > -(this.SPEED_D) && tank.getV() < tank.getVm() - this.SPEED_D)
			tank.resetV(tank.getV() + this.SPEED_D);
		else if (tank.getV() <= -(this.SPEED_D))
			tank.resetV(0);
		if (tank.getA() > -(this.ACCELERATE_D))
			tank.setA(tank.getA() + this.ACCELERATE_D);
		else tank.setA(0);
		if (tank.getBulletDamage() > -(this.DAMAGE_D))
			tank.setBulletDamage(tank.getBulletDamage() + this.DAMAGE_D);
	}
	
	@Override
	//重置坦克的状态
	public void ResetTank(AbstractTank tank) {
		if (tank instanceof Tank)
			tank.setVm(Tank.TANK_VM);
		else if (tank instanceof EnemyTank)
			tank.setVm(EnemyTank.ENEMYTANK_VM);
		if (tank.getArmor() >= this.ARMOR_D)
			tank.setArmor(tank.getArmor() - this.ARMOR_D);
		tank.setA(tank.getA() - this.ACCELERATE_D);
		tank.setBulletDamage(tank.getBulletDamage() - this.DAMAGE_D);
	}
	
	@Override
	public String toString() {
		return "山地(护甲大幅增加，最大速度、速度和加速度大幅减小)";
	}
	
	//返回用于 表示草地地形的标识数字
	@Override
	public int getMapIdentity() {
		return this.HILL_IDENTY;
	}
}
