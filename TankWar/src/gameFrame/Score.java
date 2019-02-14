package gameFrame;

public class Score {
	private double MY_CRASH_DAMAGE;
	private double MY_HIT_DAMAGE;
	private double ENEMY_DAMAGE_TOTAL;
	private int ELIMINATE_NUM;
	private int GAINED_SUPPLY_NUM;
	private int TOTAL_FIRE;
	private int TOTAL_FIREHIT;
	
	public Score() {
		this.ELIMINATE_NUM = 0;
		this.ENEMY_DAMAGE_TOTAL = 0;
		this.MY_CRASH_DAMAGE = 0;
		this.MY_HIT_DAMAGE = 0;
		this.GAINED_SUPPLY_NUM = 0;
		this.TOTAL_FIRE = 0;
		this.TOTAL_FIREHIT = 0;
	}
	
	public int getTotalFIre() {
		return this.TOTAL_FIRE;
	}
	
	public void increaseTotalFire() {
		this.TOTAL_FIRE++;
	}
	
	public int getTotalFIreHit() {
		return this.TOTAL_FIREHIT;
	}
	
	public void increaseTotalFireHit() {
		this.TOTAL_FIREHIT++;
	}
	
	public double getMyCrashDamage() {
		return this.MY_CRASH_DAMAGE;
	}
	
	public void setMyCrashDamage(double damage) {
		this.MY_CRASH_DAMAGE = damage;
	}
	
	public double getMyHitDamage() {
		return this.MY_HIT_DAMAGE;
	}
	
	public void setMyHitDamage(double hitdamage) {
		this.MY_HIT_DAMAGE = hitdamage;
	}
	
	public double getEnemyDamage() {
		return this.ENEMY_DAMAGE_TOTAL;
	}
	
	public void setEnemyDamage(double damage) {
		this.ENEMY_DAMAGE_TOTAL = damage;
	}
	
	public int getEliminateNum() {
		return this.ELIMINATE_NUM;
	}
	
	public void setEliminateNum(int num) {
		this.ELIMINATE_NUM = num;
	}
	
	public int getGainedSupplyNum() {
		return this.GAINED_SUPPLY_NUM;
	}
	
	public void setGainedSupplyNum(int num) {
		this.GAINED_SUPPLY_NUM = num;
	}
}
