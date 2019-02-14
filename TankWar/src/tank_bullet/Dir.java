package tank_bullet;

/**
 * �����������ԡ����м����ײ���͵ķ���
 * @author Asichurter
 *
 */
public enum Dir {
	UP , DOWN, LEFT, RIGHT;	
	
	/**
	 * �жϷ����Ƿ���ͬ
	 * @param my ���жϵķ���
	 * @param other ���жϵķ���
	 * @return �����Ƿ���ͬ
	 */
	public static boolean equals(Dir my, Dir other) {
		return my == other;
	}
	
	/**
	 * �������ײ���������ڲⶨ����ٶ�
	 * @param dir1 �����ķ���
	 * @param dir2 �����ķ���
	 * @return
	 * 0:����ײ��
	 * 1:ͬ��ײ��
	 * 2:��ײ
	 */
	public static int checkCrashDir(Dir dir1, Dir dir2) {
		switch(dir1) {
			case UP:
				if (dir2 == RIGHT || dir2 == LEFT) 
					return 0;
				else if (dir2 == UP)
					return 1;
				else return 2;
				
			case DOWN:
				if (dir2 == RIGHT || dir2 == LEFT)
					return 0;
				else if (dir2 == DOWN)
					return 1;
				else return 2;
				
			case RIGHT:
				if (dir2 == UP || dir2 == DOWN)
					return 0;
				else if (dir2 == RIGHT)
					return 1;
				else return 2;
				
			case LEFT:
				if (dir2 == UP || dir2 == DOWN)
					return 0;
				else if (dir2 == LEFT)
					return 1;
				else return 2;
			
				default:
					return 0;
		}
	}		
	
	/**
	 * �õ�����������������
	 * @return �������������
	 */
	public String getDes() {
			switch(this) {
				case UP:
					return "��";
				case DOWN:
					return "��";
				case LEFT:
					return "��";
				case RIGHT:
					return "��";
					default:
						return "";
			}
		}
}
