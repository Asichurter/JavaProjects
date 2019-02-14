package gameFrame;	

import tank_bullet.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import buff.Buff;
import map.MapType;
import map.MyMap;
import reinforceANDsupply.AOE_BulletKit;
import reinforceANDsupply.BulletShieldKit;
import reinforceANDsupply.FireLoadkit;
import reinforceANDsupply.PrecisionAttackKit;
import reinforceANDsupply.RepairToolkit;
import reinforceANDsupply.Supply;
import reinforceANDsupply.SupplyBuilder;
import skill.AOE_Bullet;
import skill.PrecisionAttack;

public class TankFrame extends JFrame {

	private static final long serialVersionUID = 3563627592265775405L;
	//������ʵ����
	private Toolkit tool = Toolkit.getDefaultToolkit();
	public static int WIDTH ;
	public static int HEIGHT;
	private Tank mytank;
	private Container panel;	
	private static Random rand = new Random();
	private ListIterator<EnemyTank> enemyIter; 
	private ListIterator<Bullet> enemyBulletIter;
	private ListIterator<Bullet> myBulletIter;
	private ListIterator<Supply> supplyIter;
	private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
	public static  MyMap MY_MAP;
	private Score MyScore = new Score();

	
	//���ݽṹʵ����
	private ArrayList<EnemyTank> EnemyTanks = new ArrayList<>();                                 												//��������̹�˵�����
	private ArrayList<Bullet>enemyBullets = new ArrayList<>();																								//��������̹���ӵ�������
	private ArrayList<Supply> SUPPLIES = new ArrayList<>();
	private LinkedList<Explosion> Explosions = new LinkedList<>();
	
	//������ʵ����
	private PrecisionAttack PreAtt = null;
	private final int PreAttW = 30;
	private boolean ifPause = false;
	public static int ExplosionReduce = 50;
	private int PosTipsLastTime = 100;
	public static final int W = 20; 																														//̹�˵Ŀ��
	public static final int R1 = 6;																															//̹�˵���̨�뾶	
	private int ENEMY_MAX_NUM;																						//����������
	public static double DEFAULT_V = 4;																												//����Ĭ�ϼ����ٶ�
	public static double DEFAULT_EV = 2;																											//����Ĭ�ϵз��ٶ�
	private int MODIFIED_FT = 50;																													//ÿһ֡�ڵ���װ������Ӧ���ܹ�����ʱ������
	private final int MODIFIED_Y = 2*W ;    																								//���������˵������Ҳ�Ĵ�С���
	private final int MODIFIED_X = W/2;
	private double RE_DIR_FACTOR = 0.01;																								//����ÿһ֡�з�̹��ת��ĸ���
	private int CRASH_BACKSTEP = 3;																											//ÿһ��ײ��ʱ���˵Ĳ���
	private int REPAIRTOOLKIT_NUM = 1;																										//ά�ް����������
	private int FIRELOADTOOLKIT_NUM = 1;																								//��������������
	private int SHEILDKIT_NUM = 1;
	private int AOE_KIT_NUM = 1;
	private int PREATT_KIT_NUM = 1;
	public static boolean ifLoadTimeDecresed = false;																//����̹���Ƿ��Ѿ�ʰȡ���˿����
	private int REPAIRKIT_FACTOR = 200;																									//�������Ƹ��ಹ����ˢ�µĸ������ӣ�p = 1/k
	private int FIRELOADKIT_FACTOR = 600;
	private int SHIELDKIT_FACTOR = 400;
	private int AOEKIT_FACTOR = 1000;
	private int PREATT_FACTOR = 1000;
	private int PRE_ATT_V = 20;
	private final boolean printSize = false;																									//debug��ӡ�ߴ�
	private final String DEFAULT_FILENAME = "Bank";	
	private String FILE_NAME = DEFAULT_FILENAME;
	private String GAME_DIFFICULTY;
	private Component com = new Component();
	
	public TankFrame() {
		//��ʼ����ܻ����ܹ�
		this.panel =this.getContentPane() ;		
		while (!showSimpleMenu()) {}
		readMapAndSetDifficulty();
		this.add(com);	    
		this.setBounds(0,  0, (int) tool.getScreenSize().getWidth(), (int) tool.getScreenSize().getHeight());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle("̹�ˡ��ս");
		this.setIconImage(tool.getImage("Icon/TitleIcon.png"));
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initMyTank();																				//��ʼ���ҵ�̹�˵�λ�ú��ٶ�

			
		//mytank.test();																																														

		//��ʼ��ʵ����
		TankFrame.WIDTH = (int)tool.getScreenSize().getWidth() - MODIFIED_X;
		TankFrame.HEIGHT = (int)tool.getScreenSize().getHeight()  - MODIFIED_Y;
		this.enemyIter = EnemyTanks.listIterator();																	//�з�̹�˵Ķ�д������
		this.enemyBulletIter = enemyBullets.listIterator();												//�з�̹���ӵ��Ķ�д������
		this.myBulletIter = mytank.bullets.listIterator();														//�ҷ�̹�˵��ӵ��Ķ�д������
		this.supplyIter = this.SUPPLIES.listIterator();
		this.initEnemyTanks();																														//��ʼ���з�̹��	

		panel.setBackground(new Color(185, 185, 185));														//����RGB��ɫ
		
		this.buildSupply(RepairToolkit::new);													//���ɳ�ʼ�Ĳ�����
		this.buildSupply(FireLoadkit::new);
		this.buildSupply(BulletShieldKit::new);
		this.buildSupply(AOE_BulletKit::new);
		this.buildSupply(PrecisionAttackKit::new);
		
		//���������ڶ��ƶ���ʱ�����/����/���򣬿��ڼ����ȼ���Ƿ��ܹ����ܣ��ܹ�����ʱ��ͬʱ���ÿ�����ȴʱ��
		this.addKeyListener(new KeyAdapter() { 																
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (mytank.getDir() == Dir.UP)
						mytank.Accelerate(true);																											//�ж�Ϊͬ�򣬼���
					else if (mytank.getDir() == Dir.DOWN)
						mytank.Accelerate(false);																											//�ж�Ϊ���򣬼���
					else mytank.setDir(Dir.UP);																						//�ж�Ϊת��
					break;																																								
				case KeyEvent.VK_DOWN:
					if (mytank.getDir() == Dir.DOWN)
						mytank.Accelerate(true);
					else if (mytank.getDir() == Dir.UP)
						mytank.Accelerate(false);
					else mytank.setDir(Dir.DOWN);
					break;
				case KeyEvent.VK_LEFT:
					if (mytank.getDir() == Dir.LEFT)
						mytank.Accelerate(true);
					else if (mytank.getDir() == Dir.RIGHT)
						mytank.Accelerate(false);
					else mytank.setDir(Dir.LEFT);	
					break;
				case KeyEvent.VK_RIGHT:
					if (mytank.getDir() == Dir.RIGHT)
						mytank.Accelerate(true);
					else if (mytank.getDir() == Dir.LEFT)
						mytank.Accelerate(false);
					else mytank.setDir(Dir.RIGHT);	
					break;
				case KeyEvent.VK_SPACE:																																//̹�˿���
					if(canFire(mytank)) {
						mytank.Fire();
						mytank.setFT(mytank.getLoadFT());																							//���ÿ������ȴʱ��
						TankFrame.this.MyScore.increaseTotalFire();
					}
					break;
				case KeyEvent.VK_Q:
					if (canFire(mytank) && mytank.getAOEBulletNum() > 0) {
						mytank.AOE_Fire();
						mytank.setFT(mytank.getLoadFT());
						TankFrame.this.MyScore.increaseTotalFire();
						//System.out.println("Q���ܷ��䣡\n");
					}
					break;
				case KeyEvent.VK_ESCAPE:
					System.out.println("!");
					if(!ifPause)pauseOrRestartGame(true);
					break;
				case KeyEvent.VK_ENTER:
					System.out.println("!");
					if(ifPause)pauseOrRestartGame(false);
					break;
				case KeyEvent.VK_1:
					if (mytank.getIfHavePreAtt() && TankFrame.this.PreAtt == null) {																					//����о�׼������һ�û�������ľ�׼���
						TankFrame.this.PreAtt = new PrecisionAttack(mytank.getX(), mytank.getY());
						mytank.setIfHavePreAtt(false);
					}
					else if(TankFrame.this.PreAtt != null) {																																							//����Ѿ������˾�׼�������ȡ��
						TankFrame.this.PreAtt = null;
						mytank.setIfHavePreAtt(true);
					}
					break;
				case KeyEvent.VK_R:
					if (TankFrame.this.PreAtt != null)
						TankFrame.this.PreAtt.setExploded(true);
					break;
				case KeyEvent.VK_W:
					if (TankFrame.this.PreAtt != null && !TankFrame.this.PreAtt.getIfExploded()) {
						if (TankFrame.this.PreAtt.getY() >= TankFrame.this.PRE_ATT_V)
							TankFrame.this.PreAtt.setIconXY(TankFrame.this.PreAtt.getX(), TankFrame.this.PreAtt.getY() - TankFrame.this.PRE_ATT_V);
					}
					break;
				case KeyEvent.VK_S:
					if (TankFrame.this.PreAtt != null && !TankFrame.this.PreAtt.getIfExploded()) {
						if (TankFrame.this.PreAtt.getY() + TankFrame.this.PRE_ATT_V <= TankFrame.this.getSize().getHeight())
							TankFrame.this.PreAtt.setIconXY(TankFrame.this.PreAtt.getX(), TankFrame.this.PreAtt.getY() + TankFrame.this.PRE_ATT_V);
					}
					break;
				case KeyEvent.VK_A:
					if (TankFrame.this.PreAtt != null && !TankFrame.this.PreAtt.getIfExploded()) {
						if (TankFrame.this.PreAtt.getX() >= TankFrame.this.PRE_ATT_V)
							TankFrame.this.PreAtt.setIconXY(TankFrame.this.PreAtt.getX() - TankFrame.this.PRE_ATT_V, TankFrame.this.PreAtt.getY());
					}
					break;
				case KeyEvent.VK_D:
					if (TankFrame.this.PreAtt != null && !TankFrame.this.PreAtt.getIfExploded()) {
						if (TankFrame.this.PreAtt.getX() + TankFrame.this.PRE_ATT_V <= TankFrame.this.getSize().getWidth())
							TankFrame.this.PreAtt.setIconXY(TankFrame.this.PreAtt.getX() + TankFrame.this.PRE_ATT_V, TankFrame.this.PreAtt.getY());
					}
					break;
				case KeyEvent.VK_E:
					mytank.brake();
					break;
				default:
						break;
				}																																
			}
		});
																						//��ӻ�ͼ���
		
		/*InputMap imap = com.getInputMap(JComponent.WHEN_FOCUSED);
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),"KeyUp");
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN , 0),"KeyDown");
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),"KeyLeft");
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),"KeyRight");
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,  InputEvent.CTRL_DOWN_MASK), "Red");
		
		ActionMap amap = com.getActionMap();
		amap.put("KeyUp",  new KeyAction(Dir.UP));
		amap.put("KeyDown", new KeyAction(Dir.DOWN));
		amap.put("KeyLeft", new KeyAction(Dir.LEFT));
		amap.put("KeyRight", new KeyAction(Dir.RIGHT));
		amap.put("Red", new AbstractAction() {
			public void actionPerformed(ActionEvent e ) {
				panel.setBackground(Color.RED);
			}
		});*/		
		
		/*this.EnemyTanks.add(new EnemyTank(500, 500, 4));  									//for test...
		this.EnemyTanks.add(new EnemyTank(100, 100, 3));*/
		
		if(printSize)System.out.println(this.getSize().getWidth()+" " + this.getSize().getHeight());
	}
	
	public boolean showSimpleMenu() {
		String[] options = {"��ʼ��Ϸ", "������ʾ", "��Ϸ��ʾ", "������ʾ"};
		int option = JOptionPane.showOptionDialog(
				com, "��ѡ��:", "��ʼ�˵�", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "�˳�");
		switch(option) {
			case 0:
				return true;
			case 1:
				showOpearativeTips();
				return false;
			case 2:
				showGameTips();
				return false;
			case 3:
				showPanelTips();
				return false;
			default:
				return false;
		}
	}
	
	/**
	 * ��ʾ������ʾ
	 */
	public void showPanelTips() {
		JOptionPane.showMessageDialog(com, "��ܵ����ϽǺ����Ͻ�����Ϸ�ľ�����Ϣ��ʾ�����Ƿֱ���:\n"
				+ "���Ͻǣ�\n"
				+ "1.���̹�˵Ļ���ֵ\n"
				+ "2.���̹��Ŀǰ�����ĵ�ͼ����\n"
				+ "3.���̹�˵�ǰӵ�е�Buff����\n"
				+ "4.���̹�˵�ǰӵ�е�AOE����������װ�ؽ��ȣ����AOE����û�дﵽ���޵Ļ���\n"
				+ "5.��ǰ���صĵ�ͼ��������Ϸ�Ѷ�\n"
				+ "6.����Ƿ��о�׼�������\n"
				+ "���Ͻ�:\n"
				+ "1.���̹���ܹ��ݻٵ�̹������\n"
				+ "2.���̹�˵��ڵ�����ɵ����˺�\n"
				+ "3.���̹��ͨ��ײ�����з�̹����ɵ����˺�\n"
				+ "4.���̹��ʰȡ���Ĳ�����������\n"
				+ "5.�з�̹�˶��ҷ�̹����ɵ����˺�\n"
				+ "6.���̹�˵��ڵ���������\n", "������ʾ", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * ��ʾ������ʾ
	 */
	public void showOpearativeTips() {
		JOptionPane.showMessageDialog(com, "������ʾ��\n�����:����̹�˵ķ���任�ͼӼ���\n"
				+ "�ո�:̹�˿�������ܹ�����Ļ���\n"
				+ "Q:����AOE������������㹻�ĵ����ҿ��Կ���Ļ���\n"
				+ "����1:(����/ȡ��)��׼�������׼\n"
				+ "R:��׼�������\n"
				+ "E:ɲ��\n"
				+ "WSAD:��׼�����׼�ƶ�\n"
				+ "Esc:��ͣ��Ϸ\n"
				+ "Enter:������Ϸ\n", "��Ϸ��ʾ", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * ��ʾ��Ϸ��ʾ
	 */
	public void showGameTips() {
		JOptionPane.showMessageDialog(com, "��Ϸ��ʾ��\n1.̹��ӵ�п�����ȴʱ��Ϳ������ƣ�Ĭ�Ͽ�����ȴʱ����4s����ͬ���ο��������ı䣨����ɳĮ����5s����ˮ�в��ܿ���\n"
				+ "2.̹�˺�̹��֮�䣬̹�˺͵���֮����Է�����ײ����˫���ܵ�һ�����˺�ͬʱ���ˣ�����������ٶȾ��������ܶ���ɼ��㡣���ǲ�ͬ����ײ��ʽ\n"
				+ "����ͬ������Ͳ�����ײ�˺�ֵ��һ������ȷ�������ײ�����ڿ�����ȴʱ�Եз����һ�������˺�\n"
				+ "3.̹��ӵ�л���ϵͳ���������͵��˺��������ܵ����׵ĵֵ�����ͬ���λ��ײ�ͬ\n"
				+ "4.̹��ӵ�мӼ���ϵͳ��ͬ�򰴼�Ϊ���٣����򰴼��Ǽ��٣�����Ӽ��ٵĿ����ɼ��ٶȾ�������ͬ�ĵ��μ��ٶȲ�ͬ��̹��������ٶ����ƣ���ͬ����\n"
				+ "����ٶȲ�ͬ\n"
				+ "5.̹�˵�AOE�ڵ��������Ϊ3��������3��ʱ����ķ�һ��ʱ��װ�ص�����AOE�����˺����ߣ����ҿ��Զ�һƬ���������̹�����һ����ɱ��\n"
				+ "������ͨ��ʰȡ����������AOE����\n"
				+ "6.̹�˵ľ�׼���Ҳ�Ƿ�Χ���˺���������Զ������������˺����ߣ���Χ���󣬵���ֻ��ͨ��ʰȡ����������䣬ÿ��̹��ֻ��һ������ʼʱӵ��һ��\n"
				+ "AOE��������װ�׼����ľ�׼����������ǽ�����һ������һ����������ڵ�ͼ��ˢ�¡��з�̹��Ҳ����ʰȡ�����ǲ�һ����Ч��\n"
				+ "8.��ͼ��������ǽ�ڣ�һ�ֿ��Ա��ڵ�����̹��ײ����������һ�ֲ��ܱ�������"
				, "��Ϸ��ʾ", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * ���û���ָ�����ж�ȡ��ͼ��ͬʱѡ����Ϸ�Ѷ�
	 */
	public void readMapAndSetDifficulty() {
		String[] options = {"Default", "Bank", "Road", "Maze", "����"};
		String[] diffi = {"Ӥ��", "����", "Сѧ��", "��ǿ", "ţơ", "����", "�滯"};
		int option = JOptionPane.showOptionDialog(
				com, "��ѡ��Ҫ���صĵ�ͼ������ָ��һ��������ͼ", "ѡ���ͼ", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "Default");
		if (option != 4) {
			MY_MAP = new MyMap(options[option], com);
			FILE_NAME = options[option];
		}
		else {
			String filename = JOptionPane.showInputDialog(com, "������Ҫ�����������ͼ�ļ���(���򽫻����Ĭ�ϵ�ͼ)", "���ص�ͼ", JOptionPane.INFORMATION_MESSAGE);
			if (filename == null) {
				MY_MAP = new MyMap(DEFAULT_FILENAME, com);
				FILE_NAME = DEFAULT_FILENAME;
			}
			else {
				do {
					filename = JOptionPane.showInputDialog(com, "������������ȷ��Ҫ�����������ͼ�ļ���\n(���򽫻����Ĭ�ϵ�ͼ)", "���ص�ͼ", JOptionPane.WARNING_MESSAGE);
					if (filename == null) {
						MY_MAP = new MyMap(DEFAULT_FILENAME, com);
						FILE_NAME = DEFAULT_FILENAME;
					}
					else {
						MY_MAP = new MyMap(filename, com);
						FILE_NAME = filename;
					}
				}while (!MY_MAP.getIfSuccessful());
			}
		}
		int diff = JOptionPane.showOptionDialog(
				com, "ѡ����Ϸ�Ѷȣ��⽫������ĵ������������������У���Ҫ������", "'��Ϸ�Ѷ�", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, diffi, "����");
		switch(diff) {
		case 0:
			ENEMY_MAX_NUM = 1;
			break;
		case 1:
			ENEMY_MAX_NUM = 3;
			break;
		case 2:
			ENEMY_MAX_NUM = 5;
			break;
		case 3:
			ENEMY_MAX_NUM = 7;
			break;
		case 4:
			ENEMY_MAX_NUM = 8;
			break;
		case 5:
			ENEMY_MAX_NUM = 10;
			break;
		case 6:
			ENEMY_MAX_NUM = 15;
			break;
		default:
			ENEMY_MAX_NUM = 5;
			break;
		}
		GAME_DIFFICULTY = diffi[diff];
		JOptionPane.showMessageDialog(
				com, "�뿪ʼ�Ժ���ʹ��shift�����̵�ΪӢ�ļ��̣�����", "��Ҫ����", JOptionPane.WARNING_MESSAGE);
	}
	
	public ListIterator<Explosion> getExplodeIter(){
		return this.Explosions.listIterator();
	}
	
	/**
	 * ���ڽ����еļ��ϵ��������µļ��ɷ���
	 */
	public void updateIter() {
		this.enemyIter = EnemyTanks.listIterator();
		this.enemyBulletIter = enemyBullets.listIterator();
		this.myBulletIter = mytank.bullets.listIterator();
		this.supplyIter = this.SUPPLIES.listIterator();
	}
	
	public boolean getIfPause() {
		return this.ifPause;
	}
	
	
	/**
	 * ������ͣ��������������Ϸ�ķ���
	 * @param ifpause ������������ͣ��Ϸ
	 */
	public void pauseOrRestartGame(boolean ifpause) {
		if (ifpause) {
			ifPause = true;
			JOptionPane.showMessageDialog(this.com, "��Ϸ��ͣ���س���������Ϸ��", "Pause", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			ifPause = false;
			JOptionPane.showMessageDialog(this.com, "��Ϸ������", "Pause", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * ������ʾ��Ϸ��������ʾ������һ������������
	 */
	public void showGameoverMes() {
		JOptionPane.showMessageDialog(com, "��Ϸʧ�ܣ�", "", JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(com, "����ͳ�ƣ�\n" + 
				"�ܴݻ�̹����: " + MyScore.getEliminateNum() + "\n" +
				"�ڵ�������˺�: " + DECIMAL_FORMAT.format(MyScore.getMyHitDamage()) + "\n" + 
				"ײ��������˺�: " + DECIMAL_FORMAT.format(MyScore.getMyCrashDamage()) + "\n" + 
				"ʰȡ���Ĳ���������: " + MyScore.getGainedSupplyNum() + "\n" + 
				"	�з�������˺�: " + DECIMAL_FORMAT.format(MyScore.getEnemyDamage()) + "\n" + 
				"	������: " + DECIMAL_FORMAT.format(MyScore.getTotalFIre()==0 ? 0 : (double)MyScore.getTotalFIreHit()/MyScore.getTotalFIre()*100) + "%", "", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * ͨ�����̹�˵�����״̬�������Ϸ�Ƿ����
	 * @return ��Ϸ�Ƿ����
	 */
	public boolean checkIfGameover() {
		if (mytank.getHealth() <= 0) {
			mytank.setLive(false);
			return false;
		}
		if (!mytank.getLive())
			return false;
		else return true;
	}
	
	/**
	 * ����һ���������Ĵ����ӿڣ��ڵ�ͼ���������һ��ָ���Ĳ�����
	 * @param cons ָ���Ĳ�������Ĺ���ӿ�
	 */
	public void buildSupply(SupplyBuilder<? extends Supply> cons) {
		Supply kit = cons.get();														
		while (!checkIfOverlayed(kit))
			kit = cons.get();
		this.SUPPLIES.add(kit);
	}
	
	//��鱻ʰȡ����Ĳ����������Ƴ����ǣ�ͬʱ�����µĲ�����
	/**
	 * ���ڼ�����еĲ���������������������������ʱ��һ�����ʴ�����������Ĳ������ķ���
	 */
	public void checkAndCreateSupply() {
		int RepairNum = 0;
		int LoadFireNum = 0;
		int ShieldNum = 0;
		int AOENum = 0;
		int PreAttNum = 0;
		for (int i = 0; i< this.SUPPLIES.size(); i++) {											//���Ƴ�״̬Ϊfalse�Ĳ�����
			Supply supply = SUPPLIES.get(i);
			if (!supply.getIsExist())
				this.SUPPLIES.remove(i);
		}
		//int TestNum = SUPPLIES.size();
		for (int i = 0; i< this.SUPPLIES.size(); i++) {											//�ٶԲ������ĸ�������м���
			Supply supply = SUPPLIES.get(i);
			if (supply instanceof RepairToolkit) 
				RepairNum++;
			else if (supply instanceof FireLoadkit) 
				LoadFireNum++;
			else if (supply instanceof BulletShieldKit)
				ShieldNum++;
			else if (supply instanceof AOE_BulletKit)
				AOENum++;
			else if (supply instanceof PrecisionAttackKit)
				PreAttNum++;
		}
		//System.out.println("Ŀǰֻ��" + LoadFireNum + "���������"+RepairNum+"��ά�ް���"+NullNum+"���հ�"+"���ܹ�" +TestNum+"����������"+"������"+i+"��");
		if (RepairNum < this.REPAIRTOOLKIT_NUM && rand.nextInt(this.REPAIRKIT_FACTOR) < 1)  {																	//���֮һ�ĸ���ˢ��ά�ް�
		//	System.out.println("Ŀǰֻ��" + LoadFireNum + "���������"+RepairNum+"��ά�ް���"+NullNum+"���հ�"+"���ܹ�" +TestNum+"����������"+"������"+i+"��"+ "��������������һ���µ�ά�ް�\n");
			this.buildSupply(RepairToolkit::new);
		}
		if (LoadFireNum < this.FIRELOADTOOLKIT_NUM && rand.nextInt(this.FIRELOADKIT_FACTOR) < 1) {
			//System.out.println("Ŀǰֻ��" + LoadFireNum + "���������"+RepairNum+"��ά�ް���"+NullNum+"���հ�"+"���ܹ�" +TestNum+"����������"+"������"+i+"��"+ "��������������һ���µĿ����\n");
			this.buildSupply(FireLoadkit::new);
		}
		if (ShieldNum < this.SHEILDKIT_NUM && rand.nextInt(this.SHIELDKIT_FACTOR) < 1)
			this.buildSupply(BulletShieldKit::new);
		if (AOENum < this.AOE_KIT_NUM && rand.nextInt(this.AOEKIT_FACTOR) < 1)
			this.buildSupply(AOE_BulletKit::new);
		if (PreAttNum < this.PREATT_KIT_NUM && rand.nextInt(this.PREATT_FACTOR) < 1)
			this.buildSupply(PrecisionAttackKit::new);
	}
	
	//�������Ƿ��ӵ����в�
	/**
	 * ���ڼ������̹���Ƿ��ӵ����У�����һ�����������з�������������
	 */
	public void checkAllTanksIfHit() {																											//�������̹�˵�״̬�������ȱʧ�ĵз�̹�ˣ�����checkEnemy_Fresh
		updateIter();																																				//�з�̹���ӵ��Ķ�д���������ҷ�̹�˵��ӵ��Ķ�д������												
		for (EnemyTank tank : EnemyTanks) {
			checkTankIfHit(tank, MyScore);																																	//���з�̹�ˣ�����������״̬
		}
		checkTankIfHit(mytank, MyScore);																																//��鼺��̹�ˣ���������״̬
		 checkEnemy_Fresh();																													//�Եз�̹������״̬���м�飬�������������̹�ˣ�ˢ��																											
	}
	
	//���һ�������̹���Ƿ��ӵ�����
	/**
	 * ���һ��̹���Ƿ񱻻��У������ڱ�����ʱ�����˺��ͱ�ըϵͳ��ͬʱ�Ƴ��ӵ�
	 * @param tank ������̹��
	 */
	public void checkTankIfHit(AbstractTank tank, Score score) {																															//��һ��̹�˵�λ���м�飬����
		if (tank instanceof EnemyTank) {																																						//���̹�˵�λ�ǵз���λ
			//ListIterator<Bullet> iter = mytank.bullets.listIterator();																							//��������̹�˵��ӵ��б�
			//myBulletIter = mytank.bullets.listIterator();
			updateIter();
			while(myBulletIter.hasNext()){														
				Bullet bullet = myBulletIter.next();
				if (bullet instanceof AOE_Bullet) {
					AOE_Bullet Abullet = (AOE_Bullet)bullet;
					if (!Abullet.getIfHit() && Abullet.ifHit(tank)) {																					//���AOE��������Ŀ�����û�б�������
						((AOE_Bullet) bullet).setIfHit(true);																										//��AOE������Ϊ����״̬
							((EnemyTank) tank).setIfHitByAOE(true);											
							this.Explosions.add(new AOE_Explosion(tank.getX(), tank.getY(), 50));					//���AOE��ը����
							this.Explosions.add(new Explosion(tank.getX(), tank.getY(), 50));									//�����ͨ��ը����
							score.increaseTotalFireHit();
					}
					else if (Abullet.getIfHit()) {																														//���AOE����������״̬����������ез�̹��
						for (int i = 0; i < this.EnemyTanks.size(); i++) {
							if (Abullet.ifContains(EnemyTanks.get(i)))																//����з�̹�˴��ڱ������ķ�Χ��
								EnemyTanks.get(i).beHit(Abullet, MyScore);
						}
						myBulletIter.remove();
					}
				}
				else {
					if (Point2D.distance(bullet.getX(), bullet.getY(),tank.getX() , tank.getY()) <= 2*(W+R1)/3) {					//�����ּ���̹�˵��ӵ���з�̹�˵ľ���ﵽ����ֵ��R1+W/2��
						if (!tank.getIfHasBulletShield()) {																																								//���̹���Ƿ��л���
							tank.beHit(bullet, MyScore);																																																			//���õз�̹�˱�����
							this.Explosions.add(new Explosion(bullet.getX(), bullet.getY(), 50));																	//��ԭ�����һ����ըЧ��ͼ��ˢ�·�����50
						}
					else 
						tank.setIfHasBulletShield(false);																																									//�л���ʱ����̹�˵Ļ����Ƴ�
						myBulletIter.remove();																																																				//�Ƴ�����ӵ�
						score.increaseTotalFireHit();
					}
				}
			}
		}
		else if(tank instanceof Tank) {																																											//���̹�˵�λ�Ǽ���̹��
			//ListIterator<Bullet> iter = enemyBullets.listIterator();																											//�Եз�̹�˵��ӵ����е���
			updateIter();																																																						//����ȫ�ֵĵ��������������������ͣ������һ�ε�λ��
			while(enemyBulletIter.hasNext()) {
				Bullet bullet = enemyBulletIter.next();
				if (Point2D.distance(bullet.getX(), bullet.getY(),tank.getX() , tank.getY()) <= 2*(W+R1)/3) {				//�����ֵз�̹�˵��ӵ��뼺���ľ���ﵽ����ֵ
					if(!tank.getIfHasBulletShield()) { 
						tank.beHit(bullet, MyScore);
						this.Explosions.add(new Explosion(bullet.getX(), bullet.getY(), 50));
					}
					else
						tank.setIfHasBulletShield(false);
					enemyBulletIter.remove();																																																			//�Ƴ�����������ӵ�
				}
			}
		}
	}
	
	//�½�һ���з���λ�������ڵз���λ��������и���
	/**
	 * �����ڼ�⵽�з�̹����������ʱ��ˢ�´����µĵз�̹�˵ķ������÷����Ѿ�ʵ���˲�����ˢ��
	 */
	public void buildEnemy() {																																																						//�����µĵз�̹��
		EnemyTank newtank = new EnemyTank(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), DEFAULT_EV);
		while (!checkIfOverlayed(newtank))
			newtank = new EnemyTank(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), DEFAULT_EV);			
		EnemyTanks.add(newtank);
	}
	
	//����Ƿ���ڴ�����������ĵз�̹�ˣ���һ�ַ�������BUG
	/**
	 * ���ڼ��з�̹��״̬�����Ƴ��Ѿ������ĵз�̹��ͬʱˢ�µз�̹��
	 */
	public void checkEnemy_Fresh() {																						//���з�̹�˵�״̬����ˢ�µз�̹�ˣ�������checkTank������buildEnemy
		//ListIterator<EnemyTank> iter = EnemyTanks.listIterator();
		/*enemyIter.next();
		while(enemyIter.hasNext()) {																													//�������з�̹��
			if (enemyIter.next().getLive()) {																														//���з�̹���Ƿ�����
				enemyIter.remove();
				buildEnemy();																																		//�����µ�̹��
			}
		}*/
		//BUG�޸�
		for (int i = 0; i < EnemyTanks.size(); i++) {
			if (!EnemyTanks.get(i).getLive()) {
				EnemyTanks.remove(i);
				buildEnemy();
			}
		}
	}
	
	/**
	 * ���̹��֮��Ĳ�ͬ������ص�����
	 * @param newtank ������̹��
	 * @param identifier -2:������̹��   -1:����̹��   ����[0, +��]���з�̹�ˣ��±�ָ��
	 * @return -2:û���ص�   -1:�뼺��̹���ص�   ����[0, +��]����з��±�Ϊ����ֵ��̹���ص�
	 */
	public int checkIfOverlayed(AbstractTank newtank, int identifier) {														
		if (identifier == -2) {
			for (AbstractTank tank: EnemyTanks) {																																											//�ȼ���Ƿ���з�̹���ص�
				if (Point2D.distance(newtank.getX(), newtank.getY(), tank.getX(), tank.getY()) <=2* (R1+W)/3)
					return EnemyTanks.indexOf(tank);
				}
			if (Point2D.distance(newtank.getX(), newtank.getY() , mytank.getX(), mytank.getY()) <=2*(R1 + W)/3)				//�ټ���Ƿ��뼺��̹���ص�
				return -1;
			for (MapType map: MY_MAP.type_CanNotMove) {
				if (map.ifContains(newtank.getX(), newtank.getY()))
					return -1;
			}
			return -2;
		}
		else if (identifier == -1) {
			for (AbstractTank tank: EnemyTanks) {																																											//�ȼ���Ƿ���з�̹���ص�
				if (Point2D.distance(newtank.getX(), newtank.getY(), tank.getX(), tank.getY()) <=2* (R1+W)/3)
					return EnemyTanks.indexOf(tank);
				}
			return -2;
		}
		else {
			for (int i = 0; i < EnemyTanks.size(); i++) {
				if (identifier == i)																																																				//�����Լ�
					continue;
				else if (Point2D.distance(newtank.getX(), newtank.getY(), EnemyTanks.get(i).getX(),EnemyTanks.get(i).getY()) <= 2*(R1+W)/3)
					return i;
			}
			if (Point2D.distance(newtank.getX(), newtank.getY(), mytank.getX(),mytank.getY()) <= 2*(R1+W)/3)
				return -1 ;
			return -2;
		}
	}
	
	//���ص����ڼ�ⲹ�����Ƿ���������λ�ص�
	/**
	 * 
	 * ���ص����ڼ���Ƿ����ص�����ķ�������������ר�õ�����
	 * @param supply ������Ƿ��ص��Ĳ�����
	 * @return �Ƿ����ص�����
	 */
	public boolean checkIfOverlayed(Supply supply) {
		for (AbstractTank tank: EnemyTanks) {																																											//�ȼ���Ƿ���з�̹���ص�
			if (Point2D.distance(supply.getX(),supply.getY(), tank.getX(), tank.getY()) <=2* (R1+W)/3)
				return false;
			}
		if (Point2D.distance(supply.getX(), supply.getY() , mytank.getX(), mytank.getY()) <=2*(R1 + W)/3)				//�ټ���Ƿ��뼺��̹���ص�
			return false;
		for (MapType map: MY_MAP.type_CanNotMove) {
			if (map.ifContains(supply.getX(), supply.getY()))
				return false;
		}
		if (supply.getX() <= 5*W/4 || supply.getX() >= TankFrame.WIDTH - 5*W/4 || supply.getY() <= 5*W/4 || supply.getY() >= TankFrame.HEIGHT - 5*W/4)						//��ֹˢ�²��������߽粻�ɼ���
			return false;
		return true;
	}
	
	/**
	 * ���ڼ�������ɵĵ�λ�Ƿ���֮ǰ���еĵ�λ���ߵ��η����ص�(����̹�˲���ʹ��)
	 * @param newtank �����������ɵ�̹��
	 * @return false���ص� true��û���ص�
	 */
	public boolean checkIfOverlayed(AbstractTank newtank) {
		for (EnemyTank tank: EnemyTanks) {
			if(Point2D.distance(newtank.getX(), newtank.getY(), tank.getX(), tank.getY()) <= 2* (R1+W)/3 )
				return false;
		}
		if (mytank != null && !(newtank instanceof Tank)) {
			if (Point2D.distance(newtank.getX(), newtank.getY(), mytank.getX(), mytank.getY()) <= 2* (R1+W)/3)
				return false;
		}
		for (MapType map : MY_MAP.type_CanNotMove) {
			if(map.ifContains(newtank.getX(), newtank.getY()))
				return false;
		}
		return true;
	}
	
	/**
	 * ��ʼ�������ҷ�̹�ˣ�����ӱ����غϵļ��
	 */
	public void initMyTank() {
		Random rand = new Random();
		Tank Mytank  = new Tank(rand.nextInt(1500) + 10, rand.nextInt(800) + 10, DEFAULT_V, W, R1);	
		while (!checkIfOverlayed(Mytank)) {
			Mytank  = new Tank(rand.nextInt(1500) + 10, rand.nextInt(800) + 10, DEFAULT_V, W, R1);
		}
		mytank = Mytank;
	}
	
	/**
	 * ���ݵз�̹�˵Ķ��������ʼ������һ�������ĵз�̹��
	 */
	public void initEnemyTanks() {
		for (int i = 1; i <= this.ENEMY_MAX_NUM; i++) {
			this.buildEnemy();
		}
		/*EnemyTank newtank = new EnemyTank(1200, 200, DEFAULT_EV);                                  					//������...
		this.EnemyTanks.add(newtank);
		newtank = new EnemyTank(1200, 240, DEFAULT_EV);
		this.EnemyTanks.add(newtank);
		newtank = new EnemyTank(1200, 280, DEFAULT_EV);
		this.EnemyTanks.add(newtank);*/
	}
	
	//�ƶ������͵ط�̹�ˣ�ͬʱ������õз�̹�˵ķ���
	/**
	 * �ƶ��ҷ������ез�̹�ˡ��÷�����һ��������������ͬʱ����ʵ���ҷ�̹�˵�Buff���á�Buffˢ��
	 * �����ٺ����õз�̹�˵Ĺ���
	 */
	public void moveAllTank() {																														//�ƶ�����̹��
		mytank.allBuffsWork();																																//����̹�˵������湤��
		mytank.checkIfAOEReady( 50); 																										//���AOE�ڵ��Ƿ�Ӧ��װ�����
		moveTank(mytank, false);																																//�ƶ�����̹��
		mytank.restrictVtoMax(); 																											//�ҷ�̹�˵�����	
		resetEnemyDir();
		for (int i  = 0; i < EnemyTanks.size(); i++) {																			//�ƶ��з�̹��
			EnemyTanks.get(i).allBuffsWork();																					//����̹�˵������湤��
			if (EnemyTanks.get(i).getV() < 1)
				EnemyTanks.get(i).reLaunch(TankFrame.DEFAULT_EV, false);					//̹���ٶ��½���һ��ֵʱ��������
			moveTank(EnemyTanks.get(i), false);
			EnemyTanks.get(i).restrictVtoMax();
		}
	}
	
	/**
	 * ��һ���������õط�̹�˵ĵķ���
	 */
	public void resetEnemyDir() {
		for (int i = 0; i < EnemyTanks.size(); i++) {
			if (rand.nextInt(100) < 100*this.RE_DIR_FACTOR)
				EnemyTanks.get(i).setDir(RandomDirProducer());
		}
	}
	
	/**
	 * ���ڵȸ��ʵ��������һ������
	 * @return ������ɵķ���
	 */
	public static Dir RandomDirProducer() {
		switch(rand.nextInt(4) + 1) {
		case 1:
			return Dir.UP;
		case 2:
			return Dir.RIGHT;
		case 3:
			return Dir.DOWN;
		case 4:
			return Dir.LEFT;
			default:
				return Dir.UP;
		}
	}
	
	//�ƶ������ӵ�
	/**
	 * �ƶ����е��ҷ��͵з��ӵ�������һ������������
	 */
	public void moveAllBullet() {																								
		moveBullet(enemyBullets, false);
		moveBullet(mytank.bullets, true);
	}
	
	/**
	 * �ƶ�һ�������е������ӵ�
	 * @param bullets �����ƶ��Ĵ����ӵ��ļ���
	 * @param IfMytank ���ڱ�ʶ�Ƿ����ҷ����ӵ�
	 */
	public void moveBullet(List<Bullet> bullets, boolean IfMytank) {																					//�ƶ�һ��̹�˵������ӵ�
		ListIterator<Bullet> iter;
		updateIter();
		if (IfMytank) {
			iter = myBulletIter;
		}
		else{/* if (tank instanceof EnemyTank)*/																									//����̹�˵��ӵ��ֱ���
			iter = enemyBulletIter;		
		}
		while(iter.hasNext()) {
			Bullet bullet = (Bullet)iter.next();
			if(bulletCanMove(bullet)) {
				switch(bullet.getDir()) {
					case UP:
						bullet.setXY(bullet.getX(), bullet.getY() - Bullet.V);
						iter.set(bullet);
						break;
					case DOWN:
						bullet.setXY(bullet.getX(), bullet.getY() + Bullet.V);
						iter.set(bullet);
						break;
					case LEFT:
						bullet.setXY(bullet.getX() - Bullet.V, bullet.getY());
						iter.set(bullet);
						break;
					case RIGHT:
						bullet.setXY(bullet.getX() + Bullet.V, bullet.getY());
						iter.set(bullet);
						break;
					default:
						break;
				}
			}
		}
	}
	
	//����̹�˵ķ�����ٶȸı�̹�˵����꣬�ﵽ���ƶ���Ŀ��
	/**
	 * �ƶ�һ��̹��
	 * @param mytank2 ���ƶ���̹��
	 * @param ifTest ��ʶ�Ƿ���в��ԡ�����ǣ��������ӵ������ƺ���ײ�����ڲ�����ײ�������ǲ���ˢ�µ���
	 */
	public void moveTank(AbstractTank mytank2, boolean ifTest) {																										//�ƶ�һ��̹��
			switch(mytank2.getDir()) {		
			case UP:
				if (ifTest || canMove(mytank2)) 
					mytank2.setXY(mytank2.getX(), mytank2.getY() - (int)mytank2.getV());
				break;
				
			case DOWN:
				if (ifTest || canMove(mytank2))
					mytank2.setXY(mytank2.getX(), mytank2.getY() + (int)mytank2.getV());
				break;
				
			case LEFT:
				if (ifTest || canMove(mytank2))
					mytank2.setXY(mytank2.getX() - (int)mytank2.getV(), mytank2.getY());
				break;
				
			case RIGHT:
				if (ifTest || canMove(mytank2))
					mytank2.setXY(mytank2.getX() + (int)mytank2.getV(), mytank2.getY());
				break;			
				default:
					break;
			}
			if (!ifTest)
				mytank2.refreshMapType(MY_MAP);
	}
	
	/**
	 * �������̹�˵���ײ���������һ������������
	 */
	public void checkAllCrash() {
		checkCrash(mytank);
		/*for (EnemyTank tank: EnemyTanks) {
			checkCrash(tank);
			if (tank.getHealth() <= 0) {
				EnemyTanks.remove(tank);
				this.buildEnemy();
			}
		}*/
		for (int i = 0; i < EnemyTanks.size(); i++) {
			checkCrash(EnemyTanks.get(i));
			if (EnemyTanks.get(i).getHealth() <= 0) {
				EnemyTanks.remove(i);
				this.buildEnemy();
			}
		}
	}
	
	/**
	 * ��������̹�˵���ײ״̬������һ���������������������µĻغ���ˢ��
	 */
	public void freshAllCrash() {
		mytank.setIfCrashed(false);
		for (EnemyTank tank: EnemyTanks) {
			tank.setIfCrashed(false);
		}
	}
	
	/**
	 * ���̹���Ƿ���ʰȡ��������
	 * @param tank ������̹��
	 */
	public void checkIfCrashWithSupply(AbstractTank tank) {
		this.supplyIter = this.SUPPLIES.listIterator();
		while (supplyIter.hasNext()) {
			Supply supply = supplyIter.next();
			if (supply.ifContains(tank.getX(), tank.getY()) && supply.getIsExist()) {
				supply.setIsExist(false);
				if (tank instanceof Tank)
					MyScore.setGainedSupplyNum(MyScore.getGainedSupplyNum() + 1);
				if (supply instanceof FireLoadkit) {
					if (tank instanceof Tank && !TankFrame.ifLoadTimeDecresed) {
						tank.addBuff(supply.getBuff());
						TankFrame.ifLoadTimeDecresed = true;
					}
				}
				else if (supply instanceof RepairToolkit)
					tank.addBuff(supply.getBuff());
				else if (supply instanceof BulletShieldKit) {																						//̹��û�л���ʱ���Ż�Ϊ̹����װ����
					if (!tank.getIfHasBulletShield())
						tank.addBuff(supply.getBuff());
				}
				else if (supply instanceof AOE_BulletKit) {
					if (tank instanceof Tank)
						((Tank) tank).setAOEBulletNum(Tank.AOE_BULLET_TOTAL);
				}
				else if (supply instanceof PrecisionAttackKit) {
					if (tank instanceof Tank)
						((Tank)tank).setIfHavePreAtt(true);
				}
			}
		}
	}
	
	//���һ��̹���Ƿ��������г�ײ�����ȼ�鼺��̹���ټ��з�̹��
	/**
	 * ���һ��̹���Ƿ�������̹������ײ�������������Ѿ�������ײ��������������ظ���ײ��⡣�����˶Բ�����ʰȡ�ļ��
 	 * @param tank ������̹��
	 */
	public void checkCrash(AbstractTank tank) {
		checkIfCrashWithSupply(tank);
		if (tank.getIfCrashed())
			return;  
		if (tank instanceof Tank) {																							//���ж��Ƿ���̹��֮��ĳ�ײ
			this.moveTank(tank, true);
			int identifier = checkIfOverlayed(tank, -1);	
			double tempV = tank.getV();
			if (identifier != -2) {																											//���������з������˳�ײ����
				tank.Back(CRASH_BACKSTEP, false);
				switch(Dir.checkCrashDir(tank.getDir(), EnemyTanks.get(identifier).getDir())) {												//��һ��Ϊײ����λ�ķ��򣬵ڶ���Ϊ��ײ��λ�ķ���
					case 0:
						tank.Crash(0, true, MyScore);																																//��ײ��̹�˲�������ٶ�
						EnemyTanks.get(identifier).Crash(tempV, true, MyScore);									//����ײ��̹�˽����ܵ�+������ٶ�
						break;
					case 1:
						tank.Crash(-(EnemyTanks.get(identifier).getV()), true, this.MyScore);
						EnemyTanks.get(identifier).Crash(-(tempV), true, MyScore);
						break;
					case 2:
						tank.Crash(EnemyTanks.get(identifier).getV(), true, MyScore);
						EnemyTanks.get(identifier).Crash(tempV, true, MyScore);
						break;
				}
			}
			else tank.Back(1, true);
		}
		else{																																																	//����з���з������˳�ײ
			this.moveTank(tank, true); 
			int identifier = checkIfOverlayed(tank, EnemyTanks.indexOf(tank)); 
			double tempV = mytank.getV();
			if (identifier == -1) {
				tank.Back(CRASH_BACKSTEP, false);
				switch(Dir.checkCrashDir(tank.getDir(), mytank.getDir())) {
				case 0:
					mytank.Crash(tank.getV(), true, MyScore);
					tank.Crash(0, true, MyScore);
					break;
				case 1:
					mytank.Crash(-(tank.getV()), true, MyScore);
					tank.Crash(-(tempV), true, MyScore);
					break;
				case 2:
					mytank.Crash(tank.getV(), true, MyScore);
					tank.Crash(tempV, true, MyScore);
					break;
			}
			}
			else if (identifier != -2) {																																//�з�̹��֮���ײ�������˺�
				tank.Back(CRASH_BACKSTEP, false);
				tank.Crash(-1, false, MyScore);
				EnemyTanks.get(identifier).Crash(-1, false, MyScore);
			 }
			else tank.Back(1, true);
		 }
	}
	
	//�ж��ܷ�����ƶ�
	/**
	 * ������ײ��������Ρ�����������ٶ��Ƿ�Ϊ0�ж�̹���ܷ�����ƶ�
	 * @param mytank2 ������̹��
	 * @return �Ƿ��ܹ��ƶ�
	 */
	public boolean canMove(AbstractTank mytank2) {																																
		//�ж��Ƿ��Ѿ���������ײ��
		if (mytank2.getIfCrashed())
			return false;
		
		//�ж��Ƿ�Ҫ�ƶ�������Խ������
		moveTank(mytank2, true);
		for (MapType map : TankFrame.MY_MAP.type_CanNotMove) {																//for each ѭ�������ܻ���ֲ�������
			if (map.ifContains(mytank2.getX(), mytank2.getY())) {
				mytank2.Back(this.CRASH_BACKSTEP, false);
				map.Crash(mytank2.getV());
				mytank2.Crash(0, true, MyScore);
				return false;
			}
		}
		mytank2.Back(1, true);
		
		//�ж��Ƿ����
		switch(mytank2.getDir()) {
			case UP:
				if (mytank2.getY() <= 5*W/4) {																													//���ж��Ƿ�ײǽ					
					if (mytank2 instanceof EnemyTank)																						//���ж��Ƿ��Ǽ���̹��
						((EnemyTank) mytank2).reLaunch(DEFAULT_EV, true);
					else {
						mytank2.Crash(0, true, MyScore);																														//����Ǽ���̹�ˣ���Crash
					}
					return false;
				}
				break;
			case DOWN:
				if(mytank2.getY() + 5*W/4 + MODIFIED_Y >= tool.getScreenSize().getHeight()) {
					if (mytank2 instanceof EnemyTank)
						((EnemyTank) mytank2).reLaunch(DEFAULT_EV, true);
					else {
						mytank2.Crash(0, true, MyScore);					
					}
					return false;
				}
				break;
			case LEFT:
				if (mytank2.getX() - 5*W/4 <= 0) {					
					if (mytank2 instanceof EnemyTank)
						((EnemyTank) mytank2).reLaunch(DEFAULT_EV, true);
					else {
						mytank2.Crash(0, true, MyScore);																																//���ײǽ������Ϊcrash������˺�
					}
					return false;
				}
				break;
			case RIGHT:
				if (mytank2.getX() + 5*W/4 + MODIFIED_X >= tool.getScreenSize().getWidth()) {
					if (mytank2 instanceof EnemyTank)
						((EnemyTank) mytank2).reLaunch(DEFAULT_EV, true);
					else {
						mytank2.Crash(0, true, MyScore);					
					}
					return false;
				}
				break;
			
			default:
					break;
		}
		if (mytank2.getV() != 0)
			return true;
		return false;
	}
	
	/**
	 * �з�̹���������ķ���������һ������������
	 */
	public void enemyRandomFire() {
		for (EnemyTank tank: EnemyTanks) {
			if (tank.getFT() == 0 && tank.getMapType().getIfCanFire()) {
				Bullet bullet = tank.Fire();
				bullet.setDamage(tank.getBulletDamage());
				enemyBullets.add(bullet);
				tank.setFT(tank.getLoadFT());
			}
		}
	}
	
	/**
	 * ����̹�˿������ȴʱ�䣬�ж��Ƿ��ܹ����п���
	 * @param tank ���ж��ܷ񿪻��̹��
	 * @return �Ƿ��ܹ�����
	 */
	public boolean canFire(AbstractTank tank) {
		return tank.getFT() == 0  && tank.getMapType().getIfCanFire();
	}
	
	/**
	 * ˢ��̹�˿�����ȴʱ��ķ�����ˢ��Ƶ����MODIFIED_FT������������MODIFIED_FT���ܱ�LOAD_FT����������
	 * @param tank
	 */
	public void reSetFT(AbstractTank tank) {																															//ÿһ֡������̹�˵Ŀ���ʱ��
			tank.setFT(tank.getFT() - MODIFIED_FT);
			if (tank.getFT() < 0)
				tank.setFT(0);
	}
	
	/**
	 * ˢ������̹�˿���ʱ��ķ���������һ������������
	 */
	public void freshAllFT() {
		reSetFT(mytank);
		updateIter();
		while(enemyIter.hasNext()) {
			reSetFT(enemyIter.next());
		}
		//����д
	}
	
	//�ж��ӵ��ܷ��ƶ�
	public boolean bulletCanMove(Bullet bullet) {																						
		//����д
		return true;
	}
	
	/**
	 * ����ӵ��Ƿ�����ײ��������Խ���λ��߳����������������������ò�����Խ���ε����ӵ���ײ�ķ���
	 */
	public void checkAllBullets() {																																//��������ӵ�
		mytank.removeMyBullet(this.getExplodeIter());
		/*updateIter();
		while (enemyBulletIter.hasNext()) {
			Bullet bullet = enemyBulletIter.next();																											//���������ñ���
			if (checkIfBulletOut(bullet))
				enemyIter.remove();
		}*/	
		for (int i = 0; i< enemyBullets.size(); i++) {
			if (checkIfBulletCrashWithMap(enemyBullets.get(i))) {
				Bullet bullet = enemyBullets.get(i);
				this.Explosions.add(new Explosion(bullet.getX(), bullet.getY(), 50));
				enemyBullets.remove(i);
			}
			else if (checkIfBulletOut(enemyBullets.get(i))) 
				enemyBullets.remove(i);
		}
		checkPreAtt();
	}
	
	/**
	 * ��龫׼����Ƿ�����
	 */
	public void checkPreAtt() {
		if (this.PreAtt != null) {
			if (PreAtt.getIfExploded()) {
				PreAtt.Explod(EnemyTanks, MyScore, this.Explosions);
				PreAtt = null;
			}
		}
	}
	
	/**
	 * ����ӵ��Ƿ����
	 * @param bullet �������ӵ�
	 * @return �Ƿ����
	 */
	public boolean checkIfBulletOut(Bullet bullet) {
		if (bullet.getX() < 0 || bullet.getX() >TankFrame. WIDTH || bullet.getY() < 0 || bullet.getY() > TankFrame.HEIGHT)
			return true;
		else return false;
	}
	
	/**
	 * ����ӵ��Ƿ��벻����Խ���η�����ײ�������������ͬʱ���ò�����Խ�������ӵ���ײ�ķ���
	 * @param bullet �������ӵ�
	 * @return �Ƿ�����ײ����
	 */
	public boolean checkIfBulletCrashWithMap(Bullet bullet) {
		for (MapType map : TankFrame.MY_MAP.type_CanNotMove) {
			if (map.ifContains(bullet.getX(), bullet.getY())) {
				map.Crash(bullet);
				return true;
			}
		}
		return false;
	}
	
	/*class KeyAction extends AbstractAction{
		private Dir dir;
		
		public KeyAction(Dir dir) {
			this.dir = dir;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			mytank.setDir(dir);
		}
	}*/
	
	//�滭���
	class Component extends JComponent{																													//���ڻ�ͼ�����

		private static final long serialVersionUID = -8198036568671194725L;

		@Override
		public void paintComponent(Graphics g) { 																										//���û��Ƶķ���
			Graphics2D g2 = (Graphics2D)g;
			paintMap(g2);
			paintAllSupplies(g2);
			paintTank(g2, mytank, Color.BLACK);
			
			//��д����̹�˵�Buff�������Ͻ�,��д����ͳ�������Ͻ�
			paintInfo(g2, mytank);
			
			paintHealth(g2, mytank);
			paintExplosion(g2);
			paintBullet(g2, mytank);
			for (Bullet bullet: enemyBullets) {
				paintBullet(g2, bullet);
			}
			paintFT(g2, mytank);
			for (EnemyTank tank: EnemyTanks) {
				paintTank(g2, tank, Color.WHITE);
				paintHealth(g2, tank);
				paintPrecisionAttack(g2);
			}
			paintPosTips(g2);
		}
		
		public void paintPosTips(Graphics2D g2) {
			if(PosTipsLastTime > 0) {
				g2.drawImage(tool.getImage("Icon/λ����ʾ.png"), mytank.getX()-5*Tank.W/2, mytank.getY()- 15*Tank.W/2, 6*Tank.W, 6*Tank.W, null);
				PosTipsLastTime--;
			}
		}
		
		/**
		 * �������̹�˵���Ϣ
		 * @param g2 �滭Ԫ��
		 * @param tank �ҷ�̹��
		 */
		public void paintInfo(Graphics2D g2, AbstractTank tank) {
			g2.drawString("����ֵ:" + mytank.getArmor(), 0, 15);
			g2.drawString("��ͼ����:" + mytank.getMapType().toString(), 0, 30);
			ListIterator<Buff>iter = tank.getBuffsIter();
			StringBuilder string = new StringBuilder();
			string.append("Buff: ");
			if (!tank.getBuffsIter().hasNext())
				string.append("��");
			else while (iter.hasNext()) 
				string.append(iter.next().getDes() + " ");
			g2.drawString(string.toString(), 0, 45);
			g2.drawString("����ͳ�ƣ�", 1400, 15);
			g2.drawString("�ܴݻ�̹����: " + MyScore.getEliminateNum(), 1400, 30);
			g2.drawString("�ڵ�������˺�: " + DECIMAL_FORMAT.format(MyScore.getMyHitDamage()), 1400, 45);
			g2.drawString("ײ��������˺�: " + DECIMAL_FORMAT.format(MyScore.getMyCrashDamage()), 1400, 60);
			g2.drawString("ʰȡ���Ĳ���������: " + MyScore.getGainedSupplyNum(), 1400, 75);
			g2.drawString("�з�������˺�: " + DECIMAL_FORMAT.format(MyScore.getEnemyDamage()), 1400, 90);
			g2.drawString("������: " + DECIMAL_FORMAT.format(MyScore.getTotalFIre()==0 ? 0 : (double)MyScore.getTotalFIreHit()/MyScore.getTotalFIre()*100) + "%", 1400, 105);
			g2.drawString("AOE��������: "+ mytank.getAOEBulletNum() + "  װ�ؽ���: " + mytank.getAOEtime(), 0, 60);
			g2.drawString("��ͼ����" + FILE_NAME + " ��Ϸ�Ѷ�: " + GAME_DIFFICULTY, 0, 75);
			g2.drawString("�Ƿ��о�׼�����" + (mytank.getIfHavePreAtt() ? "��" : "��"), 0, 90);
			
		}
		
		/**
		 * ���Ƶ�ͼ
		 * @param g2 �滭Ԫ��
		 */
		public void paintMap(Graphics2D g2) {
			try {
			ListIterator<LinkedList<MapType>> allIter = MY_MAP.allTypes.listIterator();
			while(allIter.hasNext()) {
				ListIterator<MapType> iter = allIter.next().listIterator();
				while (iter.hasNext()) {
					MapType temp = iter.next();
					g2.drawImage(temp.getIcon(), temp.getX() - MapType.W/2, temp.getY() - MapType.W/2, MapType.W, MapType.W, null);
				}
			}
			ListIterator<MapType> Uiter = MY_MAP.type_CanNotMove.listIterator();
			while (Uiter.hasNext()) {
				MapType temp = Uiter.next();
				g2.drawImage(temp.getIcon(), temp.getX() - MapType.W/2, temp.getY() - MapType.W/2, MapType.W, MapType.W, null);
			}
			}
			catch(NullPointerException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		/**
		 * ����Ѫ����
		 * @param g2 �滭Ԫ��
		 * @param tank �����Ƶ�̹��
		 */
		public void paintHealth(Graphics2D g2, AbstractTank tank) {
			//g2.drawString("" + tank.getHealth(), tank.getX() - 4*W/5, tank.getY() -5*W/2);											//���������
			if (tank instanceof Tank) {
				g2.setColor(Color.RED);
				g2.drawRect( tank.getX() - 4*W/5, tank.getY() -9*W/4, tank.getHealthWidth(), AbstractTank.HEALTH_HEIGHT);
				g2.fillRect( tank.getX() - 4*W/5, tank.getY() -9*W/4, (int)(tank.getHealthWidth() * (tank.getHealth()/Tank.TANK_FULLHEALTH)), AbstractTank.HEALTH_HEIGHT);
			}
			else {
				int Ww = EnemyTank.W;
				g2.setColor(Color.BLACK);
				g2.drawRect( tank.getX() - 4*Ww/5, tank.getY() -7*Ww/4, tank.getHealthWidth(), AbstractTank.HEALTH_HEIGHT);
				g2.fillRect(  tank.getX() - 4*Ww/5, tank.getY() -7*Ww/4, (int)(tank.getHealthWidth() * (tank.getHealth()/EnemyTank.ENEMY_FULLHEALTH)), AbstractTank.HEALTH_HEIGHT);
			}
		}
		
		/**
		 * �������еĲ�����
		 * @param g2 �滭Ԫ��
		 */
		public void paintAllSupplies(Graphics2D g2) {
			updateIter();
			while (supplyIter.hasNext()) {
				Supply supply = supplyIter.next();
				g2.drawImage(supply.getImage(), supply.getX() - Supply.W/2, supply.getY() - Supply.W/2, Supply.W, Supply.W, null);
			}
		}
		
		/**
		 * ���ƾ�׼�����׼��
		 * @param g2 �滭Ԫ��
		 */
		public void paintPrecisionAttack(Graphics2D g2) {
			if (PreAtt != null) {
				g2.drawImage(PreAtt.getIcon(), PreAtt.getX()-PreAttW/2, PreAtt.getY()-PreAttW/2, PreAttW, PreAttW, null);
			}
		}
		
		/**
		 * ���Ʊ�ըЧ��
		 * @param g2 �滭Ԫ��
		 */
		public void paintExplosion(Graphics2D g2) {
			ListIterator<Explosion> iter = Explosions.listIterator();
			while (iter.hasNext()) {
				Explosion explosion = iter.next();
				if (explosion.getNowTime() <= 0)
					iter.remove();
				else {
					explosion.refreshShowTime();
					if (explosion instanceof AOE_Explosion)
						((AOE_Explosion) explosion).Show(g2);
					else g2.drawImage(explosion.getImage(), explosion.getX() - Explosion.W/2, explosion.getY() - Explosion.W/2, Explosion.W, Explosion.W, null);
				}
			}
		}
		
		/**
		 * �����ҷ�̹�˵��ӵ�
		 * @param g2 �滭Ԫ��
		 * @param tank �ҷ�̹��
		 */
		public void paintBullet(Graphics2D g2, Tank tank) {																			
				updateIter();	
				int Ww, Hh;
				while(myBulletIter.hasNext()) {
					Bullet bullet = myBulletIter.next();
					if (bullet instanceof AOE_Bullet) {
						Ww = AOE_Bullet.W;
						Hh = AOE_Bullet.H;
					}
					else {
						Ww = Bullet.WIDTH;
						Hh = Bullet.HEIGHT;
					}
					switch(bullet.getDir()){
						case UP:
						case DOWN:
							g2.drawImage(bullet.getImage(), bullet.getX() - Hh/2, bullet.getY() - Ww/2, Hh, Ww, null);
							break;
						case LEFT:
						case RIGHT:
							g2.drawImage(bullet.getImage(), bullet.getX() - Ww/2, bullet.getY() - Hh/2, Ww, Hh, null);
							break;
					}
					//g2.fillRect(bullet.getX()-R1/3, bullet.getY() - W/8, 2*R1/3, W/4);
				}
		}
		
		/**
		 * ���صĻ����ӵ��ķ��������ڻ��Ƶз�̹�˵��ӵ�
		 * @param g2 �滭Ԫ��
		 * @param bullet �����Ƶ��ӵ�
		 */
		public void paintBullet(Graphics2D g2, Bullet bullet) {
			switch(bullet.getDir()){
			case UP:
			case DOWN:
				g2.drawImage(bullet.getImage(), bullet.getX() - Bullet.HEIGHT/2, bullet.getY() - Bullet.WIDTH/2, Bullet.HEIGHT, Bullet.WIDTH, null);
				break;
			case LEFT:
			case RIGHT:
				g2.drawImage(bullet.getImage(), bullet.getX() - Bullet.WIDTH/2, bullet.getY() - Bullet.HEIGHT/2, Bullet.WIDTH, Bullet.HEIGHT, null);
				break;
		}
			//int Ww = EnemyTank.W;
			//int r1 = EnemyTank.R1;
			//g2.setColor(Color.BLACK);
			//g2.fillRect(bullet.getX()-R1/3, bullet.getY() - Ww/8, 2*r1/3, Ww/4);
		}
		
		/**
		 * ���ƿ�����ȴʱ��ķ���
		 * @param g2 �滭Ԫ��
		 * @param tank �����Ƶ�̹�ˡ�����Ϊֻ���ҷ�̹�˵���ȴʱ��Żᱻ����
		 */
		public void paintFT(Graphics2D g2, Tank tank) {															
			if (tank.getFT() == 0) {
				//do nothing...
			}
			else if ( tank.getFT() >= 7*tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��1.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6,3*W/4, 3*W/4,  null);
			}
			else if(tank.getFT() < 7*tank.getLoadFT()/8 && tank.getFT() >= 3*tank.getLoadFT()/4) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��2.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < 3*tank.getLoadFT()/4 && tank.getFT() >= 5*tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��3.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < 5*tank.getLoadFT()/8 && tank.getFT() >= tank.getLoadFT()/2) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��4.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6,3*W/4, 3*W/4,  null);
			}
			else if(tank.getFT() < tank.getLoadFT()/2 && tank.getFT() >= 3*tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��5.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < 3*tank.getLoadFT()/8 && tank.getFT() >= tank.getLoadFT()/4) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��6.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6,3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < tank.getLoadFT()/4 && tank.getFT() >=tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��7.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < tank.getLoadFT()/8 && tank.getFT() > 0) {
				g2.drawImage(tool.getImage("LOADFT/��ȴʱ��8.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
		}
		
		/**
		 * ����̹�˵ĳ������̹��
		 * @param g2 �滭Ԫ��
		 * @param tank �����Ƶ�̹��
		 * @param color ������ɫ
		 */
		public void paintTank(Graphics2D g2, AbstractTank tank, Color color) {			
			int Ww;
			int r1;
			if (tank instanceof Tank) {
				Ww = W;
				r1 = R1;
			}
			else {
				Ww = EnemyTank.W;
				r1 = EnemyTank.R1;
			}
			if(tank.getIfHasBulletShield()) {
				g2.setColor(new Color(200, 240, 240));
				g2.drawOval(tank.getX() - 3*Ww/2, tank.getY()- 3*Ww/2, 3*Ww, 3*Ww);
			}
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Sarif", Font.BOLD, 10));
			if (tank instanceof Tank) 
				g2.drawString(DECIMAL_FORMAT.format(tank.getV()) + "m/s", tank.getX() - 2*Ww/3, tank.getY() -5*Ww/4 );
			g2.setColor(color);
			switch(tank.getDir()) {				
			
			case UP:
					g2.drawRect(tank.getX() - Ww/2, tank.getY() - Ww/2, Ww, Ww);                                                                                                                            //�����Ͻ�Ϊ���꣬��̹������
					g2.fillRect(tank.getX() - Ww/2 - Ww/3, tank.getY() - Ww/2 - Ww/4, Ww/3,  Ww + Ww/2);																				//�������Ĵ�
					g2.fillRect(tank.getX() + Ww/2, tank.getY() - Ww/2 - Ww/4, Ww/3, Ww + Ww/2);
					g2.drawOval(tank.getX() - r1 , tank.getY() - r1 , 2*r1, 2*r1);                     																									//���м����̨
					g2.fillRect(tank.getX() - r1/3, tank.getY() - 5*Ww/4, 2*r1/3, 5*Ww/4);    																								//���ڹ�
					break;
					
				case DOWN:
					g2.drawRect(tank.getX() - Ww/2, tank.getY() - Ww/2, Ww, Ww);                                                                                                                          
					g2.fillRect(tank.getX() - Ww/2 - Ww/3, tank.getY() - Ww/2 - Ww/4, Ww/3,  Ww + Ww/2);
					g2.fillRect(tank.getX() + Ww/2, tank.getY() - Ww/2 - Ww/4, Ww/3, Ww + Ww/2);
					g2.drawOval(tank.getX() - r1 , tank.getY() - r1 , 2*r1, 2*r1);
					g2.fillRect(tank.getX() - r1/3, tank.getY(), 2*r1/3, 5*Ww/4);
					break;
					
				case LEFT:
					g2.drawRect(tank.getX() - Ww/2, tank.getY() - Ww/2, Ww, Ww);                                                                                                                          
					g2.fillRect(tank.getX() - Ww/2 - Ww/4, tank.getY() - Ww/2 - Ww/3, Ww/2 + Ww,  Ww/3);
					g2.fillRect(tank.getX() - Ww/2 - Ww/4, tank.getY() + Ww/2 , Ww/2 + Ww, Ww/3);
					g2.drawOval(tank.getX() - r1 , tank.getY() - r1 , 2*r1, 2*r1);
					g2.fillRect(tank.getX() - 5*Ww/4, tank.getY() - r1/3,  5*Ww/4, 2*r1/3);
					break;
					
				case RIGHT:
					g2.drawRect(tank.getX() - Ww/2, tank.getY() - Ww/2, Ww, Ww);                                                                                                                          
					g2.fillRect(tank.getX() - Ww/2 - Ww/4, tank.getY() - Ww/2 - Ww/3, Ww/2 + Ww,  Ww/3);
					g2.fillRect(tank.getX() - Ww/2 - Ww/4, tank.getY() + Ww/2 , Ww/2 + Ww, Ww/3);
					g2.drawOval(tank.getX() - r1 , tank.getY() - r1 , 2*r1, 2*r1);
					g2.fillRect(tank.getX(), tank.getY() - r1/3,  5*Ww/4, 2*r1/3);
					
					default:
						break;
			}
		}
	}
}
