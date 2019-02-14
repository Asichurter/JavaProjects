package ai;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import game.GoBoard;
import game.GoException;
import game.Point;
import game.Stone;
import newPattern.Pattern;

/**
 * ʹ�����ؿ�����+UCB�㷨����������ai
 */
public class MTCS {
	
	/**
	 * �ͻ���������ɫ
	 */
	private boolean Color;
	
	/**
	 * ���������ĸ��ڵ�
	 */
	private MTCS_Node InitNode;
	
	/**
	 * ÿ�ν���Ҷ�ڵ����ģ��ʱ������ģ��Ĵ���
	 */
	public static int SimulationTimes = 5;
	
	/**
	 * ���ڵ�����ؿ����������ѭ������
	 */
	private final int MTCS_SearchLimits = 50;
	
	/**
	 * ����һ�����ؿ�����������ai
	 * @param color ai������ɫ
	 */
	public MTCS(boolean color) {
		this.Color = color;
	}
	
	/**
	 * ���ڼ���Ƿ���ģʽƥ�䣬ͬʱ�����ƥ���ģʽ����ֱ������
	 * @param board ��ǰ����
	 * @param color ��ǰ������ɫ
	 * @return �Ƿ�ģʽ����ɹ�
	 */
	private boolean checkPatterns(GoBoard board, boolean color) throws GoException, FileNotFoundException {
		Stone[][] state = board.getState();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] != null) {
					int index = 1;
					for (Pattern pattern : GoBoard.Patterns) {
						int p = pattern.checkIfMatch(state, state[j][i].getPoint(), state[j][i].getColor());
						if (p >= 0) {
							System.out.println("ģʽƥ��ɹ���ģʽ��ţ�" + index + " ģʽ�ַ�����" + pattern.getPatternString() + " ��ʽ������" + p);
							Point nextMove = pattern.getPatternedNextMove(p, state[j][i].getPoint(), state[j][i].getColor(), color);
							if (nextMove != null) {
								if (board.addStone(nextMove, color, null))
									return true;
								else throw new GoException("��ʽ�еĵ�����Ƿ�����鶨ʽָ���Ƿ�Ϸ���");
							}
							else return false;
						}
						index++;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * �ڿ�ʼ����ǰ����ʼ�����ڵ�
	 * @param board ��ǰ����
	 */
	public void initialize(GoBoard board) throws GoException, CloneNotSupportedException {
		//�������ڵ�
		this.InitNode = new MTCS_Node(board, Color, Color, true);
		//��ʼ�����ڵ�
		this.InitNode.findChildNodes();
	}
	
	/**
	 * �����ڲ���Ѱ��ai����ʱ��ÿһ��ѭ���е��õ���������������
	 */
	private void searchFromRoot() throws GoException, CloneNotSupportedException {
		this.InitNode.MTCS_Search(0);
	}
	
	/**
	 * ��panel�ⲿ���õģ����ڴ�ai�л����һ������ķ���
	 * @param board ��ǰ����
	 * @return �����Ժ������
	 * @throws FileNotFoundException 
	 */
	public GoBoard findNextStep(GoBoard board) throws GoException, CloneNotSupportedException, FileNotFoundException {
		if (checkPatterns(board, this.Color))
			return board;
		//�ȶԸ��ڵ���г�ʼ��
		this.initialize(board);
		for (int i = 0; i < MTCS_SearchLimits; i++) {
			//System.out.println("!");
			//���ø��ڵ����������
			this.searchFromRoot();
			//�ݹ�ˢ�����нڵ��UCB
			//��������ʼ�ڵ�ĸ��ڵ���ʴ�������Ϊ1
			this.InitNode.refreshAllUCB(1);
		}
		//����UCB����һ����
		return this.InitNode.getNextStep();
	}
	
	public static int externEvaluate(GoBoard board, boolean color){
		Stone[][] state = board.getState();
		int total = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null) {
					//����ÿյ���ĳ��ɫ���ۣ�Ҳ�Ὣ��ӵ�����ɫ��������
					if (board.yanDetect(j, i, color))
						total++;
				}
				else if (state[j][i].getColor() == color)
					total++;
			}
		}
		return total;
	}
}

/**
 * �������ؿ����������Ľڵ���
 */
class MTCS_Node{
	/**
	 * �ڵ����ɫ
	 */
	private boolean Color;
	
	/**
	 * ���ڵ����ɫ
	 */
	private final boolean RootColor;
	
	/**
	 * ��ǰ�ڵ��Ӧ������
	 */
	private GoBoard Board;
	
	/**
	 * ��ǰ�ڵ��Ǽ���ڵ㻹�Ǽ�С�ڵ�
	 */
	private boolean Max_Min;
	
	/**
	 * ���ڴ����ӽڵ������
	 */
	private LinkedList<MTCS_Node> ChildNodes;
	
	/**
	 * ��ǰ�ڵ����ʷ�÷�
	 */
	private double AllWinningScore;
	
	/**
	 * ��ǰ�ڵ�ķ��ʴ���
	 */
	private int AllVisitedTimes;
	
	/**
	 * ��ǰ�ڵ��UCB�����Ͻ�ֵ
	 */
	private double UCB;
	
	/**
	 * �ڵ���������
	 */
	private final int NodeLimit = 5;
	
	/**
	 * ��ѡ�Ľڵ���������
	 */
	private final int WaitingLimit = 10;
	
	/**
	 * ����UCBֵʱ��Exploration�ı���ϵ�����������Եĺ���ֵ��
	 */
	private static double UCB_Ratio = 52;
	
	private static final Random rand = new Random();
	
	public MTCS_Node(GoBoard board, boolean color, boolean rootc, boolean max_min) {
		this.Board = board;
		this.Max_Min = max_min;
		this.RootColor = rootc;
		this.Color = color;
		this.ChildNodes = new LinkedList<>();
		this.AllWinningScore = 0;
		this.AllVisitedTimes = 0;
		this.UCB = 0;
	}
	
	/**
	 *��չ�ڵ㣬����һϵ���ӽڵ�
	 * @throws GoException ������������ӵ�ʱ�����쳣
	 * @throws CloneNotSupportedException GoBoard�Ŀ�¡�쳣
	 */
	public void findChildNodes() throws GoException, CloneNotSupportedException {
		//�ȵõ���������ɸѡ���ӽڵ��ѡ��
		Point[] pos = this.Board.getLegalPos(NodeLimit, WaitingLimit, Color);
		for (Point p : pos) {
			GoBoard newBoard = this.Board.clone();
			//���ӽڵ�������
			newBoard.addStone(p, Color, false);
			//���ӽڵ���ӵ��ڵ���ӽڵ�������
			this.ChildNodes.add(new MTCS_Node(newBoard, !Color, Color, !Max_Min));
		}
	}
	
	/**
	 * ���ڵ���ⲿ���÷��������ݵ�ǰ�ӽڵ��UCBֵ���õ�һ�����UCB���߷�
	 * @return ���UCB���߷�
	 */
	public GoBoard getNextStep() {
		return this.ChildNodes.stream().max(MTCS_UCB_Comparator()).get().Board;
	}
	
	/**
	 * ִ��һ�����������ؿ�����������������������searchFromRoot�е��õģ����ݹ����������չ���ؿ����������ݸ��½ڵ�ֵ�ķ���
	 * @return ���ڵ�������Ϸ��ص�Ч��ֵ
	 */
	public double MTCS_Search(int depth) throws GoException, CloneNotSupportedException {
		//�����ǰ�ڵ���һ��Ҷ�ڵ㣬����û�б���չ�Ľڵ㣬�Ǿ��ȶ������һ��ģ�⣬ͬʱ��չ�ӽڵ�
		if (this.ChildNodes.isEmpty()) {
			double temp = 0;
			//������չ�Ľڵ��������ģ�⣬ȡƽ��ֵ
			for (int i = 1; i <= MTCS.SimulationTimes; i++) {
				temp += simulateByRandom(this.Board, this.RootColor);
			}
			//��ʼʱ��ģ��5�Σ����ҷ��ظ�ֵ
			this.AllVisitedTimes += MTCS.SimulationTimes;
			this.AllWinningScore += temp;
			//��չ�ӽڵ�
			try {
				this.findChildNodes();
			}
			catch(GoException e) {
				if (e.getMessage().equals("������û�������")) {
					if (depth == 0)
						throw new GoException("������û�������");
					else return temp/MTCS.SimulationTimes;
				}
				else {
					System.out.println("������Ϣ�����ؿ������У������� " + e.getMessage());
					throw new GoException("�����ؿ�������ģ�������У�����������֮����쳣��");
				}
			}
			//���ڻ���ʱ�����ڵ�ֻ������һ��ģ������������Ҫȡƽ��ֵ
			return this.AllWinningScore / MTCS.SimulationTimes;
		}
		else {
			//������չû�б���չ���Ľڵ�
			MTCS_Node nextNode = findAnyUnexpandedNode();
			if (nextNode != null) {
				double temp = nextNode.MTCS_Search(depth+1);
				this.AllVisitedTimes++;
				this.AllWinningScore += temp;
				//this.UCB = this.AllWinningScore/this.AllVisitedTimes + 0;  //����һ���ִ���д...
				return temp;
			}
			//�����ڵ���ȫ��չ���ˣ��ٸ��� MaxMin����ѡ����һ���ӽڵ�
			else {
				double temp;
				if (Max_Min) {
					temp = this.ChildNodes.stream().max(MTCS_UCB_Comparator()).get().MTCS_Search(depth+1);
				}
				else {
					temp = this.ChildNodes.stream().min(MTCS_UCB_Comparator()).get().MTCS_Search(depth+1);
				}
				this.AllVisitedTimes++;
				this.AllWinningScore += temp;
				//this.UCB = this.AllWinningScore/this.AllVisitedTimes + 0;  //����һ���ִ���д...
				return temp;
			}
		}
	}
	
	/**
	 * ���ڻ�öԽڵ��UCBֵ���бȽϵıȽ����ķ�������min_max�����е���
	 * @return �ڵ��UCB�Ƚ���
	 */
	private Comparator<MTCS_Node> MTCS_UCB_Comparator(){
		return (m1, m2)->{
			if (m1.UCB > m2.UCB)
				return 1;
			else if (m1.UCB < m2.UCB)
				return -1;
			else return 0;
		};
	}
	
	/**
	 * ���ݸ��ڵ�ķ��ʴ����빫ʽ UCB = aver(score) + ci * (lnN/n)^0.5���㵱ǰ�ڵ��UCBֵ
	 * @param fatherTimes ���ڵ�ķ��ʴ���
	 */
	public void refreshAllUCB(int fatherTimes) {
		if (fatherTimes == 0) System.out.println("!!!");
		//����ڵ㻹û�б���չ��������UCBӦ����Ϊ0
		if (this.AllVisitedTimes == 0)
			this.UCB = 0;
		else {
			this.UCB = this.AllWinningScore/this.AllVisitedTimes + UCB_Ratio*Math.sqrt(Math.log(fatherTimes)/this.AllVisitedTimes);
			for (MTCS_Node node: this.ChildNodes) {
				//�ݹ�ˢ�������ӽڵ��UCBֵ
				node.refreshAllUCB(this.AllVisitedTimes);
			}
		}
	}
	
	/**
	 * �ڵ�ǰ���ӽڵ��У�Ѱ���Ƿ���û�б���չ���Ľڵ㣬��������չû�б���չ���Ľڵ�
	 * @return ��һ���ҵ���û�б���չ�Ľڵ㣬���û���ҵ����򷵻�null
	 */
	private MTCS_Node findAnyUnexpandedNode() {
		for (MTCS_Node node: this.ChildNodes) {
			if (node.AllVisitedTimes == 0) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * ������Ҷ�ڵ���õģ����Ԥ����������������ؿ��巽��������
	 * @param board ��ǰ����
	 * @param color ��ǰ������ɫ
	 * @return ����Ч��ֵ
	 * @throws GoException 
	 */
	private double simulateByRandom(GoBoard board, boolean color) throws CloneNotSupportedException, GoException {
		//��ǰ���巽
		boolean curColor = color;
		//��¡��������
		GoBoard state = board.clone();
		//��ȡ��ǰ��ɫ�����кϷ������
		LinkedList<Point> points = getAllEmptyPoints(board, curColor); 
		while (!points.isEmpty()) {
			//�������еĺϷ���������������塣state�����ô��ݸı�
			 simulatePlay(state, curColor, points);
			 //���巽����
			 curColor = !curColor;
			 //�����µľ����������ɫ�����»�ȡ�Ϸ������
			 points = getAllEmptyPoints(state, curColor);
		}
		//Node.printBoard(state.getState());
		//���Ϸ������Ϊ�գ����������ʱ���������̵�״̬����ֵ
		return evaluate(state, color);
	}
	
	/**
	 * ��������������ɺ���������״��
	 * @param board ������ɺ������
	 * @param color ����������ɫ
	 * @return ����ֵ
	 */
	private double evaluate(GoBoard board, boolean color){
		Stone[][] state = board.getState();
		int total = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null) {
					//����ÿյ���ĳ��ɫ���ۣ�Ҳ�Ὣ��ӵ�����ɫ��������
					if (board.yanDetect(j, i, color))
						total++;
				}
				else if (state[j][i].getColor() == color)
					total++;
			}
		}
		return total;
	}
	
	/**
	 * �ӺϷ���������У����ѡȡһ������
	 * @param board ��ǰ����
	 * @param color ������ɫ
	 * @param points ���еĺϷ������
	 * @throws GoException �������ʱ�����쳣
	 */
	private void simulatePlay(GoBoard board, boolean color, LinkedList<Point> points) throws GoException {
		//�ںϷ�����������ѡȡһ�������������壬�������ؿ���ģ��Ĺؼ�
		Point point = points.get(rand.nextInt(points.size()));
		//��ģ���������ӵ�������
		board.addStone(point, color, false);
	}
	
	/**
	 * ��õ�ǰ��Ӧ����ɫ�����кϷ�����㣺�Ϸ��Ҳ�������
	 * @param board ��ǰ����
	 * @param color ���Ե���ɫ
	 * @return ���кϷ������
	 */
	private LinkedList<Point> getAllEmptyPoints(GoBoard board, boolean color) throws GoException, CloneNotSupportedException{
		LinkedList<Point> points = new LinkedList<>();
		Stone[][] state = board.getState();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null) {
					Point newP = new Point(j, i);
					//�õ�Ϊ�Ϸ�������Ҫ��
					//1.����Ϸ�
					//2.��������
					//3.���ܴ�� v
					if (board.testIfLegal(newP, color, 0) 
							&& !board.yanDetect(j, i, color) 
							&& !board.zedCheck(newP, color))
						points.add(newP);
				}
			}
		}
		return points;
	}
}




















