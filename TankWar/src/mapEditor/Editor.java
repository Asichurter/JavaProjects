package mapEditor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Editor {
	public static void main(String[] args) {
			/*Thread mythread = new Thread(()->{
			MainFrame com = new MainFrame();
		});
			mythread.start();*/
		EventQueue.invokeLater(()->{
			MainFrame com = new MainFrame();
		});
	}
}

/**
 * 用于显示地图的程序主框架
 * 
 * @author Asichurter
 *
 */

class MainFrame extends JFrame{
	//private final int OptionMenuW = 200;																								//选择栏宽度，高度与框架持平
	private static final double Ratio = 1;
	private final int W = (int)(1551*Ratio);
	private final int H = (int)(839*Ratio);
	public static final int UnitW = (int)(50*Ratio);
	private final Toolkit tool = Toolkit.getDefaultToolkit();
	public static int MouseType = 0;																							//用于标识当前光标代表的地图类型
	public static String ExtraMouseType  = "";																			//用于标识地图类型的附属子类型
	public static boolean ifRepaint = false;
	private boolean ifMoving = false;
	private boolean ifHavingMoved = false;
	
	private final int GrassIden = 2;
	private final int DesertIden = 3;
	private final int WaterIden = 4;
	private final int HillIden = 5;
	private final int BlockWallIden = 6;
	private final int RoadIden = 7;
	private final int PlainIden = 8;
	private final int IronWallIden = 9;
	String lastFile = "Bank";
	
	MainComponent comp;
	
	JMenuItem clearItem = new JMenuItem("清除所有地图单元");
	JMenuItem saveItem = new JMenuItem("保存地图");
	JMenuItem saveasItem = new JMenuItem("地图另存为");
	JMenuItem loadmapItem = new JMenuItem("加载已有的地图文件");
	JMenuItem clearOneItem = new JMenuItem("清除单位");
	JCheckBoxMenuItem moveItem = new JCheckBoxMenuItem("移动");
	JMenu tipsMenu = new JMenu("使用提示");
	JMenuItem tipsItem = new JMenuItem("使用方法");
	
	public MainFrame() {
		showMessage();
		this.setTitle("地图编辑器");
		this.setVisible(true);
		this.setBounds(0,  0, (int) tool.getScreenSize().getWidth(), (int) tool.getScreenSize().getHeight());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(tool.getImage("Icon/EditorIcon.png"));
		this.getContentPane().setBackground(new Color(185, 185, 185));
		 comp = new MainComponent(lastFile);
		this.add(comp);
		
		JMenuBar menubar = new JMenuBar();
		JMenu functionMenu = new JMenu("功能");
		functionMenu.add(clearItem);
		functionMenu.add(saveItem);
		functionMenu.add(saveasItem);
		functionMenu.add(loadmapItem);
		menubar.add(functionMenu);
		JMenu mapMenu = new JMenu("地图");
		JMenuItem grassItem = new JMenuItem("草地");
		grassItem.addActionListener(getMenuItemListener(this.GrassIden));
		JMenuItem desertItem = new JMenuItem("沙漠");
		desertItem.addActionListener(getMenuItemListener(this.DesertIden));
		JMenuItem hillItem = new JMenuItem("山地");
		hillItem.addActionListener(getMenuItemListener(this.HillIden));
		JMenuItem waterItem = new JMenuItem("水域");
		waterItem.addActionListener(getMenuItemListener(this.WaterIden));
		JMenuItem blockwallItem = new JMenuItem("砖墙");
		blockwallItem.addActionListener(getMenuItemListener(this.BlockWallIden));
		JMenu roadMenu = new JMenu("公路");
		JMenuItem roadItem1 = new JMenuItem("公路左上");
		roadItem1.addActionListener(getMenuItemListener(RoadIden, "公路左上"));
		JMenuItem roadItem2 = new JMenuItem("公路竖");
		roadItem2.addActionListener(getMenuItemListener(RoadIden, "公路竖"));
		JMenuItem roadItem3 = new JMenuItem("公路右上");
		roadItem3.addActionListener(getMenuItemListener(RoadIden, "公路右上"));
		JMenuItem roadItem4 = new JMenuItem("公路横");
		roadItem4.addActionListener(getMenuItemListener(RoadIden, "公路横"));
		JMenuItem roadItem5 = new JMenuItem("公路右下");
		roadItem5.addActionListener(getMenuItemListener(RoadIden, "公路右下"));
		JMenuItem roadItem6 = new JMenuItem("公路左下");
		roadItem6.addActionListener(getMenuItemListener(RoadIden, "公路左下"));
		roadMenu.add(roadItem1);
		roadMenu.add(roadItem2);
		roadMenu.add(roadItem3);
		roadMenu.add(roadItem4);
		roadMenu.add(roadItem5);
		roadMenu.add(roadItem6);
		JMenuItem plainItem = new JMenuItem("平原");
		plainItem.addActionListener(getMenuItemListener(this.PlainIden));
		JMenuItem ironWallItem = new JMenuItem("铁墙");
		ironWallItem.addActionListener(getMenuItemListener(this.IronWallIden));
		JMenuItem nullItem = new JMenuItem("空");
		nullItem.addActionListener(getMenuItemListener(0));
		mapMenu.add(grassItem);
		mapMenu.add(desertItem);
		mapMenu.add(hillItem);
		mapMenu.add(waterItem);
		mapMenu.add(blockwallItem);
		mapMenu.add(roadMenu);
		mapMenu.add(plainItem);
		mapMenu.add(ironWallItem);
		mapMenu.addSeparator();
		mapMenu.add(nullItem);
		mapMenu.addSeparator();
		mapMenu.add(clearOneItem);
		mapMenu.addSeparator();
		mapMenu.add(moveItem);
		menubar.add(mapMenu);
		
		tipsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMessage();
			}
		});
		tipsMenu.add(tipsItem);
		menubar.add(tipsMenu);
		this.setJMenuBar(menubar);
		
	}
	
	public void showMessage() {
		JOptionPane.showMessageDialog(comp, "地图编辑器使用提示:\n"
				+ "地图编辑器可以实现地图的可视化制作与修改，同时可以方便地保存与读取\n"
				+ "1.如果想要在地图上某处生成一个地形，先点击菜单栏中的地图，再选择对应的地图类型后，在想要生成地图单位的地方点击即可。\n"
				+ "这将会覆盖掉原本位置上的地图元素\n"
				+ "2.如果想要清除一个单元内的地图元素，先点击菜单栏中的地图，再选择清除按钮。只需在想要清除的位置点击即可清除该单元格\n"
				+ "内的地图\n"
				+ "3.如果想要将一个单元格内的地图元素移动到另一个位置，先点击菜单栏中的地图按钮，打开“移动“开关后，将想要移动的地图元素\n"
				+ "拖动到目的地后松开即可。注意，打开移动开关以后如果不想再移动，需关闭该开关。\n"
				+ "4.菜单栏的功能内有可以一键清除所有地图元素的按钮，可以将该地图数据保存到原地图文件或者新建的地图文件中的按钮和\n"
				+ "可以从文件中重新读入地图的按钮。\n"
				+ "5.注意，如果想要将地图新建另存为地图文件的话，必须先在根目录新建与地图同名的空文件夹后，再在地图编辑器内保存地图！\n", "使用提示",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * 用于避免与Component重名的，调用框架repaint方法的桥方法
	 */
	public void Repaint() {
		this.repaint();														//框架中repaint方法
	}
	
	/**
	 * 获得一个地图类型对应的监听器，用于将鼠标当前的图标进行更换
	 * @param iden 地图的类型标识
	 * @return 地图类型对应的监听器
	 */
	private ActionListener getMenuItemListener(int iden) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!ifMoving) {
					MouseType = iden;
				ExtraMouseType = "";
				}
				//System.out.println(MouseType);
			}
		};
	}
	
	/**
	 * 获得一个地图类型对应的监听器，为了对公路的多类型进行适应进行重载，用于将鼠标当前的图标进行更换
	 * @param iden 地图的类型标识
	 * @param extra 地图类型的额外识别符
	 * @return 地图类型对应的监听器
	 */
	
	private ActionListener getMenuItemListener(int iden, String extra) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!ifMoving) {
					MouseType = iden;
					ExtraMouseType = extra;
				}
			}
		};
	}
	
	class MainComponent extends JPanel{
		
		private LinkedList<MapData>Grass = new LinkedList<>();																											//存在待修复的错误，应该是只储存点的坐标
		private LinkedList<MapData> Desert = new LinkedList<>() ;
		private LinkedList<MapData> Water = new LinkedList<>();
		private LinkedList<MapData> Hill = new LinkedList<>();
		private LinkedList<MapData> Road = new LinkedList<>();
		private LinkedList<MapData> Plain = new LinkedList<>();
		private LinkedList<MapData> CanNotMove = new LinkedList<>();																							//将可被摧毁地形的链表设置为公有，以便其他检索方法来访问
		
		private int X;
		private int Y;
		private int Type;
		private String ExtraType;
		private MainComponent  This = this;																				//用于在内部类中区分this
		
		public MainComponent(String folderName) {
			this.setBackground(new Color(185, 185, 185));
			this.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent e) {																						//添加鼠标移动监听
					if (!ifMoving && !ifHavingMoved) {																												//如果鼠标事件当前不归属于拖动地图
						X = e.getX();
						Y = e.getY();
						Type = MainFrame.MouseType;
						ExtraType = MainFrame.ExtraMouseType;
						if (Type != 0)
							setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						else setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					Repaint();																																																									//调用外部的桥方法避免重名歧义
					//System.out.println("\n\nX = " + X +"\nY = " +  Y + "\n类型：" + Type + " 附加类型：" + ExtraType);
				}
				
				public void mouseDragged(MouseEvent e) {																													//添加鼠标拖动的监听
					if (ifMoving && ifHavingMoved) {
						X = e.getX();
						Y = e.getY();
						if (Type != 0)
							setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						else setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						Repaint();
					}
				}
			});
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {																				//添加鼠标按下监听
					if (ifMoving && !ifHavingMoved) {																													//处于移动状态但是还没有开始移动
						ifHavingMoved = true;
						Type = checkIfContainAndReplace(e.getX(), e.getY(), 0, "移动");
					}
					else if (Type != 0)																																					
						checkIfContainAndReplace(e.getX(), e.getY(), Type, ExtraType);
				}
				
				public void mouseReleased(MouseEvent e) {																					//添加鼠标释放监听
					if (ifMoving && ifHavingMoved) {
						checkIfContainAndReplace(e.getX(), e.getY(), Type, ExtraType);
						ifHavingMoved = false;
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						Repaint();
					}
				}
			});
			
			//添加各个菜单按钮的监听器
			loadmapItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = JOptionPane.showInputDialog(This, "请输入需要加载的文件夹名称:", "加载地图", JOptionPane.INFORMATION_MESSAGE);
					if (readFile(name)) { 
						JOptionPane.showMessageDialog(This, "成功读入地图文件!", "加载地图", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("Icon/正确.png"));
						lastFile = name;
					}
					else JOptionPane.showMessageDialog(
							This, "加载失败！请检查名称是否输入正确或者路径是否正确！", "加载地图", JOptionPane.WARNING_MESSAGE , new ImageIcon("Icon/错误.png"));
					//System.out.println(name);
				}
			});
			
			readFile(folderName);
			
			clearItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearAll();
					JOptionPane.showMessageDialog(This, "成功清除！", "清除地图", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("Icon/正确.png"));
					Repaint();
				}
			});
			
			saveasItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = JOptionPane.showInputDialog(This, "输入要保存到的文件夹", "保存文件", JOptionPane.INFORMATION_MESSAGE);
					if (saveFile(name))
						JOptionPane.showMessageDialog(This, "成功保存到" + name, "保存文件", JOptionPane.INFORMATION_MESSAGE,new ImageIcon("Icon/正确.png"));
					else JOptionPane.showMessageDialog(
							This, "保存失败！请检查文件夹是否存在或者排查其他原因！", "保存文件", JOptionPane.WARNING_MESSAGE, new ImageIcon("Icon/错误.png"));
				}		
			});
			
			clearOneItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MouseType = -1;
					ExtraMouseType = "";
				}
			});
			
			saveItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (saveFile(lastFile))
						JOptionPane.showMessageDialog(This, "保存成功！", "保存文件", JOptionPane.INFORMATION_MESSAGE);
					else JOptionPane.showMessageDialog(This, "保存失败", "保存文件", JOptionPane.WARNING_MESSAGE);
				}
			});
			
			moveItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (!ifMoving)
						ifMoving = true;
					else {
						ifMoving = false;
						ifHavingMoved = false;
					}
					Type = 0;
				}
			});
		}
		
		/**
		 * 将地图文件保存到文件夹中，文件夹必须事先建立好
		 * @param name 用于保存地图的文件夹名
		 * @return 是否成功保存
		 */
		public boolean saveFile(String name) {
			try{
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Map/" +name + "/Grass.dat"));
				out.writeObject(Grass);
				out  = new ObjectOutputStream(new FileOutputStream("Map/" + name + "/Desert.dat"));
				out.writeObject(Desert);
				out  = new ObjectOutputStream(new FileOutputStream("Map/" +name + "/Hill.dat"));
				out.writeObject(Hill);
				out  = new ObjectOutputStream(new FileOutputStream("Map/" +name + "/Water.dat"));
				out.writeObject(Water);
				out  = new ObjectOutputStream(new FileOutputStream("Map/" +name + "/CanNotMove.dat"));
				out.writeObject(CanNotMove);
				out  = new ObjectOutputStream(new FileOutputStream("Map/" +name + "/Road.dat"));
				out.writeObject(Road);
				out  = new ObjectOutputStream(new FileOutputStream("Map/" +name + "/Plain.dat"));
				out.writeObject(Plain);
				return true;
			}
			catch(IOException e) {
				return false;
			}
		}
		
		/**
		 * 用于清除目前缓存的地图上的所有元素
		 */
		public void clearAll() {
			Grass.clear();
			Desert.clear();
			Hill.clear();
			Water.clear();
			Road.clear();
			Plain.clear();
			CanNotMove.clear();
		}
		
		/**
		 * 用于从指定文件夹中读取地图文件
		 * @param folderName 地图文件所在的文件夹名
		 * @return 是否成功读入
		 */
		public boolean readFile(String folderName) {
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/CanNotMove.dat"));
				CanNotMove = (LinkedList<MapData>)in.readObject();
				in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Desert.dat"));
				Desert = (LinkedList<MapData>)in.readObject();
				in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Grass.dat"));
				Grass = (LinkedList<MapData>)in.readObject();
				in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Hill.dat"));
				Hill = (LinkedList<MapData>)in.readObject();
				in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Plain.dat"));
				Plain = (LinkedList<MapData>)in.readObject();
				in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Road.dat"));
				Road = (LinkedList<MapData>)in.readObject();
				in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Water.dat"));
				Water = (LinkedList<MapData>)in.readObject();
				return true;
				//System.out.println("成功读入地图文件！");
			} 
			catch (FileNotFoundException e) {
				return false;
			} 
			catch (IOException e) {
				return false;
			}
			catch(ClassNotFoundException e) {
				return false;
			}
		}
		
		/**
		 * 用于检查鼠标点击时的点是否在已有的地图元素内，并且替换掉当前的点的地图元素
		 * @param x 鼠标点击的X坐标
		 * @param y 鼠标点击的Y坐标
		 * @param iden 鼠标点击时的预设地图类型标识符
		 * @param des 鼠标点击时的预设地图的附加标识符
		 * @return 是否鼠标点击处已有地图元素
		 */
		public int checkIfContainAndReplace(int x, int y, int iden, String des) {
			int X = normalizeCoordinate(x);
			int Y = normalizeCoordinate(y);
			Iterator<MapData> iter;
			int ifContain = 0;
			/*for (MapData d: Grass) {
				if (new Rectangle2D.Double(d.getX()-UnitW/2 ,d.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (d.getIden() == iden)
						return true;
					Grass.remove(d);												//直接利用集合的修改方法而非迭代器的方法时会抛出异常
					ifContain = 2;
				}
			}*/
			iter = Grass.iterator();
			while (iter.hasNext() && ifContain == 0) {
				MapData temp = iter.next();
				if (new Rectangle2D.Double(temp.getX()-UnitW/2 ,temp.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (temp.getIden() == iden)
						return 0;
					iter.remove();
					ifContain = 2;
				}
			}
			if (ifContain == 0)iter = Desert.iterator();
			while (iter.hasNext() && ifContain == 0) {
				MapData temp = iter.next();
				if (new Rectangle2D.Double(temp.getX()-UnitW/2 ,temp.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (temp.getIden() == iden)
						return 0;
					iter.remove();
					ifContain = 3;
				}
			}
			if (ifContain == 0)iter = Hill.iterator();
			while (iter.hasNext() && ifContain == 0) {
				MapData temp = iter.next();
				if (new Rectangle2D.Double(temp.getX()-UnitW/2 ,temp.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (temp.getIden() == iden)
						return 0;
					iter.remove();
					ifContain = 5;
				}
			}
			if (ifContain == 0)iter = Water.iterator();
			while (iter.hasNext() && ifContain == 0) {
				MapData temp = iter.next();
				if (new Rectangle2D.Double(temp.getX()-UnitW/2 ,temp.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (temp.getIden() == iden)
						return 0;
					iter.remove();
					ifContain = 4;
				}
			}
			if (ifContain == 0)iter = Road.iterator();
			while (iter.hasNext() && ifContain == 0) {
				MapData temp = iter.next();
				if (new Rectangle2D.Double(temp.getX()-UnitW/2 ,temp.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (temp.getIden() == iden)
						return 0;
					if(des.equals("移动")) ExtraType = temp.getDes();
					iter.remove();
					ifContain = 7;
				}
			}
			if (ifContain == 0)iter = CanNotMove.iterator();
			while (iter.hasNext() && ifContain == 0) {
				MapData temp = iter.next();
				if (new Rectangle2D.Double(temp.getX()-UnitW/2 ,temp.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (temp.getIden() == iden)
						return 0;
					iter.remove();
					ifContain = (temp.getIden() == 6 ? 6 : 9);
				}
			}
			if (ifContain == 0)iter = Plain.iterator();
			while (iter.hasNext() && ifContain == 0) {
				MapData temp = iter.next();
				if (new Rectangle2D.Double(temp.getX()-UnitW/2 ,temp.getY()-UnitW/2, UnitW, UnitW).contains(X, Y)) {
					if (temp.getIden() == iden)
						return 0;
					iter.remove();
					ifContain = 8;
				}
			}
			switch(iden) {
				case  2:
					Grass.add(new MapData(X, Y, iden, des));
					break;
				case  3:
					Desert.add(new MapData(X, Y, iden, des));
					break;
				case  4:
					Water.add(new MapData(X, Y, iden, des));
					break;
				case  5:
					Hill.add(new MapData(X, Y, iden, des));
					break;
				case  6:
				case  9:
					CanNotMove.add(new MapData(X, Y, iden, des));
					break;
				case  7:
					Road.add(new MapData(X, Y, iden, des));
					break;
				case  8:
					Plain.add(new MapData(X, Y, iden, des));
					break;
				default:
					break;
			}
			return ifContain;
		}
		
		/**
		 * 用于将点的坐标规范化到对应的网格中心
		 * @param co 待规范的X或者Y坐标
		 * @return 规范化以后的坐标
		 */
		public int normalizeCoordinate(int co) {
			return co/UnitW*UnitW + UnitW/2;
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;

			for (MapData d: Grass) {
				g2.drawImage(MapTypeConverter.getMapImage(d.getIden(), ""), d.getX()-MainFrame.UnitW/2, d.getY()-MainFrame.UnitW/2, MainFrame.UnitW,  MainFrame.UnitW, null);
			}
			for (MapData d: Desert) {
				g2.drawImage(MapTypeConverter.getMapImage(d.getIden(), ""), d.getX()-MainFrame.UnitW/2, d.getY()-MainFrame.UnitW/2, MainFrame.UnitW,  MainFrame.UnitW, null);
			}
			for (MapData d: Water) {
				g2.drawImage(MapTypeConverter.getMapImage(d.getIden(), ""), d.getX()-MainFrame.UnitW/2, d.getY()-MainFrame.UnitW/2, MainFrame.UnitW,  MainFrame.UnitW, null);
			}
			for (MapData d: Hill) {
				g2.drawImage(MapTypeConverter.getMapImage(d.getIden(), ""), d.getX()-MainFrame.UnitW/2, d.getY()-MainFrame.UnitW/2, MainFrame.UnitW,  MainFrame.UnitW, null);
			}
			for (MapData d: Road) {
				g2.drawImage(MapTypeConverter.getMapImage(d.getIden(), d.getDes()), d.getX()-MainFrame.UnitW/2, d.getY()-MainFrame.UnitW/2, MainFrame.UnitW,  MainFrame.UnitW, null);
			}
			for (MapData d: Plain) {
				g2.drawImage(MapTypeConverter.getMapImage(d.getIden(), ""), d.getX()-MainFrame.UnitW/2, d.getY()-MainFrame.UnitW/2, MainFrame.UnitW,  MainFrame.UnitW, null);
			}
			for (MapData d: CanNotMove) {
				g2.drawImage(MapTypeConverter.getMapImage(d.getIden(), ""), d.getX()-MainFrame.UnitW/2, d.getY()-MainFrame.UnitW/2, MainFrame.UnitW,  MainFrame.UnitW, null);
			}
			for(int i = 0; 50*i < 839; i++) {
				g2.drawLine(0, 50*i, 1553, 50*i);
			}
			for (int i = 0; 50*i < 1553; i++) {
				g2.drawLine(50*i, 0, 50*i, 839);
			}
			if(ifMoving && ifHavingMoved)g2.drawImage(MapTypeConverter.getMapImage(this.Type, this.ExtraType), this.X, this.Y, MainFrame.UnitW, MainFrame.UnitW, null);
			if(!ifMoving && !ifHavingMoved)g2.drawImage(MapTypeConverter.getMapImage(this.Type, this.ExtraType), this.X, this.Y, MainFrame.UnitW, MainFrame.UnitW, null);
		}
	}
}


		
