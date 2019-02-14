package map;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.stream.Collectors;
//import java.util.ListIterator;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import mapEditor.MapData;

public class MyMap {
	public LinkedList<LinkedList<MapType>> allTypes = new LinkedList<LinkedList<MapType>>();
	private LinkedList<MapType> type_Grass = new LinkedList<>();																											//���ڴ��޸��Ĵ���Ӧ����ֻ����������
	private LinkedList<MapType> type_Desert = new LinkedList<>() ;
	private LinkedList<MapType> type_Water = new LinkedList<>();
	private LinkedList<MapType> type_Hill = new LinkedList<>();
	private LinkedList<MapType> type_Road = new LinkedList<>();
	private LinkedList<MapType> type_Plain = new LinkedList<>();
	public LinkedList<MapType> type_CanNotMove = new LinkedList<>();																							//���ɱ��ݻٵ��ε���������Ϊ���У��Ա�������������������
	/*public ListIterator<LinkedList<MapType>> allIter = allTypes.listIterator();
	public ListIterator<MapType> type1_Iter = type1.listIterator();*/
	
	private boolean ifSuccessful;
	
	
	public MyMap(String folderName, JComponent com) {
		//����д��ͼ����...
		/*type_Grass.add(new Grass(350, 350));																																																//����ĵ�ͼ����
		type_Grass.add(new Grass(350 + MapType.W, 350));
		type_Grass.add(new Grass(350 + MapType.W*2, 350));
		allTypes.add(type_Grass);
		type_Desert.add(new Desert(500, 350));
		type_Desert.add(new Desert(500 + MapType.W, 350));
		type_Desert.add(new Desert(500 + 2*MapType.W, 350));
		allTypes.add(type_Desert);
		for (int i = 1; i <= 2; i++) 
			type_Water.add(new Water(350 + MapType.W*i, 450, ""));
		for (int i = 0; i<= 2; i++)
			type_Water.add(new Water(350, 450 + MapType.W*i, ""));
		allTypes.add(type_Water);
		for (int i = 0; i <= 4; i++)
			type_Hill.add(new Hill(650 + MapType.W * i, 400, ""));
		allTypes.add(type_Hill);
		for (int i = 0; i <= 2; i++)
			this.type_CanNotMove.add(new BlockWall(650 + MapType.W * i, 500, ""));
		for (int i = 0; i <= 3; i++)
			this.type_Road.add(new Road(1000, 300 + MapType.W * i, "��"));
		type_Road.add(new Road(1000, 500, "����"));
		this.type_Road.add(new Road(1050, 500, "��"));
		allTypes.add(type_Road);
		
		this.type_Road.add(new Road(149, 149, "����"));
		for (int i = 1; i <= 8; i++) {
			type_Road.add(new Road(149, 149 + i*MapType.W,  "��"));
		}
		this.type_Road.add(new Road(149, 149 + 9*MapType.W, "����"));
		for (int i = 1; i <= 20; i++) {
			this.type_Road.add(new Road(149+i*MapType.W, 149, "��"));
		}
		this.type_Road.add(new Road(149+21*MapType.W, 149, "����"));
		for (int i = 1; i <= 8; i++) {
			type_Road.add(new Road(149+21*MapType.W, 149 + i*MapType.W,  "��"));
		}
		for (int i = 1; i <= 20; i++) {
			this.type_Road.add(new Road(149+i*MapType.W, 149 + 9*MapType.W, "��"));
		}
		this.type_Road.add(new Road(149+21*MapType.W, 149 + 9*MapType.W, "����"));
		this.allTypes.add(type_Road);
		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 8; j++) {
				this.type_Grass.add(new Grass(149+i*MapType.W, 149 + j*MapType.W, ""));
			}
		}
		for (int i = 18; i <= 20; i++) {
			for (int j = 1; j <= 8; j++) {
				this.type_Grass.add(new Grass(149+i*MapType.W, 149 + j*MapType.W, ""));
			}
		}
		this.allTypes.add(type_Grass);
		for (int i = 4; i <= 6; i++) {
			for(int j = 8; j <= 14; j++) {
				this.type_Water.add(new Water(149+j*MapType.W, 149 + i*MapType.W, ""));
			}
		}
		this.allTypes.add(type_Water);
		for (int i = 9; i<= 13; i++) {
			this.type_CanNotMove.add(new BlockWall(149+i*MapType.W, 149 + 3*MapType.W, ""));
		}
		for (int i = 9; i<= 13; i++) {
			this.type_CanNotMove.add(new BlockWall(149+i*MapType.W, 149 + 7*MapType.W, ""));
		}
		this.allTypes.add(type_CanNotMove);
		for (int i = 3; i<= 17; i++) {
			for (int j =1; j <= 2; j++) {
				this.type_Hill.add(new Hill(149+i*MapType.W, 149 + j*MapType.W, ""));
			}
		}
		this.allTypes.add(type_Hill);
		for (int i = 3; i <= 7; i++) {
			for (int j = 3; j<= 7; j++) {
				this.type_Desert.add(new Desert(149+i*MapType.W, 149 + j*MapType.W, ""));
			}
		}
		for (int i = 15; i <= 17; i++) {
			for (int j = 3; j<= 7; j++) {
				this.type_Desert.add(new Desert(149+i*MapType.W, 149 + j*MapType.W, ""));
			}
		}
		this.allTypes.add(type_Desert);
		for (int i = 1; i <= 23; i++) {
			this.type_Plain.add(new Plain(49+i*MapType.W, 149 + 10*MapType.W, ""));
		}
		this.allTypes.add(type_Plain);*/
		if (readMapData(folderName, com))
			this.ifSuccessful = true;
		else ifSuccessful = false;
	}
	
	public boolean getIfSuccessful() {
		return this.ifSuccessful;
	}
	
	/**
	 * ��ȡ��ͼ�����ļ����ҷ���MapData������
	 * @param folderName ��ȡ���ļ��е�����
	 * @param com ������ʾ��Ϣ�ĸ����
	 * @return �Ƿ�ɹ���ȡ
	 */
	public boolean readMapData(String folderName, JComponent com) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/CanNotMove.dat"));
			type_CanNotMove = ((LinkedList<MapData>)in.readObject()).stream().filter(m->m.getIden()==6).map(
					(m)->MapData.decodeMapData(m, BlockWall::new)).collect(Collectors.toCollection(LinkedList<MapType>::new));
			in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/CanNotMove.dat"));
			type_CanNotMove.addAll(((LinkedList<MapData>)in.readObject()).stream().filter(m->m.getIden()==9).map(
					m->MapData.decodeMapData(m, IronWall::new)).collect(Collectors.toCollection(LinkedList<MapType>::new)));
			in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Desert.dat"));
			type_Desert = ((LinkedList<MapData>)in.readObject()).stream().map(
					(m)->MapData.decodeMapData(m, Desert::new)).collect(Collectors.toCollection(LinkedList<MapType>::new));
			in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Grass.dat"));
			type_Grass = ((LinkedList<MapData>)in.readObject()).stream().map(
					(m)->MapData.decodeMapData(m, Grass::new)).collect(Collectors.toCollection(LinkedList<MapType>::new));
			in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Hill.dat"));
			type_Hill = ((LinkedList<MapData>)in.readObject()).stream().map(
					(m)->MapData.decodeMapData(m, Hill::new)).collect(Collectors.toCollection(LinkedList<MapType>::new));
			in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Plain.dat"));
			type_Plain = ((LinkedList<MapData>)in.readObject()).stream().map(
					(m)->MapData.decodeMapData(m, Plain::new)).collect(Collectors.toCollection(LinkedList<MapType>::new));
			in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Road.dat"));
			type_Road = ((LinkedList<MapData>)in.readObject()).stream().map(
					(m)->MapData.decodeMapData(m, Road::new)).collect(Collectors.toCollection(LinkedList<MapType>::new));
			in = new ObjectInputStream(new FileInputStream("Map/" +folderName + "/Water.dat"));
			type_Water = ((LinkedList<MapData>)in.readObject()).stream().map(
					m->MapData.decodeMapData(m, Water::new)).collect(Collectors.toCollection(LinkedList<MapType>::new));
			this.allTypes.add(type_CanNotMove);
			this.allTypes.add(type_Desert);
			this.allTypes.add(type_Grass);
			this.allTypes.add(type_Hill);
			this.allTypes.add(type_Plain);
			this.allTypes.add(type_Road);
			this.allTypes.add(type_Water);
			return true;
			//System.out.println("�ɹ������ͼ�ļ���");
		} 
		catch (FileNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(com, "����Ĭ�ϵ�ͼʧ��", "����ʧ��", JOptionPane.ERROR_MESSAGE);
			return false;
		} 
		catch (IOException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(com, "����Ĭ�ϵ�ͼʧ��", "����ʧ��", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		catch(ClassNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(com, "����Ĭ�ϵ�ͼʧ��", "����ʧ��", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	//������пɱ��ݻٵ����Ƿ񱻴ݻ٣�ͬʱ�Ƴ����ݻٵĵ���
	public void checkAllDestroy() {
		ListIterator<MapType> iter = type_CanNotMove.listIterator();
		while (iter.hasNext()) {
			MapType map = iter.next();
			if (map.getHealth() <= 0)
				iter.remove();
		}
	}
	
	public Stream<LinkedList<MapType>> getStream() {
		return this.allTypes.stream();
	}
	
	//���ڵ�����ͼ
	/*public static void main(String[] args) {
		MyMap map = new MyMap();
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Map/Grass.dat"));
			out.writeObject(map.type_Grass.stream().map(m->m.getMapData()).collect(Collectors.toCollection(LinkedList::new)));
			out.close();
			out =  new ObjectOutputStream(new FileOutputStream("Map/Desert.dat"));
			out.writeObject(map.type_Desert.stream().map(m->m.getMapData()).collect(Collectors.toCollection(LinkedList::new)));
			out.close();
			out =  new ObjectOutputStream(new FileOutputStream("Map/Water.dat"));
			out.writeObject(map.type_Water.stream().map(m->m.getMapData()).collect(Collectors.toCollection(LinkedList::new)));
			out.close();
			out =  new ObjectOutputStream(new FileOutputStream("Map/Hill.dat"));
			out.writeObject(map.type_Hill.stream().map(m->m.getMapData()).collect(Collectors.toCollection(LinkedList::new)));
			out.close();
			out =  new ObjectOutputStream(new FileOutputStream("Map/Road.dat"));
			out.writeObject(map.type_Road.stream().map(m->m.getMapData()).collect(Collectors.toCollection(LinkedList::new)));
			out.close();
			out =  new ObjectOutputStream(new FileOutputStream("Map/Plain.dat"));
			out.writeObject(map.type_Plain.stream().map(m->m.getMapData()).collect(Collectors.toCollection(LinkedList::new)));
			out.close();
			out =  new ObjectOutputStream(new FileOutputStream("Map/CanNotMove.dat"));
			out.writeObject(map.type_CanNotMove.stream().map(m->m.getMapData()).collect(Collectors.toCollection(LinkedList::new)));
			out.close();
			System.out.println("�ɹ�������ͼ��");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
