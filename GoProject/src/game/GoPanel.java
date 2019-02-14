package game;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JPanel;

import ai.GoAI;
import ai.MTCS;
import newPattern.Pattern;

/**
 * ���ڳ������̣����л�ͼ��������
 *
 */
public class GoPanel extends JPanel{
	
	/**
	 * ��������
	 */
	private int HighlightX;
	/**
	 * ��������
	 */
	private int HighlightY;
	/**
	 * ����ʵ����
	 */
	private GoBoard Board;
	/**
	 * ��ǰ�ֵ������
	 */
	private boolean CurrentPlayer;
	
	private final Image Highlight = Toolkit.getDefaultToolkit().getImage("Images/����.gif");
	private final Image CurrentMove = Toolkit.getDefaultToolkit().getImage("Images/��ǰ����.gif");
	/**
	 * ���̵�Ԫ���
	 */
	public static final int Width = 60;
	/**
	 * ���ӿ��
	 */
	public static final int QiWidth = 48;
	/**
	 * ����ͼ����
	 */
	public static final int HighlightWidth = 30;
	/**
	 * �߿���
	 */
	public static final int BorderWidth = 60;
	
	private static int AIStep = 0;
	
	/**
	 * ����С����-�¼�֦�㷨�ӳֵ�ai
	 */
	public GoAI AI;
	
	/**
	 * ���ؿ���������+UCB�㷨�ӳֵ�ai
	 */
	public MTCS mtcs;
	
	private PrintWriter fileWriter = new PrintWriter(new FileOutputStream("vs_debug.txt"), true);
	
	private PrintWriter consoleWriter = new PrintWriter(System.out, true);

	public GoPanel() throws FileNotFoundException {
		this.Board = new GoBoard();
		this.CurrentPlayer = true;
		HighlightX = 0;
		HighlightY = 0;
	}
	
	/**
	 * �ƶ�����ķ���
	 * @param dir �ƶ��ķ���
	 * @throws GoException �������޷�ʶ��ķ���ʱ�׳����쳣
	 */
	public void highlightMove(int dir) throws GoException {
		switch(dir) {
			case 1:
				if (HighlightY > 0)
					HighlightY -= 1;
				break;
			case 2:
				if (HighlightX < 8)
					HighlightX += 1;
				break;
			case 3:
				if (HighlightY < 8)
					HighlightY += 1;
				break;
			case 4:
				if (HighlightX > 0)
					HighlightX -= 1;
				break;
			default:
				throw new GoException("�ƶ�����ʱ�������쳣��������δ֪������ƶ���������룺" + dir);
		}
	}
	
	/**
	 * �������巽��ң����ֵ�aiʱ��������������
	 * @throws FileNotFoundException 
	 * @throws AWTException 
	 */
	public void togglePlayer(boolean useAI, boolean type) throws GoException, CloneNotSupportedException, FileNotFoundException{
		if (useAI) {
			Instant start = Instant.now();
			if (type) {
				this.mtcs = new MTCS(this.CurrentPlayer);
				this.Board = this.mtcs.findNextStep(Board);
			
			}
			else {
				this.AI = new GoAI(this.CurrentPlayer);
				this.Board = this.AI.searchNext(Board);
			}
			Instant end = Instant.now();
			System.out.println("��" + (++GoPanel.AIStep) + "��������ʱ��\n" + Duration.between(start, end).toMillis() + "ms\n");
		}
		this.CurrentPlayer = !this.CurrentPlayer;
	}
	
	public void simulate(int times) throws FileNotFoundException, CloneNotSupportedException, GoException {
		MTCS_VS_GameTree(times);
	}
	
	private void MTCS_VS_GameTree(int times) throws CloneNotSupportedException, GoException, FileNotFoundException {
		Score score = new Score();
		for (int i = 1; i <= times; i++) {
			consoleWriter.println("��" + i + "��ģ�⿪ʼ��");
			Instant now = Instant.now();
			GoBoard board = this.Board.clone();
			final boolean MTCS_first = true;
			try {
				//���ؿ�����������������
				boolean curColor = true;
				int counter = 1;
				while (true) {
					consoleWriter.println("��" + (counter++) + "��" + (curColor == MTCS_first ? " ���ؿ�������" : " ��ͨ����������"));
					if ((MTCS_first && curColor) || (!MTCS_first && !curColor)) {
						this.mtcs = new MTCS(curColor);
						board = mtcs.findNextStep(board);
					}
					else if ((MTCS_first && !curColor) || (!MTCS_first && curColor)) {
						this.AI = new GoAI(curColor);
						board = AI.searchNext(board);
					}
					curColor = !curColor;
				}
			}
			catch(GoException e) {
				if (e.getMessage().equals("������û�������")) {
					Instant end = Instant.now();
					fileWriter.println("\n\n��" + i + "�ֲ��Խ������ķ�ʱ�䣺" + Duration.between(now, end).toMillis() + "ms");
					showTestResult(board, MTCS_first, score);
				}
				else {
					System.out.println("������Ϣ��" + e.getMessage() + "\n���������");
					ai.Node.printBoard(board.getState());
					throw new GoException("��ģ��Ծ�ʱ������������������");
				}
			}
		}
		parseScore(times, score);
		fileWriter.close();
	}
	
	private void showTestResult(GoBoard board, boolean MTCS_Color, Score score) {
		int MTCS_Score = MTCS.externEvaluate(board, MTCS_Color);
		score.MTCS_Score = score.MTCS_Score + MTCS_Score;
		score.Gametree_Score = score.Gametree_Score + (81-MTCS_Score);
		if (MTCS_Score >= (MTCS_Color ? 41 : 40))
			score.MTCS_Times += 1;
		else score.Gametree_Times += 1;
		fileWriter.println("���ؿ����������÷֣� " + MTCS_Score + "\n��ͨ�������÷֣�" + (81-MTCS_Score) + 
				(MTCS_Score >= (MTCS_Color ? 41 : 40) ? "\n���ؿ�����������ʤ��" : "\n��ͨ������������ʤ��"));
		fileWriter.println("�������̣�");
		ai.Node.printBoard(board.getState(), fileWriter);
	}
	
	private void parseScore(int times, Score score) {
		fileWriter.println("����ͳ�ƣ�\n" + "���ؿ�������ʤ������" + score.MTCS_Times + 
				"\n��ͨ��������ʤ����:" + score.Gametree_Times + "\n���ؿ������ܵ÷֣�"+
				score.MTCS_Score + "\n��ͨ�������ܵ÷֣�"+ score.Gametree_Score + 
				"\n���ؿ�����ƽ���÷֣�" + ((double)score.MTCS_Score)/times + 
				"\n��ͨ������ƽ���÷֣�" + ((double)score.Gametree_Score)/times + 
				"\n���ؿ�����ʤ�ʣ�" + ((double)score.MTCS_Times)/times);
	}
	
	/**
	 * ��ǰ����ڵ�ǰ���㴦����
	 * @return �Ƿ�ɹ�����
	 * @throws GoException ����ʱ���ֵ��쳣
	 * @throws FileNotFoundException ��������ļ�ʱ�׳����쳣
	 */
	public boolean playerAct() throws GoException, FileNotFoundException {
		return this.Board.addStone(new Point(this.HighlightX, this.HighlightY), this.CurrentPlayer, this);
	}
	
	public boolean getCurPlayer() {
		return this.CurrentPlayer;
	}
	
	public GoBoard getBoard() {
		return this.Board;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		for (int i = 0; i <= 8; i++) {
				g2.drawString(""+ i, BorderWidth +Width*i, BorderWidth-25);
				g2.drawLine(BorderWidth +Width*i, BorderWidth, BorderWidth +Width*i, BorderWidth + 8*Width);
		}
		for (int i = 0; i <= 8; i++) {
			g2.drawString(""+ i, BorderWidth-30, BorderWidth +Width*i);
			g2.drawLine(BorderWidth, BorderWidth +Width*i, BorderWidth +8*Width, BorderWidth +Width*i);
		}
		Stone[][] stones = this.Board.getStones();
		for (int i = 0; i <= 8; i++) {
			for (int j = 0; j <= 8; j++) {
				if (stones[i][j] != null)
					g2.drawImage(stones[i][j].getImage(), BorderWidth +Width*i-QiWidth/2, BorderWidth +Width*j-QiWidth/2, QiWidth, QiWidth, null);
			}
		}
		g2.drawImage(
				Highlight, BorderWidth +HighlightX*Width-HighlightWidth/2, BorderWidth +HighlightY*Width-HighlightWidth/2, HighlightWidth, HighlightWidth, null);
		g2.drawString("��ǰ�����ߣ�" + (this.CurrentPlayer ? "����" : "����"), 20, 20);
		try {
			g2.drawImage(
					CurrentMove,
					this.Board.getCurrentPoint().getX()*Width-HighlightWidth / 2  +  BorderWidth,
					this.Board.getCurrentPoint().getY()*Width - HighlightWidth / 2 + BorderWidth,
					HighlightWidth, HighlightWidth, null);
		} 
		catch (GoException e) {
			e.printStackTrace();
		}
	}
	
	class Score{
		public int MTCS_Times;
		public int Gametree_Times;
		public int MTCS_Score;
		public int Gametree_Score;
		
		public Score() {
			MTCS_Times = 0;
			Gametree_Times = 0;
			MTCS_Score = 0;
			Gametree_Score = 0;
		}
	}
}


