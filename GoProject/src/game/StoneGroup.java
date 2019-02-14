package game;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * ���ڱ�������һƬ�����ӵ���
 *
 */
public class StoneGroup{
	
	/**
	 * Ⱥ�����ӵ���ɫ
	 */
	private final boolean Color;
	/**
	 * Ⱥ�����Ӽ���
	 */
	private LinkedList<Stone> Stones;
	/**
	 * Ⱥ��������Ŀ
	 */
	private int Qi;
	/**
	 * Ⱥ���Ƿ��Ѿ�����Ⱥ���������ʱ
	 */
	private boolean ifReplaced = false;

	/**
	 * ����ɫ������һ���µ�����Ⱥ��
	 * @param color
	 */
	public StoneGroup(boolean color) {
		this.Color = color;
		this.Stones = new LinkedList<>();
		this.Qi = 0;
	}
	
	/**
	 * ��Ⱥ�������һ��Ⱥ���е���������
	 * @param group ������ӵ�Ⱥ��
	 * @throws GoException ���Ⱥ��ʱ��ɫ����������쳣
	 */
	public void addStone(StoneGroup group) throws GoException {
		if (group.getColor() != Color)
			throw new GoException("�������ʱ��ɫ���󣨽�������ӵ��˺���ļ����У�");
		else this.Stones.addAll(group.Stones);
	}
	
	/**
	 * ��Ⱥ�������һ��������
	 * @param stone ����ӵ�����
	 * @throws GoException �������ʱ��ɫ��������������쳣
	 */
	public void addStone(Stone stone) throws GoException {
		if (stone.getColor() != Color)
			throw new GoException("�������ʱ��ɫ���󣨽�������ӵ��˺���ļ����У�");
		else this.Stones.add(stone);
	}
	
	/**
	 * ������Ⱥ���Ѿ�������������մ���
	 */
	public void replaced() {
		this.ifReplaced = !this.ifReplaced;
	}
	
	public boolean getIfReplaced() {
		return this.ifReplaced;
	}
	
	public int getQi() {
		return this.Qi;
	}
	
	public void setQi(int qi) {
		this.Qi = qi;
	}
	
	public boolean getColor() {
		return this.Color;
	}
	
	/**
	 * ����Ⱥ�����ӵĵ�����
	 * @return Ⱥ�����ӵĵ�����
	 */
	public Iterator<Stone> getStonesIter(){
		return this.Stones.iterator();
	}
	
	/**
	 * �����ж�Ⱥ���Ƿ��Ѿ�����(����)
	 * @return �Ƿ��Ѿ�����
	 */
	public boolean ifDeadGroup() {
		return !this.Stones.isEmpty() && Qi == 0;
	}
	
	public String toString() {
		return this.Stones.stream().map(Stone::toString).collect(Collectors.joining("��", "[", "]")) + " ����Ŀ: " + this.Qi;
	}
	
	public StoneGroup getClone() {
		StoneGroup g = new StoneGroup(this.Color);
		g.ifReplaced = this.ifReplaced;
		g.Qi = this.Qi;
		Iterator<Stone> iter = this.Stones.iterator();
		while (iter.hasNext()) {
			Stone s = iter.next();
			try {
				//9.8�޸�bug���ڸ���Ⱥ���ڲ�������ʱ�����ӵ�Ⱥ��Ӧ������Ϊ�µĿ�¡Ⱥ��
				Stone newStone = new Stone(s.getColor(), s.getPoint().getX(), s.getPoint().getY(), 1);
				newStone.setGroup(g);
				g.Stones.add(newStone);
			}
			catch (GoException e) {
				e.printStackTrace();
			}
		}
		return g;
	}

}
