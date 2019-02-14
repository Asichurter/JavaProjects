package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class Desert extends MapType{
	
	private double ACCELERATE_D = 0.2;																//加速度变化量	
	private double SPEED_D = 2;																						//速度变化量
	private double VM_D = 1;																								//最大速度变化量
	private int DESERT_IDENTY = 3;																			//地形标识符
	private int LOADFT_D = 1000;																					//开火冷却时间变化量
	
	public Desert(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/地形/沙漠.jpg"), true, false, true, x, y);
	}
	
	public Desert() {
		super();
	}
	
	@Override
	//用于返回一个指定类型的实例（可能存在BUG）
	public Desert  getInstance() {
		return new Desert();
	}
	
	@Override
	//地形增强坦克的方法
	public void Buff(AbstractTank tank) {
		double dV;
		tank.setVm(tank.getVm() + VM_D);
		dV = tank.getVm();
		if (tank.getV() + SPEED_D <= dV)
			tank.resetV(tank.getV() + SPEED_D);
		else tank.resetV(dV);
		tank.setA(tank.getA() + ACCELERATE_D);
	}
	
	@Override
	//地形削弱坦克的方法
	public void DeBuff(AbstractTank tank) {
		tank.setLoadFT(tank.getLoadFT() + LOADFT_D);
	}
	
	@Override
	//重置坦克的状态
	public void ResetTank(AbstractTank tank) {
		tank.setLoadFT(tank.getLoadFT() - LOADFT_D);
		tank.setVm(tank.getVm() - VM_D);
		if (tank.getV() - SPEED_D >= 3)
			tank.resetV(tank.getV() - SPEED_D);
		else tank.resetV(1);
		tank.setA(tank.getA() - ACCELERATE_D);
	}
	
	@Override
	public String toString() {
		return "沙漠(最大速度、加速度和速度提升,开火时间增加)";
	}
	
	//返回用于 表示草地地形的标识数字
	@Override
	public int getMapIdentity() {
		return this.DESERT_IDENTY;
	}
}
