package buff;

import tank_bullet.AbstractTank;

public interface Buff {
	public void Work(AbstractTank tank);
	public int getTimes();
	public String getDes();
}
