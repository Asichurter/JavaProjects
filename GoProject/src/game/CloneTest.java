package game;

import java.util.Iterator;

public class CloneTest {
	public static void main(String[] args) {
		StoneGroup group = new StoneGroup(true);
		try {
			Stone s1 = new Stone(true, 0, 0, 0);
			Stone s2 = new Stone(true, 1, 0, 0);
			Stone s3 = new Stone(true, 2, 0, 0);
			group.addStone(s1);
			group.addStone(s2);
			group.addStone(s3);
			StoneGroup anotherGroup = group.getClone();
			System.out.println(anotherGroup);
			Iterator<Stone> iter = anotherGroup.getStonesIter();
			while (iter.hasNext()) {
				Stone s = iter.next();
				if (s == s1 || s == s2 || s == s3)
					System.out.println("��¡ʧ�ܣ��������ÿ�¡��");
			}
		} catch (GoException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
}
