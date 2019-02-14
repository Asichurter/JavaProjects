package ai;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import java.time.Instant;
import java.util.Random;

import game.GoBoard;
import game.GoException;
import game.Point;
import game.Stone;

/**
 * 用于静态调用的，计算评估值估计和启发值估计的类，都为静态方法和静态参数
 */
public class UtilityEstimate {
	
	/**
	 * 当遇到一路爬的走棋时，重置的可能因子
	 */
	private static final double RetryPossibility = 0.3;
	
	/**
	 * 己方棋子的气的加成比例
	 */
	private static final double MyQiRatio = 0.15;
	
	/**
	 * 己方棋子的数量的加成比例
	 */
	private static final double MyStoneRatio = 0.3;
	
	/**
	 * 敌方棋子的数量的削减比例
	 */
	private static final double EnemyStoneRatio = 0.2;
	
	/**
	 * 敌方棋子的气的削减比例
	 */
	private static final double EnemyQiRatio = 0.05;
	
	/**
	 * 启发参数，当棋子位于棋盘底部时的打折后的得分比例（一路爬）
	 */
	private static final double BottomPosPortion = 0.8;
	
	/**
	 * 启发参数，当棋子位于棋盘中部时，打折后的得分比例（草肚皮）
	 */
	private static final double CentralPosPortion = 0.9;
	
	/**
	 * 启发参数，当棋子位于棋盘银边处时，打折后的得分比例
	 */
	private static final double BorderPosPortion = 0.95;
	
	/**
	 * 启发参数，根据棋子与上一步棋的距离进行削减
	 */
	private static final double DistanceCutOff = 0.1;
	
	/**
	 * 启发参数，判定周围的己方棋子的数目的判定范围
	 */
	private static final int NeighborWidth = 2;
	
	/**
	 * 启发参数，周围的己方棋子数量的加成比例
	 */
	private static final double NeighborPortion = 0.1;
	
	/**
	 * 启发参数，周围的敌方棋子的数量的削减比例
	 */
	private static final double NeighborEnemyPortion = 0.1;
	
	/**
	 * 启发参数，周围的敌方棋子的数量的判定范围
	 */
	private static final int NeighborEnemyWidth = 1;
	
	/**
	 * 启发参数，金角围成的面积的加成比例
	 */
	private static final double AngelEmptySitesPortion = 0.5;
	
	/**
	 * 启发参数，银边围成的面积的加成比例
	 */
	private static final double BorderEmptySitesPortion = 0.3;
	
	/**
	 * 启发参数，气的数目的加成比例
	 */
	private static final double QiPortion = 0.8;
	
	/**
	 * 用于评估底层节点的评估值的静态方法
	 * @param board 当前棋盘
	 * @param color 评估的棋子颜色
	 * @return 评估值
	 */
	public static double LastEvaluate(GoBoard board, boolean color) throws GoException {
		Stone[][] state = board.getState();
		double totalNum = 0;
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				//己方棋子和气的加成
				if (state[j][i] != null && state[j][i].getColor() == color) {
					totalNum += UtilityEstimate.MyStoneRatio;
					totalNum += board.checkQiNum(new Point(j, i)) * UtilityEstimate.MyQiRatio;
				}
				//敌方棋子和气的削弱
				else if (state[j][i] != null && state[j][i].getColor() != color){
					totalNum -= UtilityEstimate.EnemyStoneRatio;
					totalNum -= board.checkQiNum(new Point(j, i)) * UtilityEstimate.EnemyQiRatio;
				}
			}
		}
		return totalNum;
	}
	
	/**
	 * 根据当前的棋盘状态与待行棋点，得到估计的启发值
	 * @param board 当前棋盘状态
	 * @param curPos 当前行棋节点
	 * @param lastPos 上一行棋节点
	 * @param color 当前行棋的颜色
	 * @return 估计的启发评估值
	 */
	public static double PosEvaluate(GoBoard board, Point curPos, Point lastPos, boolean color) throws GoException {
		double total = 0;
		Stone[][] state = board.getState();
		
		//先得到行棋的位置及其比例系数的site对象
		Site site = PosPortionDetect(curPos);
		
		//以气的值的加权作为启发初始值：偏向气更多的点
		total += board.checkQiNum(curPos)*QiPortion;
		
		//用与上一行棋点的距离的加权值修正启发值：偏向离上一手棋更近的位置
		total -= ManHattanDistance(curPos, lastPos)*DistanceCutOff;
		
		//用行棋点附近的己方棋子的数量的加权值修正启发值：偏向己方棋子更多的地方
		total += NumOfColorInRange(state, NeighborWidth, curPos, color)*NeighborPortion;
		
		//用行棋点附近的敌方棋子的数量的加权值修正启发值：偏向敌方棋子更少的点
		total -= NumOfColorInRange(state, NeighborEnemyWidth, curPos, !color)*NeighborEnemyPortion;
		
		//用行棋点围成的附近的空白点的数量的加权值修正启发值：偏向金角和银边并且能围成更多空的点
		total += NumOfEmptySite(state, site.code, curPos);
		
		//根据行棋点的位置对应的比例系数，修正最终返回的启发值：偏向金角，银边次之，草肚皮更次
		return total*site.portion;
	}
	
	/**
	 * 探测行棋的位置及其得分的比例
	 * @param curPos 当前位置
	 * @return Site对象，其中包含了位置代码code和得分比例portion
	 */
	private static Site PosPortionDetect(Point curPos) {
		int x = curPos.getX(), y = curPos.getY();
		
		//位置为一路爬
		if (x == 0 || x == 8 || y == 0 || y == 8)
			return new Site(0, UtilityEstimate.BottomPosPortion);
		
		//位置为金角
		else if ((x <= 2 && y <= 2) || (x >= 6 && y <= 2) || (x <= 2 && y >= 6) || (x >= 6 && y >= 6))
			return new Site(1, 1.0);
		
		//位置为银边
		else if ((x > 2 && x < 6 && y <= 2) 
				|| (x > 2 && x < 6 && y > 6) 
				|| (y > 2 && y < 6 && x <= 2)
				|| (y > 2 && y < 6 && x >= 6))
			return new Site(2, UtilityEstimate.BorderPosPortion);
		
		//位置为草肚皮
		else return new Site(3, UtilityEstimate.CentralPosPortion);
	}
	
	/**
	 * 两个点之间的曼哈顿距离：d=|x1-x2|+|y1-y2|
	 */
	public static int ManHattanDistance(Point p1, Point p2) {
		return Math.abs(p1.getX()-p2.getX()) + Math.abs(p2.getY()-p2.getY());
	}
	
	/**
	 * 规范化坐标，用于在范围内的循环中调用，避免下标出界
	 * @param index 待规范的下标
	 * @return 规范化后的下标
	 */
	private static int NormalizeIndex(int index) {
		if (index > 8)
			return 8;
		else if (index < 0)
			return 0;
		else return index;
	}
	
	/**
	 * 探测指定范围内指定颜色的棋子数量
	 * @param state 棋盘状态
	 * @param range 范围大小
	 * @param curPos 当前行棋位置 
	 * @param color 待探测的棋子颜色
	 * @return 棋子数量
	 * @throws GoException 坐标溢出异常
	 */
	public static int NumOfColorInRange(Stone[][] state, int range, Point curPos, boolean color) throws GoException {
		int counter = 0;
		for (int i = NormalizeIndex(curPos.getY()-range); 
				i <= NormalizeIndex(curPos.getY()+range); i++) {
			for (int j = NormalizeIndex(curPos.getX()-range);
					j <= NormalizeIndex(curPos.getX()-range); j++) {
				if (i < 0 || j < 0 || i > 8 || j > 8)
					throw new GoException("寻找对应颜色的棋子，而遍历棋盘时，坐标溢出！溢出的坐标："+ i + "," + j);
				if (state[i][j] != null) {
					//如果检测到了当前点位置，则跳过
					if (state[i][j].getPoint().equals(curPos))
						continue;
					if (state[i][j].getColor() == color)
						counter++;
				}
			}
		}
		return counter;
	}
	
	/**
	 * 探测范围内无棋子位置的数量
	 * @param state 当前棋盘状态
	 * @param code 行棋位置代码，详情见Site的code定义
	 * @param pos 当前行棋位置
	 * @return 范围内的无棋子位置的数量
	 * @throws GoException 坐标溢出异常
	 */
	public static double NumOfEmptySite(Stone[][] state, int code, Point pos) throws GoException {
		if (code == 0 || code == 3)
			return 0;
		else {
			int counter = 0;
			for (int i = NormalizeIndex(pos.getY()-1); i < NormalizeIndex(pos.getY()+1); i++) {
				for (int j = NormalizeIndex(pos.getX()-1); j < NormalizeIndex(pos.getX()+1); j++) {
					if (i < 0 || j < 0 || i > 8 || j > 8)
						throw new GoException("寻找空位置遍历棋盘时，坐标溢出！溢出的坐标："+ i + "," + j);
					if (state[i][j] == null)
						counter++;
				}
			}
			if (code == 1)
				return counter*UtilityEstimate.AngelEmptySitesPortion;
			else return counter*UtilityEstimate.BorderEmptySitesPortion;
		}
	}
	
	/**
	 * 以一定概率重置一路爬的走棋
	 * @param point 当前行棋位置
	 * @return 重置后的走棋位置
	 * @throws GoException 生成点时的坐标错误
	 */
	public static Point RetryWhenMeetLineOne(Point point) throws GoException {
		Random rand = new Random(Instant.now().toEpochMilli());
		if (point.getX() == 0 || point.getX() == 8 || point.getY() == 0 || point.getY() == 8) {
			//如果为一路爬且随机因子在重置因子范围内
			if (rand.nextDouble() < UtilityEstimate.RetryPossibility)
				return new Point(rand.nextInt(9), rand.nextInt(9));
		}
		return point;
	}
}

/**
 * 集成了行棋位置及其比例的类
 */
class Site{
	/**
	 * 位置代码：0.一路爬 1.角 2.边 3.草肚皮
	 */
	int code;
	
	/**
	 * 位置的削减后的比例
	 */
	double portion;
	
	public Site(int c, double p) {
		this.code = c;
		this.portion = p;
	}
}



















