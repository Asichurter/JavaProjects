package game;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
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
 * 用于承载棋盘，进行绘图的面板组件
 *
 */
public class GoPanel extends JPanel{
	
	/**
	 * 焦点坐标
	 */
	private int HighlightX;
	/**
	 * 焦点坐标
	 */
	private int HighlightY;
	/**
	 * 棋盘实例域
	 */
	private GoBoard Board;
	/**
	 * 当前轮到的玩家
	 */
	private boolean CurrentPlayer;
	
	private final Image Highlight = Toolkit.getDefaultToolkit().getImage("Images/焦点.gif");
	private final Image CurrentMove = Toolkit.getDefaultToolkit().getImage("Images/当前行棋.gif");
	/**
	 * 棋盘单元宽度
	 */
	public static final int Width = 60;
	/**
	 * 棋子宽度
	 */
	public static final int QiWidth = 48;
	/**
	 * 焦点图标宽度
	 */
	public static final int HighlightWidth = 30;
	/**
	 * 边框宽度
	 */
	public static final int BorderWidth = 60;
	
	private static int AIStep = 0;
	
	/**
	 * 极大极小，α-β剪枝算法加持的ai
	 */
	public GoAI AI;
	
	/**
	 * 蒙特卡洛树搜索+UCB算法加持的ai
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
	 * 移动焦点的方法
	 * @param dir 移动的方向
	 * @throws GoException 出现了无法识别的方向时抛出的异常
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
				throw new GoException("移动棋子时发生了异常：出现了未知方向的移动：方向代码：" + dir);
		}
	}
	
	/**
	 * 交换走棋方玩家，当轮到ai时，将会启动搜索
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
			System.out.println("第" + (++GoPanel.AIStep) + "步搜索耗时：\n" + Duration.between(start, end).toMillis() + "ms\n");
		}
		this.CurrentPlayer = !this.CurrentPlayer;
	}
	
	public void simulate(int times) throws FileNotFoundException, CloneNotSupportedException, GoException {
		MTCS_VS_GameTree(times);
	}
	
	private void MTCS_VS_GameTree(int times) throws CloneNotSupportedException, GoException, FileNotFoundException {
		Score score = new Score();
		for (int i = 1; i <= times; i++) {
			consoleWriter.println("第" + i + "次模拟开始！");
			Instant now = Instant.now();
			GoBoard board = this.Board.clone();
			final boolean MTCS_first = true;
			try {
				//蒙特卡洛树搜索首先走棋
				boolean curColor = true;
				int counter = 1;
				while (true) {
					consoleWriter.println("第" + (counter++) + "步" + (curColor == MTCS_first ? " 蒙特卡洛走棋" : " 普通博弈树走棋"));
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
				if (e.getMessage().equals("棋盘上没有走棋点")) {
					Instant end = Instant.now();
					fileWriter.println("\n\n第" + i + "轮测试结束，耗费时间：" + Duration.between(now, end).toMillis() + "ms");
					showTestResult(board, MTCS_first, score);
				}
				else {
					System.out.println("错误信息：" + e.getMessage() + "\n棋盘情况：");
					ai.Node.printBoard(board.getState());
					throw new GoException("在模拟对局时，出现了意外的情况！");
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
		fileWriter.println("蒙特卡洛树搜索得分： " + MTCS_Score + "\n普通博弈树得分：" + (81-MTCS_Score) + 
				(MTCS_Score >= (MTCS_Color ? 41 : 40) ? "\n蒙特卡罗树搜索获胜！" : "\n普通博弈树搜索获胜！"));
		fileWriter.println("最终棋盘：");
		ai.Node.printBoard(board.getState(), fileWriter);
	}
	
	private void parseScore(int times, Score score) {
		fileWriter.println("技术统计：\n" + "蒙特卡洛树获胜次数：" + score.MTCS_Times + 
				"\n普通博弈树获胜次数:" + score.Gametree_Times + "\n蒙特卡洛树总得分："+
				score.MTCS_Score + "\n普通博弈树总得分："+ score.Gametree_Score + 
				"\n蒙特卡洛树平均得分：" + ((double)score.MTCS_Score)/times + 
				"\n普通博弈树平均得分：" + ((double)score.Gametree_Score)/times + 
				"\n蒙特卡洛树胜率：" + ((double)score.MTCS_Times)/times);
	}
	
	/**
	 * 当前玩家在当前焦点处行棋
	 * @return 是否成功行棋
	 * @throws GoException 行棋时出现的异常
	 * @throws FileNotFoundException 输出棋盘文件时抛出的异常
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
		g2.drawString("当前行棋者：" + (this.CurrentPlayer ? "黑棋" : "白棋"), 20, 20);
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


