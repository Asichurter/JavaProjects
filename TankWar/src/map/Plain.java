package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;

public class Plain extends MapType{
	
	private int PLAIN_IDENTY= 8;
	private int VM_D = 1;
	private NullMap map = new NullMap();										//使用代理，来用平原代替NullMap类
	
	public Plain(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/地形/平原.png"), true, false, true, x, y);
	}
	
	public Plain() {
		super();
	}
	
	//用于返回一个指定类型的实例（可能存在BUG）
	public  Plain getInstance() {
		return new Plain();
	}
	
	//地形增强坦克的方法
	public void Buff(AbstractTank tank) {
		tank.setVm(tank.getVm() + this.VM_D);
		this.map.Buff(tank);
	}
	
	//地形削弱坦克的方法
	public void DeBuff(AbstractTank tank) {
		this.map.DeBuff(tank);
	}
	
	//重置坦克的状态
	public void resetTank(AbstractTank tank) {
		tank.setVm(tank.getVm() - this.VM_D);
		this.resetTank(tank);
	}
	
	public String toString() {
		return "平原";
	}
	
	//返回空地图的标识数字
	@Override
	public int getMapIdentity() {
		return this.PLAIN_IDENTY;
	}

}
