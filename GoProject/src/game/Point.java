package game;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

/**
 * 用于表征一个点的类
 *
 */
public class Point {
	
	/**
	 * 点的X坐标
	 */
	private int X;
	
	/**
	 * 点的Y坐标
	 */
	private int Y;
	
	/**
	 * 点坐标的限制
	 */
	public static final int constrainX = 8;
	/**
	 * 点坐标的限制
	 */
	public static final int constrainY = 8;
	
	/**
	 * 走棋点的启发估计值
	 */
	private double Score = 0;

	/**
	 * 一个XY点的构造器
	 * @param x 点的X坐标
	 * @param y 点的Y坐标
	 * @throws GoException 坐标溢出时抛出异常
	 */
	public Point(int x, int y) throws GoException {
		if (x > 8 || y > 8 || x < 0 || y < 0)
			throw new GoException("坐标溢出");
		this.X = x;
		this.Y = y;
	}
	
	public Point(int x, int y, int s) throws GoException {
		if (x > 8 || y > 8 || x < 0 || y < 0)
			throw new GoException("坐标溢出");
		this.X = x;
		this.Y = y;
		this.Score = s;
	}
	
	/**
	 * 创造一个无赋值的点
     */
	public Point() {
		this.X = 0;
		this.Y = 0;
	}
	
	public void setScore(double score) {
		this.Score = score;
	}
	
	public double getScore() {
		return this.Score;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
	
	public void setX(int x) {
		this.X = x;
	}
	
	public void setY(int y) {
		this.Y = y;
	}
	
	/**
	 * 判断点的位置是否相同
	 * @param p 待判断的点
	 * @return 点是否相同
	 */
	public boolean equals(Point p) {
		return this.X == p.getX() && this.Y == p.getY(); 
	}
	
	/**
	 * 返回用于描述点情况的字符串
	 */
	public String toString() {
		return "X=" + this.getX() + " Y=" + this.getY();
	}
}
