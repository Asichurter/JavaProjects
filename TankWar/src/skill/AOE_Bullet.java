package skill;

import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import gameFrame.TankFrame;
import tank_bullet.AbstractTank;
import tank_bullet.Bullet;
import tank_bullet.Dir;

/**
 * 继承自Bullet的AOE导弹
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
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE导弹上.png"));
				break;
			case DOWN:
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE导弹下.png"));
				break;
			case LEFT:
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE导弹左.png"));
				break;
			case RIGHT:
				this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/AOE导弹右.png"));
				break;
			default:
				break;
		}*/
		this.setImage(Toolkit.getDefaultToolkit().getImage("Icon/子弹/AOE导弹" + this.getDir().getDes() + ".png"));
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
	 * 判断是否击中了敌方单位
	 * @param tank 待检测的地方单位
	 * @return 是否击中
	 */
	public boolean ifHit(AbstractTank tank) {																			//用于检测炮弹是否击中了敌方坦克
		return Point2D.distance(tank.getX(), tank.getY(), this.getX(), this.getY()) <= 2*(TankFrame.W+TankFrame.R1)/3;
	}
	
	/**
	 * 判断坦克是否在警戒范围内
	 * @param tank 待检测的坦克
	 * @return 是否在警戒范围内
	 */
	public boolean ifContains(AbstractTank tank) {																																	//用于检测敌方坦克是否在AOE炮弹的范围内
		Ellipse2D.Double ellipse = new Ellipse2D.Double(this.getX() - R/2, this.getY() - R/2, R, R);
		return ellipse.contains(tank.getX(), tank.getY());
	}
	
	/**
	 * 计算AOE炮弹的伤害值，距离越远伤害越低，最低为五分之一
	 * @param x 待计算的点的x坐标值
	 * @param y 待计算的点的y坐标值
	 * @param IfFirst 是否是引爆AOE导弹的参数，如果是，将会受到最大的中心伤害
	 * @return 伤害值
	 */
	public double caculateDamage(int x, int y, boolean IfFirst) {
		if (IfFirst)																																												//检测是否是引爆AOE炮弹的坦克，如果是的话，将会造成中心伤害
			return this.CenterDamage;
		double distance = Point2D.distance(x, y, this.getX(), this.getY());
		//System.out.println("本次撞击点的距离为" + Point2D.distance(x, y, this.getX(), this.getY()) + ",警戒值为" + 2*(TankFrame.W+TankFrame.R1)/3);
		//System.out.println("本次撞击距离为" + distance + ",R为" + this.R);
		if (distance >= R)																																				//边界及圈外不受伤害
			return 0;
		else return CenterDamage*(R/(R+4*(distance)));													//计算公式：d = D *(R / R + 4dis),中心伤害为D，最小伤害为D/5
	}
}
