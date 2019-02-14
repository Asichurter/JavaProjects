package map;

import tank_bullet.AbstractTank;

public class NullMap extends MapType{
	
	private final int NULLMAP_IDENTY = 1;
	
	//空构造器，用于返回一个指定类型的类
	public NullMap() {
		super();
	}

	//用于返回一个指定类型的实例（可能存在BUG）
	public  NullMap getInstance() {
		return new NullMap();
	}
	
	//地形增强坦克的方法
	public void Buff(AbstractTank tank) {}
	
	//地形削弱坦克的方法
	public void DeBuff(AbstractTank tank) {}
	
	//重置坦克的状态
	public void resetTank(AbstractTank tank) {}
	
	public String toString() {
		return "空";
	}
	
	//返回空地图的标识数字
	@Override
	public int getMapIdentity() {
		return this.NULLMAP_IDENTY;
	}
}
