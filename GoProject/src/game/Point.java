package game;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

/**
 * ���ڱ���һ�������
 *
 */
public class Point {
	
	/**
	 * ���X����
	 */
	private int X;
	
	/**
	 * ���Y����
	 */
	private int Y;
	
	/**
	 * �����������
	 */
	public static final int constrainX = 8;
	/**
	 * �����������
	 */
	public static final int constrainY = 8;
	
	/**
	 * ��������������ֵ
	 */
	private double Score = 0;

	/**
	 * һ��XY��Ĺ�����
	 * @param x ���X����
	 * @param y ���Y����
	 * @throws GoException �������ʱ�׳��쳣
	 */
	public Point(int x, int y) throws GoException {
		if (x > 8 || y > 8 || x < 0 || y < 0)
			throw new GoException("�������");
		this.X = x;
		this.Y = y;
	}
	
	public Point(int x, int y, int s) throws GoException {
		if (x > 8 || y > 8 || x < 0 || y < 0)
			throw new GoException("�������");
		this.X = x;
		this.Y = y;
		this.Score = s;
	}
	
	/**
	 * ����һ���޸�ֵ�ĵ�
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
	 * �жϵ��λ���Ƿ���ͬ
	 * @param p ���жϵĵ�
	 * @return ���Ƿ���ͬ
	 */
	public boolean equals(Point p) {
		return this.X == p.getX() && this.Y == p.getY(); 
	}
	
	/**
	 * ��������������������ַ���
	 */
	public String toString() {
		return "X=" + this.getX() + " Y=" + this.getY();
	}
}
