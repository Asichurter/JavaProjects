package tank_bullet;

/**
 * 表征方向属性。含有检查碰撞类型的方法
 * @author Asichurter
 *
 */
public enum Dir {
	UP , DOWN, LEFT, RIGHT;	
	
	/**
	 * 判断方向是否相同
	 * @param my 待判断的方向
	 * @param other 待判断的方向
	 * @return 方向是否相同
	 */
	public static boolean equals(Dir my, Dir other) {
		return my == other;
	}
	
	/**
	 * 检查两个撞击方向，用于测定相对速度
	 * @param dir1 待检查的方向
	 * @param dir2 待检查的方向
	 * @return
	 * 0:纵向撞击
	 * 1:同向撞击
	 * 2:对撞
	 */
	public static int checkCrashDir(Dir dir1, Dir dir2) {
		switch(dir1) {
			case UP:
				if (dir2 == RIGHT || dir2 == LEFT) 
					return 0;
				else if (dir2 == UP)
					return 1;
				else return 2;
				
			case DOWN:
				if (dir2 == RIGHT || dir2 == LEFT)
					return 0;
				else if (dir2 == DOWN)
					return 1;
				else return 2;
				
			case RIGHT:
				if (dir2 == UP || dir2 == DOWN)
					return 0;
				else if (dir2 == RIGHT)
					return 1;
				else return 2;
				
			case LEFT:
				if (dir2 == UP || dir2 == DOWN)
					return 0;
				else if (dir2 == LEFT)
					return 1;
				else return 2;
			
				default:
					return 0;
		}
	}		
	
	/**
	 * 得到方向具体的文字描述
	 * @return 方向的文字描述
	 */
	public String getDes() {
			switch(this) {
				case UP:
					return "上";
				case DOWN:
					return "下";
				case LEFT:
					return "左";
				case RIGHT:
					return "右";
					default:
						return "";
			}
		}
}
