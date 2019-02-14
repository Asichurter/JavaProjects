package ai;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import game.Stone;

import java.awt.AWTException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import game.GoBoard;
import game.GoException;
import game.Point;

/**
 * 行棋ai的客户端。通过调用searchNext返回一个GoBoard进行行棋
 */
public class GoAI {
	/**
	 * ai的行棋颜色
	 */
	private boolean Color;
	
	/**
	 * ai行棋搜索的初始节点
	 */
	private Node InitNode;
	
	/**
	 * ai搜索的深度限制
	 */
	public static final int DepthLimit = 8;
	
	/**
	 * 文件输出器
	 */
	private final PrintWriter Writer;
	
	/**
	 * 利用行棋颜色构造一个行棋ai
	 * @param color ai颜色
	 */
	public GoAI(boolean color) throws FileNotFoundException {
		this.Color = color;
		this.Writer = new PrintWriter(new FileOutputStream("GameTree.txt"), true);
	}
	
	/**
	 * 在每次轮到AI行棋时，用于初始化初始节点的方法
	 * @param board 当前棋盘
	 * @param color AI颜色方
	 */
	private void initNodeOfAI(GoBoard board) throws GoException, CloneNotSupportedException {
		//第一个节点：当前棋盘，当前AI的颜色，AI的颜色应该为Max，即true
		this.InitNode = new Node(board.clone(), Color, true);
	}
	
	/**
	 * 根据已经生成的效用值，寻找最佳的下一步棋
	 * @return 最佳的下一手棋后的棋盘
	 * @throws GoException
	 */
	private GoBoard findBestNext() throws GoException {
		if (this.InitNode == null)
			throw new GoException("初始节点尚未初始化！");
		else return InitNode.getBestNode().getBoard();
	}
	
	/**
	 * 寻找最佳的下一手的棋
	 * @param board 当前棋盘
	 * @return 下一手棋的棋盘
	 * @throws AWTException 
	 */
	public GoBoard searchNext(GoBoard board) throws GoException, CloneNotSupportedException{
		this.initNodeOfAI(board);
		//初始节点的深度为0、
		//根节点的父节点范围为null
		this.InitNode.getValueWithCut(0, null);
		//this.InitNode.getValueWithoutCut(0);
		return findBestNext();
	}
	
	/**
	 * 用于在给定状态下，生成效用估计值的静态方法
	 * 这是一个弃用的方法
	 * @param state 当前棋盘状态
	 * @param color 待估计的颜色方
	 * @return 估计效用值
	 */
	public static double utilityEstimate(GoBoard board, boolean color) throws GoException {
		//测试的效用函数：以棋子数目与气的数目为效用值
		Stone[][] state = board.getState();
		int num = 0;
		//效用值与棋子数相关
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[i][j] != null && state[i][j].getColor() == color)
					num++;
			}
		}
		//效用值与气数目相关
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[i][j] != null && state[i][j].getColor() == color)
					num+=board.checkQiNum(new Point(i, j));			
			}
		}
		return num;
	}
}