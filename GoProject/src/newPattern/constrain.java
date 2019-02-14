package newPattern;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import game.Point;

/**
 * 用于限制模式中,中心点位置的接口
 */
public interface constrain {
	boolean Check(Point p); 
}
