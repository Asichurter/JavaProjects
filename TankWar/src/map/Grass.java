package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;

public class Grass extends MapType{
	
	private static double SPEED_D = -1;
	private static double ARMOR_D= 3;
	private static double Accerate_D = -0.2;
	private final int GRASS_IDENTY = 2;
	
	public Grass(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/地形/草地.jpg"), true, false,true,  x, y);
	}
	
	//空构造器，用于返回一个指定类型的类
	public Grass() {
		super();
	}

	//用于返回一个指定类型的实例（可能存在BUG）
	public Grass  getInstance() {
		return new Grass();
	}
	
	//地形增强坦克的方法
	public void Buff(AbstractTank tank) {
		tank.setArmor(tank.getArmor() +Grass.ARMOR_D);
	}
	
	//地形削弱坦克的方法
	public void DeBuff(AbstractTank tank) {
		tank.setA(tank.getA() + Grass.Accerate_D);
		if (tank.getV() >= 1 - Grass.SPEED_D)
			tank.resetV(tank.getV() +Grass.SPEED_D);
		else if (tank.getV() > 1 && tank.getV() < 1 - Grass.SPEED_D)
			tank.resetV(1);
	}
	
	//重置坦克的状态
	public void ResetTank(AbstractTank tank) {
		tank.resetV(tank.getV() - Grass.SPEED_D);
		tank.setArmor(tank.getArmor() - Grass.ARMOR_D);
		tank.setA(tank.getA() - Grass.Accerate_D);
	}
	
	public String toString() {
		return "草地(护甲增加，速度、加速度减小)";
	}
	
	//返回用于表示草地地形的标识数字
	@Override
	public int getMapIdentity() {
		return this.GRASS_IDENTY;
	}
}
