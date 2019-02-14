package tank_bullet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import gameFrame.Score;
import gameFrame.TankFrame;
import map.MapType;
import skill.AOE_Bullet;

/**
 * 继承自AbstractTank，我方坦克的坦克类
 * @author Asichurter
 *
 */

public class Tank extends AbstractTank {
	
	public List<Bullet> bullets;												//坦克的子弹集合
	public static double TANK_VM = 10;                                                        //最大速度限制
	public static double TANK_FULLHEALTH= 100;			//坦克的最大血量
	private static final int LOADFT = 4000;													//总冷却时间
	private double MY_DAMAGE = 50;	
	public static int AOE_BULLET_TOTAL = 3;														//AOE导弹总数
	private int AOE_BULLET_NUM = Tank.AOE_BULLET_TOTAL;																//AOE导弹数量
	public static final int AOE_TOTAL_TIME = 20000;
	private int AOE_TIME = Tank.AOE_TOTAL_TIME;
	private boolean ifHavePrecisionAtt = true;
	
	public static int W ;										//坦克宽度
	public static int R1 ;										//炮台半径
	
	public Tank(int x, int y, double v, int W, int R1) {
		super(x, y, v, 3*W/2, 10);
		Tank.W = W;
		Tank.R1 = R1;
		this.bullets = new LinkedList<>();
		this.setHealth(Tank.TANK_FULLHEALTH);										//将己方坦克的血量调整至己方血量的满血量
		this.setLoadFT(Tank.LOADFT);
		this.setBulletDamage(MY_DAMAGE);
		this.setFullHealth(TANK_FULLHEALTH);
	}
	
	/**
	 * 检查AOE导弹的装载情况。如果数量未达上限，则进行装载
	 * @param reduce 每一帧的装载量
	 */
	public void checkIfAOEReady(int reduce) {
		if (this.AOE_BULLET_NUM == Tank.AOE_BULLET_TOTAL) {										//如果发现AOE导弹的数量为满
			this.AOE_TIME = Tank.AOE_TOTAL_TIME;
			return;
		}
		if (this.AOE_BULLET_NUM < Tank.AOE_BULLET_TOTAL)										//如果AOE导弹的数量不满，则刷新冷却时间
			this.AOE_TIME -= reduce;
		if (this.AOE_TIME <= 0 && this.AOE_BULLET_NUM < Tank.AOE_BULLET_TOTAL) {													//如果发现AOE导弹冷却完毕而且数量没有达到上限
			this.AOE_BULLET_NUM++;
			this.AOE_TIME = Tank.AOE_TOTAL_TIME;
		}
	}
	
	public void setIfHavePreAtt(boolean ifH) {
		this.ifHavePrecisionAtt = ifH;
	}
	
	public boolean getIfHavePreAtt() {
		return this.ifHavePrecisionAtt;
	}
	
	public int getAOEtime() {
		return this.AOE_TIME;
	}
	
	public int getAOEBulletNum() {
		return this.AOE_BULLET_NUM;
	}
	
	public void setAOEBulletNum(int num) {
		this.AOE_BULLET_NUM = num;
	}
	
	/**
	 * 描述我方坦克被子弹击中的方法
	@Override
	@param bullet 击中我方坦克的子弹
	@param socre 计分对象
	*/
	public void beHit(Bullet bullet, Score score) {
		double health = this.getHealth() - caculateHitDamage(bullet);
		if (health <= 0)																																			//被击中后血量降至0以后直接视为死亡
			this.setLive(false);
		else {
			setHealth(health);
			score.setEnemyDamage(score.getEnemyDamage() + caculateHitDamage(bullet));
		}
	}
	
	/**
	 * 根据护甲，计算我方坦克被击中时的伤害值
	 * @param bullet 击中我方坦克的子弹
	 * @return 真实伤害值
	 */
	public double caculateHitDamage(Bullet bullet) {
		if (bullet.getDamage() > this.getArmor())
			return bullet.getDamage() - this.getArmor();																			//伤害被护甲抵消了一部分
		else return 0;
	}
	
	/**
	 * 根据护甲，计算我方坦克撞击时的伤害值
	 * @param v 相对速度
	 * @return 真实伤害值
	 */
	public double caculateCrashDamage(double v) {
		double initialDamage = Math.pow((v + this.getV()), 2)/10;
		if (initialDamage > this.getArmor())																					//Ek = 0.5*K*(V*V).护甲抵消了部分伤害
			return initialDamage - this.getArmor();
		else return 0;
	}
	
	/**
	 * 对我方坦克进行限速
	@Override 
	*/
	public void restrictVtoMax() {
		if (this.getV() >this.getVm())
			this.resetV(this.getVm());
	}
	
	/**
	 * 在接受到按键以后，对我方坦克进行加速或者减速的方法
	 * @param b 表征加速还是减速
	 */
	public void Accelerate(boolean b) {
		if (b && this.getV() < this.getVm() && this.getV() + this.getA() > this.getVm())
			this.resetV(this.getVm());
		else if(b && super.getV()+ super.getA() <= this.getVm()+0.01)                    										//0.01辅助修正，因为double值有极小误差
			super.resetV(super.getV() + super.getA());
		else if (!b && this.getV() > 0 && this.getV() - this.getA() < 0)
			this.resetV(0);
		else if (!b && super.getV() - super.getA() >= 0 - 0.01) {
			super.resetV(super.getV() - super.getA());
		}
	}
	
	/**
	 * 检查我方子弹的出界和地形碰撞情况情况，同时移除出界子弹
	 * @param ExIter 如果发生了地形碰撞，用于新增爆炸效果的集合迭代器
	 */
	public void removeMyBullet(ListIterator<Explosion> ExIter) {
		Iterator<Bullet> iter = bullets.iterator();
		while(iter.hasNext()) {
			Bullet bullet = iter.next();
			if (checkIfCrashWithMap(bullet, ExIter))
				iter.remove();
			else if(checkBulletOutOfBounds(bullet))
				iter.remove();
		}
	}
	
	/**
	 * 检查子弹是否与不可逾越地形发生碰撞
	 * @param bullet 待检测的子弹
	 * @param ExIter 如果发生了地形碰撞，用于新增爆炸效果的集合迭代器
	 * @return 是否发生了碰撞
	 */
	public boolean checkIfCrashWithMap(Bullet bullet, ListIterator<Explosion> ExIter) {
			for (MapType map: TankFrame.MY_MAP.type_CanNotMove) {
				if (map.ifContains(bullet.getX(), bullet.getY())) {
					ExIter.add(new Explosion(bullet.getX(), bullet.getY(), 50));
					map.Crash(bullet);
					return true;
				}
			}
			return false;
	}
	
	/**
	 * 表征我方坦克遭遇碰撞的方法
	@Override
	@param v 碰撞相对速度
	@param type 表征是与敌方坦克发生碰撞还是与地形发生碰撞的参数
	@param score 计分对象
	*/
	public void Crash(double v, boolean type, Score score) {
		if (type) {
			//System.out.println("这次碰撞的相对速度为:" + (v+this.getV()) + "\n伤害为:" + caculateCrashDamage(v));	  										//调试语句，为解决撞地形伤害过高的BUG
			score.setEnemyDamage(score.getEnemyDamage() + caculateCrashDamage(v));
			this.setHealth(this.getHealth() - caculateCrashDamage(v));											//发生碰撞时血量下降
			this.resetV(0);
			this.setIfCrashed(true);
		}
	}
	
	/**
	 * 检测一个子弹是否出界
	 * @param bullet 待检测的子弹
	 * @return 是否出界
	 */
	public boolean checkBulletOutOfBounds(Bullet bullet) {
		if (bullet.getX() < 0 || bullet.getX() >gameFrame.TankFrame. WIDTH || bullet.getY() < 0 || bullet.getY() > gameFrame.TankFrame.HEIGHT)
			return true;
		else return false;
	}
	
	/**
	 * 我方坦克开火的方法
	 */
	public void Fire() {																																			//添加一个炮弹
		switch(super.getDir()) {
		case UP:
			this.bullets.add(new Bullet(getX(), getY()-11*W/8, getDir(), this.getBulletDamage(), false));
			break;
		case DOWN:
			this.bullets.add(new Bullet(getX(), getY() + 11*W/8, getDir(),  this.getBulletDamage(), false));
			break;
		case LEFT:
			this.bullets.add(new Bullet(getX() - 11*W/8, getY(), getDir(),  this.getBulletDamage(), false));
			break;
		case RIGHT:
			this.bullets.add(new Bullet(getX() + 11*W/8, getY(), getDir(),  this.getBulletDamage(), false));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 我方坦克发射AOE导弹的方法
	 */
	public void AOE_Fire() {																																			//添加一个AOE炮弹
		switch(getDir()) {
		case UP:
			this.bullets.add(new AOE_Bullet(getX(), getY()-11*W/8, getDir()));
			break;
		case DOWN:
			this.bullets.add(new AOE_Bullet(getX(), getY() + 11*W/8, getDir()));
			break;
		case LEFT:
			this.bullets.add(new AOE_Bullet(getX() - 11*W/8, getY(), getDir()));
			break;
		case RIGHT:
			this.bullets.add(new AOE_Bullet(getX() + 11*W/8, getY(), getDir()));
			break;
		default:
			break;
		}
		//System.out.println("Q技能发射！\n");
		this.AOE_BULLET_NUM--;
	}
}
