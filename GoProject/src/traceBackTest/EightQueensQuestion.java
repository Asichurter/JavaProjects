package traceBackTest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class EightQueensQuestion {
	public static void main(String[] args) throws FileNotFoundException {
		new Board().startSearchAndPaint();
	}
}

class Point{
	private int R;
	private int L;
	
	public Point(int r, int l) {
		L = l;
		R = r;
	}
	
	public int getR() {
		return this.R;
	}
	
	public int getL() {
		return this.L;
	}
}

class Board{
	private boolean[][] board;
	private PrintWriter writer;
	private PrintWriter debugWriter;
	private Point[] allPos;
	
	public Board() throws FileNotFoundException{
		writer = new PrintWriter(System.out, true);
		debugWriter = new PrintWriter(new FileOutputStream("debug.txt"), true);
		this.board = new boolean[8][8];
		allPos = new Point[8];
	}
	
	private void paintBoard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[j][i])
					writer.print("X ");
				else writer.print("O ");
				//System.out.print("! ");
			}
			writer.println();
		}
	}
	
	public void startSearchAndPaint() throws FileNotFoundException {
		findNextState(0);
		paintBoard();
	}
	
	//递归调用的方法
	private boolean findNextState(int line) {
		//如果行到了极限，则代表整个递归结束
		if (line >= 8)
			return true;
		//如果递归没有结束，则需要先判定合法的位置
		//如果位置合法，则放下皇后，并且进行下一次递归
		for (int i = 0; i < 8; i++) {	//从每一行的第一列开始寻找，直到列数出界
			//如果该位置合法
			if (line == 0 || testPosition(line, i)){				//空数组情况被剪枝
				board[i][line] = true;								//放下皇后
				allPos[line] = new Point(i, line);
				debugWriter.println(line + "行" + i + "列，放下皇后");
				if (!findNextState(line+1)) {						//判断后续的递归是否成功
					board[i][line] = false;				//如果在放下皇后后，后续递归不成功，则先归还皇后，再考虑下一列
					allPos[line] = null;
					debugWriter.println(line + "行" + i + "列，收回皇后");
				}
				else return true;									//如果放下皇后后，后续递归成功，则这次放皇后也是成功的
			}
		}
		debugWriter.println(line + "行没有合法的位置");
		return false;							//如果列数到了极限，都没有发现合法的位置，则该次递归失败，需要回溯
	}
	
	private boolean testPosition(int line, int row) {
		try {
		int i = 0;
		for (Point p: allPos) {
			if (p == null)
				continue;
			if (p.getR() == row || p.getL() == line) {
				debugWriter.println(line + "行" + row + "列,与第" + i + "个棋子行列重合");
				return false;
			}
			else {
				//有更简单的合法性测试方法
				//左上——>右下斜角测试
				for (int l = p.getL() + 1, r = p.getR() + 1; l < 8 && r < 8; l++, r++) {
					if (l == line && r == row) {
						debugWriter.println(line + "行" + row + "列,与第" + i + "个棋子对角线重合");
						return false;
					}
				}
				//右下——>左上测试
				for (int l = p.getL() - 1, r = p.getR() - 1; l >= 0 && r >= 0; l--, r--) {
					if (l == line && r == row) {
						debugWriter.println(line + "行" + row + "列,与第" + i + "个棋子对角线重合");
						return false;
					}
				}
				//右上——>左下斜角测试
				for (int l = p.getL() + 1, r = p.getR() - 1; l < 8 && r >= 0; l++, r--) {
					if (l == line && r == row) {
						debugWriter.println(line + "行" + row + "列,与第" + i + "个棋子对角线重合");
						return false;
					}
				}
				//左下——>右上测试
				for (int l = p.getL() - 1, r = p.getR() + 1; l >= 0 && r < 8; l--, r++) {
					if (l == line && r == row) {
						debugWriter.println(line + "行" + row + "列,与第" + i + "个棋子对角线重合");
						return false;
					}
				}
				i++;
			}
		}
		return true;
		}
		catch(Exception e) {
			System.out.println("\n错误行数：" + line);
			e.printStackTrace();
			return false;
		}
	}
}