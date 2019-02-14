package tank_bullet;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import buff.Buff;
import buff.LoadTimeDecresing;
import gameFrame.Score;
import gameFrame.TankFrame;
import map.*;

/**
 * ����̹�˵ĸ��࣬����̹�˵Ļ���Ԫ�غͷ���
 * @author Asichurter
 */

public abstract class AbstractTank {
	private int X;
	private int Y;
	private double V;																					//Ŀǰ̹�˵��ٶ�
	private double A;																					//Ŀǰ̹�˵ļ��ٶ�
	private double FIRE_TIME;													//Ŀǰ�ڵ���ȴʱ��
	private Dir direction = Dir.RIGHT;								//Ŀǰ̹�˵ķ���
	private boolean LIVE;															//Ŀǰ̹�˵�����״̬
	private boolean IF_CRASHED = false;						//̹���Ƿ��Ѿ��⵽��ײ��
	private int HEALTH_WIDTH;
	public static int HEALTH_HEIGHT = 10;							//̹��Ѫ�����ĸ߶�
	private double HEALTH;																//̹�˵�Ѫ��
	private MapType LAST_TYPE = new NullMap();												//̹����һ�ε���
	private double ARMOR = 5;														//̹�˵Ļ���
	private int LOADFT = 2000;																//��������ȴʱ��
	private double VM = 10;																		//����ٶ�����
	private boolean IF_CANFIRE = true;									//�Ƿ�����һ���������Ͽ���
	private double DAMAGE = 25;													//̹���ӵ����˺�
	private double FULL_HEALTH =100;
	private LinkedList<Buff> Buffs = new LinkedList<>();								//����̹��״̬��ʵ����
	private boolean ifHasBulletSheild = false;																//�����Ƿ����ӵ�����
	
	
	public AbstractTank(int x, int y, double v, int HEALTH_WIDTH, double Vm) {
		this.X = x;
		this.Y = y;
		this.V = v;
		this.A = 0.5;
		this.FIRE_TIME = 0;
		this.LIVE = true;
		this.HEALTH_WIDTH = HEALTH_WIDTH;
		this.VM = Vm;
	}
	
	/**
	 * Ϊ̹�����һ��buff
	 * @param buff ����ӵ�buff�ӿ�
	 */
	public void addBuff(Buff buff) {
		this.Buffs.add(buff);
	}
	
	/**
	 * ̹�˵�����buff���й�����ֻ������ִ�е�buff�Ż�ִ�У�ͬʱ���Ὣbuffʱ�䵽�޵�buff�Ƴ�
	 */
	public void allBuffsWork() {
		if (Buffs.size() == 0)
			return;
		ListIterator<Buff> iter = this.Buffs.listIterator();
		while (iter.hasNext()) {
			Buff buff = iter.next();
			if(buff.getTimes() > 0) {
				buff.Work(this);
			}
			else {
				if (buff instanceof LoadTimeDecresing)
					TankFrame.ifLoadTimeDecresed = false;
				iter.remove();
			}
		}
	}
	
	public boolean getIfHasBulletShield() {
		return this.ifHasBulletSheild;
	}
	
	public void setIfHasBulletShield(boolean If) {
		this.ifHasBulletSheild = If;
	}
	
	public double getFullHealth() {
		return this.FULL_HEALTH;
	}
	
	public void setFullHealth(double value) {
		this.FULL_HEALTH = value;
	}
	
	public double getBulletDamage() {
		return this.DAMAGE;
	}
	
	public void setBulletDamage(double damage) {
		this.DAMAGE = damage;
	}
	
	public void setIfCanFire(boolean If) {
		this.IF_CANFIRE = If;
	}
	
	public boolean getIfCanFire() {
		return this.IF_CANFIRE;
	}
	
	public void setVm(double Vm) {
		this.VM = Vm;
	}
	
	public double getVm() {
		return this.VM;
	}
	
	//����/��ÿ�����ȴ��ʱ��
	public void setLoadFT(int load_ft) {
		this.LOADFT = load_ft;
	}
	
	public int getLoadFT() {
		return this.LOADFT;
	}
	
	/**
	 * �õ�̹�˵ĵ�ǰ���εķ�����
	 * @return ̹�˵�ǰ�����ĵ���
	 */
	public MapType getMapType() {
		return this.LAST_TYPE;
	}
	
	public double getArmor() {
		return this.ARMOR;
	}
	
	public void setArmor(double armor) {
		this.ARMOR = armor;
	}
	
	public int getHealthWidth() {
		return this.HEALTH_WIDTH;
	}
	
	public double getHealth() {
		return this.HEALTH;
	}
	
	public void setHealth(double health) {
		this.HEALTH = health;
	}
	
	public void setLive(boolean live) {
		this.LIVE = live;
	}
	
	public boolean getLive() {
		return this.LIVE;
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}
	
	public void setXY(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	
	public void resetV(double v) {
		this.V = v;
	}
	
	public void setDir(Dir dir) {
		this.direction = dir;
	}
	
	public Dir getDir() {
		return  this.direction;
	}
	
	public double getV() {
		return this.V;
	}
	
	public void setA(double a) {
		this.A = a;
	}
	
	public double getA() {
		return this.A;
	}
	
	//û��Ϊ�������дsetV����
	
	//��õ�ǰ������ȴʱ��
	public double getFT() {
		return this.FIRE_TIME;
	}
	
	public void setFT(double FT) {
		this.FIRE_TIME = FT;
	}
	
	public boolean getIfCrashed() {
		return this.IF_CRASHED;
	}
	
	public void setIfCrashed(boolean ifcrashed) {
		this.IF_CRASHED = ifcrashed;
	}
	
	public void setMapType(MapType type) {
		this.LAST_TYPE = type;
	}
	
	//��������
	public void restrictVtoMax() {
		
	}
	
	/**
	 * �������Ƿ����˱仯����������˱仯������ȡ��֮ǰ���ε�buff��ͬʱˢ�µ�ǰ�ĵ��Σ�������ǰ���ε�buffЧ��
	 * @param map ��ǰϵͳ���صĵ�ͼ
	 */
	public void refreshMapType(MyMap map) {
		boolean identifier = false;
		//ListIterator<LinkedList<MapType>> all_iter = map.allTypes.listIterator();
		ExternLoop:
		for (LinkedList<MapType> linkedlist : map.allTypes) {
			for (MapType maptype: linkedlist) {
				if (maptype.ifContains(this.X, this.Y)) {																																									//���̹���Ƿ��Ѿ���ĳ������֮���ˣ����ڱ������õ���Ϊnull
					identifier = true;
					if (this.LAST_TYPE.getClass() != maptype.getClass()) {
						LAST_TYPE.ResetTank(this);																																																								//�Ƚ�̹�˵�״̬����Ϊ�������֮ǰ
						LAST_TYPE = maptype.getInstance();																																																			//�ٸ���̹�˵�ǰ�ĵ���
						LAST_TYPE.Buff(this);
						LAST_TYPE.DeBuff(this);
						break ExternLoop;																																																																	//һ����һ�η���������α仯��ֱ������ѭ��
					}
				}
			}
		}
		if (!identifier && LAST_TYPE.getClass() != new NullMap().getClass()){ 																										//������û�����κε���֮��ʱ����������Ϊnull��
			LAST_TYPE.ResetTank(this);
			this.LAST_TYPE = new NullMap();		
		}
		/*while (all_iter.hasNext()) {
			ListIterator<MapType> iter = all_iter.next().listIterator();
			while(iter.hasNext()) {
				MapType map_unit = iter.next();													
				if (LAST_TYPE.getClass() != map_unit.getClass() && LAST_TYPE.ifContains(this.X, this.Y)) {																																		//���ڼ���Ƿ�̹�˽����˲�ͬ�ĵ���
					LAST_TYPE.ResetTank(this); 																																																									//�Ƚ�̹�˵�״̬����Ϊ�������֮ǰ
					LAST_TYPE = map_unit.getInstance();																																																			//�ٸ���̹�˵�ǰ�ĵ���
					LAST_TYPE.Buff(this);
					LAST_TYPE.DeBuff(this);
					break ExternLoop;																																																																	//һ����һ�η���������α仯��ֱ������ѭ��
				}
			}
			if(!(this.LAST_TYPE instanceof NullMap))
				this.LAST_TYPE = new NullMap();
		}*/
	}
	
	/**
	 * ���ݵ�ǰ�ķ������ڽ�̹�˽��к��˲�������Ϊ�����ͺ��˺ͷǲ����Ժ��ˡ������Ժ���������ϲ�����ǰ�������ж��Ƿ�Ӧ��ˢ�µ���״̬
	 * ������ǲ����Ժ��ˣ�����ײ���˵��ζ�������������ˢ�µ���
	 * @param times ̹�˺��˵Ĳ���
	 * @param ifTest �Ƿ��ڽ��в����Ժ��˲���
	 */
	public void Back(int times, boolean ifTest) {
		for (int i =1; i <= times; i++) {
			switch(this.getDir()) {		
				case UP:
					this.setXY(this.getX(), this.getY() + (int)this.getV());
					break;
			
				case DOWN:
					this.setXY(this.getX(), this.getY() - (int)this.getV());
					break;
			
				case LEFT:
					this.setXY(this.getX() + (int)this.getV(), this.getY());
					break;
			
				case RIGHT:
					this.setXY(this.getX() - (int)this.getV(), this.getY());
					break;			
				default:
					break;
			}
		}
		if (!ifTest)
			this.refreshMapType(TankFrame.MY_MAP);
	}
	
	/**
	 * ����̹��������ײ�ķ���
	@Override
	@param v ��ײ����ٶ�
	@param type ������������̹�˷�����ײ��������η�����ײ�Ĳ���
	@param score �Ʒֶ���
	*/
	public void Crash(double v, boolean type, Score MyScore) {
		
	}
	
	//���ӵ�����
	public void beHit(Bullet bullet, Score score) {
		
	}
	
	/**
	 * ���buff�ĵ�����
	 * @return ��ǰ̹�˵�buff������
	 */
	public ListIterator<Buff> getBuffsIter(){
		return this.Buffs.listIterator();
	}
	
	/**
	 * ̹��ɲ��
	 */
	public void brake() {
		this.V = 0;
	}
}
