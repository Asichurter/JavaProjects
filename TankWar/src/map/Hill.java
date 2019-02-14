package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class Hill extends MapType{
	private double SPEED_D = -2;
	private double ACCELERATE_D = -0.2;
	private double VM_TOTAL = 4;
	private double DAMAGE_D = -10;
	private double ARMOR_D = 6;	
	private final int HILL_IDENTY = 5;
	
	//�����ڵ�ͼ�д������ε�Ԫ�Ĺ�����
	public Hill(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/����/ɽ��.png"), true, false, true, x, y);
	}
	
	//������̹�˵ĵ��η����ı��ʱ��Ϊ̹�˵ĵ���ʵ�����ṩ����Ĺ�����
	public Hill() {
		super();
	}
	
	@Override
	//���ڷ���һ��ָ�����͵�ʵ�������ܴ���BUG��
	public Hill getInstance() {
		return new Hill();
	}
	
	@Override
	//������ǿ̹�˵ķ���
	public void Buff(AbstractTank tank) {
		tank.setArmor(tank.getArmor() + this.ARMOR_D);
	}
	
	@Override
	//��������̹�˵ķ���
	public void DeBuff(AbstractTank tank) {
		tank.setVm(VM_TOTAL);
		if (tank.getV() > tank.getVm() - this.SPEED_D)
			tank.resetV(tank.getVm());
		else if (tank.getV() > -(this.SPEED_D) && tank.getV() < tank.getVm() - this.SPEED_D)
			tank.resetV(tank.getV() + this.SPEED_D);
		else if (tank.getV() <= -(this.SPEED_D))
			tank.resetV(0);
		if (tank.getA() > -(this.ACCELERATE_D))
			tank.setA(tank.getA() + this.ACCELERATE_D);
		else tank.setA(0);
		if (tank.getBulletDamage() > -(this.DAMAGE_D))
			tank.setBulletDamage(tank.getBulletDamage() + this.DAMAGE_D);
	}
	
	@Override
	//����̹�˵�״̬
	public void ResetTank(AbstractTank tank) {
		if (tank instanceof Tank)
			tank.setVm(Tank.TANK_VM);
		else if (tank instanceof EnemyTank)
			tank.setVm(EnemyTank.ENEMYTANK_VM);
		if (tank.getArmor() >= this.ARMOR_D)
			tank.setArmor(tank.getArmor() - this.ARMOR_D);
		tank.setA(tank.getA() - this.ACCELERATE_D);
		tank.setBulletDamage(tank.getBulletDamage() - this.DAMAGE_D);
	}
	
	@Override
	public String toString() {
		return "ɽ��(���״�����ӣ�����ٶȡ��ٶȺͼ��ٶȴ����С)";
	}
	
	//�������� ��ʾ�ݵص��εı�ʶ����
	@Override
	public int getMapIdentity() {
		return this.HILL_IDENTY;
	}
}
