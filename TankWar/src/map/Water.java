package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class Water extends MapType{
	
	private double VM_TOTAL = 3;
	private double SPEED_D = -2;
	private double ACCELERATE_D = -0.3;
	private double ARMOR_D = 10;
	private final boolean IF_CANFIRE = false;
	private final int WATER_IDENTY = 4;
	
	public Water(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/地形/水域.png"), true, false, false, x, y);
	}
	
	public Water() {
		super();
		this.setIfCanFire(false);
	}
	
	@Override
	//用于返回一个指定类型的实例（可能存在BUG）
	public Water getInstance() {
		return new Water();
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
	}
	
	@Override
	public String toString() {
		return "水域(护甲提升，最大速度，加速度和速度减小，不能开火)";
	}
	
	//返回用于 表示草地地形的标识数字
	@Override
	public int getMapIdentity() {
		return this.WATER_IDENTY;
	}
}
