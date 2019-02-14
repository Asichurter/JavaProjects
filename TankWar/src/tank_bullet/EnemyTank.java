package tank_bullet;

import gameFrame.Score;
import skill.AOE_Bullet;
import skill.PrecisionAttack;

/**
 * �̳���AbstractTank�������з�̹�˵�̹����
 * @author Asichurter
 *
 */
public class EnemyTank extends AbstractTank {
	
	public static final int W = 20;										//̹�˿��
	public static final int R1 = 6;										//��̨�뾶
	public static double ENEMY_FULLHEALTH = 100;		//�з�̹�˵���Ѫ��
	public static double ENEMYTANK_VM = 10;
	private boolean ifHitByAOE = false;
	
	public EnemyTank(int x, int y, double v) {
		super(x, y, v, 3*W/2, 10);
		setHealth(ENEMY_FULLHEALTH);											//�������ɵ�̹�˵�Ѫ���������з�̹�˵���Ѫ��
		this.setFullHealth(ENEMY_FULLHEALTH);
	}
	
	/**
	 * �з�̹�˵Ŀ��𷽷�
	 * @return �з�̹�˿������������ڵ�
	 */
	public Bullet Fire() {																																			//���һ���ڵ�
		Bullet bullet =  new Bullet();																												//����һ���յ��ڵ�
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
	 * ���з�̹�˽������ٵķ�����ÿ���ƶ��������
	@Override
	*/
	public void restrictVtoMax() {
		if (this.getV() > this.getVm())
			this.resetV(this.getVm());
	}
	
	/**
	 * ���������з�̹�˱����еķ���
	@Override
	@param bullet ���е��ӵ�
	*/
	public void beHit(Bullet bullet, Score score) {
		double damage;
		if (bullet instanceof AOE_Bullet) { 	
			damage = ((AOE_Bullet) bullet).caculateDamage(this.getX(), this.getY(), this.ifHitByAOE);						//ǿ������ת�ͣ����Ƿ�������AOE�ڵ������Դ��������˺��ķ���
			if (this.ifHitByAOE)
				this.ifHitByAOE = false;
			//System.out.println("����AOE�˺�ԭʼֵΪ" + damage);		
			if (damage > this.getArmor())
				damage -= this.getArmor();
			else damage = 0;			
		}
		else damage = caculateHitDamage(bullet);
		double health = this.getHealth() - damage ;
		if (health <= 0) {																																								//�����к�Ѫ������0�Ժ�ֱ����Ϊ����
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
	 * ���ݻ��ף�����з�̹�˱�����ʱ���˺�
	 * @param bullet
	 * @return ��ʵ���˺�ֵ
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
	 * ���ݻ��ף�������ײ��ʱ���˺�
	 * @param v ����ٶ�
	 * @return ��ʵ���˺�ֵ
	 */
	public double caculateCrashDamage(double v) {
		return Math.pow((v + this.getV()), 2)/7;																							//Ek = 0.5*K*(V*V)
	}
	
	/**
	 * ���ڱ����з�̹�˱�ײ��
	@Override
	@param v ����ٶ�
	@param type �Ƿ����ҷ�̹�˷���ײ������Ϊ����������η���ײ��
	@param score �Ʒֶ���
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
	 * �з�̹���ڸ�ǳ�Ժ��Զ����������ķ���
	 * @param v �������ٶ�ֵ
	 * @param ifReDir �Ƿ�ת������
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
