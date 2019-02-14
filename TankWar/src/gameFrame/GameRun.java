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
				frame.moveAllTank();																				//�ƶ�����̹�ˣ��з�̹�˽����������
				frame.moveAllBullet();																//�ƶ������ӵ�				
				frame.checkAllTanksIfHit();															//�������̹�˱��ӵ�����					
				frame.checkAllCrash();  															//�������̹�˵���ײ���				
				frame.checkAllBullets();															//��������ӵ��Ƿ��г������� 
				TankFrame.MY_MAP.checkAllDestroy(); 				//���ɱ��ݻٵ����Ƿ��б��ݻٵ�����
				frame.freshAllFT();																		//���¿������ȴʱ��	
				frame.freshAllCrash(); 															//����ˢ������̹�˵���ײ���
				frame.repaint();																					//���»���̹��				
				if (!frame.checkIfGameover()) {
					frame.showGameoverMes();
					break;																											//��鼺��̹���Ƿ��������ж��Ƿ���Ϸ����
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
