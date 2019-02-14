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
		return "X = " + this.X + " Y = " + this.Y + " 地图标识符：" + this.MapIdentity +" 描述：" + this.des; 
	}
	
	/**
	 * 用于将MapData地图数据类转换成MapType子类的地图类的方法
	 * @param T 继承自MapType的地图数据对应的地图类
	 * @param m 待转换的地图数据
	 * @param cons 待转换的地图数据对应的地图的类的构造器
	 * @return 地图数据对应的地图类
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
