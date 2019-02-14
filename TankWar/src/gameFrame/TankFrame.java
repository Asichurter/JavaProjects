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
	//公有量实例域
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

	
	//数据结构实例域
	private ArrayList<EnemyTank> EnemyTanks = new ArrayList<>();                                 												//容纳其他坦克的容器
	private ArrayList<Bullet>enemyBullets = new ArrayList<>();																								//容纳其他坦克子弹的容器
	private ArrayList<Supply> SUPPLIES = new ArrayList<>();
	private LinkedList<Explosion> Explosions = new LinkedList<>();
	
	//常量型实例域
	private PrecisionAttack PreAtt = null;
	private final int PreAttW = 30;
	private boolean ifPause = false;
	public static int ExplosionReduce = 50;
	private int PosTipsLastTime = 100;
	public static final int W = 20; 																														//坦克的宽度
	public static final int R1 = 6;																															//坦克的炮台半径	
	private int ENEMY_MAX_NUM;																						//最大敌人数量
	public static double DEFAULT_V = 4;																												//设置默认己方速度
	public static double DEFAULT_EV = 2;																											//设置默认敌方速度
	private int MODIFIED_FT = 50;																													//每一帧炮弹的装填量，应当能够被总时间整除
	private final int MODIFIED_Y = 2*W ;    																								//用于修正菜单栏和右侧的大小溢出
	private final int MODIFIED_X = W/2;
	private double RE_DIR_FACTOR = 0.01;																								//设置每一帧敌方坦克转向的概率
	private int CRASH_BACKSTEP = 3;																											//每一次撞击时后退的步数
	private int REPAIRTOOLKIT_NUM = 1;																										//维修包的最大数量
	private int FIRELOADTOOLKIT_NUM = 1;																								//开火包的最大数量
	private int SHEILDKIT_NUM = 1;
	private int AOE_KIT_NUM = 1;
	private int PREATT_KIT_NUM = 1;
	public static boolean ifLoadTimeDecresed = false;																//己方坦克是否已经拾取到了开火包
	private int REPAIRKIT_FACTOR = 200;																									//用于限制各类补给包刷新的概率因子，p = 1/k
	private int FIRELOADKIT_FACTOR = 600;
	private int SHIELDKIT_FACTOR = 400;
	private int AOEKIT_FACTOR = 1000;
	private int PREATT_FACTOR = 1000;
	private int PRE_ATT_V = 20;
	private final boolean printSize = false;																									//debug打印尺寸
	private final String DEFAULT_FILENAME = "Bank";	
	private String FILE_NAME = DEFAULT_FILENAME;
	private String GAME_DIFFICULTY;
	private Component com = new Component();
	
	public TankFrame() {
		//初始化框架基本架构
		this.panel =this.getContentPane() ;		
		while (!showSimpleMenu()) {}
		readMapAndSetDifficulty();
		this.add(com);	    
		this.setBounds(0,  0, (int) tool.getScreenSize().getWidth(), (int) tool.getScreenSize().getHeight());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle("坦克♂大战");
		this.setIconImage(tool.getImage("Icon/TitleIcon.png"));
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initMyTank();																				//初始化我的坦克的位置和速度

			
		//mytank.test();																																														

		//初始化实例域
		TankFrame.WIDTH = (int)tool.getScreenSize().getWidth() - MODIFIED_X;
		TankFrame.HEIGHT = (int)tool.getScreenSize().getHeight()  - MODIFIED_Y;
		this.enemyIter = EnemyTanks.listIterator();																	//敌方坦克的读写迭代器
		this.enemyBulletIter = enemyBullets.listIterator();												//敌方坦克子弹的读写迭代器
		this.myBulletIter = mytank.bullets.listIterator();														//我方坦克的子弹的读写迭代器
		this.supplyIter = this.SUPPLIES.listIterator();
		this.initEnemyTanks();																														//初始化敌方坦克	

		panel.setBackground(new Color(185, 185, 185));														//背景RGB颜色
		
		this.buildSupply(RepairToolkit::new);													//生成初始的补给包
		this.buildSupply(FireLoadkit::new);
		this.buildSupply(BulletShieldKit::new);
		this.buildSupply(AOE_BulletKit::new);
		this.buildSupply(PrecisionAttackKit::new);
		
		//读键器，在读移动键时会减速/加速/改向，开炮键会先检测是否能够开跑，能够开炮时会同时重置开炮冷却时间
		this.addKeyListener(new KeyAdapter() { 																
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (mytank.getDir() == Dir.UP)
						mytank.Accelerate(true);																											//判断为同向，加速
					else if (mytank.getDir() == Dir.DOWN)
						mytank.Accelerate(false);																											//判断为反向，减速
					else mytank.setDir(Dir.UP);																						//判断为转向
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
				case KeyEvent.VK_SPACE:																																//坦克开炮
					if(canFire(mytank)) {
						mytank.Fire();
						mytank.setFT(mytank.getLoadFT());																							//重置开火的冷却时间
						TankFrame.this.MyScore.increaseTotalFire();
					}
					break;
				case KeyEvent.VK_Q:
					if (canFire(mytank) && mytank.getAOEBulletNum() > 0) {
						mytank.AOE_Fire();
						mytank.setFT(mytank.getLoadFT());
						TankFrame.this.MyScore.increaseTotalFire();
						//System.out.println("Q技能发射！\n");
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
					if (mytank.getIfHavePreAtt() && TankFrame.this.PreAtt == null) {																					//如果有精准打击而且还没有启动的精准打击
						TankFrame.this.PreAtt = new PrecisionAttack(mytank.getX(), mytank.getY());
						mytank.setIfHavePreAtt(false);
					}
					else if(TankFrame.this.PreAtt != null) {																																							//如果已经启动了精准打击，则取消
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
																						//添加绘图组件
		
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
		String[] options = {"开始游戏", "操作提示", "游戏提示", "界面提示"};
		int option = JOptionPane.showOptionDialog(
				com, "请选择:", "开始菜单", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "退出");
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
	 * 显示界面提示
	 */
	public void showPanelTips() {
		JOptionPane.showMessageDialog(com, "框架的左上角和右上角有游戏的具体信息显示，他们分别是:\n"
				+ "左上角：\n"
				+ "1.玩家坦克的护甲值\n"
				+ "2.玩家坦克目前所处的地图类型\n"
				+ "3.玩家坦克当前拥有的Buff增益\n"
				+ "4.玩家坦克当前拥有的AOE导弹数量与装载进度（如果AOE导弹没有达到上限的话）\n"
				+ "5.当前加载的地图名称与游戏难度\n"
				+ "6.玩家是否有精准打击武器\n"
				+ "右上角:\n"
				+ "1.玩家坦克总共摧毁的坦克数量\n"
				+ "2.玩家坦克的炮弹类造成的总伤害\n"
				+ "3.玩家坦克通过撞击给敌方坦克造成的总伤害\n"
				+ "4.玩家坦克拾取到的补给包总数量\n"
				+ "5.敌方坦克对我方坦克造成的总伤害\n"
				+ "6.玩家坦克的炮弹的命中率\n", "界面提示", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * 显示操作提示
	 */
	public void showOpearativeTips() {
		JOptionPane.showMessageDialog(com, "操作提示：\n方向键:控制坦克的方向变换和加减速\n"
				+ "空格:坦克开火（如果能够开火的话）\n"
				+ "Q:发射AOE导弹（如果有足够的导弹且可以开火的话）\n"
				+ "数字1:(启动/取消)精准打击的瞄准\n"
				+ "R:精准打击开火\n"
				+ "E:刹车\n"
				+ "WSAD:精准打击瞄准移动\n"
				+ "Esc:暂停游戏\n"
				+ "Enter:继续游戏\n", "游戏提示", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * 显示游戏提示
	 */
	public void showGameTips() {
		JOptionPane.showMessageDialog(com, "游戏提示：\n1.坦克拥有开火冷却时间和开火限制，默认开火冷却时间是4s，不同地形可能有所改变（例如沙漠中是5s），水中不能开火\n"
				+ "2.坦克和坦克之间，坦克和地形之间可以发生碰撞并且双方受到一定的伤害同时后退，具体由相对速度决定，动能定理可计算。但是不同的碰撞方式\n"
				+ "，如同向，相向和侧向碰撞伤害值不一样。正确合理的碰撞可以在开火冷却时对敌方造成一定量的伤害\n"
				+ "3.坦克拥有护甲系统，所有类型的伤害都将会受到护甲的抵挡，不同地形护甲不同\n"
				+ "4.坦克拥有加减速系统，同向按键为减速，反向按键是减速，具体加减速的快慢由加速度决定，不同的地形加速度不同。坦克有最大速度限制，不同地形\n"
				+ "最大速度不同\n"
				+ "5.坦克的AOE炮弹最大限制为3个，不足3个时将会耗费一定时间装载导弹。AOE导弹伤害更高，并且可以对一片区域的所有坦克造成一定的杀伤\n"
				+ "，可以通过拾取补给包补充AOE导弹\n"
				+ "6.坦克的精准打击也是范围型伤害，由玩家自定义打击区域，它伤害更高，范围更大，但是只能通过拾取补给包来填充，每个坦克只有一个，初始时拥有一个\n"
				+ "AOE导弹包，装填精准打击的精准打击包。他们将会以一定数量一定概率随机在地图上刷新。敌方坦克也可以拾取，但是不一定生效。\n"
				+ "8.地图上有两种墙壁，一种可以被炮弹或者坦克撞击击穿，另一种不能被击穿。"
				, "游戏提示", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * 从用户的指定项中读取地图，同时选择游戏难度
	 */
	public void readMapAndSetDifficulty() {
		String[] options = {"Default", "Bank", "Road", "Maze", "其他"};
		String[] diffi = {"婴儿", "弱智", "小学生", "勉强", "牛啤", "神仙", "渔化"};
		int option = JOptionPane.showOptionDialog(
				com, "请选择要加载的地图或者是指定一个其他地图", "选择地图", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "Default");
		if (option != 4) {
			MY_MAP = new MyMap(options[option], com);
			FILE_NAME = options[option];
		}
		else {
			String filename = JOptionPane.showInputDialog(com, "请输入要载入的其他地图文件名(否则将会加载默认地图)", "加载地图", JOptionPane.INFORMATION_MESSAGE);
			if (filename == null) {
				MY_MAP = new MyMap(DEFAULT_FILENAME, com);
				FILE_NAME = DEFAULT_FILENAME;
			}
			else {
				do {
					filename = JOptionPane.showInputDialog(com, "请重新输入正确的要载入的其他地图文件名\n(否则将会加载默认地图)", "加载地图", JOptionPane.WARNING_MESSAGE);
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
				com, "选择游戏难度，这将决定你的敌人数量（请量力而行，不要不服）", "'游戏难度", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, diffi, "将就");
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
				com, "请开始以后先使用shift将键盘调为英文键盘！！！", "重要警告", JOptionPane.WARNING_MESSAGE);
	}
	
	public ListIterator<Explosion> getExplodeIter(){
		return this.Explosions.listIterator();
	}
	
	/**
	 * 用于将所有的集合迭代器更新的集成方法
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
	 * 用于暂停或者重新启动游戏的方法
	 * @param ifpause 是启动还是暂停游戏
	 */
	public void pauseOrRestartGame(boolean ifpause) {
		if (ifpause) {
			ifPause = true;
			JOptionPane.showMessageDialog(this.com, "游戏暂停，回车键继续游戏！", "Pause", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			ifPause = false;
			JOptionPane.showMessageDialog(this.com, "游戏继续！", "Pause", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * 用于显示游戏结束的提示。这是一个总启动方法
	 */
	public void showGameoverMes() {
		JOptionPane.showMessageDialog(com, "游戏失败！", "", JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(com, "技术统计：\n" + 
				"总摧毁坦克数: " + MyScore.getEliminateNum() + "\n" +
				"炮弹造成总伤害: " + DECIMAL_FORMAT.format(MyScore.getMyHitDamage()) + "\n" + 
				"撞击造成总伤害: " + DECIMAL_FORMAT.format(MyScore.getMyCrashDamage()) + "\n" + 
				"拾取到的补给包数量: " + MyScore.getGainedSupplyNum() + "\n" + 
				"	敌方造成总伤害: " + DECIMAL_FORMAT.format(MyScore.getEnemyDamage()) + "\n" + 
				"	命中率: " + DECIMAL_FORMAT.format(MyScore.getTotalFIre()==0 ? 0 : (double)MyScore.getTotalFIreHit()/MyScore.getTotalFIre()*100) + "%", "", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * 通过检查坦克的生命状态，检查游戏是否结束
	 * @return 游戏是否结束
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
	 * 利用一个补给包的创建接口，在地图上随机创建一个指定的补给包
	 * @param cons 指定的补给包类的构造接口
	 */
	public void buildSupply(SupplyBuilder<? extends Supply> cons) {
		Supply kit = cons.get();														
		while (!checkIfOverlayed(kit))
			kit = cons.get();
		this.SUPPLIES.add(kit);
	}
	
	//检查被拾取过后的补给包并且移除他们，同时生成新的补给包
	/**
	 * 用于检查所有的补给包的数量，并且在数量不足时以一定概率创建数量不足的补给包的方法
	 */
	public void checkAndCreateSupply() {
		int RepairNum = 0;
		int LoadFireNum = 0;
		int ShieldNum = 0;
		int AOENum = 0;
		int PreAttNum = 0;
		for (int i = 0; i< this.SUPPLIES.size(); i++) {											//先移除状态为false的补给包
			Supply supply = SUPPLIES.get(i);
			if (!supply.getIsExist())
				this.SUPPLIES.remove(i);
		}
		//int TestNum = SUPPLIES.size();
		for (int i = 0; i< this.SUPPLIES.size(); i++) {											//再对补给包的各种类进行计数
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
		//System.out.println("目前只有" + LoadFireNum + "个开火包，"+RepairNum+"个维修包，"+NullNum+"个空包"+"，总共" +TestNum+"个补给包，"+"遍历了"+i+"次");
		if (RepairNum < this.REPAIRTOOLKIT_NUM && rand.nextInt(this.REPAIRKIT_FACTOR) < 1)  {																	//万分之一的概率刷新维修包
		//	System.out.println("目前只有" + LoadFireNum + "个开火包，"+RepairNum+"个维修包，"+NullNum+"个空包"+"，总共" +TestNum+"个补给包，"+"遍历了"+i+"次"+ "，将会立即创造一个新的维修包\n");
			this.buildSupply(RepairToolkit::new);
		}
		if (LoadFireNum < this.FIRELOADTOOLKIT_NUM && rand.nextInt(this.FIRELOADKIT_FACTOR) < 1) {
			//System.out.println("目前只有" + LoadFireNum + "个开火包，"+RepairNum+"个维修包，"+NullNum+"个空包"+"，总共" +TestNum+"个补给包，"+"遍历了"+i+"次"+ "，将会立即创造一个新的开火包\n");
			this.buildSupply(FireLoadkit::new);
		}
		if (ShieldNum < this.SHEILDKIT_NUM && rand.nextInt(this.SHIELDKIT_FACTOR) < 1)
			this.buildSupply(BulletShieldKit::new);
		if (AOENum < this.AOE_KIT_NUM && rand.nextInt(this.AOEKIT_FACTOR) < 1)
			this.buildSupply(AOE_BulletKit::new);
		if (PreAttNum < this.PREATT_KIT_NUM && rand.nextInt(this.PREATT_FACTOR) < 1)
			this.buildSupply(PrecisionAttackKit::new);
	}
	
	//检所有是否被子弹击中测
	/**
	 * 用于检测所有坦克是否被子弹击中，这是一个其他检测击中方法的启动方法
	 */
	public void checkAllTanksIfHit() {																											//检查所有坦克的状态，并添加缺失的敌方坦克，调用checkEnemy_Fresh
		updateIter();																																				//敌方坦克子弹的读写迭代器，我方坦克的子弹的读写迭代器												
		for (EnemyTank tank : EnemyTanks) {
			checkTankIfHit(tank, MyScore);																																	//检查敌方坦克，重置其生命状态
		}
		checkTankIfHit(mytank, MyScore);																																//检查己方坦克，重置生命状态
		 checkEnemy_Fresh();																													//对敌方坦克生命状态进行检查，并且清除死亡的坦克，刷新																											
	}
	
	//检测一个任意的坦克是否被子弹击中
	/**
	 * 检测一个坦克是否被击中，并且在被击中时调用伤害和爆炸系统，同时移除子弹
	 * @param tank 待检测的坦克
	 */
	public void checkTankIfHit(AbstractTank tank, Score score) {																															//对一个坦克单位进行检查，调用
		if (tank instanceof EnemyTank) {																																						//如果坦克单位是敌方单位
			//ListIterator<Bullet> iter = mytank.bullets.listIterator();																							//迭代己方坦克的子弹列表
			//myBulletIter = mytank.bullets.listIterator();
			updateIter();
			while(myBulletIter.hasNext()){														
				Bullet bullet = myBulletIter.next();
				if (bullet instanceof AOE_Bullet) {
					AOE_Bullet Abullet = (AOE_Bullet)bullet;
					if (!Abullet.getIfHit() && Abullet.ifHit(tank)) {																					//如果AOE弹击中了目标而且没有被引爆过
						((AOE_Bullet) bullet).setIfHit(true);																										//将AOE弹设置为引爆状态
							((EnemyTank) tank).setIfHitByAOE(true);											
							this.Explosions.add(new AOE_Explosion(tank.getX(), tank.getY(), 50));					//添加AOE爆炸动画
							this.Explosions.add(new Explosion(tank.getX(), tank.getY(), 50));									//添加普通爆炸动画
							score.increaseTotalFireHit();
					}
					else if (Abullet.getIfHit()) {																														//如果AOE弹处于引爆状态，则遍历所有敌方坦克
						for (int i = 0; i < this.EnemyTanks.size(); i++) {
							if (Abullet.ifContains(EnemyTanks.get(i)))																//如果敌方坦克处于被波及的范围内
								EnemyTanks.get(i).beHit(Abullet, MyScore);
						}
						myBulletIter.remove();
					}
				}
				else {
					if (Point2D.distance(bullet.getX(), bullet.getY(),tank.getX() , tank.getY()) <= 2*(W+R1)/3) {					//当发现己方坦克的子弹与敌方坦克的距离达到警戒值（R1+W/2）
						if (!tank.getIfHasBulletShield()) {																																								//检查坦克是否有护盾
							tank.beHit(bullet, MyScore);																																																			//设置敌方坦克被击中
							this.Explosions.add(new Explosion(bullet.getX(), bullet.getY(), 50));																	//在原地添加一个爆炸效果图，刷新幅度是50
						}
					else 
						tank.setIfHasBulletShield(false);																																									//有护盾时，将坦克的护盾移除
						myBulletIter.remove();																																																				//移除这个子弹
						score.increaseTotalFireHit();
					}
				}
			}
		}
		else if(tank instanceof Tank) {																																											//如果坦克单位是己方坦克
			//ListIterator<Bullet> iter = enemyBullets.listIterator();																											//对敌方坦克的子弹进行迭代
			updateIter();																																																						//更新全局的迭代器，否则迭代器将会停留在上一次的位置
			while(enemyBulletIter.hasNext()) {
				Bullet bullet = enemyBulletIter.next();
				if (Point2D.distance(bullet.getX(), bullet.getY(),tank.getX() , tank.getY()) <= 2*(W+R1)/3) {				//当发现敌方坦克的子弹与己方的距离达到警戒值
					if(!tank.getIfHasBulletShield()) { 
						tank.beHit(bullet, MyScore);
						this.Explosions.add(new Explosion(bullet.getX(), bullet.getY(), 50));
					}
					else
						tank.setIfHasBulletShield(false);
					enemyBulletIter.remove();																																																			//移除这个己方的子弹
				}
			}
		}
	}
	
	//新建一个敌方单位，用于在敌方单位死亡后进行更新
	/**
	 * 用于在检测到敌方坦克数量不足时，刷新创建新的敌方坦克的方法。该方法已经实现了不覆盖刷新
	 */
	public void buildEnemy() {																																																						//建立新的敌方坦克
		EnemyTank newtank = new EnemyTank(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), DEFAULT_EV);
		while (!checkIfOverlayed(newtank))
			newtank = new EnemyTank(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), DEFAULT_EV);			
		EnemyTanks.add(newtank);
	}
	
	//检测是否存在待清理的死亡的敌方坦克，第一种方法存在BUG
	/**
	 * 用于检测敌方坦克状态，将移除已经死亡的敌方坦克同时刷新敌方坦克
	 */
	public void checkEnemy_Fresh() {																						//检查敌方坦克的状态并且刷新敌方坦克，依赖于checkTank，调用buildEnemy
		//ListIterator<EnemyTank> iter = EnemyTanks.listIterator();
		/*enemyIter.next();
		while(enemyIter.hasNext()) {																													//迭代检查敌方坦克
			if (enemyIter.next().getLive()) {																														//检查敌方坦克是否死亡
				enemyIter.remove();
				buildEnemy();																																		//构造新的坦克
			}
		}*/
		//BUG修复
		for (int i = 0; i < EnemyTanks.size(); i++) {
			if (!EnemyTanks.get(i).getLive()) {
				EnemyTanks.remove(i);
				buildEnemy();
			}
		}
	}
	
	/**
	 * 检测坦克之间的不同情况的重叠现象
	 * @param newtank 待检测的坦克
	 * @param identifier -2:新增的坦克   -1:己方坦克   其他[0, +∞]：敌方坦克，下标指定
	 * @return -2:没有重叠   -1:与己方坦克重叠   其他[0, +∞]：与敌方下标为返回值的坦克重叠
	 */
	public int checkIfOverlayed(AbstractTank newtank, int identifier) {														
		if (identifier == -2) {
			for (AbstractTank tank: EnemyTanks) {																																											//先检查是否与敌方坦克重叠
				if (Point2D.distance(newtank.getX(), newtank.getY(), tank.getX(), tank.getY()) <=2* (R1+W)/3)
					return EnemyTanks.indexOf(tank);
				}
			if (Point2D.distance(newtank.getX(), newtank.getY() , mytank.getX(), mytank.getY()) <=2*(R1 + W)/3)				//再检查是否与己方坦克重叠
				return -1;
			for (MapType map: MY_MAP.type_CanNotMove) {
				if (map.ifContains(newtank.getX(), newtank.getY()))
					return -1;
			}
			return -2;
		}
		else if (identifier == -1) {
			for (AbstractTank tank: EnemyTanks) {																																											//先检查是否与敌方坦克重叠
				if (Point2D.distance(newtank.getX(), newtank.getY(), tank.getX(), tank.getY()) <=2* (R1+W)/3)
					return EnemyTanks.indexOf(tank);
				}
			return -2;
		}
		else {
			for (int i = 0; i < EnemyTanks.size(); i++) {
				if (identifier == i)																																																				//跳过自己
					continue;
				else if (Point2D.distance(newtank.getX(), newtank.getY(), EnemyTanks.get(i).getX(),EnemyTanks.get(i).getY()) <= 2*(R1+W)/3)
					return i;
			}
			if (Point2D.distance(newtank.getX(), newtank.getY(), mytank.getX(),mytank.getY()) <= 2*(R1+W)/3)
				return -1 ;
			return -2;
		}
	}
	
	//重载的用于检测补给包是否与其他单位重叠
	/**
	 * 
	 * 重载的用于检测是否有重叠现象的方法，补给包类专用的重载
	 * @param supply 待检测是否重叠的补给包
	 * @return 是否有重叠现象
	 */
	public boolean checkIfOverlayed(Supply supply) {
		for (AbstractTank tank: EnemyTanks) {																																											//先检查是否与敌方坦克重叠
			if (Point2D.distance(supply.getX(),supply.getY(), tank.getX(), tank.getY()) <=2* (R1+W)/3)
				return false;
			}
		if (Point2D.distance(supply.getX(), supply.getY() , mytank.getX(), mytank.getY()) <=2*(R1 + W)/3)				//再检查是否与己方坦克重叠
			return false;
		for (MapType map: MY_MAP.type_CanNotMove) {
			if (map.ifContains(supply.getX(), supply.getY()))
				return false;
		}
		if (supply.getX() <= 5*W/4 || supply.getX() >= TankFrame.WIDTH - 5*W/4 || supply.getY() <= 5*W/4 || supply.getY() >= TankFrame.HEIGHT - 5*W/4)						//防止刷新补给包到边界不可见处
			return false;
		return true;
	}
	
	/**
	 * 用于检测新生成的单位是否与之前已有的单位或者地形发生重叠(己方坦克不能使用)
	 * @param newtank 待检测的新生成的坦克
	 * @return false：重叠 true：没有重叠
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
	 * 初始化生成我方坦克，已添加避免重合的检测
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
	 * 根据敌方坦克的额定数量，初始化创建一定数量的敌方坦克
	 */
	public void initEnemyTanks() {
		for (int i = 1; i <= this.ENEMY_MAX_NUM; i++) {
			this.buildEnemy();
		}
		/*EnemyTank newtank = new EnemyTank(1200, 200, DEFAULT_EV);                                  					//调试用...
		this.EnemyTanks.add(newtank);
		newtank = new EnemyTank(1200, 240, DEFAULT_EV);
		this.EnemyTanks.add(newtank);
		newtank = new EnemyTank(1200, 280, DEFAULT_EV);
		this.EnemyTanks.add(newtank);*/
	}
	
	//移动己方和地方坦克，同时随机重置敌方坦克的方向
	/**
	 * 移动我方和所有敌方坦克。该方法是一个总启动方法，同时还会实现我方坦克的Buff作用、Buff刷新
	 * 、限速和重置敌方坦克的功能
	 */
	public void moveAllTank() {																														//移动所有坦克
		mytank.allBuffsWork();																																//所有坦克的增损益工作
		mytank.checkIfAOEReady( 50); 																										//检查AOE炮弹是否应该装载完毕
		moveTank(mytank, false);																																//移动己方坦克
		mytank.restrictVtoMax(); 																											//我方坦克的限速	
		resetEnemyDir();
		for (int i  = 0; i < EnemyTanks.size(); i++) {																			//移动敌方坦克
			EnemyTanks.get(i).allBuffsWork();																					//所有坦克的增损益工作
			if (EnemyTanks.get(i).getV() < 1)
				EnemyTanks.get(i).reLaunch(TankFrame.DEFAULT_EV, false);					//坦克速度下降到一定值时重新启动
			moveTank(EnemyTanks.get(i), false);
			EnemyTanks.get(i).restrictVtoMax();
		}
	}
	
	/**
	 * 以一定概率重置地方坦克的的方向
	 */
	public void resetEnemyDir() {
		for (int i = 0; i < EnemyTanks.size(); i++) {
			if (rand.nextInt(100) < 100*this.RE_DIR_FACTOR)
				EnemyTanks.get(i).setDir(RandomDirProducer());
		}
	}
	
	/**
	 * 用于等概率的随机生成一个方向
	 * @return 随机生成的方向
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
	
	//移动所有子弹
	/**
	 * 移动所有的我方和敌方子弹。这是一个总启动方法
	 */
	public void moveAllBullet() {																								
		moveBullet(enemyBullets, false);
		moveBullet(mytank.bullets, true);
	}
	
	/**
	 * 移动一个集合中的所有子弹
	 * @param bullets 用于移动的储存子弹的集合
	 * @param IfMytank 用于标识是否是我方的子弹
	 */
	public void moveBullet(List<Bullet> bullets, boolean IfMytank) {																					//移动一个坦克的所有子弹
		ListIterator<Bullet> iter;
		updateIter();
		if (IfMytank) {
			iter = myBulletIter;
		}
		else{/* if (tank instanceof EnemyTank)*/																									//两种坦克的子弹分别处理
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
	
	//根据坦克的方向和速度改变坦克的坐标，达到了移动的目的
	/**
	 * 移动一个坦克
	 * @param mytank2 待移动的坦克
	 * @param ifTest 标识是否进行测试。如果是，将会无视地形限制和碰撞（用于测试碰撞），但是不会刷新地形
	 */
	public void moveTank(AbstractTank mytank2, boolean ifTest) {																										//移动一个坦克
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
	 * 检查所有坦克的碰撞情况。这是一个总启动方法
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
	 * 重置所有坦克的碰撞状态。这是一个总启动方法，用于在新的回合中刷新
	 */
	public void freshAllCrash() {
		mytank.setIfCrashed(false);
		for (EnemyTank tank: EnemyTanks) {
			tank.setIfCrashed(false);
		}
	}
	
	/**
	 * 检查坦克是否有拾取到补给包
	 * @param tank 待检查的坦克
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
				else if (supply instanceof BulletShieldKit) {																						//坦克没有护盾时，才会为坦克武装护盾
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
	
	//检查一个坦克是否与其他有冲撞现象，先检查己方坦克再检查敌方坦克
	/**
	 * 检查一个坦克是否与其他坦克有碰撞情况。这个方法已经利用碰撞情况参数避免了重复碰撞检测。包含了对补给包拾取的检测
 	 * @param tank 待检测的坦克
	 */
	public void checkCrash(AbstractTank tank) {
		checkIfCrashWithSupply(tank);
		if (tank.getIfCrashed())
			return;  
		if (tank instanceof Tank) {																							//再判断是否有坦克之间的冲撞
			this.moveTank(tank, true);
			int identifier = checkIfOverlayed(tank, -1);	
			double tempV = tank.getV();
			if (identifier != -2) {																											//如果己方与敌方发生了冲撞现象
				tank.Back(CRASH_BACKSTEP, false);
				switch(Dir.checkCrashDir(tank.getDir(), EnemyTanks.get(identifier).getDir())) {												//第一个为撞击单位的方向，第二个为被撞单位的方向
					case 0:
						tank.Crash(0, true, MyScore);																																//侧撞的坦克不受相对速度
						EnemyTanks.get(identifier).Crash(tempV, true, MyScore);									//被侧撞的坦克将会受到+的相对速度
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
		else{																																																	//如果敌方与敌方发生了冲撞
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
			else if (identifier != -2) {																																//敌方坦克之间对撞不产生伤害
				tank.Back(CRASH_BACKSTEP, false);
				tank.Crash(-1, false, MyScore);
				EnemyTanks.get(identifier).Crash(-1, false, MyScore);
			 }
			else tank.Back(1, true);
		 }
	}
	
	//判断能否进行移动
	/**
	 * 根据碰撞情况、地形、出界情况和速度是否为0判断坦克能否继续移动
	 * @param mytank2 待检测的坦克
	 * @return 是否能够移动
	 */
	public boolean canMove(AbstractTank mytank2) {																																
		//判断是否已经发生过碰撞了
		if (mytank2.getIfCrashed())
			return false;
		
		//判断是否将要移动到不可越过地形
		moveTank(mytank2, true);
		for (MapType map : TankFrame.MY_MAP.type_CanNotMove) {																//for each 循环，可能会出现并发错误
			if (map.ifContains(mytank2.getX(), mytank2.getY())) {
				mytank2.Back(this.CRASH_BACKSTEP, false);
				map.Crash(mytank2.getV());
				mytank2.Crash(0, true, MyScore);
				return false;
			}
		}
		mytank2.Back(1, true);
		
		//判断是否出界
		switch(mytank2.getDir()) {
			case UP:
				if (mytank2.getY() <= 5*W/4) {																													//先判定是否撞墙					
					if (mytank2 instanceof EnemyTank)																						//在判断是否是己方坦克
						((EnemyTank) mytank2).reLaunch(DEFAULT_EV, true);
					else {
						mytank2.Crash(0, true, MyScore);																														//如果是己方坦克，则Crash
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
						mytank2.Crash(0, true, MyScore);																																//如果撞墙，则视为crash且造成伤害
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
	 * 敌方坦克随机开火的方法。这是一个总启动方法
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
	 * 根据坦克开火的冷却时间，判断是否能够进行开火
	 * @param tank 待判断能否开火的坦克
	 * @return 是否能够开火
	 */
	public boolean canFire(AbstractTank tank) {
		return tank.getFT() == 0  && tank.getMapType().getIfCanFire();
	}
	
	/**
	 * 刷新坦克开火冷却时间的方法，刷新频率由MODIFIED_FT决定。已修正MODIFIED_FT不能被LOAD_FT整除的问题
	 * @param tank
	 */
	public void reSetFT(AbstractTank tank) {																															//每一帧，重置坦克的开炮时间
			tank.setFT(tank.getFT() - MODIFIED_FT);
			if (tank.getFT() < 0)
				tank.setFT(0);
	}
	
	/**
	 * 刷新所有坦克开火时间的方法。这是一个总启动方法
	 */
	public void freshAllFT() {
		reSetFT(mytank);
		updateIter();
		while(enemyIter.hasNext()) {
			reSetFT(enemyIter.next());
		}
		//待编写
	}
	
	//判断子弹能否移动
	public boolean bulletCanMove(Bullet bullet) {																						
		//待编写
		return true;
	}
	
	/**
	 * 检查子弹是否有碰撞到不可逾越地形或者出界的情况。这个方法将会调用不可逾越地形的与子弹碰撞的方法
	 */
	public void checkAllBullets() {																																//检查所有子弹
		mytank.removeMyBullet(this.getExplodeIter());
		/*updateIter();
		while (enemyBulletIter.hasNext()) {
			Bullet bullet = enemyBulletIter.next();																											//迭代器引用报错
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
	 * 检查精准打击是否引爆
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
	 * 检查子弹是否出界
	 * @param bullet 待检测的子弹
	 * @return 是否出界
	 */
	public boolean checkIfBulletOut(Bullet bullet) {
		if (bullet.getX() < 0 || bullet.getX() >TankFrame. WIDTH || bullet.getY() < 0 || bullet.getY() > TankFrame.HEIGHT)
			return true;
		else return false;
	}
	
	/**
	 * 检查子弹是否与不可逾越地形发生碰撞，这个方法将会同时调用不可逾越地形与子弹碰撞的方法
	 * @param bullet 待检测的子弹
	 * @return 是否有碰撞现象
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
	
	//绘画组件
	class Component extends JComponent{																													//用于绘图的面板

		private static final long serialVersionUID = -8198036568671194725L;

		@Override
		public void paintComponent(Graphics g) { 																										//调用绘制的方法
			Graphics2D g2 = (Graphics2D)g;
			paintMap(g2);
			paintAllSupplies(g2);
			paintTank(g2, mytank, Color.BLACK);
			
			//书写己方坦克的Buff在最左上角,书写技术统计在右上角
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
				g2.drawImage(tool.getImage("Icon/位置提示.png"), mytank.getX()-5*Tank.W/2, mytank.getY()- 15*Tank.W/2, 6*Tank.W, 6*Tank.W, null);
				PosTipsLastTime--;
			}
		}
		
		/**
		 * 绘制玩家坦克的信息
		 * @param g2 绘画元素
		 * @param tank 我方坦克
		 */
		public void paintInfo(Graphics2D g2, AbstractTank tank) {
			g2.drawString("护甲值:" + mytank.getArmor(), 0, 15);
			g2.drawString("地图类型:" + mytank.getMapType().toString(), 0, 30);
			ListIterator<Buff>iter = tank.getBuffsIter();
			StringBuilder string = new StringBuilder();
			string.append("Buff: ");
			if (!tank.getBuffsIter().hasNext())
				string.append("无");
			else while (iter.hasNext()) 
				string.append(iter.next().getDes() + " ");
			g2.drawString(string.toString(), 0, 45);
			g2.drawString("技术统计：", 1400, 15);
			g2.drawString("总摧毁坦克数: " + MyScore.getEliminateNum(), 1400, 30);
			g2.drawString("炮弹造成总伤害: " + DECIMAL_FORMAT.format(MyScore.getMyHitDamage()), 1400, 45);
			g2.drawString("撞击造成总伤害: " + DECIMAL_FORMAT.format(MyScore.getMyCrashDamage()), 1400, 60);
			g2.drawString("拾取到的补给包数量: " + MyScore.getGainedSupplyNum(), 1400, 75);
			g2.drawString("敌方造成总伤害: " + DECIMAL_FORMAT.format(MyScore.getEnemyDamage()), 1400, 90);
			g2.drawString("命中率: " + DECIMAL_FORMAT.format(MyScore.getTotalFIre()==0 ? 0 : (double)MyScore.getTotalFIreHit()/MyScore.getTotalFIre()*100) + "%", 1400, 105);
			g2.drawString("AOE导弹数量: "+ mytank.getAOEBulletNum() + "  装载进度: " + mytank.getAOEtime(), 0, 60);
			g2.drawString("地图名：" + FILE_NAME + " 游戏难度: " + GAME_DIFFICULTY, 0, 75);
			g2.drawString("是否有精准打击：" + (mytank.getIfHavePreAtt() ? "是" : "否"), 0, 90);
			
		}
		
		/**
		 * 绘制地图
		 * @param g2 绘画元素
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
		 * 绘制血量条
		 * @param g2 绘画元素
		 * @param tank 待绘制的坦克
		 */
		public void paintHealth(Graphics2D g2, AbstractTank tank) {
			//g2.drawString("" + tank.getHealth(), tank.getX() - 4*W/5, tank.getY() -5*W/2);											//测试用语句
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
		 * 绘制所有的补给包
		 * @param g2 绘画元素
		 */
		public void paintAllSupplies(Graphics2D g2) {
			updateIter();
			while (supplyIter.hasNext()) {
				Supply supply = supplyIter.next();
				g2.drawImage(supply.getImage(), supply.getX() - Supply.W/2, supply.getY() - Supply.W/2, Supply.W, Supply.W, null);
			}
		}
		
		/**
		 * 绘制精准打击瞄准镜
		 * @param g2 绘画元素
		 */
		public void paintPrecisionAttack(Graphics2D g2) {
			if (PreAtt != null) {
				g2.drawImage(PreAtt.getIcon(), PreAtt.getX()-PreAttW/2, PreAtt.getY()-PreAttW/2, PreAttW, PreAttW, null);
			}
		}
		
		/**
		 * 绘制爆炸效果
		 * @param g2 绘画元素
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
		 * 绘制我方坦克的子弹
		 * @param g2 绘画元素
		 * @param tank 我方坦克
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
		 * 重载的绘制子弹的方法，用于绘制敌方坦克的子弹
		 * @param g2 绘画元素
		 * @param bullet 待绘制的子弹
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
		 * 绘制开火冷却时间的方法
		 * @param g2 绘画元素
		 * @param tank 待绘制的坦克。设置为只有我方坦克的冷却时间才会被画出
		 */
		public void paintFT(Graphics2D g2, Tank tank) {															
			if (tank.getFT() == 0) {
				//do nothing...
			}
			else if ( tank.getFT() >= 7*tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间1.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6,3*W/4, 3*W/4,  null);
			}
			else if(tank.getFT() < 7*tank.getLoadFT()/8 && tank.getFT() >= 3*tank.getLoadFT()/4) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间2.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < 3*tank.getLoadFT()/4 && tank.getFT() >= 5*tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间3.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < 5*tank.getLoadFT()/8 && tank.getFT() >= tank.getLoadFT()/2) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间4.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6,3*W/4, 3*W/4,  null);
			}
			else if(tank.getFT() < tank.getLoadFT()/2 && tank.getFT() >= 3*tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间5.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < 3*tank.getLoadFT()/8 && tank.getFT() >= tank.getLoadFT()/4) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间6.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6,3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < tank.getLoadFT()/4 && tank.getFT() >=tank.getLoadFT()/8) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间7.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
			else if(tank.getFT() < tank.getLoadFT()/8 && tank.getFT() > 0) {
				g2.drawImage(tool.getImage("LOADFT/冷却时间8.png"), tank.getX() + 3*W/4, tank.getY()+5*W/6, 3*W/4, 3*W/4, null);
			}
		}
		
		/**
		 * 根据坦克的朝向绘制坦克
		 * @param g2 绘画元素
		 * @param tank 待绘制的坦克
		 * @param color 绘制颜色
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
					g2.drawRect(tank.getX() - Ww/2, tank.getY() - Ww/2, Ww, Ww);                                                                                                                            //以左上角为坐标，画坦克主体
					g2.fillRect(tank.getX() - Ww/2 - Ww/3, tank.getY() - Ww/2 - Ww/4, Ww/3,  Ww + Ww/2);																				//画两个履带
					g2.fillRect(tank.getX() + Ww/2, tank.getY() - Ww/2 - Ww/4, Ww/3, Ww + Ww/2);
					g2.drawOval(tank.getX() - r1 , tank.getY() - r1 , 2*r1, 2*r1);                     																									//画中间的炮台
					g2.fillRect(tank.getX() - r1/3, tank.getY() - 5*Ww/4, 2*r1/3, 5*Ww/4);    																								//画炮管
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
