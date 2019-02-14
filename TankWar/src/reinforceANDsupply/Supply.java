package reinforceANDsupply;

import java.awt.Image;
import java.awt.geom.Rectangle2D;

import buff.Buff;

public class Supply {
		private int X;
		private int Y;
		private boolean IS_EXIST = true;
		private Image image;
		public static final int W = 30;
		
		public Supply(int x, int y, Image image) {
			this.X = x;
			this.Y = y;
			this.image = image;
		}
		
		public Image getImage() {
			return this.image;
		}
		
		public int getX() {
			return this.X;
		}
		
		public int getY() {
			return this.Y;
		}
		
		public void setIsExist(boolean is) {
			this.IS_EXIST = is;
		}
		
		public boolean getIsExist() {
			return this.IS_EXIST;
		}
		
		public double getRecovery() {
			return 0;
		}
		
		public Buff getBuff() {
			return null;
		}
		
		//检查是否坦克捡到了补给包
		public boolean ifContains(int x, int y) {
			if (new Rectangle2D.Double(this.X - Supply.W/2, this.Y -Supply.W/2, Supply.W, Supply.W).contains(x, y))
				return true;
			else return false;
		}
}
