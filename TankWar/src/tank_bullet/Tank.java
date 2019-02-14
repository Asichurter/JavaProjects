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
 * �̳���AbstractTank���ҷ�̹�˵�̹����
 * @author Asichurter
 *
 */

public class Tank extends AbstractTank {
	
	public List<Bullet> bullets;												//̹�˵��ӵ�����
	public static double TANK_VM = 10;                                                        //����ٶ�����
	public static double TANK_FULLHEALTH= 100;			//̹�˵����Ѫ��
	private static final int LOADFT = 4000;													//����ȴʱ��
	private double MY_DAMAGE = 50;	
	public static int AOE_BULLET_TOTAL = 3;														//AOE��������
	private int AOE_BULLET_NUM = Tank.AOE_BULLET_TOTAL;																//AOE��������
	public static final int AOE_TOTAL_TIME = 20000;
	private int AOE_TIME = Tank.AOE_TOTAL_TIME;
	private boolean ifHavePrecisionAtt = true;
	
	public static int W ;										//̹�˿��
	public static int R1 ;										//��̨�뾶
	
	public Tank(int x, int y, double v, int W, int R1) {
		super(x, y, v, 3*W/2, 10);
		Tank.W = W;
		Tank.R1 = R1;
		this.bullets = new LinkedList<>();
		this.setHealth(Tank.TANK_FULLHEALTH);										//������̹�˵�Ѫ������������Ѫ������Ѫ��
		this.setLoadFT(Tank.LOADFT);
		this.setBulletDamage(MY_DAMAGE);
		this.setFullHealth(TANK_FULLHEALTH);
	}
	
	/**
	 * ���AOE������װ��������������δ�����ޣ������װ��
	 * @param reduce ÿһ֡��װ����
	 */
	public void checkIfAOEReady(int reduce) {
		if (this.AOE_BULLET_NUM == Tank.AOE_BULLET_TOTAL) {										//�������AOE����������Ϊ��
			this.AOE_TIME = Tank.AOE_TOTAL_TIME;
			return;
		}
		if (this.AOE_BULLET_NUM < Tank.AOE_BULLET_TOTAL)										//���AOE������������������ˢ����ȴʱ��
			this.AOE_TIME -= reduce;
		if (this.AOE_TIME <= 0 && this.AOE_BULLET_NUM < Tank.AOE_BULLET_TOTAL) {													//�������AOE������ȴ��϶�������û�дﵽ����
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
	 * �����ҷ�̹�˱��ӵ����еķ���
	@Override
	@param bullet �����ҷ�̹�˵��ӵ�
	@param socre �Ʒֶ���
	*/
	public void beHit(Bullet bullet, Score score) {
		double health = this.getHealth() - caculateHitDamage(bullet);
		if (health <= 0)																																			//�����к�Ѫ������0�Ժ�ֱ����Ϊ����
			this.setLive(false);
		else {
			setHealth(health);
			score.setEnemyDamage(score.getEnemyDamage() + caculateHitDamage(bullet));
		}
	}
	
	/**
	 * ���ݻ��ף������ҷ�̹�˱�����ʱ���˺�ֵ
	 * @param bullet �����ҷ�̹�˵��ӵ�
	 * @return ��ʵ�˺�ֵ
	 */
	public double caculateHitDamage(Bullet bullet) {
		if (bullet.getDamage() > this.getArmor())
			return bullet.getDamage() - this.getArmor();																			//�˺������׵�����һ����
		else return 0;
	}
	
	/**
	 * ���ݻ��ף������ҷ�̹��ײ��ʱ���˺�ֵ
	 * @param v ����ٶ�
	 * @return ��ʵ�˺�ֵ
	 */
	public double caculateCrashDamage(double v) {
		double initialDamage = Math.pow((v + this.getV()), 2)/10;
		if (initialDamage > this.getArmor())																					//Ek = 0.5*K*(V*V).���׵����˲����˺�
			return initialDamage - this.getArmor();
		else return 0;
	}
	
	/**
	 * ���ҷ�̹�˽�������
	@Override 
	*/
	public void restrictVtoMax() {
		if (this.getV() >this.getVm())
			this.resetV(this.getVm());
	}
	
	/**
	 * �ڽ��ܵ������Ժ󣬶��ҷ�̹�˽��м��ٻ��߼��ٵķ���
	 * @param b �������ٻ��Ǽ���
	 */
	public void Accelerate(boolean b) {
		if (b && this.getV() < this.getVm() && this.getV() + this.getA() > this.getVm())
			this.resetV(this.getVm());
		else if(b && super.getV()+ super.getA() <= this.getVm()+0.01)                    										//0.01������������Ϊdoubleֵ�м�С���
			super.resetV(super.getV() + super.getA());
		else if (!b && this.getV() > 0 && this.getV() - this.getA() < 0)
			this.resetV(0);
		else if (!b && super.getV() - super.getA() >= 0 - 0.01) {
			super.resetV(super.getV() - super.getA());
		}
	}
	
	/**
	 * ����ҷ��ӵ��ĳ���͵�����ײ��������ͬʱ�Ƴ������ӵ�
	 * @param ExIter ��������˵�����ײ������������ըЧ���ļ��ϵ�����
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
	 * ����ӵ��Ƿ��벻����Խ���η�����ײ
	 * @param bullet �������ӵ�
	 * @param ExIter ��������˵�����ײ������������ըЧ���ļ��ϵ�����
	 * @return �Ƿ�������ײ
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
	 * �����ҷ�̹��������ײ�ķ���
	@Override
	@param v ��ײ����ٶ�
	@param type ��������з�̹�˷�����ײ��������η�����ײ�Ĳ���
	@param score �Ʒֶ���
	*/
	public void Crash(double v, boolean type, Score score) {
		if (type) {
			//System.out.println("�����ײ������ٶ�Ϊ:" + (v+this.getV()) + "\n�˺�Ϊ:" + caculateCrashDamage(v));	  										//������䣬Ϊ���ײ�����˺����ߵ�BUG
			score.setEnemyDamage(score.getEnemyDamage() + caculateCrashDamage(v));
			this.setHealth(this.getHealth() - caculateCrashDamage(v));											//������ײʱѪ���½�
			this.resetV(0);
			this.setIfCrashed(true);
		}
	}
	
	/**
	 * ���һ���ӵ��Ƿ����
	 * @param bullet �������ӵ�
	 * @return �Ƿ����
	 */
	public boolean checkBulletOutOfBounds(Bullet bullet) {
		if (bullet.getX() < 0 || bullet.getX() >gameFrame.TankFrame. WIDTH || bullet.getY() < 0 || bullet.getY() > gameFrame.TankFrame.HEIGHT)
			return true;
		else return false;
	}
	
	/**
	 * �ҷ�̹�˿���ķ���
	 */
	public void Fire() {																																			//���һ���ڵ�
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
	 * �ҷ�̹�˷���AOE�����ķ���
	 */
	public void AOE_Fire() {																																			//���һ��AOE�ڵ�
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
		//System.out.println("Q���ܷ��䣡\n");
		this.AOE_BULLET_NUM--;
	}
}
