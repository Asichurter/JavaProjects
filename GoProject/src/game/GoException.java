package game;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

/**
 * ���ڱ������������е��쳣
 *
 */
public class GoException extends Exception{
	
	private String message;

	public GoException(String message) {
		super(message);
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
