package map;

/*
 * ���ڱ�ʶ���εı�ʶ����
 * 0:ԭʼ����
 * 1:�յ�ͼ
 * 2:�ݵ�
 */

import java.awt.Image;                                                                                                                                                                                                                                                                                       
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;

import mapEditor.MapData;
import tank_bullet.*;

public  class MapType implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4795751321257096073L;
	private int X;
	private int Y;
	public static int W = 50;																																																					//���Ĭ��ֵ
	private Image image = null;
	private boolean canCross = true;																																											//Ĭ������
	private boolean canDestroy = false;
	private boolean canFire = true;																																											//�ܷ��ڸõ����¿���
	
	public MapType(Image image, boolean canCross, boolean canDestroy, boolean canFire, int X, int Y) {										//����������õĳ��๹����
		this.image = image;
		this.canCross = canCross;
		this.canDestroy = canDestroy;
		this.canFire = canFire;
		this.X = X;
		this.Y = Y;
	}
	
	public MapType(Image image, int x, int y) {
		this.image = image;
		this.X = x;
		this.Y = y;
	}
	
	//�չ����������ڴ���һ��ָ����ͼ���͵��࣬Ĭ��Ϊ��ͨ���ܿ��𣬲��ܴݻ�
	public MapType() {
		
	}
	
	public boolean getIfCanFire() {
		return this.canFire;
	}
	
	public void setIfCanFire(boolean IfCanFire) {
		this.canFire = IfCanFire;
	}
	
	public <T extends MapType> T getInstance() {
		return null;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
	
	public void setIcon(Image image) {
		this.image = image;
	}
	
	public Image getIcon() {
		return this.image;
	}
	
	public boolean getIfCanCross() {
		return this.canCross;
	}
	
	public boolean getIfCanDestroy() {
		return this.canDestroy;
	}
	
	//���ζ�̹�˵���ǿ
	public void Buff(AbstractTank tank) {																			
		
	}
	
	//���ζ�̹�˵�����
	public void DeBuff(AbstractTank tank) {			
		
	}
	
	//̹���뿪�����Ժ�ԭ��״̬
	public void ResetTank(AbstractTank tank) {
		
	}
	
	//�ж�̹���Ƿ��ڸõ����ϵķ���
	public boolean ifContains(int x, int y) {
		if (new Rectangle2D.Double(this.X - MapType.W/2, this.Y -MapType.W/2, MapType.W, MapType.W).contains(x, y))
			return true;
		else return false;
	}
	
	//���ص��ε������ַ��������ڲ���
	public String toString() {
		return null;
	}
	
	//�������ڱ�ʶ���ε�����
	public int getMapIdentity() {
		return 0;
	}
	
	//�����ܱ��ݻٵ��ε�����ֵ
	public double getHealth() {
		return 1;
	}
	
	//�����ܱ��ݻٵ��ε�����ֵ
	public void setHealth(double d) {}
	
	//���������ɱ��ݻٵ����ܵ��˺�ʱ�ķ���
	public void Crash(Bullet bullet) {}
	
	public void Crash(double v) {}
	
	//���ڿɱ��ݻٵ��ε�ͼ��仯�ķ���
	public void checkIcon() {}
	
	public MapData getMapData() {
		return new MapData(this.X, this.Y, this.getMapIdentity());
	}
}
