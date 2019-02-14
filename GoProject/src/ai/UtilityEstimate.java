package ai;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

import java.time.Instant;
import java.util.Random;

import game.GoBoard;
import game.GoException;
import game.Point;
import game.Stone;

/**
 * ���ھ�̬���õģ���������ֵ���ƺ�����ֵ���Ƶ��࣬��Ϊ��̬�����;�̬����
 */
public class UtilityEstimate {
	
	/**
	 * ������һ·��������ʱ�����õĿ�������
	 */
	private static final double RetryPossibility = 0.3;
	
	/**
	 * �������ӵ����ļӳɱ���
	 */
	private static final double MyQiRatio = 0.15;
	
	/**
	 * �������ӵ������ļӳɱ���
	 */
	private static final double MyStoneRatio = 0.3;
	
	/**
	 * �з����ӵ���������������
	 */
	private static final double EnemyStoneRatio = 0.2;
	
	/**
	 * �з����ӵ�������������
	 */
	private static final double EnemyQiRatio = 0.05;
	
	/**
	 * ����������������λ�����̵ײ�ʱ�Ĵ��ۺ�ĵ÷ֱ�����һ·����
	 */
	private static final double BottomPosPortion = 0.8;
	
	/**
	 * ����������������λ�������в�ʱ�����ۺ�ĵ÷ֱ������ݶ�Ƥ��
	 */
	private static final double CentralPosPortion = 0.9;
	
	/**
	 * ����������������λ���������ߴ�ʱ�����ۺ�ĵ÷ֱ���
	 */
	private static final double BorderPosPortion = 0.95;
	
	/**
	 * ����������������������һ����ľ����������
	 */
	private static final double DistanceCutOff = 0.1;
	
	/**
	 * �����������ж���Χ�ļ������ӵ���Ŀ���ж���Χ
	 */
	private static final int NeighborWidth = 2;
	
	/**
	 * ������������Χ�ļ������������ļӳɱ���
	 */
	private static final double NeighborPortion = 0.1;
	
	/**
	 * ������������Χ�ĵз����ӵ���������������
	 */
	private static final double NeighborEnemyPortion = 0.1;
	
	/**
	 * ������������Χ�ĵз����ӵ��������ж���Χ
	 */
	private static final int NeighborEnemyWidth = 1;
	
	/**
	 * �������������Χ�ɵ�����ļӳɱ���
	 */
	private static final double AngelEmptySitesPortion = 0.5;
	
	/**
	 * ��������������Χ�ɵ�����ļӳɱ���
	 */
	private static final double BorderEmptySitesPortion = 0.3;
	
	/**
	 * ����������������Ŀ�ļӳɱ���
	 */
	private static final double QiPortion = 0.8;
	
	/**
	 * ���������ײ�ڵ������ֵ�ľ�̬����
	 * @param board ��ǰ����
	 * @param color ������������ɫ
	 * @return ����ֵ
	 */
	public static double LastEvaluate(GoBoard board, boolean color) throws GoException {
		Stone[][] state = board.getState();
		double totalNum = 0;
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				//�������Ӻ����ļӳ�
				if (state[j][i] != null && state[j][i].getColor() == color) {
					totalNum += UtilityEstimate.MyStoneRatio;
					totalNum += board.checkQiNum(new Point(j, i)) * UtilityEstimate.MyQiRatio;
				}
				//�з����Ӻ���������
				else if (state[j][i] != null && state[j][i].getColor() != color){
					totalNum -= UtilityEstimate.EnemyStoneRatio;
					totalNum -= board.checkQiNum(new Point(j, i)) * UtilityEstimate.EnemyQiRatio;
				}
			}
		}
		return totalNum;
	}
	
	/**
	 * ���ݵ�ǰ������״̬�������㣬�õ����Ƶ�����ֵ
	 * @param board ��ǰ����״̬
	 * @param curPos ��ǰ����ڵ�
	 * @param lastPos ��һ����ڵ�
	 * @param color ��ǰ�������ɫ
	 * @return ���Ƶ���������ֵ
	 */
	public static double PosEvaluate(GoBoard board, Point curPos, Point lastPos, boolean color) throws GoException {
		double total = 0;
		Stone[][] state = board.getState();
		
		//�ȵõ������λ�ü������ϵ����site����
		Site site = PosPortionDetect(curPos);
		
		//������ֵ�ļ�Ȩ��Ϊ������ʼֵ��ƫ��������ĵ�
		total += board.checkQiNum(curPos)*QiPortion;
		
		//������һ�����ľ���ļ�Ȩֵ��������ֵ��ƫ������һ���������λ��
		total -= ManHattanDistance(curPos, lastPos)*DistanceCutOff;
		
		//������㸽���ļ������ӵ������ļ�Ȩֵ��������ֵ��ƫ�򼺷����Ӹ���ĵط�
		total += NumOfColorInRange(state, NeighborWidth, curPos, color)*NeighborPortion;
		
		//������㸽���ĵз����ӵ������ļ�Ȩֵ��������ֵ��ƫ��з����Ӹ��ٵĵ�
		total -= NumOfColorInRange(state, NeighborEnemyWidth, curPos, !color)*NeighborEnemyPortion;
		
		//�������Χ�ɵĸ����Ŀհ׵�������ļ�Ȩֵ��������ֵ��ƫ���Ǻ����߲�����Χ�ɸ���յĵ�
		total += NumOfEmptySite(state, site.code, curPos);
		
		//����������λ�ö�Ӧ�ı���ϵ�����������շ��ص�����ֵ��ƫ���ǣ����ߴ�֮���ݶ�Ƥ����
		return total*site.portion;
	}
	
	/**
	 * ̽�������λ�ü���÷ֵı���
	 * @param curPos ��ǰλ��
	 * @return Site�������а�����λ�ô���code�͵÷ֱ���portion
	 */
	private static Site PosPortionDetect(Point curPos) {
		int x = curPos.getX(), y = curPos.getY();
		
		//λ��Ϊһ·��
		if (x == 0 || x == 8 || y == 0 || y == 8)
			return new Site(0, UtilityEstimate.BottomPosPortion);
		
		//λ��Ϊ���
		else if ((x <= 2 && y <= 2) || (x >= 6 && y <= 2) || (x <= 2 && y >= 6) || (x >= 6 && y >= 6))
			return new Site(1, 1.0);
		
		//λ��Ϊ����
		else if ((x > 2 && x < 6 && y <= 2) 
				|| (x > 2 && x < 6 && y > 6) 
				|| (y > 2 && y < 6 && x <= 2)
				|| (y > 2 && y < 6 && x >= 6))
			return new Site(2, UtilityEstimate.BorderPosPortion);
		
		//λ��Ϊ�ݶ�Ƥ
		else return new Site(3, UtilityEstimate.CentralPosPortion);
	}
	
	/**
	 * ������֮��������پ��룺d=|x1-x2|+|y1-y2|
	 */
	public static int ManHattanDistance(Point p1, Point p2) {
		return Math.abs(p1.getX()-p2.getX()) + Math.abs(p2.getY()-p2.getY());
	}
	
	/**
	 * �淶�����꣬�����ڷ�Χ�ڵ�ѭ���е��ã������±����
	 * @param index ���淶���±�
	 * @return �淶������±�
	 */
	private static int NormalizeIndex(int index) {
		if (index > 8)
			return 8;
		else if (index < 0)
			return 0;
		else return index;
	}
	
	/**
	 * ̽��ָ����Χ��ָ����ɫ����������
	 * @param state ����״̬
	 * @param range ��Χ��С
	 * @param curPos ��ǰ����λ�� 
	 * @param color ��̽���������ɫ
	 * @return ��������
	 * @throws GoException ��������쳣
	 */
	public static int NumOfColorInRange(Stone[][] state, int range, Point curPos, boolean color) throws GoException {
		int counter = 0;
		for (int i = NormalizeIndex(curPos.getY()-range); 
				i <= NormalizeIndex(curPos.getY()+range); i++) {
			for (int j = NormalizeIndex(curPos.getX()-range);
					j <= NormalizeIndex(curPos.getX()-range); j++) {
				if (i < 0 || j < 0 || i > 8 || j > 8)
					throw new GoException("Ѱ�Ҷ�Ӧ��ɫ�����ӣ�����������ʱ�������������������꣺"+ i + "," + j);
				if (state[i][j] != null) {
					//�����⵽�˵�ǰ��λ�ã�������
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
	 * ̽�ⷶΧ��������λ�õ�����
	 * @param state ��ǰ����״̬
	 * @param code ����λ�ô��룬�����Site��code����
	 * @param pos ��ǰ����λ��
	 * @return ��Χ�ڵ�������λ�õ�����
	 * @throws GoException ��������쳣
	 */
	public static double NumOfEmptySite(Stone[][] state, int code, Point pos) throws GoException {
		if (code == 0 || code == 3)
			return 0;
		else {
			int counter = 0;
			for (int i = NormalizeIndex(pos.getY()-1); i < NormalizeIndex(pos.getY()+1); i++) {
				for (int j = NormalizeIndex(pos.getX()-1); j < NormalizeIndex(pos.getX()+1); j++) {
					if (i < 0 || j < 0 || i > 8 || j > 8)
						throw new GoException("Ѱ�ҿ�λ�ñ�������ʱ�������������������꣺"+ i + "," + j);
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
	 * ��һ����������һ·��������
	 * @param point ��ǰ����λ��
	 * @return ���ú������λ��
	 * @throws GoException ���ɵ�ʱ���������
	 */
	public static Point RetryWhenMeetLineOne(Point point) throws GoException {
		Random rand = new Random(Instant.now().toEpochMilli());
		if (point.getX() == 0 || point.getX() == 8 || point.getY() == 0 || point.getY() == 8) {
			//���Ϊһ·��������������������ӷ�Χ��
			if (rand.nextDouble() < UtilityEstimate.RetryPossibility)
				return new Point(rand.nextInt(9), rand.nextInt(9));
		}
		return point;
	}
}

/**
 * ����������λ�ü����������
 */
class Site{
	/**
	 * λ�ô��룺0.һ·�� 1.�� 2.�� 3.�ݶ�Ƥ
	 */
	int code;
	
	/**
	 * λ�õ�������ı���
	 */
	double portion;
	
	public Site(int c, double p) {
		this.code = c;
		this.portion = p;
	}
}



















