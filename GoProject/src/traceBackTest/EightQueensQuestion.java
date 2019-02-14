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
	
	//�ݹ���õķ���
	private boolean findNextState(int line) {
		//����е��˼��ޣ�����������ݹ����
		if (line >= 8)
			return true;
		//����ݹ�û�н���������Ҫ���ж��Ϸ���λ��
		//���λ�úϷ�������»ʺ󣬲��ҽ�����һ�εݹ�
		for (int i = 0; i < 8; i++) {	//��ÿһ�еĵ�һ�п�ʼѰ�ң�ֱ����������
			//�����λ�úϷ�
			if (line == 0 || testPosition(line, i)){				//�������������֦
				board[i][line] = true;								//���»ʺ�
				allPos[line] = new Point(i, line);
				debugWriter.println(line + "��" + i + "�У����»ʺ�");
				if (!findNextState(line+1)) {						//�жϺ����ĵݹ��Ƿ�ɹ�
					board[i][line] = false;				//����ڷ��»ʺ�󣬺����ݹ鲻�ɹ������ȹ黹�ʺ��ٿ�����һ��
					allPos[line] = null;
					debugWriter.println(line + "��" + i + "�У��ջػʺ�");
				}
				else return true;									//������»ʺ�󣬺����ݹ�ɹ�������ηŻʺ�Ҳ�ǳɹ���
			}
		}
		debugWriter.println(line + "��û�кϷ���λ��");
		return false;							//����������˼��ޣ���û�з��ֺϷ���λ�ã���ôεݹ�ʧ�ܣ���Ҫ����
	}
	
	private boolean testPosition(int line, int row) {
		try {
		int i = 0;
		for (Point p: allPos) {
			if (p == null)
				continue;
			if (p.getR() == row || p.getL() == line) {
				debugWriter.println(line + "��" + row + "��,���" + i + "�����������غ�");
				return false;
			}
			else {
				//�и��򵥵ĺϷ��Բ��Է���
				//���ϡ���>����б�ǲ���
				for (int l = p.getL() + 1, r = p.getR() + 1; l < 8 && r < 8; l++, r++) {
					if (l == line && r == row) {
						debugWriter.println(line + "��" + row + "��,���" + i + "�����ӶԽ����غ�");
						return false;
					}
				}
				//���¡���>���ϲ���
				for (int l = p.getL() - 1, r = p.getR() - 1; l >= 0 && r >= 0; l--, r--) {
					if (l == line && r == row) {
						debugWriter.println(line + "��" + row + "��,���" + i + "�����ӶԽ����غ�");
						return false;
					}
				}
				//���ϡ���>����б�ǲ���
				for (int l = p.getL() + 1, r = p.getR() - 1; l < 8 && r >= 0; l++, r--) {
					if (l == line && r == row) {
						debugWriter.println(line + "��" + row + "��,���" + i + "�����ӶԽ����غ�");
						return false;
					}
				}
				//���¡���>���ϲ���
				for (int l = p.getL() - 1, r = p.getR() + 1; l >= 0 && r < 8; l--, r++) {
					if (l == line && r == row) {
						debugWriter.println(line + "��" + row + "��,���" + i + "�����ӶԽ����غ�");
						return false;
					}
				}
				i++;
			}
		}
		return true;
		}
		catch(Exception e) {
			System.out.println("\n����������" + line);
			e.printStackTrace();
			return false;
		}
	}
}