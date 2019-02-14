package skill;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import gameFrame.Score;
import gameFrame.TankFrame;
import tank_bullet.AOE_Explosion;
import tank_bullet.AbstractTank;
import tank_bullet.EnemyTank;
import tank_bullet.Explosion;

/**
 * 实现精准打击的类
 * @author Asichurter
 *
 */
public class PrecisionAttack {
	
	private int X;
	private int Y;
	private int IconX;
	private int IconY;
	private Image Icon;
	private double CenterDamage = 100;
	private int CenterRange = 150;
	private int Range = 300;
	private boolean ifExploded = false;

	public PrecisionAttack(int x, int y) {
		Icon = Toolkit.getDefaultToolkit().getImage("Icon/瞄准.png");
		this.IconX = x;
		this.IconY = y;
	}
	
	public Image getIcon() {
		return this.Icon;
	}
	
	public int getX() {
		return this.IconX;
	}
	
	public int getY() {
		return this.IconY;
	}
	
	public void setIconXY(int x, int y) {
		this.IconX = x;
		this.IconY = y ;
	}
	
	public void setExploded(boolean ifE) {
		this.ifExploded = ifE;
		this.X = this.IconX;
		this.Y = this.IconY;
	}
	
	public boolean getIfExploded() {
		return this.ifExploded;
	}
	
	private double caculateDamage(AbstractTank tank) {
		return Point2D.distance(tank.getX(), tank.getY(), this.X, this.Y)/Range*CenterDamage;
	}
	
	public void Explod(List<EnemyTank> tanks, Score score, LinkedList<Explosion> ex) {
		Ellipse2D Excircle = new Ellipse2D.Double(X-Range/2, Y-Range/2, Range, Range);
		Ellipse2D Incircle = new Ellipse2D.Double(X-CenterRange/2, Y-CenterRange/2, CenterRange, CenterRange);
		ex.add(new AOE_Explosion(X, Y, TankFrame.ExplosionReduce, Range));
		for (EnemyTank tank: tanks) {
			if (Excircle.contains(tank.getX(), tank.getY())) {
				if (Incircle.contains(tank.getX(), tank.getY())) {
					tank.beHit(CenterDamage, score);
				}
				else tank.beHit(caculateDamage(tank), score);
				ex.add(new Explosion(tank.getX(), tank.getY(), TankFrame.ExplosionReduce));
			}
		}
	}
}
