package tank_bullet;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * �̳���Explosion��AOE�����ı�ը����
 * @author Asichurter
 *
 */

public class AOE_Explosion extends Explosion{
	
	public static final int TOTAL_TIME = 300;
	private int R = 150;																												//��AOE�ڵ��ı�ը��Χһ��

	public AOE_Explosion(int x, int y, int reduce) {
		super(x, y, reduce, AOE_Explosion.TOTAL_TIME);
	}
	
	public AOE_Explosion(int x, int y, int reduce, int r) {
		super(x, y, reduce, AOE_Explosion.TOTAL_TIME);
		this.R = r;
	}
	
	
	/**
	 * AOE������ը����������
	 * @param g2 �滭Ԫ��
	 */
	public void Show(Graphics2D g2) {																		//��ӡAOE�ڵ��ĳ����
		g2.setColor(new Color(255, 130, 50));
		g2.drawOval(this.getX() - this.R/8, this.getY() - this.R/8, this.R/4, this.R/4);
		g2.drawOval(this.getX() - this.R/4, this.getY() - this.R/4, this.R/2, this.R/2);
		g2.drawOval(this.getX() - 3*this.R/8, this.getY() - 3*this.R/8,3*this.R/4, 3*this.R/4);
		g2.drawOval(this.getX() - this.R/2, this.getY() - this.R/2, this.R, this.R);
	}
}
