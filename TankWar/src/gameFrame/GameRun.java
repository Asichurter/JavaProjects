 package gameFrame;

public class GameRun {
	public static void main(String[] args) throws RuntimeException{
		
		Thread mythread = new Thread(()-> {
			TankFrame frame = new TankFrame();
			while(true) {				
				//System.out.println(frame.HEIGHT + " " + frame.WIDTH);
				while(frame.getIfPause()) {
					if (!frame.getIfPause())
						break;
				}
				frame.checkAndCreateSupply();
				frame.enemyRandomFire();
				frame.moveAllTank();																				//移动所有坦克，敌方坦克将会随机改向
				frame.moveAllBullet();																//移动所有子弹				
				frame.checkAllTanksIfHit();															//检查所有坦克被子弹击中					
				frame.checkAllCrash();  															//检查所有坦克的碰撞情况				
				frame.checkAllBullets();															//检查所有子弹是否有出界现象 
				TankFrame.MY_MAP.checkAllDestroy(); 				//检查可被摧毁地形是否有被摧毁的现象
				frame.freshAllFT();																		//更新开火的冷却时间	
				frame.freshAllCrash(); 															//重新刷新所有坦克的碰撞情况
				frame.repaint();																					//重新绘制坦克				
				if (!frame.checkIfGameover()) {
					frame.showGameoverMes();
					break;																											//检查己方坦克是否死亡来判断是否游戏结束
				}
				try {
					Thread.sleep(50);
				} 
				catch (InterruptedException e) {
					throw new RuntimeException();
				}
			} 
		});
		mythread.start();
	}
}
