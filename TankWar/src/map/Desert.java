package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class Desert extends MapType{
	
	private double ACCELERATE_D = 0.2;																//���ٶȱ仯��	
	private double SPEED_D = 2;																						//�ٶȱ仯��
	private double VM_D = 1;																								//����ٶȱ仯��
	private int DESERT_IDENTY = 3;																			//���α�ʶ��
	private int LOADFT_D = 1000;																					//������ȴʱ��仯��
	
	public Desert(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/����/ɳĮ.jpg"), true, false, true, x, y);
	}
	
	public Desert() {
		super();
	}
	
	@Override
	//���ڷ���һ��ָ�����͵�ʵ�������ܴ���BUG��
	public Desert  getInstance() {
		return new Desert();
	}
	
	@Override
	//������ǿ̹�˵ķ���
	public void Buff(AbstractTank tank) {
		double dV;
		tank.setVm(tank.getVm() + VM_D);
		dV = tank.getVm();
		if (tank.getV() + SPEED_D <= dV)
			tank.resetV(tank.getV() + SPEED_D);
		else tank.resetV(dV);
		tank.setA(tank.getA() + ACCELERATE_D);
	}
	
	@Override
	//��������̹�˵ķ���
	public void DeBuff(AbstractTank tank) {
		tank.setLoadFT(tank.getLoadFT() + LOADFT_D);
	}
	
	@Override
	//����̹�˵�״̬
	public void ResetTank(AbstractTank tank) {
		tank.setLoadFT(tank.getLoadFT() - LOADFT_D);
		tank.setVm(tank.getVm() - VM_D);
		if (tank.getV() - SPEED_D >= 3)
			tank.resetV(tank.getV() - SPEED_D);
		else tank.resetV(1);
		tank.setA(tank.getA() - ACCELERATE_D);
	}
	
	@Override
	public String toString() {
		return "ɳĮ(����ٶȡ����ٶȺ��ٶ�����,����ʱ������)";
	}
	
	//�������� ��ʾ�ݵص��εı�ʶ����
	@Override
	public int getMapIdentity() {
		return this.DESERT_IDENTY;
	}
}
