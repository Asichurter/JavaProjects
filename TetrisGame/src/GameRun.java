
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

public class GameRun {
	public static void main(String[] args) {
		Thread mythread = new Thread(()->{
			try {
				GameFrame frame = new GameFrame();
			while (true) {
				if (!frame.getIfPause())
					frame.GamePlay();	
				Thread.sleep(250);
				}
			}
			catch (GameException | InterruptedException e) {
				e.printStackTrace();
			}
		});
		mythread.start();
	}
}

/**
 * ���ڱ�����Ϸ���ݴ�����쳣��
 * @author ???
 *
 */
class GameException extends Exception{

	public GameException(String message) {
		super(message);
	}

}


/**
 * ������Ϸ�������
 * @author ???
 *
 */
class GameFrame extends JFrame{
	
	/**
	 * ���1�Ŀ�
	 */
	private GamePanel gamepanel1;
	/**
	 * ���2�Ŀ�
	 */
	private GamePanel gamepanel2;
	/**
	 * �Ƿ���ͣ
	 */
	private boolean ifPause = false;

	public GameFrame() throws GameException {
		this.setTitle("����˹����");
		this.setVisible(true);
		this.setBounds(100, 0, 1360, 810);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		this.gamepanel1 = new GamePanel();
		this.gamepanel2 = new GamePanel();
		JPanel combine = new JPanel();
		combine.setLayout(new GridLayout(1, 2));
		combine.add(gamepanel1);
		combine.add(gamepanel2);
		this.add(combine);
		
		/**
		 * ��Ӱ���������
		 */
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				
				//��ͣʱ���˼�������������������
				if (GameFrame.this.ifPause && e.getKeyCode() != KeyEvent.VK_ESCAPE)
					return;
				
				switch(e.getKeyCode()) {
				case KeyEvent.VK_A:
					gamepanel1.currentMove(true);
					GameFrame.this.repaint();
					break;
				case KeyEvent.VK_LEFT:
					gamepanel2.currentMove(true);
					GameFrame.this.repaint();
					break;
				case KeyEvent.VK_D:
					gamepanel1.currentMove(false);
					GameFrame.this.repaint();
					break;
				case KeyEvent.VK_RIGHT:
					gamepanel2.currentMove(false);
					GameFrame.this.repaint();
					break;
				case KeyEvent.VK_ESCAPE:
					GameFrame.this.ifPause = !GameFrame.this.ifPause;
					break;
				case KeyEvent.VK_SPACE:
					try {
						gamepanel1.rotateCurrent();
						GameFrame.this.repaint();
					}
					catch (CloneNotSupportedException | GameException e1) {
						e1.printStackTrace();
					}
					break;
				case KeyEvent.VK_ENTER:
					try {
						gamepanel2.rotateCurrent();
						GameFrame.this.repaint();
					}
					catch (CloneNotSupportedException | GameException e1) {
						e1.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		});
	}
	
	/**
	 * ����Ƿ���Ϸ����
	 * @return �Ƿ����
	 */
	public boolean getIfGameOver() {
		return this.gamepanel1.getIfGameOver() && this.gamepanel2.getIfGameOver();
	}
	
	/**
	 * ����Ƿ���ͣ
	 * @return �Ƿ���ͣ
	 */
	public boolean getIfPause() {
		return this.ifPause;
	}
	
	/**
	 * ��Ϸ����
	 * @throws GameException ��Ϸ�������׳����쳣
	 */
	public void GamePlay() throws GameException {
		if(!gamepanel1.getIfGameOver())gamepanel1.GameOn();
		if(!gamepanel2.getIfGameOver())gamepanel2.GameOn();
		this.repaint();
	}
	
	public void  HoldOn() {
		//...
	}
}

/**
 * ������ʾ��Ϸ�������
 * @author ???
 *
 */
class GamePanel extends JPanel{
	
	/**
	 * ���ɷ��������
	 */
	private boolean[][] GameBoard;
	/**
	 * ��ǰ�����˶��Ĺ��Ƶ�λ
	 */
	private Tetrimino CurrentOne;
	/**
	 * ��һ�����Ƶ�λ
	 */
	private Tetrimino NextOne;
	/**
	 * 
	 */
	private final int Width = 35;
	/**
	 * �Ƿ���Ϸ����
	 */
	private boolean ifGameOver = false;
	/**
	 * �÷�
	 */
	private int Score = 0;

	/**
	 * ��ʼ������
	 * @throws GameException ��Ϸ�쳣
	 */
	public GamePanel() throws GameException {
		this.GameBoard = new boolean[13][23];
		this.CurrentOne = Tetrimino.getRandomOne();
		this.NextOne = Tetrimino.getRandomOne();
	}
	
	/**
	 * �ڽ��в�����ǰ�����ж���ײ�Ƿ���
	 * @return �Ƿ�����ײ
	 * @throws GameException �����������
	 */
	private boolean testIfCollide() throws GameException {
		for (int i = 0; i <= 3; i++) {
			Point p = CurrentOne.getPoint(i);
			if (p.getY() == 22)																											//�Ƿ񴥵�
				return true;
			else if (p.getX() < 0 || p.getX() > 12 || p.getY() < 0 || p.getY() > 22)
				continue;
			else if (GameBoard[p.getX()][p.getY()]) {											//��������ײ�������Ƶĵ㴦�Ѿ��й���
				return true;																			
			}
		}
		return false;
	}
	
	/**
	 * �ڷ�����ײ�Ժ󣬽���ǰ�ƶ��Ĺ��ƹ̶����������ƶ����ơ��÷�������ü�����еķ���
	 * @throws GameException �����������
	 */
	private void resetCurrentOne() throws GameException {
		for (int i = 0; i <= 3; i++) {
			Point p = CurrentOne.getPoint(i);
			if (p.getY() < 0) {																																			//������̶��Ĺ����е㴦�ڸ�λ�ã�����Ϸ����
				this.ifGameOver = true;
				return;
			}
			GameBoard[p.getX()][p.getY()] = true;																			//�Ƚ����Ƶĵ�̶���������
		}
		int lines = 0;
		for (int i = 0; i <= 3; i++) {
				if (checkAndRemoveLine(CurrentOne.getPoint(i).getY())) {						//�ټ������
					lines++;
					i = 0;
				}
		}
		switch(lines) {																																		//���������������мӷ�
		case 1:
			Score += 10;
			break;
		case 2:
			Score += 40;
			break;
		case 3:
			Score += 90;
			break;
		case 4:
			Score += 160;
			break;
		default:
			break;
		}
		CurrentOne = NextOne;
		NextOne = Tetrimino.getRandomOne();																	//��ˢ�µ�ǰ����
	}
	
	/**
	 * ���һ�в�������
	 * @param index ����������
	 * @return �����Ƿ���ȥ
	 * @throws GameException ����������쳣
	 */ 
	private boolean checkAndRemoveLine(int index) throws GameException {
		try {
			boolean ifAll = true;
			for (int i = 0; i<= 12; i++) {
				if (!GameBoard[i][index])
					ifAll = false;
			}
			if (ifAll) {																					//���һ��ȫ�����е�λ������ȥ�����ƶ�
				for (int i = 0; i <= 12; i++) {
					GameBoard[i][index] = false;								//ȫ����ȥ
				}
				moveLine(index);														//�ƶ�ǰ�����
			}
			return ifAll;
		}
		catch(Exception e) {
			GameException E =  new GameException("�ڼ�鲢������ʱ�������쳣");
			E.initCause(e);
			throw E;
		}
	}
	
	/**
	 * ��index���ϵ��ж������ƶ����������еĸ�������
	 * @param index �ձ���ȥ����
	 */
	private void moveLine(int index) {
		for (int i = index; i >= 1; i--) {
			for (int j = 0; j <= 12; j++) {
				GameBoard[j][i] = GameBoard[j][i-1];
			}
		}
	}
	
	/**
	 * �ǰ���ʱ��Ϸ����
	 * @throws GameException
	 */
	public void GameOn() throws GameException {
		CurrentOne.move(true);
		if (testIfCollide()) {													//�����������ײ��������״̬��ͬʱ����
			CurrentOne.move(false); 								//�Ⱥ��ˣ������Ʒ��ص�ԭ���ƶ�ǰλ��
			resetCurrentOne();											//Ȼ���ٽ����ƹ̶��������ϣ�ͬʱ������в�ˢ����һ������
		}
		//���û�з�����ײ���򲻽������˻�
	}
	
	/**
	 * ��ǰ���������ƶ�
	 * @param dir �ƶ�����
	 */
	public void currentMove(boolean dir) {
		CurrentOne.parallelMove(dir, this.GameBoard);	
	}
	
	/**
	 * ��ת��ǰ����
	 * @throws CloneNotSupportedException ��¡��֧�ֵ��쳣
	 * @throws GameException ����������쳣
	 */
	public void rotateCurrent() throws CloneNotSupportedException, GameException {
		Tetrimino temp = CurrentOne.clone();										//ʹ�ÿ�¡������в���
		temp.rotate();
		for (Point p : temp.Units) {
			if (p.getY() < 0)																													//�ڸ�λ�ò����ܳ����ظ�����������ǰ�غ�
				continue;
			else if (GameBoard[p.getX()][p.getY()])												//���������ת�Ժ����غϣ���ֱ������
				return;
		}
		//�������û���غ�
		CurrentOne.rotate();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		for (int i = 0; i <= 12; i++) {
			for (int j = 0; j <= 22; j++) {
				if (GameBoard[i][j]) {
					g2.setColor(new Color(100, 180, 40));
					g2.fillRect(i*Width, j*Width, Width, Width);
					g2.setColor(Color.black);
					g2.drawRect(i*Width, j*Width, Width, Width);
				}
			}
		}
		CurrentOne.paint(g2, Width);
		NextOne.tipsPaint(g2, 25);
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(20.0F));
		g2.drawString("�÷֣�" + Score, 0, 20);
		g2.drawLine(0, 0, 0, 810);
		g2.drawLine(455, 0, 455, 810);
		if(ifGameOver) {
			g2.setColor(Color.red);
			g2.setFont(g2.getFont().deriveFont(40.0F));
			g2.drawString("GameOver", 200, 390);
		}
	}
	
	public boolean getIfGameOver() {
		return this.ifGameOver;
	}
}

/**
 * �����ĸ���ƵĹ��и���
 * @author ???
 *
 */
abstract class Tetrimino implements Cloneable{

	/**
	 * ���Ƶ���״
	 */
	protected int Shape;
	/**
	 * �����ڵĵ�
	 */
	protected Point[] Units;
	/**
	 * ��������������Ķ���
	 */
	private static Random rand = new Random();

	
	/**
	 * ����һ���µ��ĸ����
	 * @param shape ���Ƶ���״
	 * @param units �����ڵĵ�
	 */
	public Tetrimino(int shape, Point[] units) {
		this.Shape = shape;
		this.Units = units;
	}
	
	/**
	 * ���������ƶ�������ͨ�����ò������в������ƶ�
	 * @param ifAhead ǰ�����Ǻ���
	 */
	public  final void move(boolean ifAhead) {
		for (Point p: Units) {
			p.setY(p.getY() + (ifAhead ? 1 : -1));
		}
	}
	
	/**
	 * ����ƽ���ƶ�
	 * @param ifLeft �Ƿ����󣬷�������
	 * @param b ���ڼ����ת�Ϸ��Ե���������
	 */
	public final void parallelMove(boolean ifLeft, boolean[][] b) {
		for (Point p: Units) {																												//�Ȳ����ƶ�����᲻��Խ��
			if (ifLeft)	{																																
				if (p.getX() == 0)
					return;
			}
			else {
				if (p.getX() == 12)
					return;
			}
		}
		parallelMoveHelper(ifLeft);																					//�������ƶ�
		for (Point p : Units) {
			if (p.getY() < 0)
				continue;
			if (b[p.getX()][p.getY()]) {																						//��������ϸ�λ���е�λ�����ƶ��Ƿ�������֮ǰλ�ú���˳�
				parallelMoveHelper(!ifLeft);
				return;
			}
		}
		//���û���غϣ����ƶ��Ϸ������᷵��֮ǰ��λ��
	}
	
	/**
	 * �����ƶ��ĸ���������ֻ���е����ƶ�
	 * @param ifLeft ����������
	 */
	private final void parallelMoveHelper(boolean ifLeft) {
		for (Point p: Units) {
			p.setX(p.getX() - (ifLeft ? 1 : -1));
		}
	}
	
	/**
	 * ������ת
	 * @throws GameException ��תʱ������X��������Ĵ���
	 */
	public abstract void rotate() throws GameException;
	
	/**
	 * ������ˢ��ʱ�������λ�ã�����һ��������������״���¹���
	 * @return �¹���
	 * @throws GameException �����¹���ʱ��������������Ĵ���
	 */
	public static final Tetrimino getRandomOne() throws GameException {
		int option = rand.nextInt(7)+1;
		switch(option) {
		case 1:
			return I_Tetrimino.getTetriminoOfI();
		case 2:
			return J_Tetrimino.getTetriminoOfJ();
		case 3:
			return L_Tetrimino.getTetriminoOfL();
		case 4:
			return O_Tetrimino.getTetriminoOfO();
		case 5:
			return Z_Tetrimino.getTetriminoOfZ();
		case 6:
			return T_Tetrimino.getTetriminoOfT();
		case 7:
			return S_Tetrimino.getTetriminoOfS();
		default:
			return I_Tetrimino.getTetriminoOfI();
		}
	}
	
	/**
	 * ʹ���±��ù��Ƶ�һ����
	 * @param index
	 * @return
	 */
	public final Point getPoint(int index) throws GameException{
		if (index < 0 || index > 3)
			throw new GameException("���ʹ����еĵ��ʱ���±�����������±�ֵ��" + index);
		else return Units[index];
	}
	
	
	/**
	 * ���
	 */
	@Override
	public Tetrimino clone() throws CloneNotSupportedException {
		Tetrimino temp = (Tetrimino)super.clone();
		temp.Shape = Shape;
		temp.Units = Units.clone();
		return temp;
	}
	
	public void paint(Graphics2D g2, int w) {
		for (Point p : Units) {
			g2.setColor(new Color(40, 75, 220));
			g2.fillRect(p.getX()*w, p.getY()*w, w, w);
			g2.setColor(Color.BLACK);
			g2.drawRect(p.getX()*w, p.getY()*w, w, w);
		}
	}
	
	public void tipsPaint(Graphics2D g2, int w) {
		g2.setFont(g2.getFont().deriveFont(25.0F));
		g2.drawString("��һ�����ƣ�", 480, 50);
		for (int i = 0; i <= 3; i++) {
			g2.setColor(new Color(255, 0, 0));
			g2.fillRect(540+(Units[i].getX()-Units[0].getX())*w, 150+(Units[i].getY()-Units[1].getY())*w, w, w);
			g2.setColor(Color.BLACK);
			g2.drawRect(540+(Units[i].getX()-Units[0].getX())*w, 150+(Units[i].getY()-Units[1].getY())*w, w, w);
		}
	}
}

/**
 * I�͹��Ƶ���
 * @author ???
 *
 */
class I_Tetrimino extends Tetrimino{
	/**
	 * ���ڲ���������Ķ���
	 */
	private static Random rand = new Random();
	
	/**
	 * �����ڹ��������е��õģ����������������һ������
	 * @param shape ������״
	 * @param units ���Ƶĵ�
	 */
	private I_Tetrimino(int shape, Point[] units) {
		super(shape, units);
	}

	/**
	 * <p>�����ⲿ���ã�������״�������һ��I�͹��Ƶķ������������λ�ڶ���</p>
	 * <p>�����ľ����ڲ��ṹ��[0, 1, 2, 3], 1Ϊ��ת����</p>
	 * @return ������ɵ�I�͹���
	 * @throws GameException ���ɹ���ʱ���������X�������
	 */
	public static I_Tetrimino getTetriminoOfI() throws GameException {
		int shape = rand.nextInt(2)+1;
		if (shape == 1) {
			Point firstPoint = new Point(rand.nextInt(10), -1);																																//��һ����������ɣ����ĵ㶼�����һ��������
			Point[] temp = {firstPoint, new Point(firstPoint.getX()+1, -1), new Point(firstPoint.getX()+2, -1), new Point(firstPoint.getX()+3, -1)};
			return new I_Tetrimino(shape, temp);
		}
		else{
			Point firstPoint = new Point(rand.nextInt(13), -1);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -2), new Point(firstPoint.getX(), -3), new Point(firstPoint.getX(), -4)};
			return new I_Tetrimino(shape, temp);
		}
	}
	
	
	/**
	 * I�͹�����ת����ת���ģ�1
	 */
	@Override
	public void rotate() throws GameException {
		if (Shape == 1) {
			Units[0] = new Point(Units[1].getX(), Units[1].getY()-1);	
			Units[2] = new Point(Units[1].getX(), Units[1].getY()+1);
			Units[3] = new Point(Units[1].getX(), Units[1].getY()+2);
			Shape = 2;
		}
		else {
			if (Units[1].getX() <= 0 || Units[1].getX() >= 10)														//��ת����״Խ�磬�Ƿ�
				return;
			else {
				Units[0] = new Point(Units[1].getX()-1, Units[1].getY());
				Units[2] = new Point(Units[1].getX()+1, Units[1].getY());
				Units[3] = new Point(Units[1].getX()+2, Units[1].getY());
				Shape = 1;
			}
		}
	}
	
	/**
	 * ���ǿ�¡
	 */
	@Override
	public I_Tetrimino clone() throws CloneNotSupportedException {
		return (I_Tetrimino)super.clone();
	}
}

/**
 * J�͹���
 * @author ???
 *
 */
class J_Tetrimino extends Tetrimino{
	/**
	 * ���ڲ���������Ķ���
	 */
	private static Random rand = new Random();
	
	/**
	 * �����ڹ��������е��õģ����������������һ������
	 * @param shape ������״
	 * @param units ���Ƶĵ�
	 */
	private J_Tetrimino(int shape, Point[] units) {
		super(shape, units);
	}

	/**
	 * <p>�����ⲿ���ã�������״�������һ��J�͹��Ƶķ������������λ�ڶ���</p>
	 * <p>�����ľ����ڲ��ṹ:</p>
	 * <p>. .             3</p>
	 * <p>.   .           2</p>
	 * <p>0     1</p>
	 * @return ������ɵ�J�͹���
	 * @throws GameException ���ɹ���ʱ���������X�������
	 */
	public static J_Tetrimino getTetriminoOfJ() throws GameException {
		int shape = rand.nextInt(4)+1;																																								//�������һ����ʼ����
		if (shape == 1) {
			Point firstPoint = new Point(rand.nextInt(12), -1);																																//��һ����������ɣ����ĵ㶼�����һ��������
			Point[] temp = {firstPoint, new Point(firstPoint.getX()+1, -1), new Point(firstPoint.getX()+1, -2), new Point(firstPoint.getX()+1, -3)};
			return new J_Tetrimino(shape, temp);
		}
		else if (shape == 2) {
			Point firstPoint = new Point(rand.nextInt(11), -2);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -1), new Point(firstPoint.getX()+1, -1), new Point(firstPoint.getX()+2, -1)};
			return new J_Tetrimino(shape, temp);
		}
		else if (shape == 3) {
			Point firstPoint = new Point(rand.nextInt(12)+1, -3);
			Point[] temp = {firstPoint, new Point(firstPoint.getX()-1, -3), new Point(firstPoint.getX()-1, -2), new Point(firstPoint.getX()-1, -1)};
			return new J_Tetrimino(shape, temp);
		}
		else{
			Point firstPoint = new Point(rand.nextInt(11)+2, -1);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -2), new Point(firstPoint.getX()-1, -2), new Point(firstPoint.getX()-2, -2)};
			return new J_Tetrimino(shape, temp);
		}
	}
	
	
	/**
	 * J�͹�����ת����ת���ģ�1
	 */
	@Override
	public void rotate() throws GameException {
		if (Shape == 1 && Units[1].getX() <= 10) {																		//�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[0] = Units[2];
			Units[2] = new Point(Units[1].getX()+1, Units[1].getY());
			Units[3] = new Point(Units[1].getX()+2, Units[1].getY());
			Shape = 2;
		}
		else if (Shape == 2){
				Units[0] = Units[2];
				Units[2] = new Point(Units[1].getX(), Units[1].getY()+1);
				Units[3] = new Point(Units[1].getX(), Units[1].getY()+2);
				Shape = 3;
		}
		else if (Shape == 3 && Units[1].getX() >= 2){													//�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[0] = Units[2];
			Units[2] = new Point(Units[1].getX()-1, Units[1].getY());
			Units[3] = new Point(Units[1].getX()-2, Units[1].getY());
			Shape = 4;
		}
		else if (Shape == 4 && Units[1].getX() <= 11){													//�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[0] = Units[2];
			Units[2] = new Point(Units[1].getX(), Units[1].getY()-1);
			Units[3] = new Point(Units[1].getX(), Units[1].getY()-2);
			Shape = 1;
		}
	}
	
	/**
	 * ���ǿ�¡
	 */
	@Override
	public J_Tetrimino clone() throws CloneNotSupportedException {
		return (J_Tetrimino)super.clone();
	}
}


/**
 * L�͹���
 * @author ???
 *
 */
class L_Tetrimino extends Tetrimino{
	/**
	 * ���ڲ���������Ķ���
	 */
	private static Random rand = new Random();
	
	/**
	 * �����ڹ��������е��õģ����������������һ������
	 * @param shape ������״
	 * @param units ���Ƶĵ�
	 */
	private L_Tetrimino(int shape, Point[] units) {
		super(shape, units);
	}

	/**
	 * <p>�����ⲿ���ã�������״�������һ��L�͹��Ƶķ������������λ�ڶ���</p>
	 * <p>�����ľ����ڲ��ṹ:</p>
	 * <p>. .             3</p>
	 * <p>.   .           2</p>
	 * <p>.  .  1.  0</p>
	 * @return ������ɵ�L�͹���
	 * @throws GameException ���ɹ���ʱ���������X�������
	 */
	public static L_Tetrimino getTetriminoOfL() throws GameException {
		int shape = rand.nextInt(4)+1;
		if (shape == 1) {
			Point firstPoint = new Point(rand.nextInt(12)+1, -1);																																//��һ����������ɣ����ĵ㶼�����һ��������
			Point[] temp = {firstPoint, new Point(firstPoint.getX()-1, -1), new Point(firstPoint.getX()-1, -2), new Point(firstPoint.getX()-1, -3)};
			return new L_Tetrimino(shape, temp);
		}
		else if (shape == 2) {
			Point firstPoint = new Point(rand.nextInt(11), -1);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -2), new Point(firstPoint.getX()+1, -2), new Point(firstPoint.getX()+2, -2)};
			return new L_Tetrimino(shape, temp);
		}
		else if (shape == 3) {
			Point firstPoint = new Point(rand.nextInt(12), -3);
			Point[] temp = {firstPoint, new Point(firstPoint.getX()+1, -3), new Point(firstPoint.getX()+1, -2), new Point(firstPoint.getX()+1, -1)};
			return new L_Tetrimino(shape, temp);
		}
		else{
			Point firstPoint = new Point(rand.nextInt(11)+2, -2);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -1), new Point(firstPoint.getX()-1, -1), new Point(firstPoint.getX()-2, -1)};
			return new L_Tetrimino(shape, temp);
		}
	}
	

	/**
	 * L�͹�����ת����ת���ģ�1
	 */	
	@Override
	public void rotate() throws GameException {
		if (Shape == 1 && Units[1].getX() <= 10) {																		//�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[2] = Units[0];
			Units[0] = new Point(Units[1].getX(), Units[1].getY()+1);
			Units[3] = new Point(Units[1].getX()+2, Units[1].getY());
			Shape = 2;
		}
		else if (Shape == 2){
				Units[2] = Units[0];
				Units[0] = new Point(Units[1].getX()-1, Units[1].getY());
				Units[3] = new Point(Units[1].getX(), Units[1].getY()+2);
				Shape = 3;
		}
		else if (Shape == 3 && Units[1].getX() >= 2){													//�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[2] = Units[0];
			Units[0] = new Point(Units[1].getX(), Units[1].getY()-1);
			Units[3] = new Point(Units[1].getX()-2, Units[1].getY());
			Shape = 4;
		}
		else if (Shape == 4 && Units[1].getX() <= 11){													//�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[2] = Units[0];
			Units[0] = new Point(Units[1].getX()+1, Units[1].getY());
			Units[3] = new Point(Units[1].getX(), Units[1].getY()-2);
			Shape = 1;
		}
	}
	
	/**
	 * ���ǿ�¡
	 */
	@Override
	public L_Tetrimino clone() throws CloneNotSupportedException {
		return (L_Tetrimino)super.clone();
	}
}


/**
 * O�͹���
 * @author ???
 *
 */
class O_Tetrimino extends Tetrimino{

	/**
	 * ���ڲ���������Ķ���
	 */
	private static Random rand = new Random();
	
	/**
	 * �����ڹ��������е��õģ����������������һ������
	 * @param units ���Ƶĵ�
	 */
	private O_Tetrimino(Point[] units) {
		super(1, units);
	}

	/**
	 * <p>�����ⲿ���ã�������״�������һ��O�͹��Ƶķ������������λ�ڶ���</p>
	 * <p>�����ľ����ڲ��ṹ:</p>
	 * <p>0      1</p>
	 * <p>3      2</p>
	 * @return ������ɵ�O�͹���
	 * @throws GameException ���ɹ���ʱ���������X�������
	 */
	public static O_Tetrimino getTetriminoOfO() throws GameException {
		Point firstPoint = new Point(rand.nextInt(12), -2);
		Point[] temp = {firstPoint, new Point(firstPoint.getX()+1, -2), new Point(firstPoint.getX()+1, -1), new Point(firstPoint.getX(), -1)};
		return new O_Tetrimino(temp);
	}
		/**
	 * O�͹��ƾ��жԳ��ԣ���ת������
	 */
	@Override
	public void rotate() throws GameException {}
	
	/**
	 * ���ǿ�¡
	 */
	@Override
	public O_Tetrimino clone() throws CloneNotSupportedException {
		return (O_Tetrimino)super.clone();
	}
}

/**
 * ���������
 * @author ???
 *
 */
class Point {
	
	/**
	 * ���X����
	 */
	private int X;
	/**
	 * ���Y����
	 */
	private int Y;

	/**
	 * ʹ�����깹��һ����
	 * @param x x����
	 * @param y y����
	 * @throws GameException ��ӵ�ʱ����X�����쳣
	 */
	public Point(int x, int y) throws GameException {
		if (x < 0)
			throw new GameException("X����С��0");
		else if (x > 12)
			throw new GameException("X�������12");
		this.X = x;
		this.Y = y;
	}
	
	/**
	 * ���X�����ֵ
	 */
	public int getX() {
		return this.X;
	}
	
	/**
	 * ���Y�����ֵ
	 */
	public int getY() {
		return this.Y;
	}
	
	/**
	 * ����X����
	 */
	public void setX(int x) {
		this.X = x;
	}
	
	/**
	 * ����Y����
	 */
	public void setY(int y) {
		this.Y = y;
	}

}



/**
 * S�͹��Ƶ���
 * @author ???
 *
 */
class S_Tetrimino extends Tetrimino{
	/**
	 * ���ڲ���������Ķ���
	 */
	private static Random rand = new Random();
	
	/**
	 * �����ڹ��������е��õģ����������������һ������
	 * @param shape ������״
	 * @param units ���Ƶĵ�
	 */
	private S_Tetrimino(int shape, Point[] units) {
		super(shape, units);
	}

	/**
	 * <p>�����ⲿ���ã�������״�������һ��S�͹��Ƶķ������������λ�ڶ���</p>
	 * <p>�����ľ����ڲ��ṹ��</p>
	 * <p>....1      0</p>
	 * <p>3      2</p>
	 * @return ������ɵ�S�͹���
	 * @throws GameException ���ɹ���ʱ���������X�������
	 */
	public static S_Tetrimino getTetriminoOfS() throws GameException {
		int shape = rand.nextInt(2)+1;																																																//�������shape����֤��shape�ĺϷ���
		if (shape == 1) {
			Point firstPoint = new Point(rand.nextInt(11)+2, -2);																																//��һ����������ɣ����ĵ㶼�����һ��������
			Point[] temp = {firstPoint, new Point(firstPoint.getX()-1, -2), new Point(firstPoint.getX()-1, -1), new Point(firstPoint.getX()-2, -1)};
			return new S_Tetrimino(shape, temp);
		}
		else{
			Point firstPoint = new Point(rand.nextInt(12)+1, -1);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -2), new Point(firstPoint.getX()-1, -2), new Point(firstPoint.getX()-1, -3)};
			return new S_Tetrimino(shape, temp);
		}
	}
	
	
	/**
	 * S�͹�����ת����ת���ģ�1
	 */
	@Override
	public void rotate() throws GameException {
		if (Shape == 1) {
			Units[0] = Units[2];
			Units[0] = new Point(Units[1].getX()-1, Units[1].getY());
			Units[3] = new Point(Units[1].getX()-1, Units[1].getY()-1);
			Shape = 2;
		}
		else if(Units[1].getX() >= 1 && Units[1].getX() <= 11) {
				Units[0] = new Point(Units[1].getX()+1, Units[1].getY());
				Units[2] = new Point(Units[1].getX(), Units[1].getY()+1);
				Units[3] = new Point(Units[1].getX()-1, Units[1].getY()+1);
				Shape = 1;
		}
	}
	
	/**
	 * ���ǿ�¡
	 */
	@Override
	public S_Tetrimino clone() throws CloneNotSupportedException {
		return (S_Tetrimino)super.clone();
	}
}



/**
 * T�͹���
 * @author ???
 *
 */
class T_Tetrimino extends Tetrimino{
	/**
	 * ���ڲ���������Ķ���
	 */
	private static Random rand = new Random();
	
	/**
	 * �����ڹ��������е��õģ����������������һ������
	 * @param shape ������״
	 * @param units ���Ƶĵ�
	 */
	private T_Tetrimino(int shape, Point[] units) {
		super(shape, units);
	}

	/**
	 * <p>�����ⲿ���ã�������״�������һ��T�͹��Ƶķ������������λ�ڶ���</p>
	 * <p>�����ľ����ڲ��ṹ:</p>
	 * <p>.   .          3</p>
	 * <p>0.  1      2</p>
	 * @return ������ɵ�T�͹���
	 * @throws GameException ���ɹ���ʱ���������X�������
	 */
	public static T_Tetrimino getTetriminoOfT() throws GameException {
		int shape = rand.nextInt(4)+1;
		if (shape == 1) {
			Point firstPoint = new Point(rand.nextInt(11), -1);																																//��һ����������ɣ����ĵ㶼�����һ��������
			Point[] temp = {firstPoint, new Point(firstPoint.getX()+1, -1), new Point(firstPoint.getX()+2, -1), new Point(firstPoint.getX()+1, -2)};
			return new T_Tetrimino(shape, temp);
		}
		else if (shape == 2) {
			Point firstPoint = new Point(rand.nextInt(12), -3);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -2), new Point(firstPoint.getX(), -1), new Point(firstPoint.getX()+1, -2)};
			return new T_Tetrimino(shape, temp);
		}
		else if (shape == 3) {
			Point firstPoint = new Point(rand.nextInt(11)+2, -2);
			Point[] temp = {firstPoint, new Point(firstPoint.getX()-1, -2), new Point(firstPoint.getX()-2, -2), new Point(firstPoint.getX()-1, -1)};
			return new T_Tetrimino(shape, temp);
		}
		else{
			Point firstPoint = new Point(rand.nextInt(12)+1, -1);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -2), new Point(firstPoint.getX(), -3), new Point(firstPoint.getX()-1, -2)};
			return new T_Tetrimino(shape, temp);
		}
	}
	

	/**
	 * T�͹�����ת����ת���ģ�1
	 */	
	@Override
	public void rotate() throws GameException {
		if (Shape == 1 ) {																		
			Units[0] = Units[3];
			Units[3] = Units[2];
			Units[2] = new Point(Units[1].getX(), Units[1].getY()+1);
			Shape = 2;
		}
		else if (Shape == 2 && Units[1].getX() >= 1 && Units[1].getX() <= 11){                       //�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[0] = Units[3];
			Units[3] = Units[2];
			Units[2] = new Point(Units[1].getX()-1, Units[1].getY());
			Shape = 3;
		}
		else if (Shape == 3){												
			Units[0] = Units[3];
			Units[3] = Units[2];
			Units[2] = new Point(Units[1].getX(), Units[1].getY()-1);
			Shape = 4;
		}
		else if (Shape == 4 && Units[1].getX() >= 1 && Units[1].getX() <= 11){					//�ж���ת��X�����Ƿ����ΪԽ����Ƿ�
			Units[0] = Units[3];
			Units[3] = Units[2];
			Units[2] = new Point(Units[1].getX()+1, Units[1].getY());
			Shape = 1;
		}
	}
	
	/**
	 * ���ǿ�¡
	 */
	@Override
	public T_Tetrimino clone() throws CloneNotSupportedException {
		return (T_Tetrimino)super.clone();
	}
}



/**
 * Z�͹��Ƶ���
 * @author ???
 *
 */
class Z_Tetrimino extends Tetrimino{
	/**
	 * ���ڲ���������Ķ���
	 */
	private static Random rand = new Random();
	
	/**
	 * �����ڹ��������е��õģ����������������һ������
	 * @param shape ������״
	 * @param units ���Ƶĵ�
	 */
	private Z_Tetrimino(int shape, Point[] units) {
		super(shape, units);
	}

	/**
	 * <p>�����ⲿ���ã�������״�������һ��Z�͹��Ƶķ������������λ�ڶ���</p>
	 * <p>�����ľ����ڲ��ṹ��</p>
	 * <p>0      1</p>
	 * <p>....2      3</p>
	 * @return ������ɵ�Z�͹���
	 * @throws GameException ���ɹ���ʱ���������X�������
	 */
	public static Z_Tetrimino getTetriminoOfZ() throws GameException {
		int shape = rand.nextInt(2)+1;
		if (shape == 1) {
			Point firstPoint = new Point(rand.nextInt(11), -2);																																//��һ����������ɣ����ĵ㶼�����һ��������
			Point[] temp = {firstPoint, new Point(firstPoint.getX()+1, -2), new Point(firstPoint.getX()+1, -1), new Point(firstPoint.getX()+2, -1)};
			return new Z_Tetrimino(shape, temp);
		}
		else{
			Point firstPoint = new Point(rand.nextInt(12)+1, -3);
			Point[] temp = {firstPoint, new Point(firstPoint.getX(), -2), new Point(firstPoint.getX()-1, -2), new Point(firstPoint.getX()-1, -1)};
			return new Z_Tetrimino(shape, temp);
		}
	}
	
	
	/**
	 * Z�͹�����ת����ת���ģ�1
	 */
	@Override
	public void rotate() throws GameException {
		if (Shape == 1) {
			Units[2] = Units[0];
			Units[0] = new Point(Units[1].getX(), Units[1].getY()-1);
			Units[3] = new Point(Units[1].getX()-1, Units[1].getY()+1);
			Shape = 2;
		}
		else if(Units[1].getX() >= 1 && Units[1].getX() <= 11) {
				Units[0] = new Point(Units[1].getX()-1, Units[1].getY());
				Units[2] = new Point(Units[1].getX(), Units[1].getY()+1);
				Units[3] = new Point(Units[1].getX()+1, Units[1].getY()+1);
				Shape = 1;
		}
	}
	
	/**
	 * ���ǿ�¡
	 */
	@Override
	public Z_Tetrimino clone() throws CloneNotSupportedException {
		return (Z_Tetrimino)super.clone();
	}
}





