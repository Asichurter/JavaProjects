package buff;

import tank_bullet.AbstractTank;

public class BulletShield implements Buff{
	private int Times;
	private boolean ifReadyWorked = true;														//判断这个Buff是否还没有开始工作

	public BulletShield(int time) {
		this.Times = time;
	}
	
	public void Work(AbstractTank tank) {
		if (this.ifReadyWorked) {																																	//如果Buff还没有工作过
			this.ifReadyWorked = false;
			tank.setIfHasBulletShield(true);
		}
		else if (tank.getIfHasBulletShield() && !this.ifReadyWorked) {							//如果Buff已经工作过了，而且坦克还没有被击中
			if (Times > 1)
				Times--;
			else if (Times == 1) {
				tank.setIfHasBulletShield(false);
				Times--;
			}
		}
		else if (!tank.getIfHasBulletShield() && !this.ifReadyWorked)	{									//如果Buff已经工作过了，而且坦克被击中了
			this.Times =0;
		}
	}
	
	public int getTimes() {
		return this.Times;
	}
	
	public String getDes() {
		return "子弹护盾(" + this.Times + ") ";
	}

}
