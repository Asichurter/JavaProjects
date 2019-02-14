package buff;

import tank_bullet.AbstractTank;

public class BulletShield implements Buff{
	private int Times;
	private boolean ifReadyWorked = true;														//�ж����Buff�Ƿ�û�п�ʼ����

	public BulletShield(int time) {
		this.Times = time;
	}
	
	public void Work(AbstractTank tank) {
		if (this.ifReadyWorked) {																																	//���Buff��û�й�����
			this.ifReadyWorked = false;
			tank.setIfHasBulletShield(true);
		}
		else if (tank.getIfHasBulletShield() && !this.ifReadyWorked) {							//���Buff�Ѿ��������ˣ�����̹�˻�û�б�����
			if (Times > 1)
				Times--;
			else if (Times == 1) {
				tank.setIfHasBulletShield(false);
				Times--;
			}
		}
		else if (!tank.getIfHasBulletShield() && !this.ifReadyWorked)	{									//���Buff�Ѿ��������ˣ�����̹�˱�������
			this.Times =0;
		}
	}
	
	public int getTimes() {
		return this.Times;
	}
	
	public String getDes() {
		return "�ӵ�����(" + this.Times + ") ";
	}

}
