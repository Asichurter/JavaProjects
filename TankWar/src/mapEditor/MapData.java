package mapEditor;

import java.io.Serializable;

import map.Desert;
import map.Grass;
import map.MapBuilder;
import map.MapType;
import map.Water;

public class MapData implements Serializable{
	
	private  int X;
	private  int Y;
	private  int MapIdentity;
	private String des = "";

	public MapData(int x, int y, int iden) {
		this.X = x;
		this.Y = y;
		this.MapIdentity = iden;
	}
	
	public MapData(int x, int y, int iden, String des) {
		this.X = x;
		this.Y = y;
		this.MapIdentity = iden;
		this.des = des;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y ;
	}
	
	public int getIden() {
		return this.MapIdentity;
	}

	public String getDes() {
		return this.des;
	}
	
	public void resetMapData(int x, int y, int iden, String des) {
		this.X = x;
		this.Y = y;
		this.MapIdentity = iden;
		this.des = des;
	}
	
	public String toString() {
		return "X = " + this.X + " Y = " + this.Y + " ��ͼ��ʶ����" + this.MapIdentity +" ������" + this.des; 
	}
	
	/**
	 * ���ڽ�MapData��ͼ������ת����MapType����ĵ�ͼ��ķ���
	 * @param T �̳���MapType�ĵ�ͼ���ݶ�Ӧ�ĵ�ͼ��
	 * @param m ��ת���ĵ�ͼ����
	 * @param cons ��ת���ĵ�ͼ���ݶ�Ӧ�ĵ�ͼ����Ĺ�����
	 * @return ��ͼ���ݶ�Ӧ�ĵ�ͼ��
	 */
	public static final <T extends MapType> T decodeMapData(MapData m, MapBuilder<T> cons) {
		switch(m.getIden()) {
			case 7:
				return cons.get(m.getX(), m.getY(), m.getDes());
			default:
				return cons.get(m.getX(), m.getY(), "");
		}
	}
}
