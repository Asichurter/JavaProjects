package tank_bullet;

import gameFrame.Score;
import skill.AOE_Bullet;
import skill.PrecisionAttack;

/**
 * 继承自AbstractTank，表征敌方坦克的坦克类
 * @author Asichurter
 *
 */
public class EnemyTank extends AbstractTank {
	
	public static final int W = 20;										//坦克宽度
	public static final int R1 = 6;										//炮台半径
	public static double ENEMY_FULLHEALTH = 100;		//敌方坦克的满血量
	public static double ENEMYTANK_VM = 10;
	private boolean ifHitByAOE = false;
	
	public EnemyTank(int x, int y, double v) {
		super(x, y, v, 3*W/2, 10);
		setHealth(ENEMY_FULLHEALTH);											//将新生成的坦克的血量调整至敌方坦克的满血量
		this.setFullHealth(ENEMY_FULLHEALTH);
	}
	
	/**
	 * 敌方坦克的开火方法
	 * @return 敌方坦克开火所产生的炮弹
	 */
	public Bullet Fire() {																																			//添加一个炮弹
		Bullet bullet =  new Bullet();																												//创建一个空的炮弹
		bullet.setDir(getDir(), true);
		switch(super.getDir()) {
		case UP:
			bullet.setXY(getX(), getY()-11*W/8);		
			return bullet;
		case DOWN:
			bullet.setXY(getX(), getY() + 11*W/8);
			return bullet;
		case LEFT:
			bullet.setXY(getX() - 11*W/8, getY());
			return bullet;
		case RIGHT:
			bullet.setXY(getX() + 11*W/8, getY());
			return bullet;
		default:
			return null;
		}
	}
	
	public void setIfHitByAOE(boolean If) {
		this.ifHitByAOE  = If;
	}
	
	/**
	 * 将敌方坦克进行限速的方法。每次移动都会调用
	@Override
	*/
	public void restrictVtoMax() {
		if (this.getV() > this.getVm())
			this.resetV(this.getVm());
	}
	
	/**
	 * 用于描述敌方坦克被击中的方法
	@Override
	@param bullet 击中的子弹
	*/
	public void beHit(Bullet bullet, Score score) {
		double damage;
		if (bullet instanceof AOE_Bullet) { 	
			damage = ((AOE_Bullet) bullet).caculateDamage(this.getX(), this.getY(), this.ifHitByAOE);						//强制向下转型，将是否是引爆AOE炮弹的属性传给计算伤害的方法
			if (this.ifHitByAOE)
				this.ifHitByAOE = false;
			//System.out.println("本次AOE伤害原始值为" + damage);		
			if (damage > this.getArmor())
				damage -= this.getArmor();
			else damage = 0;			
		}
		else damage = caculateHitDamage(bullet);
		double health = this.getHealth() - damage ;
		if (health <= 0) {																																								//被击中后血量降至0以后直接视为死亡
			this.setLive(false);
			score.setMyHitDamage(score.getMyHitDamage() + this.getHealth());
			score.setEliminateNum(score.getEliminateNum() + 1);
		}
		else {
			setHealth(health);
			score.setMyHitDamage(score.getMyHitDamage() + damage);
		}
	}
	
	public void beHit(double damage, Score score) {
		double health = this.getHealth() - caculateHitDamage(damage);
		if (health <= 0) {
			score.setMyHitDamage(score.getMyHitDamage() + this.getHealth());
			this.setLive(false);
			this.setHealth(0);
		}
		else {
			this.setHealth(health);
			score.setMyHitDamage(score.getMyHitDamage() + caculateHitDamage(damage));
		}
	}
	
	/**
	 * 根据护甲，计算敌方坦克被击中时的伤害
	 * @param bullet
	 * @return 真实的伤害值
	 */
	public double caculateHitDamage(Bullet bullet) {
		if (bullet.getDamage() > this.getArmor())
			return bullet.getDamage() - this.getArmor();
		else return 0;
	}
	
	public double caculateHitDamage(double damage) {
		if (damage > this.getArmor())
			return damage - this.getArmor();
		else return 0;
	}
	
	/**
	 * 根据护甲，计算在撞击时的伤害
	 * @param v 相对速度
	 * @return 真实的伤害值
	 */
	public double caculateCrashDamage(double v) {
		return Math.pow((v + this.getV()), 2)/7;																							//Ek = 0.5*K*(V*V)
	}
	
	/**
	 * 用于表征敌方坦克被撞击
	@Override
	@param v 相对速度
	@param type 是否与我方坦克发生撞击。若为否，则是与地形发生撞击
	@param score 计分对象
	*/
	public void Crash(double v, boolean type, Score score) {
		if (type) {
			score.setMyCrashDamage(score.getMyCrashDamage() + caculateCrashDamage(v));
			if (this.getHealth() <= caculateCrashDamage(v))
				score.setEliminateNum(score.getEliminateNum() + 1);
			this.setHealth(this.getHealth() -  this.caculateCrashDamage(v));
			this.setIfCrashed(true);
			this.reLaunch(gameFrame.TankFrame.DEFAULT_EV, true);
		}
		else {
			score.setMyCrashDamage(score.getMyCrashDamage() + caculateCrashDamage(v));
			this.setHealth(this.getHealth() -  this.caculateCrashDamage(v));
			this.setIfCrashed(true);
			this.reLaunch(gameFrame.TankFrame.DEFAULT_EV, true);
		}
	}
	
	/**
	 * 敌方坦克在搁浅以后自动重新启动的方法
	 * @param v 重启的速度值
	 * @param ifReDir 是否转换方向
	 */
	public void reLaunch(double v, boolean ifReDir) {
		super.resetV(v);
		if (ifReDir) {
			Dir dir = gameFrame.TankFrame.RandomDirProducer();
		while (Dir.equals(dir, super.getDir())) {
			dir = gameFrame.TankFrame.RandomDirProducer();
		}
		super.setDir(dir);
		}
	}
}
