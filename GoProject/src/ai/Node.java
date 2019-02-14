package ai;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.LinkedList;

import game.GoBoard;
import game.GoException;
import game.Point;
import game.Stone;

/**
 * �������ڵ��ࡣͨ����߲㸸�ڵ�ݹ����getValue������findBestNext�õ�����ӽڵ�
 */
public class Node{

	/**
	 * �ڵ���ӽڵ��������ɫ
	 */
	private boolean Color;
	
	/**�����Ǽ�С�ڵ㡣�⽫����ѡ�񼫴��Ǽ�С��Ч��ֵ�Ľڵ�
	 * <p>True:���� </p>
	 * <p>False:��С</p>
	 */
	private boolean Max_Min;
	
	/**
	 * ���ڵ���������
	 */
	private final GoBoard Board;
	
	/**
	 * Ч��ֵ�ķ�Χ�����ڦ�-�¼�֦
	 */
	private Range V_Range;
	
	/**
	 * Ч��ֵ������ǵײ�ڵ㣬��UtilityEstimate���㣬������ǵײ�ڵ㣬�����ӽڵ�Ч��ֵ��max_min��������
	 */
	private double Value;
	
	/**
	 * �νڵ㼯��
	 */
	private LinkedList<Node> Nodes;
	
	/**
	 * ��̬�ڵ���������ơ���findChildNodes�����е���
	 */
	private final int NodesLimit = 3;
	
	private final int WaitingNodesLimit = 6;
	
	private final PrintWriter writer_2 = new PrintWriter(System.out);
	
	/**
	 * ���켫��С�������Ľڵ�
	 * @param state ��ǰ����״̬
	 * @param min_max �����Ǽ�С�ڵ�
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
	 * ������getValue�ݹ鷽���е��ã���������ɵ����Nodes����Ľڵ�
	 * @param depth ��ǰ���
	 * @param board ��ǰ����״̬
	 * @throws CloneNotSupportedException GoBoard�Ŀ�¡�쳣
	 */
	public void findChildNode(int depth, GoBoard board) throws CloneNotSupportedException, GoException {
		Point[] allPoints = this.getLegalPoints();
		try {
		for (Point p: allPoints) {
			try {
				GoBoard newBoard = this.Board.clone();
				newBoard.addStone(p, Color, depth==0);
				//��ǰ�ڵ���ӽڵ����ɫ��max_minӦ�÷�ת
				this.Nodes.add(new Node(newBoard, !Color, !Max_Min));
			}
			catch(GoException e){
				GoException E;
				if (p == null)
					E = new GoException("��getLegalPoints�����еõ��ĺϷ��������ڱ���ʱΪ�գ�");	
				else E = new GoException("���ɲ������ڵ�ʱ������������\n���̵�λ�ã�(" + p.getX() + "," + p.getY() + ")");
				E.initCause(e);
				throw E;
			}
		}
		}
		catch(GoException e) {
			Node.printBoard(this.Board.getState());
			GoException E = new GoException("���ɲ������ڵ�ʱ������������\n");
			E.initCause(e);
			throw E;
		}
	}

	/**
	 * ���ݵ�ǰ����״̬��������ڳ�ʼ�����̡���findChildNodes�е�������Ѱ�ҺϷ����ӽڵ�λ��
	 * @return �Ϸ��Һ�����ƶ��ĵ������
	 */
	private Point[] getLegalPoints() throws GoException, CloneNotSupportedException {
		return this.Board.getLegalPos(NodesLimit, WaitingNodesLimit, Color);
	}
	
	/**
	 * ͨ���ݹ���ã�������ȣ��������ӽڵ��Ч��ֵ��Ȼ������ӽڵ�Ч��ֵˢ�¸��ڵ��Ч��ֵ
	 * @param depth ��������ǰ���
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
	 * ���æ�-�¼�֦��ͨ���ݹ���������ӽڵ��Ч��ֵ�������ӽڵ�Ч��ֵˢ�¸��ڵ��Ч��ֵ
	 * @param depth ��������ǰ���
	 * @param fatherValueRange ���ڵ�����е�Ч��ֵ��Χ�����ڼ�֦
	 */
	public void getValueWithCut(int depth, Range fatherValueRange) throws CloneNotSupportedException, GoException {
		//9.8�޸�bug���ݹ����ʱ������Ĳ���GoBoardӦ���Ǳ��ڵ��GoBoard��������Ϊ���������GoBoard
		//�ݹ�����������ݹ鵽������ȼ���
		if (depth == GoAI.DepthLimit) {
			this.Value = UtilityEstimate.LastEvaluate(this.Board, this.Color);
			//this.Value = GoAI.utilityEstimate(this.Board, this.Color);
			return;
		}
		//Ѱ��Ч��ֵǰ�����ҵ��ӽڵ�
		try {
			this.findChildNode(depth, this.Board);
		}
		catch(GoException e) {
			if (e.getMessage().equals("������û�������")) {
				this.Value = UtilityEstimate.LastEvaluate(this.Board, this.Color);
				if (depth == 0)
					throw new GoException("������û�������");
				else return;
			}
		}
		
		//�ٶ�ÿһ���ӽڵ㣬�ݹ������ӽڵ��Ч��ֵ
		//��-�¼�֦�����ӽڵ�Ч��ֵ
		for (int i = 0; i < this.Nodes.size(); i++) {
			//���ɵ�һ���ڵ�ʱ������range����
			if (i == 0) {
				//��һ���ڵ㲻��Ҫ���鸸�ڵ��Ч��ֵ��Χ����֦����˴���null����
				this.Nodes.get(0).getValueWithCut(depth+1, null);
				//���õ�һ���ӽڵ��Ч��ֵ�����챾�ڵ��Ч��ֵ��Χ
				this.V_Range = new Range(this.Max_Min, this.Nodes.get(0).Value);
				//������ڵ��Ч��ֵ��Χ�����˽��� ��������Ҫ��֦
				if (fatherValueRange != null) {
					//������ַ�Χ�պ÷��ϼ�֦����
					if (this.V_Range.checkRangeOfCut(fatherValueRange)) {
						//�����ñ��ڵ��Ч��ֵΪ��һ���ӽڵ��ֵ
						this.Value = this.Nodes.get(0).Value;
						//��ֱ��return���أ����ٿ��Ǳ��ڵ�ĺ����ӽڵ�
						return;
					}
					//���Ϊ��һ���ڵ㣬�и��ڵ�Ч��ֵ��Χ�����ǲ��ܼ�֦���Ǿͼ��������ӽڵ�
				}
				//���û�и��ڵ�Ч��ֵ�����ܼ�֦�����������ӽڵ�
			}
			
			//��һ���ڵ��Ľڵ㣺����Ч��ֵ��Χ������ܹ���֦�ͼ�֦�����ӽڵ�
			else if (i > 0) {
				//���ڵ�һ���ڵ������Ч��ֵ��Χ����˿��Դ��ݸ��������ӽڵ���Ϊ���ڵ�Ч��ֵ��Χ
				this.Nodes.get(i).getValueWithCut(depth+1, this.V_Range);
				//����������ӽڵ�ķ�Χ��ǰ����ӽڵ����ɵķ�Χ��С�Ļ������±��ڵ��Ч��ֵ��Χ
				if (this.V_Range.rangeContains(this.Nodes.get(i).Value)) {
					this.V_Range.setRange(this.V_Range.Direction, this.Nodes.get(i).Value);
				}
				//������range�Ժ��ټ��һ���ܷ��֦
				if (fatherValueRange != null) {
					if (this.V_Range.checkRangeOfCut(fatherValueRange)) {
						//������ֿ��Լ�֦���򽫱��ڵ��Ч��ֵ��Ϊ��Χ�ı߽�ֵ
						this.Value = this.V_Range.Value;
						//ֱ��return���أ���ֹ�Ժ����ӽڵ�ļ���
						return;
					}
					//����µķ�Χ��Ȼ�����Լ�֦���Ǿͼ��������������ӽڵ�
				}
				//���û�и��ڵ��Ч��ֵ��Χ���������Ǹ��ڵ�ĵ�һ���ӽڵ㣬���ܽ��м�֦������ֻ�ܼ������������ڵ�
			}
		}
		
		//����������������ӽڵ㣨û�м�֦������Ч��ֵ����ΪЧ��ֵ��Χ�ı߽�ֵ
		this.Value = this.V_Range.Value;
	}
	
	/**
	 * ������AI���е��ã���ȡ��ǰ��ʼ�ڵ�������һ�ڵ㣨����������ai����ʱ���ã���˵�һ�㶼��max��
	 * @return �����һ�ڵ�
	 */
	public Node getBestNode() {
		return this.Nodes.stream().max(new NodeValueComparator()).get();
	}
	
	/**
	 * ���ڱȽϽڵ��Ч��ֵ�ıȽ���
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
	 * ��ʾ��Χ���࣬������һЩ�ڽ��м�֦ʱ�������жϵķ���
	 */
	class Range{
		/**
		 * ��Χ�ķ���trueΪ���ڵ��ڣ�falseΪС�ڵ���
		 */
		boolean Direction;
		double Value;
		
		public Range(boolean d, double v) {
			this.Direction = d;
			this.Value = v;
		}
		
		/**
		 * �жϷ�Χ�ܷ�ʵ�ּ�֦
		 * @param r ���жϵķ�Χ
		 * @return <p>true:���Լ�֦</p>
		 *         <p>false:���ܼ�֦</p>
		 */
		public  boolean checkRangeOfCut(Range r) {
			return (this.Direction && this.Value > r.Value) || (!this.Direction && this.Value < r.Value);
		}
		
		/**
		 * �ж�ֵ�Ƿ��ڷ�Χ�ڣ����ڸ���range��Χ
		 * @param value ���ж�ֵ
		 * @return <p>true:�ڷ�Χ�ڣ���Ҫ����</p>
		 *         <p>false:���ڷ�Χ�ڣ�����Ҫ����</p>
		 */
		public boolean rangeContains(double value) {
			return (this.Direction && this.Value < value) || (!this.Direction && this.Value > value);
		}
		
		/**
		 * ����range��ֵ
		 * @param dir �µĲ��Ⱥŷ���
		 * @param v �µı߽�ֵ
		 */
		public void setRange(boolean dir, double v) {
			this.Direction = dir;
			this.Value = v;
		}
	}
}
