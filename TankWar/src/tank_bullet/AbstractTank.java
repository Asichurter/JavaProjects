package tank_bullet;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import buff.Buff;
import buff.LoadTimeDecresing;
import gameFrame.Score;
import gameFrame.TankFrame;
import map.*;

/**
 * 所有坦克的父类，含有坦克的基本元素和方法
 * @author Asichurter
 */

public abstract class AbstractTank {
	private int X;
	private int Y;
	private double V;																					//目前坦克的速度
	private double A;																					//目前坦克的加速度
	private double FIRE_TIME;													//目前炮的冷却时间
	private Dir direction = Dir.RIGHT;								//目前坦克的方向
	private boolean LIVE;															//目前坦克的生存状态
	private boolean IF_CRASHED = false;						//坦克是否已经遭到了撞击
	private int HEALTH_WIDTH;
	public static int HEALTH_HEIGHT = 10;							//坦克血量条的高度
	private double HEALTH;																//坦克的血量
	private MapType LAST_TYPE = new NullMap();												//坦克上一次地形
	private double ARMOR = 5;														//坦克的护甲
	private int LOADFT = 2000;																//开火总冷却时间
	private double VM = 10;																		//最大速度限制
	private boolean IF_CANFIRE = true;									//是否能在一定条件下上开火
	private double DAMAGE = 25;													//坦克子弹的伤害
	private double FULL_HEALTH =100;
	private LinkedList<Buff> Buffs = new LinkedList<>();								//描述坦克状态的实例域
	private boolean ifHasBulletSheild = false;																//描述是否有子弹护盾
	
	
	public AbstractTank(int x, int y, double v, int HEALTH_WIDTH, double Vm) {
		this.X = x;
		this.Y = y;
		this.V = v;
		this.A = 0.5;
		this.FIRE_TIME = 0;
		this.LIVE = true;
		this.HEALTH_WIDTH = HEALTH_WIDTH;
		this.VM = Vm;
	}
	
	/**
	 * 为坦克添加一个buff
	 * @param buff 待添加的buff接口
	 */
	public void addBuff(Buff buff) {
		this.Buffs.add(buff);
	}
	
	/**
	 * 坦克的所有buff进行工作，只有主动执行的buff才会执行，同时还会将buff时间到限的buff移除
	 */
	public void allBuffsWork() {
		if (Buffs.size() == 0)
			return;
		ListIterator<Buff> iter = this.Buffs.listIterator();
		while (iter.hasNext()) {
			Buff buff = iter.next();
			if(buff.getTimes() > 0) {
				buff.Work(this);
			}
			else {
				if (buff instanceof LoadTimeDecresing)
					TankFrame.ifLoadTimeDecresed = false;
				iter.remove();
			}
		}
	}
	
	public boolean getIfHasBulletShield() {
		return this.ifHasBulletSheild;
	}
	
	public void setIfHasBulletShield(boolean If) {
		this.ifHasBulletSheild = If;
	}
	
	public double getFullHealth() {
		return this.FULL_HEALTH;
	}
	
	public void setFullHealth(double value) {
		this.FULL_HEALTH = value;
	}
	
	public double getBulletDamage() {
		return this.DAMAGE;
	}
	
	public void setBulletDamage(double damage) {
		this.DAMAGE = damage;
	}
	
	public void setIfCanFire(boolean If) {
		this.IF_CANFIRE = If;
	}
	
	public boolean getIfCanFire() {
		return this.IF_CANFIRE;
	}
	
	public void setVm(double Vm) {
		this.VM = Vm;
	}
	
	public double getVm() {
		return this.VM;
	}
	
	//设置/获得开火冷却总时间
	public void setLoadFT(int load_ft) {
		this.LOADFT = load_ft;
	}
	
	public int getLoadFT() {
		return this.LOADFT;
	}
	
	/**
	 * 得到坦克的当前地形的访问器
	 * @return 坦克当前所处的地形
	 */
	public MapType getMapType() {
		return this.LAST_TYPE;
	}
	
	public double getArmor() {
		return this.ARMOR;
	}
	
	public void setArmor(double armor) {
		this.ARMOR = armor;
	}
	
	public int getHealthWidth() {
		return this.HEALTH_WIDTH;
	}
	
	public double getHealth() {
		return this.HEALTH;
	}
	
	public void setHealth(double health) {
		this.HEALTH = health;
	}
	
	public void setLive(boolean live) {
		this.LIVE = live;
	}
	
	public boolean getLive() {
		return this.LIVE;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
	
	public void setXY(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
	public void resetV(double v) {
		this.V = v;
	}
	
	public void setDir(Dir dir) {
		this.direction = dir;
	}
	
	public Dir getDir() {
		return  this.direction;
	}
	
	public double getV() {
		return this.V;
	}
	
	public void setA(double a) {
		this.A = a;
	}
	
	public double getA() {
		return this.A;
	}
	
	//没有为抽象父类编写setV方法
	
	//获得当前开火冷却时间
	public double getFT() {
		return this.FIRE_TIME;
	}
	
	public void setFT(double FT) {
		this.FIRE_TIME = FT;
	}
	
	public boolean getIfCrashed() {
		return this.IF_CRASHED;
	}
	
	public void setIfCrashed(boolean ifcrashed) {
		this.IF_CRASHED = ifcrashed;
	}
	
	public void setMapType(MapType type) {
		this.LAST_TYPE = type;
	}
	
	//用于限速
	public void restrictVtoMax() {
		
	}
	
	/**
	 * 检测地形是否发生了变化。如果发生了变化，将会取消之前地形的buff，同时刷新当前的地形，新增当前地形的buff效果
	 * @param map 当前系统加载的地图
	 */
	public void refreshMapType(MyMap map) {
		boolean identifier = false;
		//ListIterator<LinkedList<MapType>> all_iter = map.allTypes.listIterator();
		ExternLoop:
		for (LinkedList<MapType> linkedlist : map.allTypes) {
			for (MapType maptype: linkedlist) {
				if (maptype.ifContains(this.X, this.Y)) {																																									//检测坦克是否已经在某个地形之内了，用于避免重置地形为null
					identifier = true;
					if (this.LAST_TYPE.getClass() != maptype.getClass()) {
						LAST_TYPE.ResetTank(this);																																																								//先将坦克的状态重置为进入地形之前
						LAST_TYPE = maptype.getInstance();																																																			//再更新坦克当前的地形
						LAST_TYPE.Buff(this);
						LAST_TYPE.DeBuff(this);
						break ExternLoop;																																																																	//一旦第一次发现满足地形变化，直接跳出循环
					}
				}
			}
		}
		if (!identifier && LAST_TYPE.getClass() != new NullMap().getClass()){ 																										//用于在没有在任何地形之内时，将地形置为null型
			LAST_TYPE.ResetTank(this);
			this.LAST_TYPE = new NullMap();		
		}
		/*while (all_iter.hasNext()) {
			ListIterator<MapType> iter = all_iter.next().listIterator();
			while(iter.hasNext()) {
				MapType map_unit = iter.next();													
				if (LAST_TYPE.getClass() != map_unit.getClass() && LAST_TYPE.ifContains(this.X, this.Y)) {																																		//用于检查是否坦克进入了不同的地形
					LAST_TYPE.ResetTank(this); 																																																									//先将坦克的状态重置为进入地形之前
					LAST_TYPE = map_unit.getInstance();																																																			//再更新坦克当前的地形
					LAST_TYPE.Buff(this);
					LAST_TYPE.DeBuff(this);
					break ExternLoop;																																																																	//一旦第一次发现满足地形变化，直接跳出循环
				}
			}
			if(!(this.LAST_TYPE instanceof NullMap))
				this.LAST_TYPE = new NullMap();
		}*/
	}
	
	/**
	 * 根据当前的方向，用于将坦克进行后退操作。分为测试型后退和非测试性后退。测试性后退用于配合测试性前进用于判断是否应该刷新地形状态
	 * 如果不是测试性后退（例如撞到了地形而反弹），将会刷新地形
	 * @param times 坦克后退的步数
	 * @param ifTest 是否在进行测试性后退操作
	 */
	public void Back(int times, boolean ifTest) {
		for (int i =1; i <= times; i++) {
			switch(this.getDir()) {		
				case UP:
					this.setXY(this.getX(), this.getY() + (int)this.getV());
					break;
			
				case DOWN:
					this.setXY(this.getX(), this.getY() - (int)this.getV());
					break;
			
				case LEFT:
					this.setXY(this.getX() + (int)this.getV(), this.getY());
					break;
			
				case RIGHT:
					this.setXY(this.getX() - (int)this.getV(), this.getY());
					break;			
				default:
					break;
			}
		}
		if (!ifTest)
			this.refreshMapType(TankFrame.MY_MAP);
	}
	
	/**
	 * 表征坦克遭遇碰撞的方法
	@Override
	@param v 碰撞相对速度
	@param type 表征是与其他坦克发生碰撞还是与地形发生碰撞的参数
	@param score 计分对象
	*/
	public void Crash(double v, boolean type, Score MyScore) {
		
	}
	
	//被子弹击中
	public void beHit(Bullet bullet, Score score) {
		
	}
	
	/**
	 * 获得buff的迭代器
	 * @return 当前坦克的buff迭代器
	 */
	public ListIterator<Buff> getBuffsIter(){
		return this.Buffs.listIterator();
	}
	
	/**
	 * 坦克刹车
	 */
	public void brake() {
		this.V = 0;
	}
}
