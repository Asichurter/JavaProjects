package newPattern;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
 */

import game.GoException;
import game.Point;
import game.Stone;
	
/**
 * ģʽ���࣬���а����˶�ʽ���߷���ģʽ�жϵķ���
 */
public class Pattern {

	/**
	 * ��ʽ�ܳ���
	 */
	private final int PatternLength;
	
	/**
	 * <p>ģʽ�ַ���</p>
	 * <p>����涨��</p>
	 * <p>1.�̶��ĺ��壺x��Сд�������壺o��Сд��</p>
	 * <p>2.����㣺*</p>
	 * <p>3.�����ĵĵ㣺?</p>
	 * <p>4.�����ӣ�_</p>
	 * <p>5.��ʽ�߷���A,B,C,D...T(���20��)����˳�򣬺ڰ���������</p>
	 * 
	 * <p>�ؼ��㣺</p>
	 * <p>���ĵ�һ��Ϊ��ɫ����ģʽ�ĵ�һ��һ��Ϊ��������</p>
	 */
	private String PatternString;
	
	/**
	 * ���ĵ����������
	 */
	private final constrain Constraint;
	
	/**
	 * ģʽ������Ŀ��
	 */
	public static int Width = 5;
	
	public Pattern(String ps, constrain cons, int l) {
		this.PatternString = ps;
		this.Constraint = cons;
		this.PatternLength = l;
	}
	
	/**
	 * �жϵ�ǰ�����Ƿ�ƥ�䱾ģʽ
	 * @param state ��ǰ����״̬
	 * @param p ���ĵ�����
	 * @param color ���ĵ����ɫ
	 * @return ��ǰ״̬ƥ�䱾ģʽ�Ķ�ʽ�ĵڼ�������û��ƥ�䣬�򷵻ظ���
	 * @throws GoException �����ʱ��������������쳣
	 */
	public int checkIfMatch(Stone[][] state, Point p, boolean color) throws GoException {
		//�ȼ���Ƿ����ĵ��Ƿ�������������
		if (!checkPermission(p))
			return -1;
		//���ڼ�¼ģʽ���Ƿ�������߿�ȱ�����飬falseΪ��ģʽ��ȱʡ��trueΪ�õ����
		boolean[] patternTags = new boolean[PatternLength];
		//��������ÿ�������м��
		for (int i = 1; i <= 24; i++) {
			//���ģʽ�ַ�����Ӧ��λ�ÿ�����������ʽ���ڣ��ף��գ����磩
			if (PatternString.charAt(i-1) == '?')
				continue;
			//�ȵõ�λ�ô�����������ĵ�ĵ�
			Point shiftP = getPointWhenDecoding(p, i);
			//���ĳ�����磬���䶨��Ϊ*
			if (shiftP == null) {
				//�����������ĵ㣬��λ�ó��磬�����ַ����и�λ��û�г���
				if (PatternString.charAt(i-1) != '*') {
					return -2;
				}
				//���ģʽ�ַ����и�λ��Ҳ��*
				else continue;
			}
			//���û�г���
			else {
				//�����϶�Ӧ�ĵ�Ϊ��ʱ
				if (state[shiftP.getX()][shiftP.getY()] == null) {
					//���ģʽ�ַ����Ķ�Ӧλ��ҲΪ�գ������
					if (PatternString.charAt(i-1) == '_')
						continue;
					//������Ϊ�����ַ�����Ӧλ��ΪA-T�Ķ�ʽ�ַ���
					else if (PatternString.charAt(i-1) >= 'A' && PatternString.charAt(i-1) <= 'T') {
						//��ʽ��ĳһ��û���ߵ�����¼��û���ߵ��ĵ�Ϊfalse
						patternTags[PatternString.codePointAt(i-1)-65] = false;
					}
					//Ϊ�յ���ַ���λ�ò��ǡ�_����Ҳ����A-T����ô˵����λ��ƥ��ʧ��
					else return -3;
				}
				else {
					//������ֵ���õ��ı�׼���Ժ�������ϵ�λ�õ���ɫ��Ӧ���ַ�������ɫ��Ӧ����������ɫͬ�����ĵ���ɫ
					boolean boardColor = XnOR(state[shiftP.getX()][shiftP.getY()].getColor(), color);
					//���ģʽ�ַ����еĸ�λ����A-T
					if (PatternString.charAt(i-1) >= 'A' && PatternString.charAt(i-1) <= 'T') {
						//�����׼���������ϵĶ�Ӧ����ɫ���ַ�����Ӧ�Ķ�ʽ��ɫ��ͬ�����λ��ƥ��ɹ�
						if (XnOR(((PatternString.codePointAt(i-1)-64) % 2 == 1), boardColor))
							//��¼ƥ��ɹ��ĸ�λ��
							patternTags[PatternString.codePointAt(i-1)-65] = true;
						//��ɫ��ͬ��ƥ��ʧ��
						else return -4;
					}
					//����ַ�����λ����һ���̶��ĺ��壬�ұ�׼���Ժ�������ϵĵ����ɫҲ�Ǻڣ�ƥ��ɹ�
					else if (PatternString.charAt(i-1) == 'x' && boardColor)
						continue;
					//����ַ�����λ����һ���̶��İ��壬�ұ�׼���Ժ�������ϵĵ����ɫҲ�ǰף�ƥ��ɹ�
					else if (PatternString.charAt(i-1) == 'o' && !boardColor)
						continue;
					//��������£����ж���ƥ��
					else return -5;
				}
			}
		}
		//��������ڼ�¼��ǰ�Ķ�ʽƥ�䵽�˵ڼ���
		int max = 0;
		for (int i = 0; i < PatternLength; i++) {
			//�����iƥ�䵽�ˣ������maxֵ
			if (patternTags[i]) {
				//���µ�ǰ�Ķ�ʽ�ߵ��˵ڼ���
				max = i + 1;
			}
			//���򣬲鿴�Ƿ�պ�ģʽ����ֹ����һ������ȡ���ڸ�λ���Ժ�ĵ㻹�Ƿ��з�falseֵ
			else {
				for (int j = i; j < PatternLength; j++) {
					//���������ڷ�false�㣬˵��ģʽ�ж��ˣ�ƥ��ʧ��
					if (patternTags[j])
						return -6;
				}
				//�������ͷ��û��ƥ��ʧ�ܣ�˵������ȫ��false��ƥ��ɹ�
				return max;
			}
		}
		//���ص�ǰ�Ķ�ʽλ�ã��ɹ�ƥ������£���ֵ��һ���Ǹ���
		return max;
	}
	
	/**
	 * �ӵ�ǰ��ʽ��ȡ��һ������ķ���
	 * @param mode ��ǰ��ʽ�Ĵ���
	 * @param center ���ĵ�λ��
	 * @return ��һ�����λ��
	 * @throws GoException ��õĵ�λ�ó��磬����BUG
	 */
	public Point getPatternedNextMove(int mode, Point center, boolean centerColor, boolean nextColor) throws GoException {
		//�����ʽ�������ܶ�ʽ������ͬ��������ʽ��Ȼʶ�𣬵����Ѿ����꣬û����һ����
		if (mode == PatternLength)
			return null;
		if (mode > PatternLength)
			throw new GoException("��ģʽ�Ĵ����ȡ��ʱ������ֵ������ģʽ�ܳ��ȣ�");
		if (mode < 0)
			throw new GoException("��ģʽ�Ĵ����ȡ��ʱ������ֵΪ�����������д���BUG��");
		if (!checkColorOfPatternMove(centerColor, nextColor, mode+1)) {
			//System.err.println("ģʽ����˳��ߵ�");
			return null;
		}
		Point p = getPointWhenDecoding(center, PatternString.indexOf(mode+65)+1);
		if (p == null)
			throw new GoException("�ڸ��ݵ�ǰ�Ķ�ʽ����ʱ���Ӷ�ʽ�еõ��ĵ��λ�ó��磡");
		else return p;
	}
	
	/**
	 * <p>�����һ���������ɫ�붨ʽ��������һ��������ɫ�Ƿ�һ��</p>
	 * <p>�˼�����ģʽ���������Ķ�ʽ������ɫһ��Ҫ��������ɫһ�µ���ʵ</p>
	 * @param centerColor ������ɫ
	 * @param nextColor ��һ��������ɫ
	 * @param mode ƥ�䵽��ģʽ����
	 * @return �Ƿ������ɫҪ��
	 */
	private boolean checkColorOfPatternMove(boolean centerColor, boolean nextColor, int mode) {
		//��������ɫ����������������������ɫ��ͬ��ż��������������ɫ��һ��
		//��������ͨ����ֵ��õ���
		boolean expectingColor = XnOR(mode % 2 == 1, centerColor);
		//���������ɫ��ʵ����ɫ��ͬ�������Ҫ�󣬷�֮������
		return XnOR(expectingColor, nextColor);
	}
	
	/**
	 * �õ�����boolֵ��ͬ��ֵ
	 */
	private boolean XnOR(boolean v1, boolean v2) {
		return !(v1^v2);
	}
	
	
	/**
	 * ���ݱ�ģʽ�е�constrain���ж����ĵ��Ƿ�����Ҫ��
	 * @param p ���ĵ�����
	 * @return �Ƿ�����ģʽ��Ҫ��
	 */
	private boolean checkPermission(Point p) {
		return this.Constraint.Check(p);
	}
	
	/**
	 * ���ݸ������ַ���ƥ��λ�������ĵ�λ�ã�������������ĵ�Ķ�Ӧ��λ�õ�
	 * @param p ���ĵ�
	 * @param position λ�����꣨1~24)
	 * @return �������ĵ���λ�������ƫ�Ƶ㣬�������磬�򷵻�null
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
				throw new GoException("����Pattern���������뷽��ʱ����λ�û�ȡ��ĵ�����������Χ��");
			}	
		}
		//����ڹ�����ʱ�������쳣����������ˣ���ֻ�践��null
		catch(GoException e) {
			return null;
		}
	}
	
	public String getPatternString() {
		return this.PatternString;
	}
}













