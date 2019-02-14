package map;

/*
 * 用于标识地形的标识符：
 * 0:原始父类
 * 1:空地图
 * 2:草地
 */

import java.awt.Image;                                                                                                                                                                                                                                                                                       
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;

import mapEditor.MapData;
import tank_bullet.*;

public  class MapType implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4795751321257096073L;
	private int X;
	private int Y;
	public static int W = 50;																																																					//宽度默认值
	private Image image = null;
	private boolean canCross = true;																																											//默认属性
	private boolean canDestroy = false;
	private boolean canFire = true;																																											//能否在该地形下开火
	
	public MapType(Image image, boolean canCross, boolean canDestroy, boolean canFire, int X, int Y) {										//用于子类调用的超类构造器
		this.image = image;
		this.canCross = canCross;
		this.canDestroy = canDestroy;
		this.canFire = canFire;
		this.X = X;
		this.Y = Y;
	}
	
	public MapType(Image image, int x, int y) {
		this.image = image;
		this.X = x;
		this.Y = y;
	}
	
	//空构造器，用于创建一个指定地图类型的类，默认为能通过能开火，不能摧毁
	public MapType() {
		
	}
	
	public boolean getIfCanFire() {
		return this.canFire;
	}
	
	public void setIfCanFire(boolean IfCanFire) {
		this.canFire = IfCanFire;
	}
	
	public <T extends MapType> T getInstance() {
		return null;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
	
	public void setIcon(Image image) {
		this.image = image;
	}
	
	public Image getIcon() {
		return this.image;
	}
	
	public boolean getIfCanCross() {
		return this.canCross;
	}
	
	public boolean getIfCanDestroy() {
		return this.canDestroy;
	}
	
	//地形对坦克的增强
	public void Buff(AbstractTank tank) {																			
		
	}
	
	//地形对坦克的削弱
	public void DeBuff(AbstractTank tank) {			
		
	}
	
	//坦克离开地形以后复原其状态
	public void ResetTank(AbstractTank tank) {
		
	}
	
	//判断坦克是否在该地形上的方法
	public boolean ifContains(int x, int y) {
		if (new Rectangle2D.Double(this.X - MapType.W/2, this.Y -MapType.W/2, MapType.W, MapType.W).contains(x, y))
			return true;
		else return false;
	}
	
	//返回地形的描述字符串，用于测试
	public String toString() {
		return null;
	}
	
	//返回用于标识地形的数字
	public int getMapIdentity() {
		return 0;
	}
	
	//返回能被摧毁地形的生命值
	public double getHealth() {
		return 1;
	}
	
	//设置能被摧毁地形的生命值
	public void setHealth(double d) {}
	
	//用于描述可被摧毁地形受到伤害时的方法
	public void Crash(Bullet bullet) {}
	
	public void Crash(double v) {}
	
	//用于可被摧毁地形的图标变化的方法
	public void checkIcon() {}
	
	public MapData getMapData() {
		return new MapData(this.X, this.Y, this.getMapIdentity());
	}
}
