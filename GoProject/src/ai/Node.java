package ai;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.LinkedList;

import game.GoBoard;
import game.GoException;
import game.Point;
import game.Stone;

/**
 * 博弈树节点类。通过最高层父节点递归调用getValue后，利用findBestNext得到最佳子节点
 */
public class Node{

	/**
	 * 节点的子节点的行棋颜色
	 */
	private boolean Color;
	
	/**极大还是极小节点。这将决定选择极大还是极小的效用值的节点
	 * <p>True:极大 </p>
	 * <p>False:极小</p>
	 */
	private boolean Max_Min;
	
	/**
	 * 本节点代表的棋盘
	 */
	private final GoBoard Board;
	
	/**
	 * 效用值的范围。用于α-β剪枝
	 */
	private Range V_Range;
	
	/**
	 * 效用值。如果是底层节点，由UtilityEstimate计算，如果不是底层节点，则由子节点效用值和max_min参数计算
	 */
	private double Value;
	
	/**
	 * 次节点集合
	 */
	private LinkedList<Node> Nodes;
	
	/**
	 * 次态节点的数量限制。在findChildNodes方法中调用
	 */
	private final int NodesLimit = 3;
	
	private final int WaitingNodesLimit = 6;
	
	private final PrintWriter writer_2 = new PrintWriter(System.out);
	
	/**
	 * 构造极大极小博弈树的节点
	 * @param state 当前棋盘状态
	 * @param min_max 极大还是极小节点
	 * @throws GoException
	 * @throws CloneNotSupportedException 
	 */
	public Node(GoBoard board, boolean color, boolean max_min) throws GoException, CloneNotSupportedException {
		this.Color = color;
		this.Board = board;
		this.Max_Min = max_min;
		this.V_Range = null;
		this.Nodes = new LinkedList<>();
	}
	
	/**
	 * 用于在getValue递归方法中调用，先随机生成的填充Nodes链表的节点
	 * @param depth 当前深度
	 * @param board 当前棋盘状态
	 * @throws CloneNotSupportedException GoBoard的克隆异常
	 */
	public void findChildNode(int depth, GoBoard board) throws CloneNotSupportedException, GoException {
		Point[] allPoints = this.getLegalPoints();
		try {
		for (Point p: allPoints) {
			try {
				GoBoard newBoard = this.Board.clone();
				newBoard.addStone(p, Color, depth==0);
				//当前节点的子节点的颜色和max_min应该反转
				this.Nodes.add(new Node(newBoard, !Color, !Max_Min));
			}
			catch(GoException e){
				GoException E;
				if (p == null)
					E = new GoException("从getLegalPoints方法中得到的合法点棋子在遍历时为空！");	
				else E = new GoException("生成博弈树节点时发生致命错误！\n棋盘点位置：(" + p.getX() + "," + p.getY() + ")");
				E.initCause(e);
				throw E;
			}
		}
		}
		catch(GoException e) {
			Node.printBoard(this.Board.getState());
			GoException E = new GoException("生成博弈树节点时发生致命错误！\n");
			E.initCause(e);
			throw E;
		}
	}

	/**
	 * 根据当前棋盘状态，获得用于初始化棋盘。在findChildNodes中调用用于寻找合法的子节点位置
	 * @return 合法且合理的移动的点的数组
	 */
	private Point[] getLegalPoints() throws GoException, CloneNotSupportedException {
		return this.Board.getLegalPos(NodesLimit, WaitingNodesLimit, Color);
	}
	
	/**
	 * 通过递归调用，深度优先，逐渐生成子节点的效用值，然后根据子节点效用值刷新父节点的效用值
	 * @param depth 博弈树当前深度
	 */
	public void getValueWithoutCut(int depth) throws GoException, CloneNotSupportedException {
		if (depth == GoAI.DepthLimit) {
			this.Value = GoAI.utilityEstimate(this.Board, this.Color);
			return;
		}
		this.findChildNode(depth, this.Board);
		for (Node node : this.Nodes) {
			node.getValueWithoutCut(depth+1);
		}
		if (Max_Min) {
			this.Value = this.Nodes.stream().max(new NodeValueComparator()).get().Value;
		}
		else {
			this.Value = this.Nodes.stream().min(new NodeValueComparator()).get().Value;
		}
	}
	
	/**
	 * 利用α-β剪枝，通过递归调用生成子节点的效用值，根据子节点效用值刷新父节点的效用值
	 * @param depth 博弈树当前深度
	 * @param fatherValueRange 父节点的已有的效用值范围，用于剪枝
	 */
	public void getValueWithCut(int depth, Range fatherValueRange) throws CloneNotSupportedException, GoException {
		//9.8修复bug：递归调用时，引入的参数GoBoard应该是本节点的GoBoard，而非作为参数引入的GoBoard
		//递归基线条件：递归到达了深度极限
		if (depth == GoAI.DepthLimit) {
			this.Value = UtilityEstimate.LastEvaluate(this.Board, this.Color);
			//this.Value = GoAI.utilityEstimate(this.Board, this.Color);
			return;
		}
		//寻找效用值前，先找到子节点
		try {
			this.findChildNode(depth, this.Board);
		}
		catch(GoException e) {
			if (e.getMessage().equals("棋盘上没有走棋点")) {
				this.Value = UtilityEstimate.LastEvaluate(this.Board, this.Color);
				if (depth == 0)
					throw new GoException("棋盘上没有走棋点");
				else return;
			}
		}
		
		//再对每一个子节点，递归生成子节点的效用值
		//α-β剪枝更新子节点效用值
		for (int i = 0; i < this.Nodes.size(); i++) {
			//生成第一个节点时，构造range对象
			if (i == 0) {
				//第一个节点不需要考查父节点的效用值范围来剪枝，因此传递null参数
				this.Nodes.get(0).getValueWithCut(depth+1, null);
				//利用第一个子节点的效用值，构造本节点的效用值范围
				this.V_Range = new Range(this.Max_Min, this.Nodes.get(0).Value);
				//如果父节点的效用值范围传递了进来 ，代表需要剪枝
				if (fatherValueRange != null) {
					//如果发现范围刚好符合剪枝条件
					if (this.V_Range.checkRangeOfCut(fatherValueRange)) {
						//先设置本节点的效用值为第一个子节点的值
						this.Value = this.Nodes.get(0).Value;
						//再直接return返回，不再考虑本节点的后续子节点
						return;
					}
					//如果为第一个节点，有父节点效用值范围，但是不能剪枝，那就继续遍历子节点
				}
				//如果没有父节点效用值，不能剪枝，继续遍历子节点
			}
			
			//第一个节点后的节点：更新效用值范围，如果能够剪枝就剪枝后续子节点
			else if (i > 0) {
				//由于第一个节点会生成效用值范围，因此可以传递给后续的子节点作为父节点效用值范围
				this.Nodes.get(i).getValueWithCut(depth+1, this.V_Range);
				//如果后续的子节点的范围比前面的子节点生成的范围更小的话，更新本节点的效用值范围
				if (this.V_Range.rangeContains(this.Nodes.get(i).Value)) {
					this.V_Range.setRange(this.V_Range.Direction, this.Nodes.get(i).Value);
				}
				//更新完range以后，再检查一次能否剪枝
				if (fatherValueRange != null) {
					if (this.V_Range.checkRangeOfCut(fatherValueRange)) {
						//如果发现可以剪枝，则将本节点的效用值置为范围的边界值
						this.Value = this.V_Range.Value;
						//直接return返回，终止对后续子节点的检索
						return;
					}
					//如果新的范围仍然不足以剪枝，那就继续检索后续的子节点
				}
				//如果没有父节点的效用值范围，代表这是父节点的第一个子节点，不能进行剪枝操作，只能继续遍历后续节点
			}
		}
		
		//如果遍历完了所有子节点（没有剪枝），则将效用值设置为效用值范围的边界值
		this.Value = this.V_Range.Value;
	}
	
	/**
	 * 用于在AI类中调用，获取当前初始节点的最佳下一节点（由于总是在ai行棋时调用，因此第一层都是max）
	 * @return 最佳下一节点
	 */
	public Node getBestNode() {
		return this.Nodes.stream().max(new NodeValueComparator()).get();
	}
	
	/**
	 * 用于比较节点的效用值的比较器
	 */
	class NodeValueComparator implements Comparator<Node> {
		public NodeValueComparator() {}
		
		public int compare(Node n1, Node n2) {
			if (n1.Value - n2.Value < 0)
				return -1;
			else if (n1.Value - n2.Value > 0)
				return 1;
			else return 0;
		}
	}
	
	public GoBoard getBoard() {
		return this.Board;
	}

	public static void printBoard(Stone[][] state) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null)
					System.out.print("_ ");
				else if (state[j][i].getColor())
					System.out.print("X ");
				else System.out.print("O ");
			}
			System.out.print("\n");	
		}
		System.out.print("\n");
	}
	
	public static void printBoard(Stone[][] state, PrintWriter writer) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[j][i] == null)
					writer.print("_ ");
				else if (state[j][i].getColor())
					writer.print("X ");
				else writer.print("O ");
			}
			writer.print("\n");	
		}
		writer.print("\n\n\n");
		writer.flush();
	}
	
	/**
	 * 表示范围的类，包含了一些在进行剪枝时，进行判断的方法
	 */
	class Range{
		/**
		 * 范围的方向：true为大于等于，false为小于等于
		 */
		boolean Direction;
		double Value;
		
		public Range(boolean d, double v) {
			this.Direction = d;
			this.Value = v;
		}
		
		/**
		 * 判断范围能否实现剪枝
		 * @param r 待判断的范围
		 * @return <p>true:可以剪枝</p>
		 *         <p>false:不能剪枝</p>
		 */
		public  boolean checkRangeOfCut(Range r) {
			return (this.Direction && this.Value > r.Value) || (!this.Direction && this.Value < r.Value);
		}
		
		/**
		 * 判断值是否在范围内，用于更新range范围
		 * @param value 待判断值
		 * @return <p>true:在范围内，需要更新</p>
		 *         <p>false:不在范围内，不需要更新</p>
		 */
		public boolean rangeContains(double value) {
			return (this.Direction && this.Value < value) || (!this.Direction && this.Value > value);
		}
		
		/**
		 * 更新range的值
		 * @param dir 新的不等号方向
		 * @param v 新的边界值
		 */
		public void setRange(boolean dir, double v) {
			this.Direction = dir;
			this.Value = v;
		}
	}
}
