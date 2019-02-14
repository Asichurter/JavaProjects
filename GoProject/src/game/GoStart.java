package game;
import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */


/**
 * 用于启动程序的线程
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
