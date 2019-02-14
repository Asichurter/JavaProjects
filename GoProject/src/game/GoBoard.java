package game;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import ai.Node;
import ai.UtilityEstimate;
import newPattern.Pattern;
import ai.MTCS;

/**
 * 用于容纳棋子的棋盘的类
 *
 */
public class GoBoard implements Cloneable{
	
	/**
	 * 用于记录棋盘状态的写手
	 */
	private PrintWriter writer = new PrintWriter(new FileOutputStream("debug_board.txt"), true);
	
	/**
	 * 用于表征当前的行棋点
	 */
	private int CurrentX = 0;
	/**
	 * 用于表征当前的行棋点
	 */
	private int CurrentY = 0;
	
	/**
	 * 用于储存每个棋子的二维数组
	 */
	private Stone[][] stones = new Stone[9][9];
	
	/**
	 * 用于储存每种颜色棋子的群组的集合
	 */
	private LinkedList<StoneGroup> BlackGroup;
	/**
	 * 用于储存每种颜色棋子的群组的集合
	 */
	private LinkedList<StoneGroup> WhiteGroup;
	
	/**
	 * 生成随机数的公用生成器
	 */
	private static final Random rand = new Random();
	
	/**
	 * 合法行棋位置搜索耐心值，在耐心值耗尽后，将会抛出异常
	 */
	private final int SearchPatience = 30;
	
	/**
	 * 在检测耐心耗尽但是合法位置还存在时，耐心值的重置值
	 */
	private final int PatienceResetNum = 20;

	
	//****************************************************
	//模式的规定：中心点一定为黑色，且模式的第一步一定为黑棋走棋
	//****************************************************
	/**
	 * 模式的储存
	 */
	public static LinkedList<Pattern> Patterns;
	
	/**
	 * 模式的初始化块
	 */
	static {
		Patterns = new LinkedList<>();
		Patterns.add(new Pattern("_A_______Bo_________C___"
				, p->p.getX() == 2 && p.getY() == 2
				, 3));
		Patterns.add(new Pattern("__AoBC____G_EDF_________"
				, p->p.getX() == 2 && p.getY() == 2
				, 7));
		Patterns.add(new Pattern("BC___xooADE?????????_xx_",
				p->p.getX() >= 1 && p.getX() <= 6 && p.getY() >= 1 && p.getY() <= 6
				, 5));
		Patterns.add(new Pattern("BCE___D__oA____*****__F_",
				p-> p.getX() == 1 && p.getY() == 2
				, 6));			
	}
	
	/**
	 * 创建一个初始化的棋盘 
	 * @throws FileNotFoundException debug文件输出异常
	 */
	public GoBoard() throws FileNotFoundException {
		for (int i = 0; i <= 8; i++) {
			stones[i] = new Stone[9];
		}
		BlackGroup = new LinkedList<>();
		WhiteGroup = new LinkedList<>();
	}
	
	/**
	 * 用于返回棋盘上所有棋子的二维数组的引用
	 * @return 棋盘二维数组
	 */
	public Stone[][] getStones(){
		return this.stones;
	}
	
	/**
	 * 检查一个点的气的数目
	 * @param point 待检查的点
	 * @return 气的数目
	 */
	public int checkQiNum(Point point){
		int num = 4;
		if (point.getX()-1 >= 0) {
			if (stones[point.getX()-1][point.getY()] != null)
				num--;
		}
		else {num--;}
		if (point.getX()+1 <= 8) {
			if (stones[point.getX()+1][point.getY()] != null)
				num--;
		}
		else {num--;}
		if (point.getY()-1 >= 0) {
			if (stones[point.getX()][point.getY()-1] != null)
				num--;
		}
		else {num--;}
		if (point.getY()+1 <= 8) {
			if (stones[point.getX()][point.getY()+1] != null)
				num--;
		}
		else {num--;}
		return num;
	}
	
	/**
	 * 向棋盘中添加一个新的棋子
	 * @param color 添加的棋子颜色
	 * @param point 添加的棋子的点
	 * @param panel 容纳棋子的棋盘面板
	 * @return <p><code>true</code> 成功添加一个棋子</p><code>false 
	 * 								   </code> 走棋失败，因为走棋非法或者是该位置已经有棋子了
	 * 							
	 * @throws GoException 添加棋子时的抛出异常
	 * @throws FileNotFoundException 重置输出文件器时抛出的异常
	 */
	public boolean addStone(Point point, boolean color, GoPanel panel) throws GoException, FileNotFoundException {
		if (stones[point.getX()][point.getY()] != null) {
			JOptionPane.showMessageDialog(panel, "当前位置已有棋子！");
			return false;
		}				//如果向一个已有棋子的位置添加，则不会有任何的反应
		else {																								
			if (testIfLegal(point, color, 0)) {																												//如果合法
				stones[point.getX()][point.getY()] = new Stone(color, point, checkQiNum(point));
				CurrentX = point.getX();
				CurrentY = point.getY();
				resetNeighbors(point, color, false);
				refreshBoard(color, false);
				outputStonesToFile("Board.txt");
				return true;
			}
			else {
				JOptionPane.showMessageDialog(panel, "位置非法！");
				//showDebugMessage();
				return false;
			}
		}
	}
	
	/**
	 * 用于在博弈树中调用的，方便添加下一层棋子的重载方法
	 * @param point 添加棋子的点
	 * @param color 棋子颜色
	 * @throws GoException 添加棋子时出现的重叠或者非法位置现象
	 */
	public void addStone(Point point, boolean color, boolean ifPrint) throws GoException{
		try {
		if (stones[point.getX()][point.getY()] != null) {
			throw new GoException("致命错误！博弈树在已有棋子的地方添加了一个棋子！");
		}
		else {																								
			if (testIfLegal(point, color, 0)) {
				//如果合法
				stones[point.getX()][point.getY()] = new Stone(color, point, checkQiNum(point));
				CurrentX = point.getX();
				CurrentY = point.getY();
				resetNeighbors(point, color, false);
				refreshBoard(color, false);
			}
			else {
				throw new GoException("致命错误！博弈树在非法位置添加了一个棋子！");
			}
		}
		}
		catch(NullPointerException e) {
			GoException E;
			if (point == null) {
				E = new GoException("addStone时，点point为null");
			}
			else {
				E = new GoException("addStone时，未知的null指针异常");
				System.out.println("点位置：" + point);
				System.out.println("当前棋盘：");
				Node.printBoard(this.getState());
			}
			E.initCause(e);
			throw E;
		}
	}
	
	/**
	 * 蒙特卡洛树搜索中，用于判断是否打劫的重载方法
	 * @param point 待添加的棋子的点
	 * @param color 待添加的棋子颜色
	 * @return 被吃掉的单子
	 */
	public Point addStone(Point point, boolean color) throws GoException {
		stones[point.getX()][point.getY()] = new Stone(color, point, checkQiNum(point));
		CurrentX = point.getX();
		CurrentY = point.getY();
		resetNeighbors(point, color, false);
		return refreshSingles(color);
	}
	
	/**
	 * 蒙特卡洛树搜索中，判断打劫的辅助方法，用于返回一个被吃掉的唯一单子作为潜在的打劫点
	 * @param color
	 * @return
	 * @throws GoException 
	 */
	private Point refreshSingles(boolean color) throws GoException {
		Point p = null;
		//再对敌方颜色单棋子气的判断
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[i][j] != null) {
					if (stones[i][j].getGroup() == null) {
						if (checkQiNum(stones[i][j].getPoint()) == 0 && stones[i][j].getColor() != color){
							if (p == null) { 
								p = stones[i][j].getPoint();
								stones[i][j] = null;
							}
							else {
								stones[i][j] = null;
								return null;
							}
						}
					}
				}
			}
		}
		return p;
	}
	
	/**
	 * 用于进行所有群组的debug方法
	 */
	@SuppressWarnings("unused")
	private void showDebugMessage() {
		System.out.println("黑棋群组：\n" + this.BlackGroup.stream().map(StoneGroup::toString).collect(Collectors.joining("，", "{", "}")));
		System.out.println("白棋群组：\n" + this.WhiteGroup.stream().map(StoneGroup::toString).collect(Collectors.joining("，", "{", "}")) + "\n");
	}

	/**
	 * 刷新棋盘状态，将旧群组删除，将所有无气的棋子提出
	 * @throws GoException 有群组的气小于0的异常
	 */
	private void refreshBoard(boolean CurColor, boolean ifPrint) throws GoException {
		if (ifPrint) Node.printBoard(this.getState());
		//先对敌方群组进行操作
		refreshAllQi(!CurColor);
		Iterator<StoneGroup> iter = (CurColor ? this.WhiteGroup : this.BlackGroup).iterator();
		while (iter.hasNext()) {
			StoneGroup g = iter.next();
			//9.1修改
			//由于被替代群组的标记和回收都是在方法combineNewGroups里面进行的，所以与外界无关
			//如果发现有群组的气小于0
			if (g.getQi() < 0) {
				throw new GoException("刷新棋盘状态时出现了异常，有群组的气小于0");
			}
			//如果发现有群组的气为0，则将棋盘上所有群组内的棋子都移除，再移除群组
			else if (g.getQi() == 0) {
				Iterator<Stone> iter2 = g.getStonesIter();
				while (iter2.hasNext()) {
					Stone s = iter2.next();
					stones[s.getPoint().getX()][s.getPoint().getY()] = null;
				}
				iter.remove();
			}
		}
		
		//再对敌方颜色单棋子气的判断
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[i][j] != null) {
					if (stones[i][j].getGroup() == null) {
						if (stones[i][j].getQi() == 0 && stones[i][j].getColor() != CurColor){
							stones[i][j] = null;
						}
					}
				}
			}
		}
		
		//再刷新完对方颜色以后再刷新己方的颜色
		refreshAllQi(CurColor);
		
		iter = (!CurColor ? this.WhiteGroup : this.BlackGroup).iterator();
		while (iter.hasNext()) {
			StoneGroup g = iter.next();
			//如果发现有群组的气小于0
			if (g.getQi() < 0) {
				throw new GoException("刷新棋盘状态时出现了异常，有群组的气小于0");
			}
			//如果发现有群组的气为0，则将棋盘上所有群组内的棋子都移除，再移除群组
			else if (g.getQi() == 0) {
				if(ifPrint) System.out.println("我方群组无气");
				Iterator<Stone> iter2 = g.getStonesIter();
				while (iter2.hasNext()) {
					Stone s = iter2.next();
					stones[s.getPoint().getX()][s.getPoint().getY()] = null;
				}
				iter.remove();
			}
		}
		//再对单棋子进行操作
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[i][j] != null) {
					if (stones[i][j].getGroup() == null) {
						//9.1修改
						if (stones[i][j].getQi() == 0 && stones[i][j].getColor() == CurColor){
							stones[i][j] = null;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 刷新一个玩家的对方玩家的所欲棋子的气的状态，用于在行棋以后判断是否有因提子而发生气的变化(RunTime Check)
	 * @param CurColor 当前玩家的颜色
	 * @throws GoException 刷新当前颜色所有棋子的气时抛出的异常
	 */
	private void refreshAllQi(boolean CurColor) throws GoException {
		//重置当前颜色下所有群组的气
		Iterator<StoneGroup> iter = (CurColor ? this.BlackGroup : this.WhiteGroup).iterator();
		while(iter.hasNext()) {
			StoneGroup g = iter.next();
			Iterator<Stone> iter2 = g.getStonesIter();
			int Qi = 0;
			while (iter2.hasNext()) {
				Qi += checkQiNum(iter2.next().getPoint());
			}
			g.setQi(Qi);
		}
		//重置当前颜色下所有单棋子
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[i][j] != null && stones[i][j].getGroup() == null && stones[i][j].getColor() == CurColor)
					stones[i][j].setQi(checkQiNum(stones[i][j].getPoint()));
			}
		}
	}

	
	@SuppressWarnings("unused")
	public boolean testIfLegal(Point point, boolean color, int iden) throws GoException {
		if (this.stones[point.getX()][point.getY()] != null)
			return false;
		stones[point.getX()][point.getY()] = new Stone(color, point, 0);
		//boolean deadOrAlive = false;
		try {
			//测试性生成左侧的点
			Point test = new Point(point.getX()-1, point.getY());
			//如果左侧点没有出界，则测试是否有棋子
			if (stones[point.getX()-1][point.getY()] != null) {
				//如果有棋子，就测试是否同一颜色
				if (stones[point.getX()-1][point.getY()].getColor() == color)
					return legalTestHelper(point, true);
				//如果不是同一颜色，就测试是否是有吃子
				else if (testIfDead(point.getX()-1, point.getY(), color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		//如果棋子出界，则会捕获到Point构造器的异常，从而不对邻近点做任何测试
		catch (GoException e) {} //catch后不做任何事
		try {
			//测试性生成左侧的点
			Point test = new Point(point.getX(), point.getY()-1);
			//如果左侧点没有出界，则测试是否有棋子
			if (stones[point.getX()][point.getY()-1] != null) {
				//如果有棋子，就测试是否同一颜色
				if (stones[point.getX()][point.getY()-1].getColor() == color)
					return legalTestHelper(point, true);
				//如果不是同一颜色，就测试是否是有吃子
				else if (testIfDead(point.getX(), point.getY()-1, color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		catch (GoException e) {} //catch后不做任何事
		try {
			//测试性生成左侧的点
			Point test = new Point(point.getX()+1, point.getY());
			//如果左侧点没有出界，则测试是否有棋子
			if (stones[point.getX()+1][point.getY()] != null) {
				//如果有棋子，就测试是否同一颜色
				if (stones[point.getX()+1][point.getY()].getColor() == color)
					return legalTestHelper(point, true);
				//如果不是同一颜色，就测试是否是有吃子
				else if (testIfDead(point.getX()+1, point.getY(), color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		catch (GoException e) {} //catch后不做任何事
		try {
			//测试性生成左侧的点
			Point test = new Point(point.getX(), point.getY()+1);
			//如果左侧点没有出界，则测试是否有棋子
			if (stones[point.getX()][point.getY()+1] != null) {
				//如果有棋子，就测试是否同一颜色
				if (stones[point.getX()][point.getY()+1].getColor() == color)
					return legalTestHelper(point, true);
				//如果不是同一颜色，就测试是否是有吃子
				else if (testIfDead(point.getX(), point.getY()+1, color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		catch (GoException e) {} //catch后不做任何事
		return legalTestHelper(point, false);
	}
	
	private boolean legalTestHelper(Point p, boolean value) {
		this.stones[p.getX()][p.getY()] = null;
		return value;
	}
	
	/**
	 * 用于辅助测试函数，用于测试一个方向的单位，要么是单棋子，要么是群组是否在测试性添子以后死亡
	 * @param x 测试方向的X坐标
	 * @param y 测试方向的Y坐标
	 * @param 已经测试过的群组
	 * @return 是否死亡
	 * @throws GoException 
	 */
	private boolean testIfDead(int x, int y, boolean color) throws GoException {
		if (stones[x][y].getGroup() == null) {
			if (checkQiNum(stones[x][y].getPoint()) == 0) {
				return true;
			}
		}
		//9.1修改
		//检查某点处是否有吃群组的子的情况，只需检查改群组即可，无需遍历所有群组
		else {
			StoneGroup g = stones[x][y].getGroup();
			Iterator<Stone> iter = g.getStonesIter();
			while (iter.hasNext()) {
				//只要有一个群组棋子有气，就代表群组没被吃掉
				if (checkQiNum(iter.next().getPoint()) != 0)
					return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 检查同时重置邻近的棋子的状态，如果是敌方棋子或者群组则紧气，如果是己方单棋子或者群组则进行组合
	 * @param point 待检查的位置
	 * @param color 棋子颜色
	 * @throws GoException 添加群组时颜色不一致的异常
	 */
	private void resetNeighbors(Point point,boolean color, boolean ifPrint) throws GoException {
		//if (color) System.out.println("!!!!");
		LinkedList<StoneGroup> tempG = new LinkedList<>();
		LinkedList<Stone> tempS = new LinkedList<>();
		Stone s;
		//如果左侧没有出界
		if (point.getX() > 0) {
			//如果左侧有棋子
			s = stones[point.getX()-1][point.getY()];
			if (s != null) {
				//如果左侧有群组
				if (s.getGroup() != null) {
					//如果左侧群组颜色一致
					//9.1修改：
					//1.为了避免添加重复的群组，改为使用链表代替数组
					//2.由于使用了checkQiNum的检查，因此弃用了所有群组和棋子的气实例域，因此不再紧气，只是添加群组
					if (s.getGroup().getColor() == color) {
						//如果没有重复包含同一群组，就添加之
						if (!tempG.contains(s.getGroup()))
							tempG.add(s.getGroup());
					}
				}
				//左侧无群组
				else {
					//单棋子颜色一致
					if (s.getColor() == color) {
						tempS.add(s);
					}
				}
			}
		}
		
		//上侧
		if (point.getY() > 0) {
			s = stones[point.getX()][point.getY()-1];
			if (s != null) {
				if (s.getGroup() != null) {
					if (s.getGroup().getColor() == color) {
						if (!tempG.contains(s.getGroup()))
							tempG.add(s.getGroup());
					}
				}
				else {
					if (s.getColor() == color)
						tempS.add(s);
				}
			}
		}
		
		//右侧
		if (point.getX() < 8) {
			s = stones[point.getX()+1][point.getY()];
			if (s != null) {
				if (s.getGroup() != null) {
					if (s.getGroup().getColor() == color) {
						if (!tempG.contains(s.getGroup()))
							tempG.add(s.getGroup());
					}
				}
				else {
					if (s.getColor() == color)
						tempS.add(s);
				}
			}
		}
		
		//下侧
		if (point.getY() < 8) {
			s = stones[point.getX()][point.getY()+1];
			if (s != null) {
				if (s.getGroup() != null) {
					if (s.getGroup().getColor() == color) {
						if (!tempG.contains(s.getGroup()))
							tempG.add(s.getGroup());
					}//结束颜色相同
				}//结束群组非空
				else {
					if (s.getColor() == color)
						tempS.add(s);
				}//结束群组为空
			} //结束位置非空
		}
			
		//list转array
		Stone[] ArrayS = new Stone[tempS.size()];
		StoneGroup[] ArrayG = new StoneGroup[tempG.size()];
		ArrayG = tempG.toArray(ArrayG);
		ArrayS = tempS.toArray(ArrayS);
		//将所有颜色一致的群组或者单棋子进行组合
		combineNewGroup(stones[point.getX()][point.getY()], ArrayS, ArrayG, color, false);
	}
	
	/**
	 * 用于将单棋子或者群组组合成为新群组
	 * @param stones 待组合的旧单棋子数组
	 * @param groups 待组合的旧群组数组
	 * @param color 待组合的颜色
	 * @throws GoException 群组组合时的异常
	 */
	private void combineNewGroup(Stone Curstone, Stone[] stones, StoneGroup[] groups, boolean color, boolean ifPrint) throws GoException {
		//9.1修改
		//如果新添加的棋子周围没有己方棋子或者群组（stones和groups均无元素），则直接跳出
		if (stones.length == 0 && groups.length == 0) {
			return;
		}
		//新群组
		StoneGroup newG = new StoneGroup(color);
		newG.addStone(Curstone);
		newG.setQi(0);
		Curstone.setGroup(newG);
		//9.1修改
		//1.由于群组和单棋子气的弃用，因此在添加群组时不再考虑其气的问题
		//由于在方法开头已经对周围没有群组的情况进行了检查，因此不再需要检查是否没有群组和单棋子的添加
		//将单棋子添加到新群组中
		try {
				for (Stone s : stones) {	
					if (s != null) {
						s.setGroup(newG);
						newG.addStone(s);
					}
				}
		//将旧群组添加到新群组中
			if (groups.length > 0) {
				for (StoneGroup g : groups) {														
					if (g != null) {
						newG.addStone(g);
						Iterator<Stone> iter = g.getStonesIter();	
						//迭代将旧群组中的棋子的群组都设置为新群组
						while (iter.hasNext()) {															
							iter.next().setGroup(newG);
						}
						//回收旧群组
						g.replaced();
					}
				}
			}
		//遍历群组集合，将标记的旧群组回收
		Iterator<StoneGroup> iter2 = (color ? this.BlackGroup : this.WhiteGroup).iterator();
		while (iter2.hasNext()) {
			if (iter2.next().getIfReplaced())
				iter2.remove();
		}
		//添加新群组
		(color ? this.BlackGroup : this.WhiteGroup).add(newG);
	}
		catch(Exception e) {
			GoException E = new GoException("添加群组时出现了异常， 原始原因：" + e.getMessage());
			E.initCause(e);
			throw E;
		}
	}
	
	/**
	 * 用于将棋盘上当前的所有棋子与棋盘打印到文件中
	 * @throws FileNotFoundException 指定文件没有找到的异常
	 */
	private void outputStonesToFile(String fname) throws FileNotFoundException {
		this.writer = new PrintWriter(new FileOutputStream(fname), true);
		writer.println("棋盘状态\n");
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[j][i] == null)
					writer.print(" ");
				else if (stones[j][i].getColor())
					writer.print("X");
				else if (!stones[j][i].getColor())
					writer.print("O");
				if (j != 8)
					writer.print("——");
				else writer.println();
			}
			if (i != 8)
				writer.println("|  |  |  |  |  |  |  |  |");
		}
	}
	
	/**
	 * 用于得到当前的行棋点的外部调用方法
	 * @return 当前行棋点
	 * @throws GoException 点溢出的异常
	 */
	public Point getCurrentPoint() throws GoException {
		return new Point(CurrentX, CurrentY);
	}
	
	/**
	 * @return 当前棋盘的状态
	 */
	public Stone[][] getState() {
		return this.stones;
	}
	
	/**
	 * 在克隆方法中调用。从已有的群组中，找到对应点位置的棋子
	 * @param groups 已有的群组
	 * @param point 点的位置
	 * @param color 棋子对应的颜色
	 * @return 群组中对应点的棋子
	 */
	private Stone findMatchStoneFromGroups(LinkedList<StoneGroup> groups, Point point, boolean color) throws GoException {
		for (StoneGroup g : groups) {
			Iterator<Stone> iter = g.getStonesIter();
			while (iter.hasNext()) {
				Stone stone = iter.next();
				if (stone.getPoint().equals(point))
					return stone;
			}
		}
		throw new GoException("GoBoard深克隆时出现异常！在克隆完群组时，从群组中找不到相应位置的点！");
	}
	
	/**
	 * 用于外部的ai的node调用，利用节点的board生成curPos附近的随机合法。在findChildNode中调用
	 * @param limit 节点数量限制
	 * @param color 节点颜色
	 * @return 合法位置
	 * @throws CloneNotSupportedException 
	 */
	public Point[] getLegalPos(final int limit, final int waiting, boolean color) throws GoException, CloneNotSupportedException {
		if (getEmptySiteNumOnBoard(this) < 30)
			return pointShortageHelper(limit, color);
		//修复bug：声明数组时，长度按照limit声明，但是由于limit变化导致数组可能并未按照limit长度填充
		ArrayList<Point> pos = new ArrayList<>();
		//使用waiting可变化来代替固定的waiting值
		int i, Waiting = waiting;
		for (i = 0; i < Waiting; i++) {
			//为了减少一路爬的臭棋，因此使用静态方法一定概率重置一路爬的棋
			Point point = UtilityEstimate.RetryWhenMeetLineOne(new Point(rand.nextInt(9), rand.nextInt(9)));
			//每一个预选点的搜索有耐心值限制
			int patience = this.SearchPatience;
			
			//检查点是否能够合法，不合法时消耗一个耐心值重置点的位置
			while(!testIfLegal(point, color, 0)) {
				//耐心耗尽时
				if (patience == 0) {
					//如果合法位置数量不足时
					if (getEmptySiteNumOnBoard(this) < limit)
						throw new GoException("棋盘上合法位置数量已经小于子节点数量了!");
					//如果重启搜索次数过多导致waiting为0时
					//如果合法位置还充足时，重启搜索并且减少合法位置数量
					else {
						patience+=PatienceResetNum;
						Waiting--;
						if (Waiting < 3)
							return pointShortageHelper(limit, color);
					}
				}
				patience--;
				point = UtilityEstimate.RetryWhenMeetLineOne(new Point(rand.nextInt(9), rand.nextInt(9)));
			}
			//没有搜索到点时，数组中的点将会是null
			point.setScore(UtilityEstimate.PosEvaluate(this, point, new Point(CurrentX, CurrentY), color));
			pos.add(point);
		}
		//如果得到的合法点数量过少
		if (pos.size() < limit)
			throw new GoException("搜索失败！得到的合法点甚至不足NodeLimit!");
		
		//如果合法点数量过多，应该先排序，再取启发值较大的一些点返回
		if(pos.size() > limit)
			pos.sort(new PointScoreComparator());
		
		Point[] realPos = new Point[pos.size()];
		
		//返回启发值较大的点
		return Arrays.copyOfRange(pos.toArray(realPos), 0, limit);
	}
	
	private Point[] pointShortageHelper(final int limit, boolean color) throws GoException, CloneNotSupportedException {
		LinkedList<Point> all = this.getAllEmptyPoints(color);
		if (all.size() >= limit) {
			all.sort(new PointScoreComparator());
			Point[] re = new Point[limit];
			return Arrays.copyOfRange(all.toArray(re), 0, limit);
		}
		else if (all.size() > 0){
			Point[] re = new Point[all.size()];
			return Arrays.copyOfRange(all.toArray(re), 0, all.size());
		}
		else {
			throw new GoException("棋盘上没有走棋点");
		}
	}
	
	/**
	 * 获得棋盘上空的点的数目
	 * @param board 棋盘
	 * @return 空位置数目
	 */
	public int getEmptySiteNumOnBoard(GoBoard board) {
		int num = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board.getState()[j][i] == null)
					num++;
			}
		}
		return num;
	}
	
	
	public boolean yanDetect(int x, int y) throws GoException {
		return yanDetect(x, y, true) || yanDetect(x, y, false);
	}
	
	/**
	 * ai调用。探查一个位置是否为己方的真眼，用于随机模拟时，不能进行填眼
	 * @param x
	 * @param y
	 * @param color
	 * @return
	 * @throws GoException
	 */
	public boolean yanDetect(int x, int y, boolean color){
		int num;
		boolean XBoarder = false, YBoarder = false;
		if (x - 1 >= 0) {
			if (stones[x-1][y] == null)
				return false;
			else if (stones[x-1][y].getColor() != color)
				return false;
		}
		else {
			XBoarder = true;
		}
		if (x + 1 <= 8) {
			if (stones[x+1][y] == null)
				return false;
			else if (stones[x+1][y].getColor() != color)
				return false;
		}
		else {
			XBoarder = true;
		}
		if (y - 1 >= 0) {
			if (stones[x][y-1] == null)
				return false;
			else if (stones[x][y-1].getColor() != color)
				return false;
		}
		else {
			YBoarder = true;
		}
		if (y + 1 <= 8) {
			if (stones[x][y+1] == null)
				return false;
			else if (stones[x][y+1].getColor() != color)
				return false;
		}
		else {
			YBoarder = true;
		}
		if (XBoarder || YBoarder)
			num = 1;
		else num = 2;
		if (x-1 >= 0 && y-1 >= 0) {
			if (stones[x-1][y-1] != null && stones[x-1][y-1].getColor() != color)
				num--;
		}
		if (x+1 < 9 && y+1 < 9) {
			if (stones[x+1][y+1] != null && stones[x+1][y+1].getColor() != color)
				num--;
		}
		if (x-1 >= 0 && y+1 < 9) {
			if (stones[x-1][y+1] != null && stones[x-1][y+1].getColor() != color)
				num--;
		}
		if (x+1 < 9 && y-1 >= 0) {
			if (stones[x+1][y-1] != null && stones[x+1][y-1].getColor() != color)
				num--;
		}
		return num > 0;
	}
	
	/**
	 * 用于判断一个位置是否为劫
	 * @param board 当前棋盘
	 * @param point 待判断的点
	 * @param color 判断的颜色
	 * @return 是否为劫
	 */
	public boolean zedCheck(Point point, boolean color) throws CloneNotSupportedException, GoException {
		GoBoard newB = this.clone();
		//调用特殊的addStone方法，该方法将会把吃掉的唯一一个棋子返回出来
		Point p = newB.addStone(point, color);
		//如果这步棋没有吃掉子，或者是吃掉了一个以上的棋子（不满足打劫条件）
		if (p == null) {
			return false;
		}
		else {
			//如果被吃掉的点不合法，则不能成劫
			if (!newB.testIfLegal(p, !color, 0)) {
				return false;
			}
			Point P = newB.addStone(p, !color);
			//如果反走吃掉的点但是没有吃子，不能成劫
			if (P == null) {
				return false;
			}
			//如果反走吃掉的点吃掉的棋子和之前吃掉的棋子的点位置一样，则成劫
			else return P.equals(point);
		}
	}
	
	private LinkedList<Point> getAllEmptyPoints(boolean color) throws GoException, CloneNotSupportedException{
		LinkedList<Point> points = new LinkedList<>();
		Stone[][] state = this.getState();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null) {
					Point newP = new Point(j, i);
					//该点为合法行棋点的要求：
					//1.走棋合法
					//2.不能填眼
					//3.不能打劫 v
					if (this.testIfLegal(newP, color, 0) 
							&& !this.yanDetect(j, i, color) 
							&& !this.zedCheck(newP, color))
						points.add(newP);
				}
			}
		}
		return points;
	}
	
	@Override
	public GoBoard clone() throws CloneNotSupportedException {
		GoBoard cloneBoard = (GoBoard)super.clone();
		cloneBoard.stones = new Stone[9][9];
		cloneBoard.BlackGroup = new LinkedList<StoneGroup>();
		cloneBoard.WhiteGroup = new LinkedList<StoneGroup>();
		
		//group类的深克隆：对新建的list对象，通过调用group的深克隆方法getClone进行填充
		for (StoneGroup g : this.BlackGroup) {
			cloneBoard.BlackGroup.add(g.getClone());
		}
		for (StoneGroup g : this.WhiteGroup) {
			cloneBoard.WhiteGroup.add(g.getClone());
		}
		
		//9.8修复bug：board棋盘上的stone应该与群组内部的stone相联系
		//棋盘的克隆，通过传递相同参数入构造器得到深克隆，同时还要设定棋子的群组
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) { 
				try {
				if (this.stones[j][i] != null) {
					Stone s = this.stones[j][i];
					//如果被克隆的棋盘上，某处的棋子有群组，则应该从已经克隆的群组中找出点
					if (s.getGroup() != null) {
						cloneBoard.stones[j][i] = 
								findMatchStoneFromGroups(
								(s.getColor() ? cloneBoard.BlackGroup : cloneBoard.WhiteGroup), 
								s.getPoint(), 
								s.getColor());
					}
					//如果被克隆的棋盘上，某处的棋子没有群组，则直接调用构造器给克隆棋盘赋值即可
					else cloneBoard.stones[j][i] = new Stone(s.getColor(), s.getPoint(), 0);
				}
				}
				catch(GoException e) {
					System.err.println("深克隆时，调用构造器或者findMatchStoneFromGroups方法时出现异常！");
					e.printStackTrace();
				}
			}
		}
		return cloneBoard;
	}
}

class PointScoreComparator implements Comparator<Point>{
	
	public PointScoreComparator() {};
	
	public int compare(Point p2, Point p1) {
		if (p1.getScore() > p2.getScore())
			return 1;
		if (p1.getScore() < p2.getScore())
			return -1;
		else return 0;
	}
}



































