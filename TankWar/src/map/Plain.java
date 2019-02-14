package map;

import java.awt.Toolkit;

import tank_bullet.AbstractTank;

public class Plain extends MapType{
	
	private int PLAIN_IDENTY= 8;
	private int VM_D = 1;
	private NullMap map = new NullMap();										//ʹ�ô�������ƽԭ����NullMap��
	
	public Plain(int x, int y, String des) {
		super(Toolkit.getDefaultToolkit().getImage("Icon/����/ƽԭ.png"), true, false, true, x, y);
	}
	
	public Plain() {
		super();
	}
	
	//���ڷ���һ��ָ�����͵�ʵ�������ܴ���BUG��
	public  Plain getInstance() {
		return new Plain();
	}
	
	//������ǿ̹�˵ķ���
	public void Buff(AbstractTank tank) {
		tank.setVm(tank.getVm() + this.VM_D);
		this.map.Buff(tank);
	}
	
	//��������̹�˵ķ���
	public void DeBuff(AbstractTank tank) {
		this.map.DeBuff(tank);
	}
	
	//����̹�˵�״̬
	public void resetTank(AbstractTank tank) {
		tank.setVm(tank.getVm() - this.VM_D);
		this.resetTank(tank);
	}
	
	public String toString() {
		return "ƽԭ";
	}
	
	//���ؿյ�ͼ�ı�ʶ����
	@Override
	public int getMapIdentity() {
		return this.PLAIN_IDENTY;
	}

}
