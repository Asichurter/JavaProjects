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
 * ������ʾ��ͼ�ĳ��������
 * 
 * @author Asichurter
 *
 */

class MainFrame extends JFrame{
	//private final int OptionMenuW = 200;																								//ѡ������ȣ��߶����ܳ�ƽ
	private static final double Ratio = 1;
	private final int W = (int)(1551*Ratio);
	private final int H = (int)(839*Ratio);
	public static final int UnitW = (int)(50*Ratio);
	private final Toolkit tool = Toolkit.getDefaultToolkit();
	public static int MouseType = 0;																							//���ڱ�ʶ��ǰ������ĵ�ͼ����
	public static String ExtraMouseType  = "";																			//���ڱ�ʶ��ͼ���͵ĸ���������
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
	
	JMenuItem clearItem = new JMenuItem("������е�ͼ��Ԫ");
	JMenuItem saveItem = new JMenuItem("�����ͼ");
	JMenuItem saveasItem = new JMenuItem("��ͼ���Ϊ");
	JMenuItem loadmapItem = new JMenuItem("�������еĵ�ͼ�ļ�");
	JMenuItem clearOneItem = new JMenuItem("�����λ");
	JCheckBoxMenuItem moveItem = new JCheckBoxMenuItem("�ƶ�");
	JMenu tipsMenu = new JMenu("ʹ����ʾ");
	JMenuItem tipsItem = new JMenuItem("ʹ�÷���");
	
	public MainFrame() {
		showMessage();
		this.setTitle("��ͼ�༭��");
		this.setVisible(true);
		this.setBounds(0,  0, (int) tool.getScreenSize().getWidth(), (int) tool.getScreenSize().getHeight());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(tool.getImage("Icon/EditorIcon.png"));
		this.getContentPane().setBackground(new Color(185, 185, 185));
		 comp = new MainComponent(lastFile);
		this.add(comp);
		
		JMenuBar menubar = new JMenuBar();
		JMenu functionMenu = new JMenu("����");
		functionMenu.add(clearItem);
		functionMenu.add(saveItem);
		functionMenu.add(saveasItem);
		functionMenu.add(loadmapItem);
		menubar.add(functionMenu);
		JMenu mapMenu = new JMenu("��ͼ");
		JMenuItem grassItem = new JMenuItem("�ݵ�");
		grassItem.addActionListener(getMenuItemListener(this.GrassIden));
		JMenuItem desertItem = new JMenuItem("ɳĮ");
		desertItem.addActionListener(getMenuItemListener(this.DesertIden));
		JMenuItem hillItem = new JMenuItem("ɽ��");
		hillItem.addActionListener(getMenuItemListener(this.HillIden));
		JMenuItem waterItem = new JMenuItem("ˮ��");
		waterItem.addActionListener(getMenuItemListener(this.WaterIden));
		JMenuItem blockwallItem = new JMenuItem("שǽ");
		blockwallItem.addActionListener(getMenuItemListener(this.BlockWallIden));
		JMenu roadMenu = new JMenu("��·");
		JMenuItem roadItem1 = new JMenuItem("��·����");
		roadItem1.addActionListener(getMenuItemListener(RoadIden, "��·����"));
		JMenuItem roadItem2 = new JMenuItem("��·��");
		roadItem2.addActionListener(getMenuItemListener(RoadIden, "��·��"));
		JMenuItem roadItem3 = new JMenuItem("��·����");
		roadItem3.addActionListener(getMenuItemListener(RoadIden, "��·����"));
		JMenuItem roadItem4 = new JMenuItem("��·��");
		roadItem4.addActionListener(getMenuItemListener(RoadIden, "��·��"));
		JMenuItem roadItem5 = new JMenuItem("��·����");
		roadItem5.addActionListener(getMenuItemListener(RoadIden, "��·����"));
		JMenuItem roadItem6 = new JMenuItem("��·����");
		roadItem6.addActionListener(getMenuItemListener(RoadIden, "��·����"));
		roadMenu.add(roadItem1);
		roadMenu.add(roadItem2);
		roadMenu.add(roadItem3);
		roadMenu.add(roadItem4);
		roadMenu.add(roadItem5);
		roadMenu.add(roadItem6);
		JMenuItem plainItem = new JMenuItem("ƽԭ");
		plainItem.addActionListener(getMenuItemListener(this.PlainIden));
		JMenuItem ironWallItem = new JMenuItem("��ǽ");
		ironWallItem.addActionListener(getMenuItemListener(this.IronWallIden));
		JMenuItem nullItem = new JMenuItem("��");
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
		JOptionPane.showMessageDialog(comp, "��ͼ�༭��ʹ����ʾ:\n"
				+ "��ͼ�༭������ʵ�ֵ�ͼ�Ŀ��ӻ��������޸ģ�ͬʱ���Է���ر������ȡ\n"
				+ "1.�����Ҫ�ڵ�ͼ��ĳ������һ�����Σ��ȵ���˵����еĵ�ͼ����ѡ���Ӧ�ĵ�ͼ���ͺ�����Ҫ���ɵ�ͼ��λ�ĵط�������ɡ�\n"
				+ "�⽫�Ḳ�ǵ�ԭ��λ���ϵĵ�ͼԪ��\n"
				+ "2.�����Ҫ���һ����Ԫ�ڵĵ�ͼԪ�أ��ȵ���˵����еĵ�ͼ����ѡ�������ť��ֻ������Ҫ�����λ�õ����������õ�Ԫ��\n"
				+ "�ڵĵ�ͼ\n"
				+ "3.�����Ҫ��һ����Ԫ���ڵĵ�ͼԪ���ƶ�����һ��λ�ã��ȵ���˵����еĵ�ͼ��ť���򿪡��ƶ������غ󣬽���Ҫ�ƶ��ĵ�ͼԪ��\n"
				+ "�϶���Ŀ�ĵغ��ɿ����ɡ�ע�⣬���ƶ������Ժ�����������ƶ�����رոÿ��ء�\n"
				+ "4.�˵����Ĺ������п���һ��������е�ͼԪ�صİ�ť�����Խ��õ�ͼ���ݱ��浽ԭ��ͼ�ļ������½��ĵ�ͼ�ļ��еİ�ť��\n"
				+ "���Դ��ļ������¶����ͼ�İ�ť��\n"
				+ "5.ע�⣬�����Ҫ����ͼ�½����Ϊ��ͼ�ļ��Ļ����������ڸ�Ŀ¼�½����ͼͬ���Ŀ��ļ��к����ڵ�ͼ�༭���ڱ����ͼ��\n", "ʹ����ʾ",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * ���ڱ�����Component�����ģ����ÿ��repaint�������ŷ���
	 */
	public void Repaint() {
		this.repaint();														//�����repaint����
	}
	
	/**
	 * ���һ����ͼ���Ͷ�Ӧ�ļ����������ڽ���굱ǰ��ͼ����и���
	 * @param iden ��ͼ�����ͱ�ʶ
	 * @return ��ͼ���Ͷ�Ӧ�ļ�����
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
	 * ���һ����ͼ���Ͷ�Ӧ�ļ�������Ϊ�˶Թ�·�Ķ����ͽ�����Ӧ�������أ����ڽ���굱ǰ��ͼ����и���
	 * @param iden ��ͼ�����ͱ�ʶ
	 * @param extra ��ͼ���͵Ķ���ʶ���
	 * @return ��ͼ���Ͷ�Ӧ�ļ�����
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
		
		private LinkedList<MapData>Grass = new LinkedList<>();																											//���ڴ��޸��Ĵ���Ӧ����ֻ����������
		private LinkedList<MapData> Desert = new LinkedList<>() ;
		private LinkedList<MapData> Water = new LinkedList<>();
		private LinkedList<MapData> Hill = new LinkedList<>();
		private LinkedList<MapData> Road = new LinkedList<>();
		private LinkedList<MapData> Plain = new LinkedList<>();
		private LinkedList<MapData> CanNotMove = new LinkedList<>();																							//���ɱ��ݻٵ��ε���������Ϊ���У��Ա�������������������
		
		private int X;
		private int Y;
		private int Type;
		private String ExtraType;
		private MainComponent  This = this;																				//�������ڲ���������this
		
		public MainComponent(String folderName) {
			this.setBackground(new Color(185, 185, 185));
			this.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent e) {																						//�������ƶ�����
					if (!ifMoving && !ifHavingMoved) {																												//�������¼���ǰ���������϶���ͼ
						X = e.getX();
						Y = e.getY();
						Type = MainFrame.MouseType;
						ExtraType = MainFrame.ExtraMouseType;
						if (Type != 0)
							setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						else setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					Repaint();																																																									//�����ⲿ���ŷ���������������
					//System.out.println("\n\nX = " + X +"\nY = " +  Y + "\n���ͣ�" + Type + " �������ͣ�" + ExtraType);
				}
				
				public void mouseDragged(MouseEvent e) {																													//�������϶��ļ���
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
				public void mousePressed(MouseEvent e) {																				//�����갴�¼���
					if (ifMoving && !ifHavingMoved) {																													//�����ƶ�״̬���ǻ�û�п�ʼ�ƶ�
						ifHavingMoved = true;
						Type = checkIfContainAndReplace(e.getX(), e.getY(), 0, "�ƶ�");
					}
					else if (Type != 0)																																					
						checkIfContainAndReplace(e.getX(), e.getY(), Type, ExtraType);
				}
				
				public void mouseReleased(MouseEvent e) {																					//�������ͷż���
					if (ifMoving && ifHavingMoved) {
						checkIfContainAndReplace(e.getX(), e.getY(), Type, ExtraType);
						ifHavingMoved = false;
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						Repaint();
					}
				}
			});
			
			//��Ӹ����˵���ť�ļ�����
			loadmapItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = JOptionPane.showInputDialog(This, "��������Ҫ���ص��ļ�������:", "���ص�ͼ", JOptionPane.INFORMATION_MESSAGE);
					if (readFile(name)) { 
						JOptionPane.showMessageDialog(This, "�ɹ������ͼ�ļ�!", "���ص�ͼ", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("Icon/��ȷ.png"));
						lastFile = name;
					}
					else JOptionPane.showMessageDialog(
							This, "����ʧ�ܣ����������Ƿ�������ȷ����·���Ƿ���ȷ��", "���ص�ͼ", JOptionPane.WARNING_MESSAGE , new ImageIcon("Icon/����.png"));
					//System.out.println(name);
				}
			});
			
			readFile(folderName);
			
			clearItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearAll();
					JOptionPane.showMessageDialog(This, "�ɹ������", "�����ͼ", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("Icon/��ȷ.png"));
					Repaint();
				}
			});
			
			saveasItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = JOptionPane.showInputDialog(This, "����Ҫ���浽���ļ���", "�����ļ�", JOptionPane.INFORMATION_MESSAGE);
					if (saveFile(name))
						JOptionPane.showMessageDialog(This, "�ɹ����浽" + name, "�����ļ�", JOptionPane.INFORMATION_MESSAGE,new ImageIcon("Icon/��ȷ.png"));
					else JOptionPane.showMessageDialog(
							This, "����ʧ�ܣ������ļ����Ƿ���ڻ����Ų�����ԭ��", "�����ļ�", JOptionPane.WARNING_MESSAGE, new ImageIcon("Icon/����.png"));
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
						JOptionPane.showMessageDialog(This, "����ɹ���", "�����ļ�", JOptionPane.INFORMATION_MESSAGE);
					else JOptionPane.showMessageDialog(This, "����ʧ��", "�����ļ�", JOptionPane.WARNING_MESSAGE);
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
		 * ����ͼ�ļ����浽�ļ����У��ļ��б������Ƚ�����
		 * @param name ���ڱ����ͼ���ļ�����
		 * @return �Ƿ�ɹ�����
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
		 * �������Ŀǰ����ĵ�ͼ�ϵ�����Ԫ��
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
		 * ���ڴ�ָ���ļ����ж�ȡ��ͼ�ļ�
		 * @param folderName ��ͼ�ļ����ڵ��ļ�����
		 * @return �Ƿ�ɹ�����
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
				//System.out.println("�ɹ������ͼ�ļ���");
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
		 * ���ڼ�������ʱ�ĵ��Ƿ������еĵ�ͼԪ���ڣ������滻����ǰ�ĵ�ĵ�ͼԪ��
		 * @param x �������X����
		 * @param y �������Y����
		 * @param iden �����ʱ��Ԥ���ͼ���ͱ�ʶ��
		 * @param des �����ʱ��Ԥ���ͼ�ĸ��ӱ�ʶ��
		 * @return �Ƿ�����������е�ͼԪ��
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
					Grass.remove(d);												//ֱ�����ü��ϵ��޸ķ������ǵ������ķ���ʱ���׳��쳣
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
					if(des.equals("�ƶ�")) ExtraType = temp.getDes();
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
		 * ���ڽ��������淶������Ӧ����������
		 * @param co ���淶��X����Y����
		 * @return �淶���Ժ������
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


		
