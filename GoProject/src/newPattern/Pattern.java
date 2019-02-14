package newPattern;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import game.GoException;
import game.Point;
import game.Stone;
	
/**
 * 模式的类，其中包含了定式的走法与模式判断的方法
 */
public class Pattern {

	/**
	 * 定式总长度
	 */
	private final int PatternLength;
	
	/**
	 * <p>模式字符串</p>
	 * <p>编码规定：</p>
	 * <p>1.固定的黑棋：x（小写），白棋：o（小写）</p>
	 * <p>2.出界点：*</p>
	 * <p>3.不关心的点：?</p>
	 * <p>4.无棋子：_</p>
	 * <p>5.定式走法：A,B,C,D...T(最多20步)，按顺序，黑白依次走棋</p>
	 * 
	 * <p>关键点：</p>
	 * <p>中心点一定为黑色，且模式的第一步一定为黑棋走棋</p>
	 */
	private String PatternString;
	
	/**
	 * 中心点的限制条件
	 */
	private final constrain Constraint;
	
	/**
	 * 模式搜索框的宽度
	 */
	public static int Width = 5;
	
	public Pattern(String ps, constrain cons, int l) {
		this.PatternString = ps;
		this.Constraint = cons;
		this.PatternLength = l;
	}
	
	/**
	 * 判断当前棋盘是否匹配本模式
	 * @param state 当前棋盘状态
	 * @param p 中心点坐标
	 * @param color 中心点的颜色
	 * @return 当前状态匹配本模式的定式的第几步，若没有匹配，则返回负数
	 * @throws GoException 构造点时出现坐标溢出等异常
	 */
	public int checkIfMatch(Stone[][] state, Point p, boolean color) throws GoException {
		//先检查是否中心点是否满足限制条件
		if (!checkPermission(p))
			return -1;
		//用于记录模式点是否满足或者空缺的数组，false为该模式点缺省，true为该点存在
		boolean[] patternTags = new boolean[PatternLength];
		//对棋盘上每个码点进行检查
		for (int i = 1; i <= 24; i++) {
			//如果模式字符串对应的位置可以是任意形式（黑，白，空，出界）
			if (PatternString.charAt(i-1) == '?')
				continue;
			//先得到位置代码相对于中心点的点
			Point shiftP = getPointWhenDecoding(p, i);
			//如果某码点出界，则将其定义为*
			if (shiftP == null) {
				//如果相对于中心点，该位置出界，但是字符串中该位置没有出界
				if (PatternString.charAt(i-1) != '*') {
					return -2;
				}
				//如果模式字符串中该位置也是*
				else continue;
			}
			//码点没有出界
			else {
				//棋盘上对应的点为空时
				if (state[shiftP.getX()][shiftP.getY()] == null) {
					//如果模式字符串的对应位置也为空，则合理
					if (PatternString.charAt(i-1) == '_')
						continue;
					//棋盘上为空且字符串对应位置为A-T的定式字符串
					else if (PatternString.charAt(i-1) >= 'A' && PatternString.charAt(i-1) <= 'T') {
						//定式中某一步没有走到，记录改没有走到的点为false
						patternTags[PatternString.codePointAt(i-1)-65] = false;
					}
					//为空点的字符串位置不是‘_’，也不是A-T，那么说明该位置匹配失败
					else return -3;
				}
				else {
					//利用真值表，得到的标准化以后的棋盘上的位置的颜色对应的字符串中颜色，应该是棋盘颜色同或中心点颜色
					boolean boardColor = XnOR(state[shiftP.getX()][shiftP.getY()].getColor(), color);
					//如果模式字符串中的该位置是A-T
					if (PatternString.charAt(i-1) >= 'A' && PatternString.charAt(i-1) <= 'T') {
						//如果标准化后，棋盘上的对应点颜色与字符串对应的定式颜色相同，则该位置匹配成功
						if (XnOR(((PatternString.codePointAt(i-1)-64) % 2 == 1), boardColor))
							//记录匹配成功的该位置
							patternTags[PatternString.codePointAt(i-1)-65] = true;
						//颜色不同，匹配失败
						else return -4;
					}
					//如果字符串该位置是一个固定的黑棋，且标准化以后的棋盘上的点的颜色也是黑，匹配成功
					else if (PatternString.charAt(i-1) == 'x' && boardColor)
						continue;
					//如果字符串该位置是一个固定的白棋，且标准化以后的棋盘上的点的颜色也是白，匹配成功
					else if (PatternString.charAt(i-1) == 'o' && !boardColor)
						continue;
					//其余情况下，所有都不匹配
					else return -5;
				}
			}
		}
		//定义的用于记录当前的定式匹配到了第几步
		int max = 0;
		for (int i = 0; i < PatternLength; i++) {
			//如果第i匹配到了，则更新max值
			if (patternTags[i]) {
				//更新当前的定式走到了第几步
				max = i + 1;
			}
			//否则，查看是否刚好模式就终止于这一步，这取决于该位置以后的点还是否有非false值
			else {
				for (int j = i; j < PatternLength; j++) {
					//后续还存在非false点，说明模式中断了，匹配失败
					if (patternTags[j])
						return -6;
				}
				//如果到了头都没有匹配失败，说明后续全是false，匹配成功
				return max;
			}
		}
		//返回当前的定式位置，成功匹配情况下，改值是一个非负数
		return max;
	}
	
	/**
	 * 从当前定式获取下一步走棋的方法
	 * @param mode 当前定式的代码
	 * @param center 中心点位置
	 * @return 下一步棋的位置
	 * @throws GoException 获得的点位置出界，属于BUG
	 */
	public Point getPatternedNextMove(int mode, Point center, boolean centerColor, boolean nextColor) throws GoException {
		//如果定式代码与总定式长度相同，代表本定式虽然识别，但是已经走完，没有下一步棋
		if (mode == PatternLength)
			return null;
		if (mode > PatternLength)
			throw new GoException("从模式的代码获取点时，代码值超过了模式总长度！");
		if (mode < 0)
			throw new GoException("从模式的代码获取点时，代码值为负数！程序中存在BUG！");
		if (!checkColorOfPatternMove(centerColor, nextColor, mode+1)) {
			//System.err.println("模式走棋顺序颠倒");
			return null;
		}
		Point p = getPointWhenDecoding(center, PatternString.indexOf(mode+65)+1);
		if (p == null)
			throw new GoException("在根据当前的定式走棋时，从定式中得到的点的位置出界！");
		else return p;
	}
	
	/**
	 * <p>检查下一步走棋的颜色与定式期望的下一步走棋颜色是否一致</p>
	 * <p>此检查基于模式中奇数步的定式走棋颜色一定要与中心颜色一致的事实</p>
	 * @param centerColor 中心颜色
	 * @param nextColor 下一步走棋颜色
	 * @param mode 匹配到的模式代码
	 * @return 是否符号颜色要求
	 */
	private boolean checkColorOfPatternMove(boolean centerColor, boolean nextColor, int mode) {
		//期望的颜色：奇数步棋与中心棋子颜色相同，偶数步棋与中心颜色不一致
		//这个结果是通过真值表得到的
		boolean expectingColor = XnOR(mode % 2 == 1, centerColor);
		//如果期望颜色与实际颜色相同，则符合要求，反之不符合
		return XnOR(expectingColor, nextColor);
	}
	
	/**
	 * 得到两个bool值的同或值
	 */
	private boolean XnOR(boolean v1, boolean v2) {
		return !(v1^v2);
	}
	
	
	/**
	 * 根据本模式中的constrain，判断中心点是否满足要求
	 * @param p 中心点坐标
	 * @return 是否满足模式的要求
	 */
	private boolean checkPermission(Point p) {
		return this.Constraint.Check(p);
	}
	
	/**
	 * 根据给定的字符串匹配位置与中心点位置，返回相对于中心点的对应的位置点
	 * @param p 中心点
	 * @param position 位置坐标（1~24)
	 * @return 根据中心点与位置坐标的偏移点，如果点出界，则返回null
	 */
	private Point getPointWhenDecoding(Point p, int position){
		try {
			switch(position) {
			case 1:
				return new Point(p.getX()+1, p.getY());
			case 2:
				return new Point(p.getX()+1, p.getY()-1);
			case 3:
				return new Point(p.getX(), p.getY()-1);
			case 4:
				return new Point(p.getX()-1, p.getY()-1);
			case 5:
				return new Point(p.getX()-1, p.getY());
			case 6:
				return new Point(p.getX()-1, p.getY()+1);
			case 7:
				return new Point(p.getX(), p.getY()+1);
			case 8:
				return new Point(p.getX()+1, p.getY()+1);
			case 9:
				return new Point(p.getX()+2, p.getY()+1);
			case 10:
				return new Point(p.getX()+2, p.getY());
			case 11:
				return new Point(p.getX()+2, p.getY()-1);
			case 12:
				return new Point(p.getX()+2, p.getY()-2);
			case 13:
				return new Point(p.getX()+1, p.getY()-2);
			case 14:
				return new Point(p.getX(), p.getY()-2);
			case 15:
				return new Point(p.getX()-1, p.getY()-2);
			case 16:
				return new Point(p.getX()-2, p.getY()-2);
			case 17:
				return new Point(p.getX()-2, p.getY()-1);
			case 18:
				return new Point(p.getX()-2, p.getY());
			case 19:
				return new Point(p.getX()-2, p.getY()+1);							
			case 20:
				return new Point(p.getX()-2, p.getY()+2);
			case 21:
				return new Point(p.getX()-1, p.getY()+2);
			case 22:
				return new Point(p.getX(), p.getY()+2);
			case 23:
				return new Point(p.getX()+1, p.getY()+2);
			case 24:
				return new Point(p.getX()+2, p.getY()+2);
			default:
				throw new GoException("调用Pattern方法的译码方法时，以位置获取点的点数量超过范围！");
			}	
		}
		//如果在构建点时出现了异常，即点出界了，那只需返回null
		catch(GoException e) {
			return null;
		}
	}
	
	public String getPatternString() {
		return this.PatternString;
	}
}













