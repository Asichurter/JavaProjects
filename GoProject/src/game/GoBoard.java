package game;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
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
 * �����������ӵ����̵���
 *
 */
public class GoBoard implements Cloneable{
	
	/**
	 * ���ڼ�¼����״̬��д��
	 */
	private PrintWriter writer = new PrintWriter(new FileOutputStream("debug_board.txt"), true);
	
	/**
	 * ���ڱ�����ǰ�������
	 */
	private int CurrentX = 0;
	/**
	 * ���ڱ�����ǰ�������
	 */
	private int CurrentY = 0;
	
	/**
	 * ���ڴ���ÿ�����ӵĶ�ά����
	 */
	private Stone[][] stones = new Stone[9][9];
	
	/**
	 * ���ڴ���ÿ����ɫ���ӵ�Ⱥ��ļ���
	 */
	private LinkedList<StoneGroup> BlackGroup;
	/**
	 * ���ڴ���ÿ����ɫ���ӵ�Ⱥ��ļ���
	 */
	private LinkedList<StoneGroup> WhiteGroup;
	
	/**
	 * ����������Ĺ���������
	 */
	private static final Random rand = new Random();
	
	/**
	 * �Ϸ�����λ����������ֵ��������ֵ�ľ��󣬽����׳��쳣
	 */
	private final int SearchPatience = 30;
	
	/**
	 * �ڼ�����ĺľ����ǺϷ�λ�û�����ʱ������ֵ������ֵ
	 */
	private final int PatienceResetNum = 20;

	
	//****************************************************
	//ģʽ�Ĺ涨�����ĵ�һ��Ϊ��ɫ����ģʽ�ĵ�һ��һ��Ϊ��������
	//****************************************************
	/**
	 * ģʽ�Ĵ���
	 */
	public static LinkedList<Pattern> Patterns;
	
	/**
	 * ģʽ�ĳ�ʼ����
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
	 * ����һ����ʼ�������� 
	 * @throws FileNotFoundException debug�ļ�����쳣
	 */
	public GoBoard() throws FileNotFoundException {
		for (int i = 0; i <= 8; i++) {
			stones[i] = new Stone[9];
		}
		BlackGroup = new LinkedList<>();
		WhiteGroup = new LinkedList<>();
	}
	
	/**
	 * ���ڷ����������������ӵĶ�ά���������
	 * @return ���̶�ά����
	 */
	public Stone[][] getStones(){
		return this.stones;
	}
	
	/**
	 * ���һ�����������Ŀ
	 * @param point �����ĵ�
	 * @return ������Ŀ
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
	 * �����������һ���µ�����
	 * @param color ��ӵ�������ɫ
	 * @param point ��ӵ����ӵĵ�
	 * @param panel �������ӵ��������
	 * @return <p><code>true</code> �ɹ����һ������</p><code>false 
	 * 								   </code> ����ʧ�ܣ���Ϊ����Ƿ������Ǹ�λ���Ѿ���������
	 * 							
	 * @throws GoException �������ʱ���׳��쳣
	 * @throws FileNotFoundException ��������ļ���ʱ�׳����쳣
	 */
	public boolean addStone(Point point, boolean color, GoPanel panel) throws GoException, FileNotFoundException {
		if (stones[point.getX()][point.getY()] != null) {
			JOptionPane.showMessageDialog(panel, "��ǰλ���������ӣ�");
			return false;
		}				//�����һ���������ӵ�λ����ӣ��򲻻����κεķ�Ӧ
		else {																								
			if (testIfLegal(point, color, 0)) {																												//����Ϸ�
				stones[point.getX()][point.getY()] = new Stone(color, point, checkQiNum(point));
				CurrentX = point.getX();
				CurrentY = point.getY();
				resetNeighbors(point, color, false);
				refreshBoard(color, false);
				outputStonesToFile("Board.txt");
				return true;
			}
			else {
				JOptionPane.showMessageDialog(panel, "λ�÷Ƿ���");
				//showDebugMessage();
				return false;
			}
		}
	}
	
	/**
	 * �����ڲ������е��õģ����������һ�����ӵ����ط���
	 * @param point ������ӵĵ�
	 * @param color ������ɫ
	 * @throws GoException �������ʱ���ֵ��ص����߷Ƿ�λ������
	 */
	public void addStone(Point point, boolean color, boolean ifPrint) throws GoException{
		try {
		if (stones[point.getX()][point.getY()] != null) {
			throw new GoException("�������󣡲��������������ӵĵط������һ�����ӣ�");
		}
		else {																								
			if (testIfLegal(point, color, 0)) {
				//����Ϸ�
				stones[point.getX()][point.getY()] = new Stone(color, point, checkQiNum(point));
				CurrentX = point.getX();
				CurrentY = point.getY();
				resetNeighbors(point, color, false);
				refreshBoard(color, false);
			}
			else {
				throw new GoException("�������󣡲������ڷǷ�λ�������һ�����ӣ�");
			}
		}
		}
		catch(NullPointerException e) {
			GoException E;
			if (point == null) {
				E = new GoException("addStoneʱ����pointΪnull");
			}
			else {
				E = new GoException("addStoneʱ��δ֪��nullָ���쳣");
				System.out.println("��λ�ã�" + point);
				System.out.println("��ǰ���̣�");
				Node.printBoard(this.getState());
			}
			E.initCause(e);
			throw E;
		}
	}
	
	/**
	 * ���ؿ����������У������ж��Ƿ��ٵ����ط���
	 * @param point ����ӵ����ӵĵ�
	 * @param color ����ӵ�������ɫ
	 * @return ���Ե��ĵ���
	 */
	public Point addStone(Point point, boolean color) throws GoException {
		stones[point.getX()][point.getY()] = new Stone(color, point, checkQiNum(point));
		CurrentX = point.getX();
		CurrentY = point.getY();
		resetNeighbors(point, color, false);
		return refreshSingles(color);
	}
	
	/**
	 * ���ؿ����������У��жϴ�ٵĸ������������ڷ���һ�����Ե���Ψһ������ΪǱ�ڵĴ�ٵ�
	 * @param color
	 * @return
	 * @throws GoException 
	 */
	private Point refreshSingles(boolean color) throws GoException {
		Point p = null;
		//�ٶԵз���ɫ�����������ж�
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
	 * ���ڽ�������Ⱥ���debug����
	 */
	@SuppressWarnings("unused")
	private void showDebugMessage() {
		System.out.println("����Ⱥ�飺\n" + this.BlackGroup.stream().map(StoneGroup::toString).collect(Collectors.joining("��", "{", "}")));
		System.out.println("����Ⱥ�飺\n" + this.WhiteGroup.stream().map(StoneGroup::toString).collect(Collectors.joining("��", "{", "}")) + "\n");
	}

	/**
	 * ˢ������״̬������Ⱥ��ɾ�����������������������
	 * @throws GoException ��Ⱥ�����С��0���쳣
	 */
	private void refreshBoard(boolean CurColor, boolean ifPrint) throws GoException {
		if (ifPrint) Node.printBoard(this.getState());
		//�ȶԵз�Ⱥ����в���
		refreshAllQi(!CurColor);
		Iterator<StoneGroup> iter = (CurColor ? this.WhiteGroup : this.BlackGroup).iterator();
		while (iter.hasNext()) {
			StoneGroup g = iter.next();
			//9.1�޸�
			//���ڱ����Ⱥ��ı�Ǻͻ��ն����ڷ���combineNewGroups������еģ�����������޹�
			//���������Ⱥ�����С��0
			if (g.getQi() < 0) {
				throw new GoException("ˢ������״̬ʱ�������쳣����Ⱥ�����С��0");
			}
			//���������Ⱥ�����Ϊ0��������������Ⱥ���ڵ����Ӷ��Ƴ������Ƴ�Ⱥ��
			else if (g.getQi() == 0) {
				Iterator<Stone> iter2 = g.getStonesIter();
				while (iter2.hasNext()) {
					Stone s = iter2.next();
					stones[s.getPoint().getX()][s.getPoint().getY()] = null;
				}
				iter.remove();
			}
		}
		
		//�ٶԵз���ɫ�����������ж�
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
		
		//��ˢ����Է���ɫ�Ժ���ˢ�¼�������ɫ
		refreshAllQi(CurColor);
		
		iter = (!CurColor ? this.WhiteGroup : this.BlackGroup).iterator();
		while (iter.hasNext()) {
			StoneGroup g = iter.next();
			//���������Ⱥ�����С��0
			if (g.getQi() < 0) {
				throw new GoException("ˢ������״̬ʱ�������쳣����Ⱥ�����С��0");
			}
			//���������Ⱥ�����Ϊ0��������������Ⱥ���ڵ����Ӷ��Ƴ������Ƴ�Ⱥ��
			else if (g.getQi() == 0) {
				if(ifPrint) System.out.println("�ҷ�Ⱥ������");
				Iterator<Stone> iter2 = g.getStonesIter();
				while (iter2.hasNext()) {
					Stone s = iter2.next();
					stones[s.getPoint().getX()][s.getPoint().getY()] = null;
				}
				iter.remove();
			}
		}
		//�ٶԵ����ӽ��в���
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[i][j] != null) {
					if (stones[i][j].getGroup() == null) {
						//9.1�޸�
						if (stones[i][j].getQi() == 0 && stones[i][j].getColor() == CurColor){
							stones[i][j] = null;
						}
					}
				}
			}
		}
	}
	
	/**
	 * ˢ��һ����ҵĶԷ���ҵ��������ӵ�����״̬�������������Ժ��ж��Ƿ��������Ӷ��������ı仯(RunTime Check)
	 * @param CurColor ��ǰ��ҵ���ɫ
	 * @throws GoException ˢ�µ�ǰ��ɫ�������ӵ���ʱ�׳����쳣
	 */
	private void refreshAllQi(boolean CurColor) throws GoException {
		//���õ�ǰ��ɫ������Ⱥ�����
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
		//���õ�ǰ��ɫ�����е�����
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
			//�������������ĵ�
			Point test = new Point(point.getX()-1, point.getY());
			//�������û�г��磬������Ƿ�������
			if (stones[point.getX()-1][point.getY()] != null) {
				//��������ӣ��Ͳ����Ƿ�ͬһ��ɫ
				if (stones[point.getX()-1][point.getY()].getColor() == color)
					return legalTestHelper(point, true);
				//�������ͬһ��ɫ���Ͳ����Ƿ����г���
				else if (testIfDead(point.getX()-1, point.getY(), color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		//������ӳ��磬��Ჶ��Point���������쳣���Ӷ������ڽ������κβ���
		catch (GoException e) {} //catch�����κ���
		try {
			//�������������ĵ�
			Point test = new Point(point.getX(), point.getY()-1);
			//�������û�г��磬������Ƿ�������
			if (stones[point.getX()][point.getY()-1] != null) {
				//��������ӣ��Ͳ����Ƿ�ͬһ��ɫ
				if (stones[point.getX()][point.getY()-1].getColor() == color)
					return legalTestHelper(point, true);
				//�������ͬһ��ɫ���Ͳ����Ƿ����г���
				else if (testIfDead(point.getX(), point.getY()-1, color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		catch (GoException e) {} //catch�����κ���
		try {
			//�������������ĵ�
			Point test = new Point(point.getX()+1, point.getY());
			//�������û�г��磬������Ƿ�������
			if (stones[point.getX()+1][point.getY()] != null) {
				//��������ӣ��Ͳ����Ƿ�ͬһ��ɫ
				if (stones[point.getX()+1][point.getY()].getColor() == color)
					return legalTestHelper(point, true);
				//�������ͬһ��ɫ���Ͳ����Ƿ����г���
				else if (testIfDead(point.getX()+1, point.getY(), color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		catch (GoException e) {} //catch�����κ���
		try {
			//�������������ĵ�
			Point test = new Point(point.getX(), point.getY()+1);
			//�������û�г��磬������Ƿ�������
			if (stones[point.getX()][point.getY()+1] != null) {
				//��������ӣ��Ͳ����Ƿ�ͬһ��ɫ
				if (stones[point.getX()][point.getY()+1].getColor() == color)
					return legalTestHelper(point, true);
				//�������ͬһ��ɫ���Ͳ����Ƿ����г���
				else if (testIfDead(point.getX(), point.getY()+1, color))
					return legalTestHelper(point, true);
			}
			else return legalTestHelper(point, true);
		}
		catch (GoException e) {} //catch�����κ���
		return legalTestHelper(point, false);
	}
	
	private boolean legalTestHelper(Point p, boolean value) {
		this.stones[p.getX()][p.getY()] = null;
		return value;
	}
	
	/**
	 * ���ڸ������Ժ��������ڲ���һ������ĵ�λ��Ҫô�ǵ����ӣ�Ҫô��Ⱥ���Ƿ��ڲ����������Ժ�����
	 * @param x ���Է����X����
	 * @param y ���Է����Y����
	 * @param �Ѿ����Թ���Ⱥ��
	 * @return �Ƿ�����
	 * @throws GoException 
	 */
	private boolean testIfDead(int x, int y, boolean color) throws GoException {
		if (stones[x][y].getGroup() == null) {
			if (checkQiNum(stones[x][y].getPoint()) == 0) {
				return true;
			}
		}
		//9.1�޸�
		//���ĳ�㴦�Ƿ��г�Ⱥ����ӵ������ֻ�����Ⱥ�鼴�ɣ������������Ⱥ��
		else {
			StoneGroup g = stones[x][y].getGroup();
			Iterator<Stone> iter = g.getStonesIter();
			while (iter.hasNext()) {
				//ֻҪ��һ��Ⱥ�������������ʹ���Ⱥ��û���Ե�
				if (checkQiNum(iter.next().getPoint()) != 0)
					return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * ���ͬʱ�����ڽ������ӵ�״̬������ǵз����ӻ���Ⱥ�������������Ǽ��������ӻ���Ⱥ����������
	 * @param point ������λ��
	 * @param color ������ɫ
	 * @throws GoException ���Ⱥ��ʱ��ɫ��һ�µ��쳣
	 */
	private void resetNeighbors(Point point,boolean color, boolean ifPrint) throws GoException {
		//if (color) System.out.println("!!!!");
		LinkedList<StoneGroup> tempG = new LinkedList<>();
		LinkedList<Stone> tempS = new LinkedList<>();
		Stone s;
		//������û�г���
		if (point.getX() > 0) {
			//������������
			s = stones[point.getX()-1][point.getY()];
			if (s != null) {
				//��������Ⱥ��
				if (s.getGroup() != null) {
					//������Ⱥ����ɫһ��
					//9.1�޸ģ�
					//1.Ϊ�˱�������ظ���Ⱥ�飬��Ϊʹ�������������
					//2.����ʹ����checkQiNum�ļ�飬�������������Ⱥ������ӵ���ʵ������˲��ٽ�����ֻ�����Ⱥ��
					if (s.getGroup().getColor() == color) {
						//���û���ظ�����ͬһȺ�飬�����֮
						if (!tempG.contains(s.getGroup()))
							tempG.add(s.getGroup());
					}
				}
				//�����Ⱥ��
				else {
					//��������ɫһ��
					if (s.getColor() == color) {
						tempS.add(s);
					}
				}
			}
		}
		
		//�ϲ�
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
		
		//�Ҳ�
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
		
		//�²�
		if (point.getY() < 8) {
			s = stones[point.getX()][point.getY()+1];
			if (s != null) {
				if (s.getGroup() != null) {
					if (s.getGroup().getColor() == color) {
						if (!tempG.contains(s.getGroup()))
							tempG.add(s.getGroup());
					}//������ɫ��ͬ
				}//����Ⱥ��ǿ�
				else {
					if (s.getColor() == color)
						tempS.add(s);
				}//����Ⱥ��Ϊ��
			} //����λ�÷ǿ�
		}
			
		//listתarray
		Stone[] ArrayS = new Stone[tempS.size()];
		StoneGroup[] ArrayG = new StoneGroup[tempG.size()];
		ArrayG = tempG.toArray(ArrayG);
		ArrayS = tempS.toArray(ArrayS);
		//��������ɫһ�µ�Ⱥ����ߵ����ӽ������
		combineNewGroup(stones[point.getX()][point.getY()], ArrayS, ArrayG, color, false);
	}
	
	/**
	 * ���ڽ������ӻ���Ⱥ����ϳ�Ϊ��Ⱥ��
	 * @param stones ����ϵľɵ���������
	 * @param groups ����ϵľ�Ⱥ������
	 * @param color ����ϵ���ɫ
	 * @throws GoException Ⱥ�����ʱ���쳣
	 */
	private void combineNewGroup(Stone Curstone, Stone[] stones, StoneGroup[] groups, boolean color, boolean ifPrint) throws GoException {
		//9.1�޸�
		//�������ӵ�������Χû�м������ӻ���Ⱥ�飨stones��groups����Ԫ�أ�����ֱ������
		if (stones.length == 0 && groups.length == 0) {
			return;
		}
		//��Ⱥ��
		StoneGroup newG = new StoneGroup(color);
		newG.addStone(Curstone);
		newG.setQi(0);
		Curstone.setGroup(newG);
		//9.1�޸�
		//1.����Ⱥ��͵������������ã���������Ⱥ��ʱ���ٿ�������������
		//�����ڷ�����ͷ�Ѿ�����Χû��Ⱥ�����������˼�飬��˲�����Ҫ����Ƿ�û��Ⱥ��͵����ӵ����
		//����������ӵ���Ⱥ����
		try {
				for (Stone s : stones) {	
					if (s != null) {
						s.setGroup(newG);
						newG.addStone(s);
					}
				}
		//����Ⱥ����ӵ���Ⱥ����
			if (groups.length > 0) {
				for (StoneGroup g : groups) {														
					if (g != null) {
						newG.addStone(g);
						Iterator<Stone> iter = g.getStonesIter();	
						//��������Ⱥ���е����ӵ�Ⱥ�鶼����Ϊ��Ⱥ��
						while (iter.hasNext()) {															
							iter.next().setGroup(newG);
						}
						//���վ�Ⱥ��
						g.replaced();
					}
				}
			}
		//����Ⱥ�鼯�ϣ�����ǵľ�Ⱥ�����
		Iterator<StoneGroup> iter2 = (color ? this.BlackGroup : this.WhiteGroup).iterator();
		while (iter2.hasNext()) {
			if (iter2.next().getIfReplaced())
				iter2.remove();
		}
		//�����Ⱥ��
		(color ? this.BlackGroup : this.WhiteGroup).add(newG);
	}
		catch(Exception e) {
			GoException E = new GoException("���Ⱥ��ʱ�������쳣�� ԭʼԭ��" + e.getMessage());
			E.initCause(e);
			throw E;
		}
	}
	
	/**
	 * ���ڽ������ϵ�ǰ���������������̴�ӡ���ļ���
	 * @throws FileNotFoundException ָ���ļ�û���ҵ����쳣
	 */
	private void outputStonesToFile(String fname) throws FileNotFoundException {
		this.writer = new PrintWriter(new FileOutputStream(fname), true);
		writer.println("����״̬\n");
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[j][i] == null)
					writer.print(" ");
				else if (stones[j][i].getColor())
					writer.print("X");
				else if (!stones[j][i].getColor())
					writer.print("O");
				if (j != 8)
					writer.print("����");
				else writer.println();
			}
			if (i != 8)
				writer.println("|  |  |  |  |  |  |  |  |");
		}
	}
	
	/**
	 * ���ڵõ���ǰ���������ⲿ���÷���
	 * @return ��ǰ�����
	 * @throws GoException ��������쳣
	 */
	public Point getCurrentPoint() throws GoException {
		return new Point(CurrentX, CurrentY);
	}
	
	/**
	 * @return ��ǰ���̵�״̬
	 */
	public Stone[][] getState() {
		return this.stones;
	}
	
	/**
	 * �ڿ�¡�����е��á������е�Ⱥ���У��ҵ���Ӧ��λ�õ�����
	 * @param groups ���е�Ⱥ��
	 * @param point ���λ��
	 * @param color ���Ӷ�Ӧ����ɫ
	 * @return Ⱥ���ж�Ӧ�������
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
		throw new GoException("GoBoard���¡ʱ�����쳣���ڿ�¡��Ⱥ��ʱ����Ⱥ�����Ҳ�����Ӧλ�õĵ㣡");
	}
	
	/**
	 * �����ⲿ��ai��node���ã����ýڵ��board����curPos����������Ϸ�����findChildNode�е���
	 * @param limit �ڵ���������
	 * @param color �ڵ���ɫ
	 * @return �Ϸ�λ��
	 * @throws CloneNotSupportedException 
	 */
	public Point[] getLegalPos(final int limit, final int waiting, boolean color) throws GoException, CloneNotSupportedException {
		if (getEmptySiteNumOnBoard(this) < 30)
			return pointShortageHelper(limit, color);
		//�޸�bug����������ʱ�����Ȱ���limit��������������limit�仯����������ܲ�δ����limit�������
		ArrayList<Point> pos = new ArrayList<>();
		//ʹ��waiting�ɱ仯������̶���waitingֵ
		int i, Waiting = waiting;
		for (i = 0; i < Waiting; i++) {
			//Ϊ�˼���һ·���ĳ��壬���ʹ�þ�̬����һ����������һ·������
			Point point = UtilityEstimate.RetryWhenMeetLineOne(new Point(rand.nextInt(9), rand.nextInt(9)));
			//ÿһ��Ԥѡ�������������ֵ����
			int patience = this.SearchPatience;
			
			//�����Ƿ��ܹ��Ϸ������Ϸ�ʱ����һ������ֵ���õ��λ��
			while(!testIfLegal(point, color, 0)) {
				//���ĺľ�ʱ
				if (patience == 0) {
					//����Ϸ�λ����������ʱ
					if (getEmptySiteNumOnBoard(this) < limit)
						throw new GoException("�����ϺϷ�λ�������Ѿ�С���ӽڵ�������!");
					//������������������ർ��waitingΪ0ʱ
					//����Ϸ�λ�û�����ʱ�������������Ҽ��ٺϷ�λ������
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
			//û����������ʱ�������еĵ㽫����null
			point.setScore(UtilityEstimate.PosEvaluate(this, point, new Point(CurrentX, CurrentY), color));
			pos.add(point);
		}
		//����õ��ĺϷ�����������
		if (pos.size() < limit)
			throw new GoException("����ʧ�ܣ��õ��ĺϷ�����������NodeLimit!");
		
		//����Ϸ����������࣬Ӧ����������ȡ����ֵ�ϴ��һЩ�㷵��
		if(pos.size() > limit)
			pos.sort(new PointScoreComparator());
		
		Point[] realPos = new Point[pos.size()];
		
		//��������ֵ�ϴ�ĵ�
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
			throw new GoException("������û�������");
		}
	}
	
	/**
	 * ��������Ͽյĵ����Ŀ
	 * @param board ����
	 * @return ��λ����Ŀ
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
	 * ai���á�̽��һ��λ���Ƿ�Ϊ���������ۣ��������ģ��ʱ�����ܽ�������
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
	 * �����ж�һ��λ���Ƿ�Ϊ��
	 * @param board ��ǰ����
	 * @param point ���жϵĵ�
	 * @param color �жϵ���ɫ
	 * @return �Ƿ�Ϊ��
	 */
	public boolean zedCheck(Point point, boolean color) throws CloneNotSupportedException, GoException {
		GoBoard newB = this.clone();
		//���������addStone�������÷�������ѳԵ���Ψһһ�����ӷ��س���
		Point p = newB.addStone(point, color);
		//����ⲽ��û�гԵ��ӣ������ǳԵ���һ�����ϵ����ӣ���������������
		if (p == null) {
			return false;
		}
		else {
			//������Ե��ĵ㲻�Ϸ������ܳɽ�
			if (!newB.testIfLegal(p, !color, 0)) {
				return false;
			}
			Point P = newB.addStone(p, !color);
			//������߳Ե��ĵ㵫��û�г��ӣ����ܳɽ�
			if (P == null) {
				return false;
			}
			//������߳Ե��ĵ�Ե������Ӻ�֮ǰ�Ե������ӵĵ�λ��һ������ɽ�
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
					//�õ�Ϊ�Ϸ�������Ҫ��
					//1.����Ϸ�
					//2.��������
					//3.���ܴ�� v
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
		
		//group������¡�����½���list����ͨ������group�����¡����getClone�������
		for (StoneGroup g : this.BlackGroup) {
			cloneBoard.BlackGroup.add(g.getClone());
		}
		for (StoneGroup g : this.WhiteGroup) {
			cloneBoard.WhiteGroup.add(g.getClone());
		}
		
		//9.8�޸�bug��board�����ϵ�stoneӦ����Ⱥ���ڲ���stone����ϵ
		//���̵Ŀ�¡��ͨ��������ͬ�����빹�����õ����¡��ͬʱ��Ҫ�趨���ӵ�Ⱥ��
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) { 
				try {
				if (this.stones[j][i] != null) {
					Stone s = this.stones[j][i];
					//�������¡�������ϣ�ĳ����������Ⱥ�飬��Ӧ�ô��Ѿ���¡��Ⱥ�����ҳ���
					if (s.getGroup() != null) {
						cloneBoard.stones[j][i] = 
								findMatchStoneFromGroups(
								(s.getColor() ? cloneBoard.BlackGroup : cloneBoard.WhiteGroup), 
								s.getPoint(), 
								s.getColor());
					}
					//�������¡�������ϣ�ĳ��������û��Ⱥ�飬��ֱ�ӵ��ù���������¡���̸�ֵ����
					else cloneBoard.stones[j][i] = new Stone(s.getColor(), s.getPoint(), 0);
				}
				}
				catch(GoException e) {
					System.err.println("���¡ʱ�����ù���������findMatchStoneFromGroups����ʱ�����쳣��");
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



































