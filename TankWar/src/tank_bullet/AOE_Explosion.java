package tank_bullet;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * 继承自Explosion，AOE导弹的爆炸动画
 * @author Asichurter
 *
 */

public class AOE_Explosion extends Explosion{
	
	public static final int TOTAL_TIME = 300;
	private int R = 150;																												//与AOE炮弹的爆炸范围一致

	public AOE_Explosion(int x, int y, int reduce) {
		super(x, y, reduce, AOE_Explosion.TOTAL_TIME);
	}
	
	public AOE_Explosion(int x, int y, int reduce, int r) {
		super(x, y, reduce, AOE_Explosion.TOTAL_TIME);
		this.R = r;
	}
	
	
	/**
	 * AOE导弹爆炸的启动方法
	 * @param g2 绘画元素
	 */
	public void Show(Graphics2D g2) {																		//打印AOE炮弹的冲击波
		g2.setColor(new Color(255, 130, 50));
		g2.drawOval(this.getX() - this.R/8, this.getY() - this.R/8, this.R/4, this.R/4);
		g2.drawOval(this.getX() - this.R/4, this.getY() - this.R/4, this.R/2, this.R/2);
		g2.drawOval(this.getX() - 3*this.R/8, this.getY() - 3*this.R/8,3*this.R/4, 3*this.R/4);
		g2.drawOval(this.getX() - this.R/2, this.getY() - this.R/2, this.R, this.R);
	}
}
