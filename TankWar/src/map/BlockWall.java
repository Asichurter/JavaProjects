package map;

import java.awt.Toolkit;

import tank_bullet.Bullet;

public class BlockWall extends MapType{
	/**
	 * 
	 */
	private static final long serialVersionUID = 953641468130686724L;
	private final boolean CAN_CROSS = false;
	private final boolean CAN_DESTROY = true;
	private double HEALTH = 200;
	private final double FULL_HEALTH = 200;
	private double ARMOR = 5;
	private final Toolkit tool = Toolkit.getDefaultToolkit();
	private final int BLOCKWALL_IDENTY = 6;
	
	public BlockWall(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/����/שǽ.png"), false, true, false, x, y);
	}
	
	/**
	 * ���ڸ�����ʹ�õĹ�����
	 * @param x X����
	 * @param y Y����
	 */
	public BlockWall(int x, int y) {

	}
	
	@Override
	public double getHealth() {
		return this.HEALTH;
	}
	
	public void setArmor(double a) {
		this.ARMOR = a;
	}
	
	private double caculateDamage(Bullet bullet) {
		if (bullet.getDamage() > this.ARMOR)	
			return bullet.getDamage() - this.ARMOR;
		else return 0;
	}
	
	private double caculateDamage(double v) {
		if (Math.pow(v, 2)/7 > this.ARMOR)	
			return Math.pow(v, 2)/7 - this.ARMOR;
		else return 0;
	}
	
	
	//���ӵ�����
	public void beHit(Bullet bullet) {
		if (caculateDamage(bullet) < this.HEALTH )
			HEALTH -= caculateDamage(bullet);
		else this.HEALTH = 0;
	}
	
	//��̹��ײ��
	public void beHit(double v) {
		//System.out.println("���ײ���ٶ�Ϊ"+ v +"\n��ǽ������˺�Ϊ" + caculateDamage(v));											//�������
		if (caculateDamage(v) < this.HEALTH)
			HEALTH -= caculateDamage(v);
		else this.HEALTH = 0;
	}
	
	@Override
	//�������ӵ�����̹�˷���ײ���Ķ���
	public void Crash(Bullet bullet) {
		this.beHit(bullet);
		this.checkIcon();
	}
	
	public void Crash(double v) {
		this.beHit(v);
		this.checkIcon();
	}
	
	//���ڼ��ͼ���Ƿ��б仯��ֻ�ڱ�ײ��behit��ʱ�����
	public void checkIcon() {
		if (this.HEALTH > 3*this.FULL_HEALTH/4)
			this.setIcon(tool.getImage("Icon/����/שǽ.png"));
		else if (this.HEALTH > this.FULL_HEALTH/2)
			this.setIcon(tool.getImage("Icon/����/שǽ1.png"));
		else if (this.HEALTH > this.FULL_HEALTH/4)
			this.setIcon(tool.getImage("Icon/����/שǽ2.png"));
		else this.setIcon(tool.getImage("Icon/����/שǽ3.png"));
	}
	
	public int getMapIdentity() {
		return this.BLOCKWALL_IDENTY;
	}
}
