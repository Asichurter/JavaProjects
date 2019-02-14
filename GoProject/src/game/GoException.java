package game;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

/**
 * 用于表征程序中所有的异常
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
