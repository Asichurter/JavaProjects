package buff;

import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Tank;

public class LoadTimeDecresing implements Buff{
	private int DecresingValue;
	private int Times;
	private boolean IfReadyWorked = true;																									//判断这个buff是否还没有开始工作
	
	public LoadTimeDecresing(int x, int t) {
		this.DecresingValue = x;
		this.Times = t;
	}
	
	public void Work(AbstractTank tank) {
		if (IfReadyWorked) {																																				//如果还没作用过
			if (tank instanceof EnemyTank) {																					//敌方坦克不会发生作用																			
				this.IfReadyWorked = false;
			}
			else {
				if (IfReadyWorked && tank.getLoadFT() > DecresingValue) {							//开始作用，设置生效
					tank.setLoadFT(tank.getLoadFT() - DecresingValue);
					this.IfReadyWorked = false;
				}
				else if (tank.getLoadFT() <= DecresingValue) {					
					tank.setLoadFT(500);																												//最短开火时间
					this.IfReadyWorked = false;																								//停止反复设置
				}
			}
		}
		if (tank instanceof Tank && Times == 1)																															//效果结束，buff返回
				tank.setLoadFT(tank.getLoadFT() + DecresingValue);
		Times--;
	}
	
	public int getTimes() {
		return this.Times;
	}

	public String getDes() {
		return "开火冷却减少(" + this.Times + ") ";
	}
}
