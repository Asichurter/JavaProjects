package buff;

import tank_bullet.AbstractTank;

public class Recovering implements Buff{
	
	private double RecoveryValue;
	private int Times = 200;
	
	public Recovering(double x) {
		this.RecoveryValue = x;
	}
	
	public void Work(AbstractTank tank) {
		if (tank.getHealth() < tank.getFullHealth()) {
			tank.setHealth(tank.getHealth() + RecoveryValue);
			Times--;
		}
		else this.Times = 0;
	}
	
	public int getTimes() {
		return this.Times;
	}
	
	public String getDes() {
		return "Î¬ÐÞÖÐ(" + this.Times + ") ";
	}
}
