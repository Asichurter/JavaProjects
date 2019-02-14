package mapEditor;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * ���ڽ���ͼ�������ɵ�ͼͼ�����
 */
public class MapTypeConverter{
	/**
	 * 
	 * @param iden ��ͼ�ı�ʶ����
	 * @param otherDes ��ͼ���͵Ķ�������������ǹ�·���͵Ļ���������ȫ�ƣ�
	 * @return	��ͼ���Ͷ�Ӧ��Image����
	 */
	public static Image getMapImage(int iden, String otherDes) {
		Toolkit tool = Toolkit.getDefaultToolkit();
		switch(iden) {
			case 2:
				return tool.getImage("Icon/����/�ݵ�.jpg");
			case 3:
				return tool.getImage("Icon/����/ɳĮ.jpg");
			case 4:
				return tool.getImage("Icon/����/ˮ��.png");
			case 5:
				return tool.getImage("Icon/����/ɽ��.png");
			case 6:
				return tool.getImage("Icon/����/שǽ.png");
			case 7:
				return tool.getImage("Icon/����/" + otherDes + ".png");
			case 8:
				return tool.getImage("Icon/����/ƽԭ.png");
			case 9:
				return tool.getImage("Icon/����/��ǽ.png");
			/*case -1:
				return tool.getImage("Icon/ɾ��.png");*/
			default:
				return null;
		}
	}
}
