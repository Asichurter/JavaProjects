package game;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * 用于表征连成一片的棋子的类
 *
 */
public class StoneGroup{
	
	/**
	 * 群组棋子的颜色
	 */
	private final boolean Color;
	/**
	 * 群组棋子集合
	 */
	private LinkedList<Stone> Stones;
	/**
	 * 群组气的数目
	 */
	private int Qi;
	/**
	 * 群组是否已经被新群组替代而过时
	 */
	private boolean ifReplaced = false;

	/**
	 * 用颜色来创建一个新的棋子群组
	 * @param color
	 */
	public StoneGroup(boolean color) {
		this.Color = color;
		this.Stones = new LinkedList<>();
		this.Qi = 0;
	}
	
	/**
	 * 向群组中添加一个群组中的所有棋子
	 * @param group 待被添加的群组
	 * @throws GoException 添加群组时颜色错误产生的异常
	 */
	public void addStone(StoneGroup group) throws GoException {
		if (group.getColor() != Color)
			throw new GoException("添加棋子时颜色错误（将白棋添加到了黑棋的集合中）");
		else this.Stones.addAll(group.Stones);
	}
	
	/**
	 * 向群组中添加一个单棋子
	 * @param stone 待添加的棋子
	 * @throws GoException 添加棋子时颜色发生错误产生的异常
	 */
	public void addStone(Stone stone) throws GoException {
		if (stone.getColor() != Color)
			throw new GoException("添加棋子时颜色错误（将白棋添加到了黑棋的集合中）");
		else this.Stones.add(stone);
	}
	
	/**
	 * 表征该群组已经被替代，待回收处理
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
	 * 返回群组棋子的迭代器
	 * @return 群组棋子的迭代器
	 */
	public Iterator<Stone> getStonesIter(){
		return this.Stones.iterator();
	}
	
	/**
	 * 用于判断群组是否已经死亡(弃用)
	 * @return 是否已经死亡
	 */
	public boolean ifDeadGroup() {
		return !this.Stones.isEmpty() && Qi == 0;
	}
	
	public String toString() {
		return this.Stones.stream().map(Stone::toString).collect(Collectors.joining("，", "[", "]")) + " 气数目: " + this.Qi;
	}
	
	public StoneGroup getClone() {
		StoneGroup g = new StoneGroup(this.Color);
		g.ifReplaced = this.ifReplaced;
		g.Qi = this.Qi;
		Iterator<Stone> iter = this.Stones.iterator();
		while (iter.hasNext()) {
			Stone s = iter.next();
			try {
				//9.8修复bug：在复制群组内部的棋子时，棋子的群组应该设置为新的克隆群组
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
