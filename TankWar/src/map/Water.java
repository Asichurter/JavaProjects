package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class Water extends MapType{
	
	private double VM_TOTAL = 3;
	private double SPEED_D = -2;
	private double ACCELERATE_D = -0.3;
	private double ARMOR_D = 10;
	private final boolean IF_CANFIRE = false;
	private final int WATER_IDENTY = 4;
	
	public Water(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/����/ˮ��.png"), true, false, false, x, y);
	}
	
	public Water() {
		super();
		this.setIfCanFire(false);
	}
	
	@Override
	//���ڷ���һ��ָ�����͵�ʵ�������ܴ���BUG��
	public Water getInstance() {
		return new Water();
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
	}
	
	@Override
	public String toString() {
		return "ˮ��(��������������ٶȣ����ٶȺ��ٶȼ�С�����ܿ���)";
	}
	
	//�������� ��ʾ�ݵص��εı�ʶ����
	@Override
	public int getMapIdentity() {
		return this.WATER_IDENTY;
	}
}
