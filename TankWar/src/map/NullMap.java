package map;

import tank_bullet.AbstractTank;

public class NullMap extends MapType{
	
	private final int NULLMAP_IDENTY = 1;
	
	//�չ����������ڷ���һ��ָ�����͵���
	public NullMap() {
		super();
	}

	//���ڷ���һ��ָ�����͵�ʵ�������ܴ���BUG��
	public  NullMap getInstance() {
		return new NullMap();
	}
	
	//������ǿ̹�˵ķ���
	public void Buff(AbstractTank tank) {}
	
	//��������̹�˵ķ���
	public void DeBuff(AbstractTank tank) {}
	
	//����̹�˵�״̬
	public void resetTank(AbstractTank tank) {}
	
	public String toString() {
		return "��";
	}
	
	//���ؿյ�ͼ�ı�ʶ����
	@Override
	public int getMapIdentity() {
		return this.NULLMAP_IDENTY;
	}
}
