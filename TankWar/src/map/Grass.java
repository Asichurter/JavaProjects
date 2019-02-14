package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;

public class Grass extends MapType{
	
	private static double SPEED_D = -1;
	private static double ARMOR_D= 3;
	private static double Accerate_D = -0.2;
	private final int GRASS_IDENTY = 2;
	
	public Grass(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/����/�ݵ�.jpg"), true, false,true,  x, y);
	}
	
	//�չ����������ڷ���һ��ָ�����͵���
	public Grass() {
		super();
	}

	//���ڷ���һ��ָ�����͵�ʵ�������ܴ���BUG��
	public Grass  getInstance() {
		return new Grass();
	}
	
	//������ǿ̹�˵ķ���
	public void Buff(AbstractTank tank) {
		tank.setArmor(tank.getArmor() +Grass.ARMOR_D);
	}
	
	//��������̹�˵ķ���
	public void DeBuff(AbstractTank tank) {
		tank.setA(tank.getA() + Grass.Accerate_D);
		if (tank.getV() >= 1 - Grass.SPEED_D)
			tank.resetV(tank.getV() +Grass.SPEED_D);
		else if (tank.getV() > 1 && tank.getV() < 1 - Grass.SPEED_D)
			tank.resetV(1);
	}
	
	//����̹�˵�״̬
	public void ResetTank(AbstractTank tank) {
		tank.resetV(tank.getV() - Grass.SPEED_D);
		tank.setArmor(tank.getArmor() - Grass.ARMOR_D);
		tank.setA(tank.getA() - Grass.Accerate_D);
	}
	
	public String toString() {
		return "�ݵ�(�������ӣ��ٶȡ����ٶȼ�С)";
	}
	
	//�������ڱ�ʾ�ݵص��εı�ʶ����
	@Override
	public int getMapIdentity() {
		return this.GRASS_IDENTY;
	}
}
