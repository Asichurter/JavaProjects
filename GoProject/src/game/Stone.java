package game;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

/**
 * 用于表征棋子的类
 *
 */

public class Stone {
	
	/**
	 * 黑棋：true  白棋：false
	 */
	private boolean Color;
	/**
	 * 该单棋子所在的点
	 */
	private Point point;
	/**
	 * 该类颜色的棋子的图片
	 */
	private Image image;
	/**
	 * 该棋子所属的群组，默认为null
	 */
	private StoneGroup group = null;
	/**
	 * 该棋子的气的数目
	 */
	private int Qi;

	/**
	 * 用颜色和XY位置坐标创建一个棋子
	 * @param color 黑棋还是白棋
	 * @param x x坐标
	 * @param y y坐标
	 * @param q 新棋子位置的气
	 * @throws GoException 创建棋子时坐标溢出
	 */
	public Stone(boolean color, int x, int y, int q) throws GoException {
		this.Color = color;
		this.point = new Point(x, y);
		this.image = Toolkit.getDefaultToolkit().getImage("Images/" + (this.Color ? "黑棋.gif" : "白棋.gif"));
		this.Qi = q;
	}
	
	/**
	 * 用颜色和一个点来构建一个棋子
	 * @param color 棋子颜色
	 * @param point 棋子位于的点
	 * @param q 新棋子位置的气
	 * @throws GoException 创建棋子时坐标溢出
	 */
	public Stone(boolean color, Point point, int q) throws GoException {
		this(color, point.getX(), point.getY(), q);
	}
	
	public Stone(boolean color, Point point, int q, StoneGroup g) throws GoException {
		this(color, point.getX(), point.getY(), q);
		this.setGroup(g);
	}
	
	/**
	 * 创造一个用于测试的棋子
	 * @param color 棋子颜色
	 * @param point 棋子点位置
	 * @throws GoException 创建棋子时坐标溢出
	 */
	public Stone(boolean color, Point point) throws GoException {
		this(color, point, 4);
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public Image getImage() {
		return this.image;
	}
	
	public boolean getColor() {
		return this.Color;
	}
	
	/**
	 * 设置该棋子属于的群组的引用
	 * @param group 该棋子属于的群组
	 */
	public void setGroup(StoneGroup group) {
		//System.out.println(this.point + "处的棋子被设置了群组");
		this.group = group;
	}
	
	public StoneGroup getGroup() {
		return this.group;
	}
	
	public int getQi() {
		return this.Qi;
	}
	
	public void setQi(int qi) {
		this.Qi = qi;
	}
	
	/**
	 * 返回棋子的颜色，位置等信息的描述型字符串
	 */
	public String toString() {
		return (this.Color ? "黑棋" : "白棋") + "坐标:  " + this.point;
	}
}




















