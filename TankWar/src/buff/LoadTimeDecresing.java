package buff;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class LoadTimeDecresing implements Buff{
	private int DecresingValue;
	private int Times;
	private boolean IfReadyWorked = true;																									//�ж����buff�Ƿ�û�п�ʼ����
	
	public LoadTimeDecresing(int x, int t) {
		this.DecresingValue = x;
		this.Times = t;
	}
	
	public void Work(AbstractTank tank) {
		if (IfReadyWorked) {																																				//�����û���ù�
			if (tank instanceof EnemyTank) {																					//�з�̹�˲��ᷢ������																			
				this.IfReadyWorked = false;
			}
			else {
				if (IfReadyWorked && tank.getLoadFT() > DecresingValue) {							//��ʼ���ã�������Ч
					tank.setLoadFT(tank.getLoadFT() - DecresingValue);
					this.IfReadyWorked = false;
				}
				else if (tank.getLoadFT() <= DecresingValue) {					
					tank.setLoadFT(500);																												//��̿���ʱ��
					this.IfReadyWorked = false;																								//ֹͣ��������
				}
			}
		}
		if (tank instanceof Tank && Times == 1)																															//Ч��������buff����
				tank.setLoadFT(tank.getLoadFT() + DecresingValue);
		Times--;
	}
	
	public int getTimes() {
		return this.Times;
	}

	public String getDes() {
		return "������ȴ����(" + this.Times + ") ";
	}
}
