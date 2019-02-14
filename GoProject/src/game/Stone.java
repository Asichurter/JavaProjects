package game;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

/**
 * ���ڱ������ӵ���
 *
 */

public class Stone {
	
	/**
	 * ���壺true  ���壺false
	 */
	private boolean Color;
	/**
	 * �õ��������ڵĵ�
	 */
	private Point point;
	/**
	 * ������ɫ�����ӵ�ͼƬ
	 */
	private Image image;
	/**
	 * ������������Ⱥ�飬Ĭ��Ϊnull
	 */
	private StoneGroup group = null;
	/**
	 * �����ӵ�������Ŀ
	 */
	private int Qi;

	/**
	 * ����ɫ��XYλ�����괴��һ������
	 * @param color ���廹�ǰ���
	 * @param x x����
	 * @param y y����
	 * @param q ������λ�õ���
	 * @throws GoException ��������ʱ�������
	 */
	public Stone(boolean color, int x, int y, int q) throws GoException {
		this.Color = color;
		this.point = new Point(x, y);
		this.image = Toolkit.getDefaultToolkit().getImage("Images/" + (this.Color ? "����.gif" : "����.gif"));
		this.Qi = q;
	}
	
	/**
	 * ����ɫ��һ����������һ������
	 * @param color ������ɫ
	 * @param point ����λ�ڵĵ�
	 * @param q ������λ�õ���
	 * @throws GoException ��������ʱ�������
	 */
	public Stone(boolean color, Point point, int q) throws GoException {
		this(color, point.getX(), point.getY(), q);
	}
	
	public Stone(boolean color, Point point, int q, StoneGroup g) throws GoException {
		this(color, point.getX(), point.getY(), q);
		this.setGroup(g);
	}
	
	/**
	 * ����һ�����ڲ��Ե�����
	 * @param color ������ɫ
	 * @param point ���ӵ�λ��
	 * @throws GoException ��������ʱ�������
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
	 * ���ø��������ڵ�Ⱥ�������
	 * @param group ���������ڵ�Ⱥ��
	 */
	public void setGroup(StoneGroup group) {
		//System.out.println(this.point + "�������ӱ�������Ⱥ��");
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
	 * �������ӵ���ɫ��λ�õ���Ϣ���������ַ���
	 */
	public String toString() {
		return (this.Color ? "����" : "����") + "����:  " + this.point;
	}
}




















