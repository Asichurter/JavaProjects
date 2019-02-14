package game;
import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */


/**
 * ��������������߳�
 *
 */

public class GoStart {
	private static boolean ifAuto = false;
	
	public static void main(String[]  args) throws FileNotFoundException, GoException {
		if (!ifAuto) {
			EventQueue.invokeLater(()->{
				try {
					GoClient client = new GoClient();
				}
				catch (GoException  |  FileNotFoundException e) {
					e.printStackTrace();
				}
			});
		}
		else {
			GoClient client = new GoClient();
			Thread myThread = new Thread(()->{
				while (true) {
					try {
						client.act();
						Thread.sleep(500);
					} catch (FileNotFoundException | GoException | CloneNotSupportedException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			myThread.run();
		}
	}
}
