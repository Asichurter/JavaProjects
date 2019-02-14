package skill;

import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import gameFrame.TankFrame;
import tank_bullet.AbstractTank;
import tank_bullet.Bullet;
import tank_bullet.Dir;

/**
 * �̳���Bullet��AOE����
 * @author Asichurter
 *
 */
public class AOE_Bullet extends Bullet{
	private int R = 150;
	private double CenterDamage = 75;
	public static int W = 40;
	public static int H = 20;
	private boolean ifHit = false;
	
	public AOE_Bullet(int x, int y, Dir dir) {
		this.setXY(x, y);
		this.setDir(dir);
		this.setDamage(CenterDamage);
		/*switch(this.getDir()) {
			case UP:
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE������.png"));
				break;
			case DOWN:
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE������.png"));
				break;
			case LEFT:
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE������.png"));
				break;
			case RIGHT:
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE������.png"));
				break;
			default:
				break;
		}*/
		this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/�ӵ�/AOE����" + this.getDir().getDes() + ".png"));
	}
	
	public boolean getIfHit() {
		return this.ifHit;
	}
	
	public void setIfHit(boolean If) {
		this.ifHit = If;
	}
	
	public int getR() {
		return this.R;
	}
	
	/**
	 * �ж��Ƿ�����˵з���λ
	 * @param tank �����ĵط���λ
	 * @return �Ƿ����
	 */
	public boolean ifHit(AbstractTank tank) {																			//���ڼ���ڵ��Ƿ�����˵з�̹��
		return Point2D.distance(tank.getX(), tank.getY(), this.getX(), this.getY()) <= 2*(TankFrame.W+TankFrame.R1)/3;
	}
	
	/**
	 * �ж�̹���Ƿ��ھ��䷶Χ��
	 * @param tank ������̹��
	 * @return �Ƿ��ھ��䷶Χ��
	 */
	public boolean ifContains(AbstractTank tank) {																																	//���ڼ��з�̹���Ƿ���AOE�ڵ��ķ�Χ��
		Ellipse2D.Double ellipse = new Ellipse2D.Double(this.getX() - R/2, this.getY() - R/2, R, R);
		return ellipse.contains(tank.getX(), tank.getY());
	}
	
	/**
	 * ����AOE�ڵ����˺�ֵ������ԽԶ�˺�Խ�ͣ����Ϊ���֮һ
	 * @param x ������ĵ��x����ֵ
	 * @param y ������ĵ��y����ֵ
	 * @param IfFirst �Ƿ�������AOE�����Ĳ���������ǣ������ܵ����������˺�
	 * @return �˺�ֵ
	 */
	public double caculateDamage(int x, int y, boolean IfFirst) {
		if (IfFirst)																																												//����Ƿ�������AOE�ڵ���̹�ˣ�����ǵĻ���������������˺�
			return this.CenterDamage;
		double distance = Point2D.distance(x, y, this.getX(), this.getY());
		//System.out.println("����ײ����ľ���Ϊ" + Point2D.distance(x, y, this.getX(), this.getY()) + ",����ֵΪ" + 2*(TankFrame.W+TankFrame.R1)/3);
		//System.out.println("����ײ������Ϊ" + distance + ",RΪ" + this.R);
		if (distance >= R)																																				//�߽缰Ȧ�ⲻ���˺�
			return 0;
		else return CenterDamage*(R/(R+4*(distance)));													//���㹫ʽ��d = D *(R / R + 4dis),�����˺�ΪD����С�˺�ΪD/5
	}
}
