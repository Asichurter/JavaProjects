package map;

import java.awt.Toolkit;

import mapEditor.MapData;
import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class Road extends MapType{
	private double SPEED_D = 3;
	private double ACCELERATE_D = 0.5;
	private double VM_TOTAL = 12;
	private String ROAD_TYPE;															//����������·������ַ��������ڶ�Ӧ��ͼ��������
	private final int ROAD_IDENTY = 7;
	
	public Road(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/����/" + des + ".png"), true, false,true,  x, y);
		this.ROAD_TYPE = des;
	}
	
	public void setRoadType(String des) {
		this.ROAD_TYPE = des;
	}
	
	public String getRoadType() {
		return this.ROAD_TYPE;
	}
	
	//�չ����������ڷ���һ��ָ�����͵���
	public Road() {
		super();
	}

	//���ڷ���һ��ָ�����͵�ʵ�������ܴ���BUG��
	public Road  getInstance() {
		return new Road();
	}
	
	//������ǿ̹�˵ķ���
	public void Buff(AbstractTank tank) {
		tank.setVm(VM_TOTAL);
		if (tank.getV() + this.SPEED_D > VM_TOTAL)
			tank.resetV(VM_TOTAL);
		else tank.resetV(tank.getV() + SPEED_D);
		tank.setA(tank.getA() + ACCELERATE_D);
	}
	
	//��������̹�˵ķ���
	public void DeBuff(AbstractTank tank) {}
	
	//����̹�˵�״̬
	public void ResetTank(AbstractTank tank) {
		if (tank instanceof Tank)
			tank.setVm(Tank.TANK_VM);
		else if (tank instanceof EnemyTank)
			tank.setVm(EnemyTank.ENEMYTANK_VM);
		if (tank.getV() < SPEED_D)
			tank.resetV(1);
		else if (tank.getV() - SPEED_D <= tank.getVm())
			tank.resetV(tank.getV() - SPEED_D);
		else tank.restrictVtoMax();
		tank.setA(tank.getA() - this.ACCELERATE_D);
	}
	
	public String toString() {
		return "��·(����ٶȡ����ٶȺ��ٶ�����)";
	}
	
	//�������ڱ�ʾ�ݵص��εı�ʶ����
	@Override
	public int getMapIdentity() {
		return this.ROAD_IDENTY;
	}
	
	public MapData getMapData() {
		return new MapData(this.getX(), this.getY(), this.getMapIdentity(), this.ROAD_TYPE);
	}
}
