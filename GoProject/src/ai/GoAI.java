package ai;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
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
 * ����ai�Ŀͻ��ˡ�ͨ������searchNext����һ��GoBoard��������
 */
public class GoAI {
	/**
	 * ai��������ɫ
	 */
	private boolean Color;
	
	/**
	 * ai���������ĳ�ʼ�ڵ�
	 */
	private Node InitNode;
	
	/**
	 * ai�������������
	 */
	public static final int DepthLimit = 8;
	
	/**
	 * �ļ������
	 */
	private final PrintWriter Writer;
	
	/**
	 * ����������ɫ����һ������ai
	 * @param color ai��ɫ
	 */
	public GoAI(boolean color) throws FileNotFoundException {
		this.Color = color;
		this.Writer = new PrintWriter(new FileOutputStream("GameTree.txt"), true);
	}
	
	/**
	 * ��ÿ���ֵ�AI����ʱ�����ڳ�ʼ����ʼ�ڵ�ķ���
	 * @param board ��ǰ����
	 * @param color AI��ɫ��
	 */
	private void initNodeOfAI(GoBoard board) throws GoException, CloneNotSupportedException {
		//��һ���ڵ㣺��ǰ���̣���ǰAI����ɫ��AI����ɫӦ��ΪMax����true
		this.InitNode = new Node(board.clone(), Color, true);
	}
	
	/**
	 * �����Ѿ����ɵ�Ч��ֵ��Ѱ����ѵ���һ����
	 * @return ��ѵ���һ����������
	 * @throws GoException
	 */
	private GoBoard findBestNext() throws GoException {
		if (this.InitNode == null)
			throw new GoException("��ʼ�ڵ���δ��ʼ����");
		else return InitNode.getBestNode().getBoard();
	}
	
	/**
	 * Ѱ����ѵ���һ�ֵ���
	 * @param board ��ǰ����
	 * @return ��һ���������
	 * @throws AWTException 
	 */
	public GoBoard searchNext(GoBoard board) throws GoException, CloneNotSupportedException{
		this.initNodeOfAI(board);
		//��ʼ�ڵ�����Ϊ0��
		//���ڵ�ĸ��ڵ㷶ΧΪnull
		this.InitNode.getValueWithCut(0, null);
		//this.InitNode.getValueWithoutCut(0);
		return findBestNext();
	}
	
	/**
	 * �����ڸ���״̬�£�����Ч�ù���ֵ�ľ�̬����
	 * ����һ�����õķ���
	 * @param state ��ǰ����״̬
	 * @param color �����Ƶ���ɫ��
	 * @return ����Ч��ֵ
	 */
	public static double utilityEstimate(GoBoard board, boolean color) throws GoException {
		//���Ե�Ч�ú�������������Ŀ��������ĿΪЧ��ֵ
		Stone[][] state = board.getState();
		int num = 0;
		//Ч��ֵ�����������
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[i][j] != null && state[i][j].getColor() == color)
					num++;
			}
		}
		//Ч��ֵ������Ŀ���
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (state[i][j] != null && state[i][j].getColor() == color)
					num+=board.checkQiNum(new Point(i, j));			
			}
		}
		return num;
	}
}