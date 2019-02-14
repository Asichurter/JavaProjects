package ai;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
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
 * 使用蒙特卡洛树+UCB算法进行搜索的ai
 */
public class MTCS {
	
	/**
	 * 客户端行棋颜色
	 */
	private boolean Color;
	
	/**
	 * 进行搜索的根节点
	 */
	private MTCS_Node InitNode;
	
	/**
	 * 每次进行叶节点进行模拟时，进行模拟的次数
	 */
	public static int SimulationTimes = 5;
	
	/**
	 * 根节点的蒙特卡洛搜索最大循环次数
	 */
	private final int MTCS_SearchLimits = 50;
	
	/**
	 * 创建一个蒙特卡洛树搜索的ai
	 * @param color ai行棋颜色
	 */
	public MTCS(boolean color) {
		this.Color = color;
	}
	
	/**
	 * 用于检测是否有模式匹配，同时如果有匹配的模式，则直接走棋
	 * @param board 当前棋盘
	 * @param color 当前走棋颜色
	 * @return 是否按模式走棋成功
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
							System.out.println("模式匹配成功，模式编号：" + index + " 模式字符串：" + pattern.getPatternString() + " 定式步数：" + p);
							Point nextMove = pattern.getPatternedNextMove(p, state[j][i].getPoint(), state[j][i].getColor(), color);
							if (nextMove != null) {
								if (board.addStone(nextMove, color, null))
									return true;
								else throw new GoException("定式中的点走棋非法！检查定式指定是否合法！");
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
	 * 在开始搜索前，初始化根节点
	 * @param board 当前棋盘
	 */
	public void initialize(GoBoard board) throws GoException, CloneNotSupportedException {
		//建立根节点
		this.InitNode = new MTCS_Node(board, Color, Color, true);
		//初始化根节点
		this.InitNode.findChildNodes();
	}
	
	/**
	 * 用于内部在寻找ai走棋时，每一轮循环中调用的总启动搜索方法
	 */
	private void searchFromRoot() throws GoException, CloneNotSupportedException {
		this.InitNode.MTCS_Search(0);
	}
	
	/**
	 * 从panel外部调用的，用于从ai中获得下一步走棋的方法
	 * @param board 当前棋盘
	 * @return 走棋以后的棋盘
	 * @throws FileNotFoundException 
	 */
	public GoBoard findNextStep(GoBoard board) throws GoException, CloneNotSupportedException, FileNotFoundException {
		if (checkPatterns(board, this.Color))
			return board;
		//先对根节点进行初始化
		this.initialize(board);
		for (int i = 0; i < MTCS_SearchLimits; i++) {
			//System.out.println("!");
			//调用根节点的启动方法
			this.searchFromRoot();
			//递归刷新所有节点的UCB
			//修正：初始节点的父节点访问次数设置为1
			this.InitNode.refreshAllUCB(1);
		}
		//返回UCB最大的一步棋
		return this.InitNode.getNextStep();
	}
	
	public static int externEvaluate(GoBoard board, boolean color){
		Stone[][] state = board.getState();
		int total = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null) {
					//如果该空点是某颜色的眼，也会将其加到该颜色总数量上
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
 * 进行蒙特卡洛树搜索的节点类
 */
class MTCS_Node{
	/**
	 * 节点的颜色
	 */
	private boolean Color;
	
	/**
	 * 根节点的颜色
	 */
	private final boolean RootColor;
	
	/**
	 * 当前节点对应的棋盘
	 */
	private GoBoard Board;
	
	/**
	 * 当前节点是极大节点还是极小节点
	 */
	private boolean Max_Min;
	
	/**
	 * 用于储存子节点的链表
	 */
	private LinkedList<MTCS_Node> ChildNodes;
	
	/**
	 * 当前节点的历史得分
	 */
	private double AllWinningScore;
	
	/**
	 * 当前节点的访问次数
	 */
	private int AllVisitedTimes;
	
	/**
	 * 当前节点的UCB信心上界值
	 */
	private double UCB;
	
	/**
	 * 节点数量限制
	 */
	private final int NodeLimit = 5;
	
	/**
	 * 待选的节点数量限制
	 */
	private final int WaitingLimit = 10;
	
	/**
	 * 计算UCB值时，Exploration的比例系数（经过调试的合适值）
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
	 *拓展节点，产生一系列子节点
	 * @throws GoException 向棋盘添加棋子的时候发生异常
	 * @throws CloneNotSupportedException GoBoard的克隆异常
	 */
	public void findChildNodes() throws GoException, CloneNotSupportedException {
		//先得到经过启发筛选的子节点待选点
		Point[] pos = this.Board.getLegalPos(NodeLimit, WaitingLimit, Color);
		for (Point p : pos) {
			GoBoard newBoard = this.Board.clone();
			//在子节点上走棋
			newBoard.addStone(p, Color, false);
			//将子节点添加到节点的子节点链表中
			this.ChildNodes.add(new MTCS_Node(newBoard, !Color, Color, !Max_Min));
		}
	}
	
	/**
	 * 根节点的外部调用方法，根据当前子节点的UCB值，得到一个最大UCB的走法
	 * @return 最大UCB的走法
	 */
	public GoBoard getNextStep() {
		return this.ChildNodes.stream().max(MTCS_UCB_Comparator()).get().Board;
	}
	
	/**
	 * 执行一次完整的蒙特卡洛搜索。用于在主调方法searchFromRoot中调用的，层层递归调用用于扩展蒙特卡洛树，回溯更新节点值的方法
	 * @return 本节点从下向上返回的效用值
	 */
	public double MTCS_Search(int depth) throws GoException, CloneNotSupportedException {
		//如果当前节点是一个叶节点，即还没有被扩展的节点，那就先对其进行一次模拟，同时扩展子节点
		if (this.ChildNodes.isEmpty()) {
			double temp = 0;
			//对新扩展的节点进行数次模拟，取平均值
			for (int i = 1; i <= MTCS.SimulationTimes; i++) {
				temp += simulateByRandom(this.Board, this.RootColor);
			}
			//初始时，模拟5次，并且返回改值
			this.AllVisitedTimes += MTCS.SimulationTimes;
			this.AllWinningScore += temp;
			//扩展子节点
			try {
				this.findChildNodes();
			}
			catch(GoException e) {
				if (e.getMessage().equals("棋盘上没有走棋点")) {
					if (depth == 0)
						throw new GoException("棋盘上没有走棋点");
					else return temp/MTCS.SimulationTimes;
				}
				else {
					System.out.println("错误信息：蒙特卡洛树中，出现了 " + e.getMessage());
					throw new GoException("在蒙特卡洛树的模拟行棋中，出现了意料之外的异常！");
				}
			}
			//由于回溯时，父节点只会增加一次模拟次数，因此需要取平均值
			return this.AllWinningScore / MTCS.SimulationTimes;
		}
		else {
			//优先扩展没有被扩展过的节点
			MTCS_Node nextNode = findAnyUnexpandedNode();
			if (nextNode != null) {
				double temp = nextNode.MTCS_Search(depth+1);
				this.AllVisitedTimes++;
				this.AllWinningScore += temp;
				//this.UCB = this.AllWinningScore/this.AllVisitedTimes + 0;  //后面一部分待编写...
				return temp;
			}
			//若本节点完全扩展完了，再根据 MaxMin决定选择哪一个子节点
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
				//this.UCB = this.AllWinningScore/this.AllVisitedTimes + 0;  //后面一部分待编写...
				return temp;
			}
		}
	}
	
	/**
	 * 用于获得对节点的UCB值进行比较的比较器的方法，在min_max过程中调用
	 * @return 节点的UCB比较器
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
	 * 根据父节点的访问次数与公式 UCB = aver(score) + ci * (lnN/n)^0.5计算当前节点的UCB值
	 * @param fatherTimes 父节点的访问次数
	 */
	public void refreshAllUCB(int fatherTimes) {
		if (fatherTimes == 0) System.out.println("!!!");
		//如果节点还没有被扩展过，则其UCB应该置为0
		if (this.AllVisitedTimes == 0)
			this.UCB = 0;
		else {
			this.UCB = this.AllWinningScore/this.AllVisitedTimes + UCB_Ratio*Math.sqrt(Math.log(fatherTimes)/this.AllVisitedTimes);
			for (MTCS_Node node: this.ChildNodes) {
				//递归刷新所有子节点的UCB值
				node.refreshAllUCB(this.AllVisitedTimes);
			}
		}
	}
	
	/**
	 * 在当前的子节点中，寻找是否有没有被扩展过的节点，并优先扩展没有被扩展过的节点
	 * @return 第一个找到的没有被扩展的节点，如果没有找到，则返回null
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
	 * 用于在叶节点调用的，随机预测棋局走向。这是蒙特卡洛方法的体现
	 * @param board 当前棋盘
	 * @param color 当前行棋颜色
	 * @return 估计效用值
	 * @throws GoException 
	 */
	private double simulateByRandom(GoBoard board, boolean color) throws CloneNotSupportedException, GoException {
		//当前走棋方
		boolean curColor = color;
		//克隆走棋棋盘
		GoBoard state = board.clone();
		//获取当前颜色的所有合法行棋点
		LinkedList<Point> points = getAllEmptyPoints(board, curColor); 
		while (!points.isEmpty()) {
			//根据已有的合法行棋点进行随机走棋。state按引用传递改变
			 simulatePlay(state, curColor, points);
			 //行棋方交换
			 curColor = !curColor;
			 //根据新的局面和走棋颜色，重新获取合法走棋点
			 points = getAllEmptyPoints(state, curColor);
		}
		//Node.printBoard(state.getState());
		//当合法行棋点为空，即行棋完毕时，返回棋盘的状态估计值
		return evaluate(state, color);
	}
	
	/**
	 * 当棋局随机对弈完成后，评估棋盘状况
	 * @param board 对弈完成后的棋盘
	 * @param color 待评估的颜色
	 * @return 评估值
	 */
	private double evaluate(GoBoard board, boolean color){
		Stone[][] state = board.getState();
		int total = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null) {
					//如果该空点是某颜色的眼，也会将其加到该颜色总数量上
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
	 * 从合法的行棋点中，随机选取一步行棋
	 * @param board 当前棋盘
	 * @param color 行棋颜色
	 * @param points 所有的合法行棋点
	 * @throws GoException 添加棋子时发生异常
	 */
	private void simulatePlay(GoBoard board, boolean color, LinkedList<Point> points) throws GoException {
		//在合法行棋点内随机选取一个行棋点进行走棋，这是蒙特卡洛模拟的关键
		Point point = points.get(rand.nextInt(points.size()));
		//将模拟行棋点添加到棋盘上
		board.addStone(point, color, false);
	}
	
	/**
	 * 获得当前对应的颜色的所有合法行棋点：合法且不能填眼
	 * @param board 当前棋盘
	 * @param color 测试的颜色
	 * @return 所有合法行棋点
	 */
	private LinkedList<Point> getAllEmptyPoints(GoBoard board, boolean color) throws GoException, CloneNotSupportedException{
		LinkedList<Point> points = new LinkedList<>();
		Stone[][] state = board.getState();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null) {
					Point newP = new Point(j, i);
					//该点为合法行棋点的要求：
					//1.走棋合法
					//2.不能填眼
					//3.不能打劫 v
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




















